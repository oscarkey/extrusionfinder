package uk.ac.cam.cl.echo.extrusionfinder.server.database.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.cam.echo.extrusionfinder.database.ItemNotFoundException;
import uk.ac.cam.echo.extrusionfinder.database.MongoDBManager;
import uk.ac.cam.echo.extrusionfinder.parts.Part;

import java.net.UnknownHostException;

import static org.junit.Assert.*;

/**
 * Tests MongoDBManager.
 *
 * The tests in this file are really integration tests rather than unit tests,
 * but since the only thing the code does is immediately save, there isn't
 * really a unit to be able to test
 *
 * @author as2388
 */
public class MongoDBManagerTester {
    private MongoDBManager dbManager;

    /**
     * Creates a connection to a database called 'test'
     * @throws UnknownHostException
     */
    @Before
    public void setUp() throws UnknownHostException {
        dbManager = new MongoDBManager("test");
    }

    /**
     * Tests clearing the database, and that saved parts can be loaded.
     */
    @Test
    public void testPartsAndClear() {
        // test that part with id "id_test" is not in DB
        dbManager.clearDatabase();
        try {
            dbManager.loadPart("id_test");
            fail("Loading from an empty database did not trigger an exception");
        } catch (ItemNotFoundException e) {
            assertTrue(e.getMessage().equals("Item with id 'id_test' not found in database"));
        }

        // create a new part and insert it into the database
        Part part = new Part("id_test", "", "", "link", "imageL");
        dbManager.savePart(part);

        // test that the part can now be correctly loaded from the database
        try {
            Part loadedPart = dbManager.loadPart("id_test");
            assertTrue(loadedPart.equals(part));
        } catch (ItemNotFoundException e) {
            fail("Part just saved not found in database");
        }

        // insert a new part which has the same identifier. This should succeed: test by loading from database
        part = new Part("id_test", "", "", "link2", "imageLink2");
        dbManager.savePart(part);
        try {
            Part loadedPart = dbManager.loadPart("id_test");
            assertTrue(loadedPart.equals(part));
        } catch (ItemNotFoundException e) {
            fail("Modified part not saved in DB");
        }

        // clear the database, and attempt to load the part again. This should fail
        dbManager.clearDatabase();
        try {
            dbManager.loadPart("id_test");
            fail("Loading from an empty database did not trigger an exception");
        } catch (ItemNotFoundException e) {
            assertTrue(e.getMessage().equals("Item with id 'id_test' not found in database"));
        }
    }

    /**
     * Tests that multiple MongoDBManagers can be created which reference the same database, and that after saving
     * a part with one DBManager enables the part to be loaded from another
     */
    @Test
    public void testMultipleDatabaseRequests() throws UnknownHostException {
        MongoDBManager db1 = new MongoDBManager("test-2");
        MongoDBManager db2 = new MongoDBManager("test-2");

        Part part = new Part("Mid", "Pid", "link", "imageL");
        db1.savePart(part);

        // test that the part can now be correctly loaded from the database
        try {
            Part loadedPart = db2.loadPart(part.get_id());
            assertTrue(loadedPart.equals(part));
        } catch (ItemNotFoundException e) {
            fail("Part just saved not found in database");
        }
    }

    /**
     * Removes contents of the database 'test'
     */
    @After
    public void tearDown() {
        dbManager.clearDatabase();
    }
}
