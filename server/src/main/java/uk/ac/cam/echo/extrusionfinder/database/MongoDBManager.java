package uk.ac.cam.echo.extrusionfinder.database;

import uk.ac.cam.echo.extrusionfinder.parts.Part;

/**
 * {@inheritDoc}
 *
 * This implementation uses MongoDB
 * @author as2388
 */
public class MongoDBManager implements IDBManager {
    public MongoDBManager(String DBName) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void savePart(Part part) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Part loadPart(String _id) throws PartNotFoundException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearDatabase() {

    }
}
