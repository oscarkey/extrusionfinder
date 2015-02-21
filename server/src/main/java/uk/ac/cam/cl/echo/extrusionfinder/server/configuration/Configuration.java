package uk.ac.cam.cl.echo.extrusionfinder.server.configuration;

public class Configuration {
    public static final String MONGO_HOST = "localhost";
    public static final int MONGO_PORT = 27017;

    public static final String ZERNIKE_MAP_ID = "zernike";
    public static final String DEFAULT_DATABASE_NAME = "extrusionDB";
    public static final int DEFAULT_NUMBER_OF_MATCHES = 15;
    public static final int DEFULT_ZERNIKE_DEGREE = 6;

    public static final String OPENCV_LIBRARY_NAME = "opencv_java249";

    public static final int PROFILE_DETECTION_STANDARD_IMAGE_SIZE = 200;
    public static final int PROFILE_DETECTION_STANDARD_BILATERAL_FILTER_BLUR_DIAMETER = 8;
    public static final int PROFILE_DETECTION_STANDARD_BILATERAL_FILTER_SIGMA = 15;

    private Configuration(){}
}
