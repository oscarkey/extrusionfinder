package uk.ac.cam.cl.echo.extrusionfinder.server.orchestration;

import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.ZernikeMap;

import java.util.Map;
import java.util.stream.Stream;

/**
 * A singleton for managing ZernikeMap objects. It is neded to prevent reloading of the map from the database
 * every time the servlet receives a request
 *
 * @author as2388
 */
public class ZernikeManager {
    private ZernikeManager(){}

    /**
     * If no ZernikeMap has been loaded, then load it from the database,
     * else do nothing
     */
    private static void initialiseZernikeMap() {

    }

    /**
     * Returns the contents of the current ZernikeMap as a stream
     * @return  Current ZernikeMap
     */
    public static Stream<Map.Entry<String, Double[]>> getZernikeMoments() {
        new ZernikeMap(null).getZernikeMap().entrySet().stream();
        return null;
    }

    /**
     * Updates the ZernikeMap being used to {@param map}. Use to update the map while the servlet is running
     * @param zernikeMap ZernikeMap to update internal ZernikeMap with
     */
    public static void updateZernikeMap(ZernikeMap zernikeMap) {

    }
}
