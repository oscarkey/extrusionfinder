package uk.ac.cam.echo.extrusionfinder.database;

import com.mongodb.DB;
import org.mongojack.JacksonDBCollection;
import uk.ac.cam.echo.extrusionfinder.parts.Part;

import java.net.UnknownHostException;

/**
 * {@inheritDoc}
 *
 * This implementation uses MongoDB
 */
public class MongoDBManager implements IDBManager {
    private final MongoDBCollectionManager<Part> partManager;

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
    public Part loadPart(String _id) throws PartNotFoundException {
        try {
            return partManager.load(_id);
        } catch (ItemNotFoundException e) {
            throw new PartNotFoundException(_id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearDatabase() {
        partManager.clear();
    }
}
