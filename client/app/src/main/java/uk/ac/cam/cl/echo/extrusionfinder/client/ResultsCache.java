package uk.ac.cam.cl.echo.extrusionfinder.client;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.UUID;

import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;

//TODO check/improve thread safety

/**
 * Created by oscar on 06/02/15.
 * A singleton that provides a file based cache for the latest request.
 * It is thread safe so can be used concurrently by the UI and service.
 * Are singletons cool?
 * In fact, this whole idea seems a little dodgy...
 */
public class ResultsCache {
    private static final String LOG_TAG = "ResultsCache";
    private static final String CACHE_FILENAME = "results_cache";

    private static ResultsCache resultsCache;
    private static int clientCount;

    private final Context context;
    private ResultRequest request;


    /**
     * Get the singleton instance of the ResultsCache
     * Must call .close() when finished with this instance
     * @return the instance
     */
    public static synchronized ResultsCache getInstance(Context context) {
        if(resultsCache == null) {
            resultsCache = new ResultsCache(context);
        }

        clientCount ++;

        return resultsCache;
    }

    // private as singleton
    private ResultsCache(Context context) {
        this.context = context;

        // load the existing cache from the file
        request = loadFromFile();
    }


    /**
     * Call this when you have finished using the ResultsCache.
     * THis will determine if everyone has finished using the cache and save it to disk if necessary
     */
    public synchronized void close() {
        clientCount --;

        // check if we now have no clients and so need to save ready for destruction
        if(clientCount <= 0) {
            saveToFile(request);
        }
    }

    /**
     * Store a new request in the cache
     * @param image The ImageData to look up in this request
     * @return the uuid of the new request
     */
    public synchronized String putRequest(RGBImageData image) {
        String uuid = UUID.randomUUID().toString();
        request = new ResultRequest(uuid, image);
        return uuid;
    }

    /**
     * Store the results for a request
     * @param uuid The id of the request
     * @param results The list of results to store
     */
    public synchronized void putResults(String uuid, List<Result> results) {
        // only store the results if they are for the current request
        if(isDesiredRequest(uuid)) {
            request.putResults(results);
        }
    }

    /**
     * Check if the currently cached request has this id
     * @param uuid the id to test
     * @return true if we do have this request, else false
     */
    public synchronized boolean hasRequest(String uuid) {
        return isDesiredRequest(uuid);
    }

    /**
     * Check if we have results for the given request id
     * @param uuid id of the request
     * @return true if we do have results else false
     */
    public synchronized boolean hasResults(String uuid) {
        // check if we have any request, if it's the right request and if it has results
        return (isDesiredRequest(uuid) && request.hasResults());
    }

    /**
     * Get the image for a given request id
     * @param uuid id of the request
     * @return ImageData representing the image, null if we don't have this request
     */
    public synchronized RGBImageData getImage(String uuid) {
        // only return the image if this is the right request
        if(isDesiredRequest(uuid)) {
            return request.getImage();
        }
        else {
            return null;
        }
    }

    /**
     * Get the results associated with a given id
     * @param uuid the id
     * @return the results or null if they're aren't any for this uuid
     */
    public synchronized List<Result> getResults(String uuid) {
        // only return the results if this is the right uuid
        if(isDesiredRequest(uuid)) {
            return request.getResults();
        }
        else {
            return null;
        }
    }

    /**
     * Test if the cache has a request for this uuid
     * @param uuid the uuid to test
     * @return true if there is a request else false
     */
    private synchronized boolean isDesiredRequest(String uuid) {
        return (request != null && request.getRequestUuid().equals(uuid));
    }

    /**
     * Tries to load the results from the cache file
     * @return A ResultSet containing the results
     */
    private ResultRequest loadFromFile() {
        // find the path of the cache file
        File cacheFile = getCacheFile();

        // check if the cache file exists and either load from it or generate a new cache
        if(cacheFile.exists()) {
            // try to de-serialize the results
            try {
                BufferedInputStream bIS = new BufferedInputStream(new FileInputStream(cacheFile));
                ObjectInputStream oIS = new ObjectInputStream(bIS);

                ResultRequest loadedResults = (ResultRequest) oIS.readObject();

                oIS.close();
                bIS.close();

                return loadedResults;
            }
            catch(IOException e) {
                Log.e(LOG_TAG, "Could not open cache file for reading: " + e);
                // just give up and indicate that we have no cached results
                // doesn't really matter as the results can just be fetched again
                return null;
            }
            catch(ClassNotFoundException | ClassCastException e) {
                Log.e(LOG_TAG, "Could not construct Results from cache file: " + e);
                // just give up and indicate that we have no cached results
                // doesn't really matter as the results can just be fetched again
                return null;
            }
        }

        // either the file didn't exist or de-serialization failed
        // return null to indicate that we don't have any results
        return null;
    }


    /**
     * Saves a set of results out to the cache file
     * @param results The ResultSet to be saved
     */
    private void saveToFile(ResultRequest results) {
        // if we don't have any results then there's nothing to do
        if(results == null) {
            return;
        }

        // find the path of the cache file
        File cacheFile = getCacheFile();

        // try and write out the results
        try {
            BufferedOutputStream bOS = new BufferedOutputStream(new FileOutputStream(cacheFile));
            ObjectOutputStream oOS = new ObjectOutputStream(bOS);

            oOS.writeObject(results);
            oOS.flush();

            oOS.close();
            bOS.close();
        }
        catch(IOException e) {
            // this error doesn't really matter as the results will just be re-fetched
            Log.e(LOG_TAG, "Exception occurred saving the results cache: " + e);
        }
    }

    /**
     * Computes the path of the cache file
     * @return A File representing the cache file
     */
    private File getCacheFile() {
        return new File(context.getCacheDir().getAbsolutePath() + "/" + CACHE_FILENAME);
    }

}
