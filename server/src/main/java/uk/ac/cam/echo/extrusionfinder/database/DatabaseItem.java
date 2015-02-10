package uk.ac.cam.echo.extrusionfinder.database;

/**
 * Indicates that an object can be stored by IDBManager.
 * Each object to be stored is required to have a unique identifier (_id).
 */
public interface DatabaseItem {
    /**
     * @return Unique identifier of this object
     */
    public String get_id();
}
