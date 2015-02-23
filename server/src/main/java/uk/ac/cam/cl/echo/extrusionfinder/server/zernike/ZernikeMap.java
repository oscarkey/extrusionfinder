package uk.ac.cam.cl.echo.extrusionfinder.server.zernike;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.DatabaseItem;

import java.util.Map;

/**
 * Stores a map from part identifiers to Zernike Moments
 *
 * @author as2388
 */
public class ZernikeMap implements DatabaseItem {
    private final String _id;
    private final Map<String, double[]> zernikeMap;

    @JsonCreator
    public ZernikeMap(@JsonProperty("_id") String _id, @JsonProperty("zernikeMap") Map<String, double[]> map) {
        this._id = _id;
        this.zernikeMap = map;
    }

    /**
     * @param map Map from part identifiers to Zernike Moments
     */
    public ZernikeMap(Map<String, double[]> map) {
        this._id = Configuration.ZERNIKE_MAP_ID;
        this.zernikeMap = map;
    }

    @Override
    public String get_id() {
        return _id;
    }

    /**
     * @return Map from part identifiers to Zernike Moments
     */
    public Map<String, double[]> getZernikeMap() {
        return zernikeMap;
    }
}
