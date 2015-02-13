package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

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
    private final Map<String, Double[]> zernikeMap;

    @JsonCreator
    public ZernikeMap(@JsonProperty("_id") String _id, @JsonProperty("zernikeMap") Map<String, Double[]> map) {
        this._id = _id;
        this.zernikeMap = map;
    }

    /**
     * @param map Map from part identifiers to Zernike Moments
     */
    public ZernikeMap(Map<String, Double[]> map) {
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
    public Map<String, Double[]> getZernikeMap() {
        return zernikeMap;
    }
}
