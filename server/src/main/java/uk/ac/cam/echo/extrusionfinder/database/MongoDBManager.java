package uk.ac.cam.echo.extrusionfinder.database;

import com.mongodb.DB;
import org.mongojack.JacksonDBCollection;
import uk.ac.cam.echo.extrusionfinder.parts.Part;

import java.net.UnknownHostException;

/**
 * A database wrapper which provides APIs for loading and saving parts
 * and TODO classifiers
 *
 * This implementation uses MongoDB. Many instances of MongoDBManager can be created; all will share the
 * same connection
 */
public class MongoDBManager implements IDBManager {
    private final MongoDBCollectionManager<Part> partManager;

    /**
     * @param databaseName          Name of database to connect to
     * @throws UnknownHostException Thrown if unable to connect to MongoDB
     */
    public MongoDBManager(String databaseName) throws UnknownHostException {
        DB database = MongoInstance.getDatabase(databaseName);

        partManager = new MongoDBCollectionManager<>(
                JacksonDBCollection.wrap(
                    database.getCollection("parts"), Part.class, String.class
                )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void savePart(Part part) {
        partManager.save(part);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Part loadPart(String _id) throws ItemNotFoundException {
            return partManager.load(_id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearDatabase() {
        partManager.clear();
    }
}
