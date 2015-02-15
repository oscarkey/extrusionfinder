package uk.ac.cam.cl.echo.extrusionfinder.server.orchestration;

import org.easymock.*;
import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.ZernikeMap;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * Test suite for ZernikeManager
 */
public class ZernikeManagerTester {
    @Test
    public void testGetZernikeMoments() throws ItemNotFoundException, UnknownHostException {
        // Test getZernikeMoments() with an empty map
        Map<String, Double[]> map = new HashMap<>();
        testGetZernikeMomentsAndClose(new ZernikeMap(map));

        // Test again with a non-empty map
        map.put("p0", new Double[]{0.0, 0.1, 0.2});
        map.put("p1", new Double[]{0.5, 1.2, -0.2});
        testGetZernikeMomentsAndClose(new ZernikeMap(map));
    }

    /**
     * Tests ZernikeManager.getZernikeMoments() hits the database the first time it is called only,
     * and creates a Stream with Map.Entries equivalent to those in the map "loaded" from database.
     * Database loading is mocked with EasyMock.
     * If ZernikeManager.close() doesn't work then successive calls to this function will fail tests
     * @param zernikeMap Map to mock loading from database of
     */
    private void testGetZernikeMomentsAndClose(ZernikeMap zernikeMap) {
        IDBManager dbManager = EasyMock.createMock(IDBManager.class);
        EasyMock.expect(dbManager.getDatabaseName()).andReturn("test");
        EasyMock.replay(dbManager);

        try (ZernikeManager zernikeManager = new ZernikeManager(dbManager)) {
            EasyMock.reset(dbManager);
            // Test loading for the first time. This should load data from the mocked database
            EasyMock.expect(dbManager.loadZernikeMap()).andReturn(zernikeMap);
            EasyMock.replay(dbManager);
            Stream<Map.Entry<String, Double[]>> stream = zernikeManager.getZernikeMoments();
            final int[] streamSize = {0};
            stream.forEach(x -> {
                        streamSize[0]++;
                        assertTrue(zernikeMap.getZernikeMap().containsKey(x.getKey()));
                        assertTrue(Arrays.equals(
                                        zernikeMap.getZernikeMap().get(x.getKey()),
                                        x.getValue())
                        );
                    }
            );
            assertTrue(streamSize[0] == zernikeMap.getZernikeMap().size());
            EasyMock.verify(dbManager);

            // Test loading again. This should not load data from the mocked database
            stream = zernikeManager.getZernikeMoments();
            streamSize[0] = 0;
            stream.forEach(x -> {
                        streamSize[0]++;
                        assertTrue(zernikeMap.getZernikeMap().containsKey(x.getKey()));
                        assertTrue(Arrays.equals(
                                        zernikeMap.getZernikeMap().get(x.getKey()),
                                        x.getValue())
                        );
                    }
            );
            assertTrue(streamSize[0] == zernikeMap.getZernikeMap().size());
            EasyMock.verify(dbManager);
        } catch (ItemNotFoundException e) {
            // The mocked IDBManager never throws this, so it's ok to ignore this.
        }
    }

    /**
     * Tests that ZernikeManager updates the database and its internal map having not initialised its map
     */
    @Test
    public void testUpdateZernikeMapUninitialised() {
        IDBManager dbManager = EasyMock.createMock(IDBManager.class);
        EasyMock.expect(dbManager.getDatabaseName()).andReturn("test");
        EasyMock.replay(dbManager);

        try (ZernikeManager zernikeManager = new ZernikeManager(dbManager)) {
            EasyMock.reset(dbManager);
            testUpdateZernikeMapCommon(zernikeManager, dbManager);
        } catch (ItemNotFoundException | UnknownHostException e) {
            // Mocked IDBManager doesn't throw these, so it's ok to ignore them
        }
    }

    /**
     * Tests that ZernikeManager updates the database and its internal map having initialised its map
     */
    @Test
    public void testUpdateZernikeMapInitialised() {
        IDBManager dbManager = EasyMock.createMock(IDBManager.class);
        EasyMock.expect(dbManager.getDatabaseName()).andReturn("test");
        EasyMock.replay(dbManager);

        try (ZernikeManager zernikeManager = new ZernikeManager(dbManager)) {
            EasyMock.reset(dbManager);

            Map<String, Double[]> map = new HashMap<>();
            map.put("p0", new Double[]{0.0, 0.1, 0.2});
            map.put("p1", new Double[]{0.5, 1.2, -0.2});
            EasyMock.expect(dbManager.loadZernikeMap()).andReturn(new ZernikeMap(map));
            EasyMock.replay(dbManager);
            zernikeManager.getZernikeMoments();
            EasyMock.verify(dbManager);
            EasyMock.reset(dbManager);

            testUpdateZernikeMapCommon(zernikeManager, dbManager);
        } catch (ItemNotFoundException | UnknownHostException e) {
            // Mocked IDBManager doesn't throw these, so it's ok to ignore them
        }
    }

    /**
     * Calls ZernikeManager.updateZernikeMap(), followed by ZernikeManager.getZernikeMoments() and tests the result
     * of the latter is the input to the former. Also tests that updateZernikeMap() makes a call to its database
     * reference in order to update the database.
     * @param zernikeManager         Zernike manager to make calls on
     * @param dbManager              Mocked database for Zernike manager to use
     * @throws UnknownHostException
     * @throws ItemNotFoundException
     */
    private void testUpdateZernikeMapCommon(ZernikeManager zernikeManager, IDBManager dbManager)
            throws UnknownHostException, ItemNotFoundException {
        Map<String, Double[]> map = new HashMap<>();
        map.put("p1", new Double[]{0.0, 0.1, 0.2});
        map.put("p3", new Double[]{0.9, 1.2, -0.2});
        ZernikeMap zernikeMap = new ZernikeMap(map);
        dbManager.saveZernikeMap(zernikeMap);
        EasyMock.expectLastCall().once();
        EasyMock.replay(dbManager);
        zernikeManager.updateZernikeMap(zernikeMap);
        EasyMock.verify(dbManager);

        Stream<Map.Entry<String, Double[]>> stream = zernikeManager.getZernikeMoments();
        final int[] streamSize = {0};
        stream.forEach(x -> {
                    streamSize[0]++;
                    assertTrue(zernikeMap.getZernikeMap().containsKey(x.getKey()));
                    assertTrue(Arrays.equals(
                                    zernikeMap.getZernikeMap().get(x.getKey()),
                                    x.getValue())
                    );
                }
        );
        assertTrue(streamSize[0] == zernikeMap.getZernikeMap().size());
        EasyMock.verify(dbManager);
    }
}
