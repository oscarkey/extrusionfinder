package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;

/**
 * Created by oscar on 08/02/15.
 * Tests for ResultsCache.
 * DOES NOT test concurrent users yet. Should do this.
 */
public class ResultsCacheTest extends AndroidTestCase {

    private static final int TEST_IMAGE_SIZE_BYTES = 2000000;
    private static final int TEST_RESULTS_COUNT = 10;

    public void testPutHasRequest() throws Exception {
        ResultsCache cache = ResultsCache.getInstance(getContext());

        // create a new request
        String uuid = cache.putRequest(generateTestImage());

        // check we have that request and don't have a random request
        assertTrue(cache.hasRequest(uuid));
        assertFalse(cache.hasRequest(generateTestUuid()));
    }

    public void testGetImage() throws Exception {
        ResultsCache cache = ResultsCache.getInstance(getContext());

        // create a new image and request
        RGBImageData image = generateTestImage();
        String uuid = cache.putRequest(image);

        // check that we get correct image for a known uuid and get null for a random uuid
        assertEquals(image, cache.getImage(uuid));
        assertNull(cache.getImage(generateTestUuid()));
    }

    public void testPutHasGetResults() throws Exception {
        ResultsCache cache = ResultsCache.getInstance(getContext());

        // create an image and put the request
        String uuid = cache.putRequest(generateTestImage());

        // create and put some results
        List<Result> results = generateTestResults();
        cache.putResults(uuid, results);

        // check the cache has the right results but not the wrong results
        assertTrue(cache.hasResults(uuid));
        assertFalse(cache.hasResults(generateTestUuid()));

        // check we can get the right results but not the wrong results
        assertEquals(results, cache.getResults(uuid));
        assertNull(cache.getResults(generateTestUuid()));
    }

    public void testPersistence() throws Exception {
        //TODO add tests for persistence, not sure how to do this...
    }


    private RGBImageData generateTestImage() {
        //FIXME should probably generate a byte array that actually is an image
        // create a large byte array
        byte[] data = new byte[TEST_IMAGE_SIZE_BYTES];
        Random random = new Random();
        random.nextBytes(data);

        return new RGBImageData(data, 1024, 1024);
    }

    private String generateTestUuid() {
        return UUID.randomUUID().toString();
    }

    private List<Result> generateTestResults() {
        List<Result> results = new ArrayList<>(TEST_RESULTS_COUNT);

        for(int i = 0; i < TEST_RESULTS_COUNT; i++) {
            results.add(new Result("part" + i));
        }

        return results;
    }
}
