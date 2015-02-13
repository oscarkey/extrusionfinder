package uk.ac.cam.cl.echo.extrusionfinder.server.database;

/**
 * Indicates that an object can be stored by IDBManager.
 * Each object to be stored is required to have a unique identifier (_id).
 *
 * @author as2388
 */
public interface DatabaseItem {
    /**
     * @return Unique identifier of this object
     */
    public String get_id();
}
