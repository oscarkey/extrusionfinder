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
    private ResultSet results;


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
        results = loadFromFile();
    }


    /**
     * Call this when you have finished using the ResultsCache.
     * THis will determine if everyone has finished using the cache and save it to disk if necessary
     */
    public synchronized void close() {
        clientCount --;

        // check if we now have no clients and so need to save ready for destruction
        if(clientCount <= 0) {
            saveToFile(results);
        }
    }


    /**
     * Save this list of results to the cache associated with the given id.
     * Won't be saved to disk until cache is closed.
     * @param uuid String uuid of the request
     * @param results List of results to cache
     */
    public synchronized void putResults(String uuid, List<Result> results) {
        this.results = new ResultSet(uuid, results);
    }


    /**
     * Get an List of the cached results for this uuid
     * @return List of cached results
     */
    public synchronized List<Result> getResults(String uuid) {
        // check if we have results and that they have the right uuid
        if(results != null && results.getRequestUuid().equals(uuid)) {
            return results.getResults();
        }
        else {
            return null;
        }
    }


    /**
     * Check if we have cached results for this uuid
     * @param uuid String uui dto search for
     * @return true if there are cached results or false if there aren't
     */
    public synchronized boolean hasResults(String uuid) {
        // false if results is null
        return results != null && results.getRequestUuid().equals(uuid);
    }


    /**
     * Tries to load the results from the cache file
     * @return A ResultSet containing the results
     */
    private synchronized ResultSet loadFromFile() {
        // find the path of the cache file
        File cacheFile = getCacheFile();

        // check if the cache file exists and either load from it or generate a new cache
        if(cacheFile.exists()) {
            // try to de-serialize the results
            try {
                BufferedInputStream bIS = new BufferedInputStream(new FileInputStream(cacheFile));
                ObjectInputStream oIS = new ObjectInputStream(bIS);

                ResultSet loadedResults = (ResultSet) oIS.readObject();

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
    private void saveToFile(ResultSet results) {
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
