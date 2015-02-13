package uk.ac.cam.cl.echo.extrusionfinder.server.database;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to a database handle.
 *
 * @author as2388
 */
class MongoInstance {
    private static Map<String, DB> databases = new HashMap<>();

    private MongoInstance(){}

    /**
     * @return                      Handle to a MongoDB database
     * @throws UnknownHostException Thrown if connecting to MongoDB failed.
     */
    public static DB getDatabase(String databaseName) throws UnknownHostException {
        if (!databases.containsKey(databaseName)) {
            MongoClient client =
                    new MongoClient(Configuration.MONGO_HOST, Configuration.MONGO_PORT);
            databases.put(databaseName, client.getDB(databaseName));
        }
        return databases.get(databaseName);
    }
}
