package uk.ac.cam.echo.extrusionfinder.database;

import uk.ac.cam.echo.extrusionfinder.parts.Part;

/**
 * A database wrapper which provides APIs for loading and saving parts
 * and TODO classifiers
 *
 * @author as2388
 */
public interface IDBManager {

    /**
     * Inserts a part into the database.
     * If a part with the same ._id property already exists in the database, that part is overwritten with the new part
     *
     * @param part  Part to insert
     */
    public void savePart(Part part);

    /**
     * Loads the part with the specified identifier from the database
     * @param _id   Identifier of part to be loaded
     * @return      Part with identifier _id
     * @throws PartNotFoundException    Thrown if part with id _id is not found in the database
     */
    public Part loadPart(String _id) throws PartNotFoundException;

    // TODO: Load some sort of classifier

    /**
     * Removes all items from the database
     */
    public void clearDatabase();
}
