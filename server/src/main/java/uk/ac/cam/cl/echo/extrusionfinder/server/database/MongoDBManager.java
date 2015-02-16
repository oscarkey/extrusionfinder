package uk.ac.cam.cl.echo.extrusionfinder.server.database;

import com.mongodb.DB;
import org.mongojack.JacksonDBCollection;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.ZernikeMap;

import java.net.UnknownHostException;

/**
 * A database wrapper which provides APIs for loading and saving parts zernike maps
 *
 * This implementation uses MongoDB. Many instances of MongoDBManager can be created; all will share the
 * same connection
 */
public class MongoDBManager implements IDBManager {
    private final MongoDBCollectionManager<Part> partManager;
    private final MongoDBCollectionManager<ZernikeMap> zernikeManager;

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

        zernikeManager = new MongoDBCollectionManager<>(
                JacksonDBCollection.wrap(
                        database.getCollection("zernikemap"), ZernikeMap.class, String.class
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
    public void saveZernikeMap(ZernikeMap zernikeMap) {
        zernikeManager.save(zernikeMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZernikeMap loadZernikeMap() throws ItemNotFoundException {
        return zernikeManager.load(Configuration.ZERNIKE_MAP_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearDatabase() {
        partManager.clear();
        zernikeManager.clear();
    }
}
