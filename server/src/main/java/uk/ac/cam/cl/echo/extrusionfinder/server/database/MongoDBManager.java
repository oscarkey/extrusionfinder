package uk.ac.cam.cl.echo.extrusionfinder.server.database;

import com.mongodb.DB;
import org.mongojack.JacksonDBCollection;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.ZernikeMap;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A database wrapper which provides APIs for loading and saving parts zernike maps.
 * <p>
 * This implementation uses MongoDB. Many instances of MongoDBManager can be created; all will share the
 * same connection. This manager keeps a static, in-memory thread-safe cache of zernike maps
 *
 * @author as2388
 */
public class MongoDBManager implements IDBManager {
    private final String databaseName;
    private static Map<String, ZernikeMap> zernikeMapCache = new ConcurrentHashMap<>();
    private final MongoDBCollectionManager<Part> partManager;
    private final MongoDBCollectionManager<ZernikeMap> zernikeManager;

    /**
     * @param databaseName          Name of database to connect to
     * @throws UnknownHostException Thrown if unable to connect to MongoDB
     */
    public MongoDBManager(String databaseName) throws UnknownHostException {
        this.databaseName = databaseName;

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
     * <p>
     * This implementation only makes one database call, so is more efficient than making successive savePart() calls
     * @param parts List of parts to insert.
     */
    @Override
    public void saveParts(Collection<Part> parts) {
        partManager.save(parts);
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
        zernikeMapCache.put(databaseName, zernikeMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZernikeMap loadZernikeMap() throws ItemNotFoundException {
        if (!zernikeMapCache.containsKey(databaseName)) {
            zernikeMapCache.put(databaseName, zernikeManager.load(Configuration.ZERNIKE_MAP_ID));
        }
        return zernikeMapCache.get(databaseName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearDatabase() {
        partManager.clear();
        zernikeManager.clear();
        zernikeMapCache = new ConcurrentHashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public String getDatabaseName() {
        return databaseName;
    }
}
