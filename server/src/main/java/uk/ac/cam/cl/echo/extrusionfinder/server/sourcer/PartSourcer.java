package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Manufacturers;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Manufacturers.Name;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Manufacturer;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.Controller;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.CrawlControllerFactory;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.CrawlerException;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.ExtendedCrawler;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.util.FileUtility;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.util.FileUtilityException;
import org.apache.batik.transcoder.TranscoderException;

import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.Zernike;
import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.ZernikeMap;
import uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.ProfileFitting;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.GrayscaleImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.ImageLoadException;
import uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web.SeagatePDFProcessor;
import uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web.ProfileNotFoundException;
import java.awt.geom.Point2D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Program that crawls plastic extrusion vendors for information about the
 * extrusions they sell, in particular links and images.
 * The found extrusions + metadata is saved to a database.
 */
public class PartSourcer {

    private static final Logger logger =
        LoggerFactory.getLogger(PartSourcer.class);

    /**
     * Commandline invocation of the part sourcer.
     * @param args  String array containing the arguments. First argument is the
     *              name of the database where the parts are saved. If no argument,
     *              use the default database name as specified in Configuration.
     */
    public static void main(String[] args) {

        String dbName = args.length > 0 ? args[0] : Configuration.DEFAULT_DATABASE_NAME;

        try {

            IDBManager db = new MongoDBManager(dbName);
            db.clearDatabase();

            // run the crawlers and put the found extrusions into the database
            Collection<Controller<? extends ExtendedCrawler>> crs = getControllers();
            updateDatabase(db, crs);

        } catch (UnknownHostException
                |CrawlerException
                |IllegalArgumentException e) {

            logger.error(e.getLocalizedMessage());

        }

    }

    /**
     * @return A collection of all crawlers as found in the static Manufacturers class.
     * @throws CrawlerException         if the controller constructor failed.
     * @throws IllegalArgumentException if the manufacturer seed array was empty.
     */
    public static Collection<Controller<? extends ExtendedCrawler>> getControllers()
        throws CrawlerException, IllegalArgumentException {

        // initialise collection of website crawlers
        Collection<Controller<? extends ExtendedCrawler>> controllers =
            new ArrayList<Controller<? extends ExtendedCrawler>>();

        // get config options from Configuration file
        String dir = Configuration.getCrawlStorageFolder();
        int maxDepth = Configuration.getMaxCrawlDepth();
        int maxPages = Configuration.getMaxCrawlPages();

        // get manufacturers
        Map<Name, Manufacturer> mans = Manufacturers.getAll();

        for (Manufacturer manufacturer : mans.values()) {

            ExtendedCrawler crawler = manufacturer.getCrawler();
            String[] seeds = manufacturer.getSeeds();

            // NOTE: must call CrawlControllerFactory again for each
            // crawler! Each crawler must have their own CrawlController
            // instance. (crawlcontroller is the crawler4j controller)

            Controller<? extends ExtendedCrawler> c =
                new Controller<ExtendedCrawler>(
                    CrawlControllerFactory.get(dir, maxDepth, maxPages),
                    crawler,
                    seeds
                );

            controllers.add(c);
        }

        return controllers;
    }

    /**
     * Crawls the passed crawlers for extrusions, saving the found parts in db.
     * @param dbManager     Database manager for saving the found parts
     * @param controllers   Collection of crawlers to be run
     */
    public static void updateDatabase(IDBManager dbManager,
        Collection<Controller<? extends ExtendedCrawler>> controllers) {

        logger.info("Updating database...");

        // create temp folder for saving pdfs
        String storageFolder = FileUtility.createPath(
            Configuration.getCrawlStorageFolder(), "temp");
        FileUtility.makeDir(storageFolder);

        for (Controller<? extends ExtendedCrawler> c : controllers) {

            try {
                Set<Part> parts = c.crawl();
                String msg = String.format("Found %d parts.", parts.size());
                logger.info(msg);

                saveParts(storageFolder, dbManager, parts);

            } finally {

                // cleanup; it's nice and safe and all, but not strictly
                // necessary, since if run through the commandline, this
                // program will be closing immediately afterwards anyway.
                c.stop();

            }
        }

        // delete the temp folder and all its contents
        FileUtility.delete(storageFolder);
    }

    /**
     * Saves the parts and their zernike moments to the database.
     * @param tempFolder    Temporary folder to hold pdf data
     * @param dbManager     Database manager used for database access
     * @param parts         The parts to be zernike'd and saved
     */
    private static void saveParts(String tempFolder, IDBManager dbManager, Set<Part> parts) {

        ZernikeMap map = new ZernikeMap(new HashMap<String, double[]>());
        Set<Part> partz = new HashSet<Part>();

        for (Part p : parts) {

            String id = p.getManufacturerId();
            GrayscaleImageData gsid = null;

            // if seagate, initialise the grayscaleimagedata from pdf
            if (Manufacturers.get(Name.SEAGATE).getManufacturerId().equals(id)) {
                try {

                    // download the pdf
                    String link = p.getLink();
                    String pdfPath = FileUtility.downloadFile(link, tempFolder);
                    String imgPath = FileUtility.createPath(tempFolder, "temp.png");

                    // process pdf into png
                    SeagatePDFProcessor.process(pdfPath, imgPath);

                    // create grayscaleimage from the png image
                    gsid = GrayscaleImageData.load(imgPath);

                } catch (IOException
                        |FileUtilityException
                        |TranscoderException
                        |InterruptedException
                        |ProfileNotFoundException
                        |ImageLoadException e) {

                   logger.error(e.getMessage());
                }
            }

            if (gsid != null) {

                // compute the zernike moment
                ProfileFitting pf = new ProfileFitting(gsid);
                final int degree = Configuration.DEFAULT_ZERNIKE_DEGREE;
                Point2D center = pf.getCentre();
                double radius = pf.getRadius();
                double[] zernikem = Zernike.zernikeMoments(
                    gsid.data, gsid.width, degree, center, radius);

                map.add(p.get_id(), zernikem);

                // save the part to the database, only if we have a matching zernike moment
                partz.add(p);

            }
        }

        dbManager.saveZernikeMap(map);
        dbManager.saveParts(partz);
    }
}


