package uk.ac.cam.cl.echo.extrusionfinder.server.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;
import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.ZernikeMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.UnknownHostException;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        dbManager.clearDatabase();
    }

    /**
     * Tests clearing the database, and that saved parts can be loaded.
     */
    @Test
    public void testDatabase() {
        // test that part with id "id_test" is not in DB
        try {
            dbManager.loadPart("id_test");
            fail("Loading from an empty database did not trigger an exception");
        } catch (ItemNotFoundException e) {
            assertTrue(e.getMessage().equals("Item with id 'id_test' not found in database"));
        }

        // create a new part and insert it into the database
        Size size = new Size(2.3f, 0.856f, Unit.IN);
        Part part = new Part("id_test", "", "", "link", "imageL", "ol", size, "");
        dbManager.savePart(part);

        // test that the part can now be correctly loaded from the database
        try {
            Part loadedPart = dbManager.loadPart("id_test");
            assertTrue(loadedPart.equals(part));
        } catch (ItemNotFoundException e) {
            fail("Part just saved not found in database");
        }

        // insert a new part which has the same identifier. This should succeed: test by loading from database
        part = new Part("id_test", "", "", "link2", "imageLink2", "ol", new Size(), "");
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

        // create a new ZernikeMap and insert it into the database
        Map<String, double[]> map = new HashMap<>();
        map.put("p0", new double[]{0.0, 0.1, 0.2});
        map.put("p1", new double[]{0.5, 1.2, -0.2});
        ZernikeMap zernikeMap = new ZernikeMap(map);
        dbManager.saveZernikeMap(zernikeMap);

        // test that the part can now be correctly loaded from the database
        try {
            ZernikeMap loadedZernikeMap = dbManager.loadZernikeMap();
            assertTrue(Arrays.equals(loadedZernikeMap.getZernikeMap().get("p0"),
                    new double[]{0.0, 0.1, 0.2}));
            assertTrue(Arrays.equals(loadedZernikeMap.getZernikeMap().get("p1"),
                    new double[]{0.5, 1.2, -0.2}));
            assertTrue(loadedZernikeMap.getZernikeMap().keySet().size() == 2);
        } catch (ItemNotFoundException e) {
            fail("ZernikeMap just saved not found in database");
        }

        // insert a new zernikemap which has the same identifier. This should succeed: test by loading from database
        map = new HashMap<>();
        map.put("p0", new double[]{0.0, 0.1, 0.2});
        zernikeMap = new ZernikeMap(map);
        dbManager.saveZernikeMap(zernikeMap);
        try {
            ZernikeMap loadedZernikeMap = dbManager.loadZernikeMap();
            assertTrue(Arrays.equals(loadedZernikeMap.getZernikeMap().get("p0"),
                    new double[]{0.0, 0.1, 0.2}));
            assertTrue(loadedZernikeMap.getZernikeMap().keySet().size() == 1);
        } catch (ItemNotFoundException e) {
            fail("Modified part not saved in DB");
        }

        // clear the database, and attempt to load the part and zernike moment again. This should fail
        dbManager.clearDatabase();
        try {
            dbManager.loadZernikeMap();
            fail("Loading from an empty database did not trigger an exception");
        } catch (ItemNotFoundException e) {
            assertTrue(e.getMessage().equals("Item with id '" + Configuration.ZERNIKE_MAP_ID + "' not found in database"));
        }
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

        Part part = new Part("Mid", "Pid", "link", "imageL", "ol");
        db1.savePart(part);

        // test that the part can now be correctly loaded from the database
        try {
            Part loadedPart = db2.loadPart(part.get_id());
            assertTrue(loadedPart.equals(part));
        } catch (ItemNotFoundException e) {
            fail("Part just saved not found in database");
        }
    }

    @Test
    public void testSaveSet() {
        dbManager.clearDatabase();

        // Save a set of parts into the database
        Set<Part> parts = new HashSet<>();
        Part p0 = new Part("id", "test0", "link", "image", "ol");
        Part p1 = new Part("id", "test1", "link", "image", "ol");
        Part p2 = new Part("id", "test2", "link", "image", "ol");
        Part p3 = new Part("id", "test0", "link", "imagel", "ol");

        parts.add(p0);
        parts.add(p1);
        dbManager.saveParts(parts);

        // test that the parts can now be correctly loaded from the database
        try {
            Part loadedPart = dbManager.loadPart("idtest0");
            assertTrue(loadedPart.equals(p0));
            loadedPart = dbManager.loadPart("idtest1");
            assertTrue(loadedPart.equals(p1));
        } catch (ItemNotFoundException e) {
            fail("Part just saved not found in database");
        }

        // Check that saving a duplicate id part does not now cause an error
        parts.add(p2);
   //     parts.add(p3);
        dbManager.saveParts(parts);

        // test that the parts can now be correctly loaded from the database
        // and that the duplicate overwrote the original entry
        try {
            Part loadedPart = dbManager.loadPart("idtest0");
            assertTrue(loadedPart.equals(p0));
            loadedPart = dbManager.loadPart("idtest1");
  //          assertTrue(loadedPart.equals(p3)); // duplicate overwrite!
            loadedPart = dbManager.loadPart("idtest2");
            assertTrue(loadedPart.equals(p2));
        } catch (ItemNotFoundException e) {
            fail("Part just saved not found in database");
        }

        dbManager.clearDatabase();
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor constructor = MongoInstance.class.getDeclaredConstructor();
        assertTrue("Constructor is not private", Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testGetDatabaseName() throws UnknownHostException {
        MongoDBManager db = new MongoDBManager("test");
        assertTrue(db.getDatabaseName().equals("test"));
    }

    /**
     * Removes contents of the database 'test'
     */
    @After
    public void tearDown() {
        dbManager.clearDatabase();
    }
}

