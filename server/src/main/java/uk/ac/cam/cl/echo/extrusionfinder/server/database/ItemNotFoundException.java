package uk.ac.cam.cl.echo.extrusionfinder.server.database;

/**
 * Indicates that an item with the requested id was not found in the database being used
 *
 * @author as2388
 */
public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String msg) {
        super(msg);
    }
}
