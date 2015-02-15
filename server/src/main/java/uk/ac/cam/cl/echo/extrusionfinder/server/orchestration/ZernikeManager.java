package uk.ac.cam.cl.echo.extrusionfinder.server.orchestration;

import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.ZernikeMap;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A singleton for managing ZernikeMap objects and interactions with the database.
 * <p>
 * ZernikeManagers connected to the same database share the same in-memory ZernikeMap
 *
 * @author as2388
 */
public class ZernikeManager implements AutoCloseable {
    private final String databaseName;
    private final IDBManager dbManager;
    private static final Map<String, ZernikeMap> zernikeMap = new HashMap<>();

    /**
     * @param dbManager Database to use for accessing/saving Zernike Maps
     */
    public ZernikeManager(IDBManager dbManager) {
        this.databaseName = dbManager.getDatabaseName();
        this.dbManager = dbManager;
    }
    /**
     * If no ZernikeMap has been loaded, then load it from the database,
     * else do nothing
     */
    private void initialiseZernikeMap() throws ItemNotFoundException {
        if (!zernikeMap.containsKey(databaseName)) {
            zernikeMap.put(databaseName, dbManager.loadZernikeMap());
        }
    }

    /**
     * Returns the contents of the current ZernikeMap as a stream
     * @return Current ZernikeMap
     */
    public Stream<Map.Entry<String, Double[]>> getZernikeMoments() throws ItemNotFoundException {
        initialiseZernikeMap();
        return zernikeMap.get(databaseName).getZernikeMap().entrySet().parallelStream();
    }

    /**
     * Updates the ZernikeMap being used by this database
     * @param map ZernikeMap to update internal ZernikeMap with
     */
    public void updateZernikeMap(ZernikeMap map) throws UnknownHostException {
        dbManager.saveZernikeMap(map);
        zernikeMap.put(databaseName, map);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        zernikeMap.remove(databaseName);
    }
}
