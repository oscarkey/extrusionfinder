package uk.ac.cam.echo.extrusionfinder.database;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import uk.ac.cam.echo.extrusionfinder.configuration.Configuration;

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

    /**
     * @return                      Handle to a MongoDB database
     * @throws UnknownHostException Thrown if connecting to MongoDB failed.
     */
    public static DB getDatabase(String databaseName) throws UnknownHostException {
        if (!databases.containsKey(databaseName)) {
            MongoClient client =
                    new MongoClient(Configuration.getMongoHost(), Configuration.getMongoPort());
            databases.put(databaseName, client.getDB(databaseName));
        }
        return databases.get(databaseName);
    }
}
