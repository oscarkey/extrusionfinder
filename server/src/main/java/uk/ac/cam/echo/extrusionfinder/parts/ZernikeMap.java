package uk.ac.cam.echo.extrusionfinder.parts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.cam.echo.extrusionfinder.configuration.Configuration;
import uk.ac.cam.echo.extrusionfinder.database.DatabaseItem;

import java.util.Map;

/**
 * Stores a map from part identifiers to Zernike Moments
 *
 * @author as2388
 */
public class ZernikeMap implements DatabaseItem {
    private final String _id;
    private final Map<String, Float[]> zernikeMap;

    @JsonCreator
    public ZernikeMap(@JsonProperty("_id") String _id, @JsonProperty("zernikeMap") Map<String, Float[]> map) {
        this._id = _id;
        this.zernikeMap = map;
    }

    /**
     * @param map Map from part identifiers to Zernike Moments
     */
    public ZernikeMap(Map<String, Float[]> map) {
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
    public Map<String, Float[]> getZernikeMap() {
        return zernikeMap;
    }
}
