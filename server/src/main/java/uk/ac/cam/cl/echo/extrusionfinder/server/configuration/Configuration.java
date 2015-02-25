package uk.ac.cam.cl.echo.extrusionfinder.server.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.ExtendedCrawler;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.SeagateCrawler;

public class Configuration {
    public static final String MONGO_HOST = "localhost";
    public static final int MONGO_PORT = 27017;

    public static final String ZERNIKE_MAP_ID = "zernike";
    public static final String DEFAULT_DATABASE_NAME = "extrusionDB";
    public static final int DEFAULT_NUMBER_OF_MATCHES = 15;
    public static final int DEFAULT_ZERNIKE_DEGREE = 6;

    public static final String INKSCAPE_LOCATION = "inkscape";

    public static final String OPENCV_LIBRARY_NAME = "opencv_java249";

    public static final int PROFILE_DETECTION_STANDARD_IMAGE_SIZE = 200;
    public static final int PROFILE_DETECTION_STANDARD_BILATERAL_FILTER_BLUR_DIAMETER = 8;
    public static final int PROFILE_DETECTION_STANDARD_BILATERAL_FILTER_SIGMA = 15;

    /* Crawler configuration.
     * Need to have getters to allow mocking in tests.
     * Max crawl depth and max crawl pages can be -1 if there is no upper limit.
     */
    private static final String CRAWL_STORAGE_FOLDER = "crawlerdata/root";
    private static final int MAX_CRAWL_DEPTH = 5;
    private static final int MAX_CRAWL_PAGES = 20;


    private Configuration(){}

    /**
     * @return  Path to the folder that stores intermediate crawl data.
     */
    public static String getCrawlStorageFolder() {
        return CRAWL_STORAGE_FOLDER;
    }

    /**
     * @return  The maximum depth of recursive crawls. -1 if unlimited.
     */
    public static int getMaxCrawlDepth() {
        return MAX_CRAWL_DEPTH;
    }

    /**
     * @return  The maximum number of pages visited during crawling. -1 if unlimited.
     */
    public static int getMaxCrawlPages() {
        return MAX_CRAWL_PAGES;
    }

}
