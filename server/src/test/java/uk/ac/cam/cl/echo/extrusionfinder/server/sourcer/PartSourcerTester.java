package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Manufacturers;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Manufacturers.Name;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Manufacturer;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.Controller;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.CrawlControllerFactory;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.CrawlerException;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.ExtendedCrawler;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.easymock.PowerMock.*;

/* <p>Unit tests the part sourcer.</p>
 * <p>Cannot test main() because it relies on other static methods within the
 * same class, which is currently not testable with PowerMock.
 * (see SourcerIntegrationTester for integration test of main)</p>
 *
 * <p>Note that the above mentioned staticness is necessary because PartSourcer
 * contains an entrypoint (main function) to let it be run on the commandline.</p>
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, Manufacturers.class, CrawlControllerFactory.class})
public class PartSourcerTester {

    /**
     * <p>Tests the updateDatabase() method, which starts all the crawlers and
     * saves all the found parts to the database.</p>
     * <p>NOTE: uses mockito!</p>
     * <p></p>
     * <p>Suppresses unchecked warning due to mocking generic class (Controller)
     * without typing its parameter.</p>
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateDatabase() throws CrawlerException {

        // create mock controller<extendedcrawler>
        MongoDBManager dbManager = mock(MongoDBManager.class);
        Controller<ExtendedCrawler> c1 = mock(Controller.class);
        Controller<ExtendedCrawler> c2 = mock(Controller.class);

        // create list containing only the above controllers
        Collection<Controller<? extends ExtendedCrawler>> controllers =
            new ArrayList<Controller<? extends ExtendedCrawler>>();
        controllers.add(c1);
        controllers.add(c2);

        // create mock of parts returned by both of the controllers when run
        Set<Part> fakeparts = new HashSet<Part>();
        Part part = mock(Part.class);
        fakeparts.add(part);
        when(c1.crawl()).thenReturn(fakeparts);
        when(c2.crawl()).thenReturn(fakeparts);

        // run the method!
        PartSourcer.updateDatabase(dbManager, controllers);

        // assert that the required methods are invoked
        verify(c1).crawl();
        verify(c2).crawl();
        verify(dbManager, times(2)).savePart(part);
    }

    /**
     * <p>Tests the getControllers() method which gets all the crawler config
     * options and the configured crawlers, and initialises controllers for
     * each.</p>
     * <p>NOTE: uses powermock! (due to mocking static methods)</p>
     * <p></p>
     * <p>Suppresses unchecked warning due to mocking generic class (Controller)
     * without typing its parameter.</p>
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testGetControllers() throws Exception{

        mockStatic(Configuration.class);
        mockStatic(Manufacturers.class);
        mockStatic(CrawlControllerFactory.class);

        // our dummy/mocked objects
        CrawlController crawlcontroller = createMock(CrawlController.class);
        ExtendedCrawler crawler = createMock(ExtendedCrawler.class);
        Manufacturer manufacturer = createMock(Manufacturer.class);
        Map manufacturers = new HashMap<Name, Manufacturer>();
        manufacturers.put(Name.SEAGATE, manufacturer);

        // expect calls to configuration
        expect(Configuration.getCrawlStorageFolder()).andReturn("foo");
        expect(Configuration.getMaxCrawlDepth()).andReturn(1);
        expect(Configuration.getMaxCrawlPages()).andReturn(2);
        expect(Manufacturers.getAll()).andReturn(manufacturers);

        // when retrieving crawler from manufacturer, return mocked crawler
        expect(manufacturer.getCrawler()).andReturn(crawler);
        expect(manufacturer.getSeeds()).andReturn(new String[] { "bar" });

        // expect constructors/factory calls for controller and crawlcontroller
        expect(CrawlControllerFactory.get("foo", 1, 2)).andReturn(crawlcontroller);
        crawlcontroller.addSeed("bar");
        expectLastCall().once();

        replayAll();

        // check that the call returned a collection of one crawler as expected
        Collection<Controller<? extends ExtendedCrawler>> cs =
            PartSourcer.getControllers();
        assertTrue(cs.size() == 1);

        verifyAll();
    }
}
