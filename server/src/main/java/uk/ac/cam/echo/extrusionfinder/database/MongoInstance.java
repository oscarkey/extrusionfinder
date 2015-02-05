package uk.ac.cam.echo.extrusionfinder.database;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to a database handle.
 *
 * @author as2388
 */
class MongoInstance { //TODO: Modify public modifier to whatever it should really be
    private static Map<String, DB> databases = new HashMap<>();

    /**
     * @return                      Handle to a MongoDB database
     * @throws UnknownHostException Thrown if connecting to MongoDB failed.
     */
    public static DB getDatabase(String databaseName) throws UnknownHostException {
        if (!databases.containsKey(databaseName)) {
            MongoClient client = new MongoClient("localhost", 27017); //TODO: un-hard code host and port
            databases.put(databaseName, client.getDB(databaseName)); //TODO: database name
        }
        return databases.get(databaseName);
    }
}
