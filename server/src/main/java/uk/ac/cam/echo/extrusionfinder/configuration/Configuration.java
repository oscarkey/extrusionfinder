package uk.ac.cam.echo.extrusionfinder.configuration;

public class Configuration {
    private static final String MONGO_HOST = "localhost";
    private static final int MONGO_PORT = 27017;

    public static String getMongoHost() {
        return MONGO_HOST;
    }

    public static int getMongoPort() {
        return MONGO_PORT;
    }
}
