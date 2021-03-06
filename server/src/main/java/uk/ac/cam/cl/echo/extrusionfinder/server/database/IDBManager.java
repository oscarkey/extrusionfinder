package uk.ac.cam.cl.echo.extrusionfinder.server.database;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.ZernikeMap;

import java.util.Collection;
import java.util.Set;

/**
 * A database wrapper which provides APIs for loading and saving parts and zernike maps
 *
 * @author as2388
 */
public interface IDBManager {

    /**
     * Inserts a part into the database.
     * If a part with the same ._id property already exists in the database, that part is overwritten with the new part
     *
     * @param part  Part to insert.
     */
    public void savePart(Part part);

    /**
     * Inserts a set of parts into the database
     * If any of the parts with the same _id property already exist in the database, those will be overwritten.
     * If the collection has multiple parts with the same _id property, one of those will be selected at random for
     * insertion.
     * @param parts Collection of parts to insert.
     */
    public void saveParts(Collection<Part> parts);

    /**
     * Loads the part with the specified identifier from the database
     * @param _id                    Identifier of part to be loaded
     * @return                       Part with identifier _id
     * @throws ItemNotFoundException Thrown if part with id _id is not found in the database
     */
    public Part loadPart(String _id) throws ItemNotFoundException;

    /**
     * Inserts a zernike map into the database.
     * If the database already contains a zernike map, that map is overwritten
     * @param zernikeMap    ZernikeMap to insert
     */
    public void saveZernikeMap(ZernikeMap zernikeMap);

    /**
     * Loads the ZernikeMap in the database from the database
     * @return                       Current ZernikeMap in database
     * @throws ItemNotFoundException Thrown if database does not contain a ZernikeMap
     */
    public ZernikeMap loadZernikeMap() throws ItemNotFoundException;

    /**
     * Removes all items from the database
     */
    public void clearDatabase();

    /**
     * @return Name of database
     */
    public String getDatabaseName();
}
