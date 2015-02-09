package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by oscar on 08/02/15.
 * Tests for ResultsCache.
 * DOES NOT test concurrent users yet. Should do this.
 */
public class ResultsCacheTest extends AndroidTestCase {
    private ResultsCache cache;

    private String uuid;
    private List<Result> results;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        cache = ResultsCache.getInstance(getContext());

        uuid = UUID.randomUUID().toString();
        results = Arrays.asList(new Result("part1"), new Result("part2"));

        cache.putResults(uuid, results);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        cache.close();
    }

    public void testSetGetResults() throws Exception {
        // check that we can take the results out of the cache
        List<Result> testResults = cache.getResults(uuid);
        assertEquals(results, testResults);

        // check that asking for a made up uuid doesn't return anything
        assertNull(cache.getResults("sfsdfsdfgb"));
    }

    public void testHasResults() throws Exception {
        // check that the cache has the right results
        assertTrue(cache.hasResults(uuid));
        // check that it doesn't have a made up result
        assertFalse(cache.hasResults("runfidbk"));
    }

    public void testSave() throws Exception {
        //FIXME doesn't actually test if this works as ResultsCache won't be garbage collected
        // close the cache and reopen it, checking that the results are still there
        cache.close();
        ResultsCache cache2 = ResultsCache.getInstance(getContext());
        List<Result> testResults = cache2.getResults(uuid);
        assertEquals(results, testResults);
    }
}
