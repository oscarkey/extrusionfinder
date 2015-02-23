package uk.ac.cam.cl.echo.extrusionfinder.server.orchestration;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.GrayscaleImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagematching.ImageMatcher;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.ProfileDetector;
import uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.ProfileFitting;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Provides the function findMatches() which returns a list of n best matches from the database
 *
 * @author as2388
 */
public class ExtrusionFinder {

    private ExtrusionFinder(){}

    static class CorrelationPair implements Comparable<CorrelationPair> {
        private final String id;
        private final double correlation;

        CorrelationPair(String id, double correlation) {
            this.id = id;
            this.correlation = correlation;
        }

        @Override
        public int compareTo(CorrelationPair o) {
            // Negate to sort in reverse order
            return - Double.compare(this.correlation, o.correlation);
        }
    }

    /**
     * Produces a list of best matches to the given extrusion image using the contents of the database.
     * @param inputImage   Image to find matches for. Automatic preprocessing on this image is performed TODO
     * @param database     Database to load find matches in.
     * @param maxResults   Maximum number of results to include in output list
     * @return             List of best matches found in the database
     * @throws ItemNotFoundException Thrown if the Zernike Map or a part referenced by the Zernike Map is not in DB
     */
    public static List<MatchedPart> findMatches(RGBImageData inputImage, IDBManager database, int maxResults)
            throws ItemNotFoundException {
        // Call preprocessor to clean up inputImage before proceeding
        GrayscaleImageData grayscaleImageData = new ProfileDetector().process(inputImage);

        // Call preprocessor for center and radius data
        ProfileFitting fitter = new ProfileFitting(grayscaleImageData);
        double radius = fitter.getRadius();
        Point2D center = fitter.getCentre();

        // Having processed the image, find matches
        return findMatches(new ImageMatcher(grayscaleImageData, Configuration.DEFULT_ZERNIKE_DEGREE, center, radius),
                database, maxResults);
    }

    /**
     * Produces a list of best matches to the given extrusion image using the contents of the database.
     * @param imageMatcher Class to call to compare Zernike Moments against
     * @param database     Database to load find matches in.
     * @param maxResults   Maximum number of results to include in output list
     * @return             List of best matches found in the database
     * @throws ItemNotFoundException Thrown if the Zernike Map or a part referenced by the Zernike Map is not in DB
     */
    static List<MatchedPart> findMatches(ImageMatcher imageMatcher, IDBManager database, int maxResults)
            throws ItemNotFoundException {
        // Load the map of zernike moments from the database
        Set<Map.Entry<String, double[]>> zernikeMoments =
                database.loadZernikeMap().getZernikeMap().entrySet();

        // Create a priority queue into which the ids of the parts most matching inputImage will be sorted by
        // their correlation. But simply doing this may result in a very large queue into which inserting becomes
        // expensive, and has high memory requirements. So instead items in this queue are sorted in reverse order,
        // so that when the queue reaches the size of maxResults, the worst item can be removed in constant time.
        PriorityQueue<CorrelationPair> bestParts = new PriorityQueue<>(maxResults + 1);

        // Compare the input image with every zernike moment just loaded from the database, and put the result
        // in the priority queue. Having done this, if the priority queue is bigger than the requested number of
        // results, remove the top of the priority queue (which is the worst value).
        for (Map.Entry<String, double[]> moment : zernikeMoments) {
            double correlation = imageMatcher.compare(moment.getValue());
            bestParts.add(new CorrelationPair(moment.getKey(), correlation));

            if (bestParts.size() > maxResults) {
                bestParts.poll();
            }
        }

        // Determine how many results should be returned: this is the maximum of the number of results requested
        // and the number of parts in the database
        if (zernikeMoments.size() < maxResults) {
            maxResults = zernikeMoments.size();
        }

        // Repeat (number of parts to return) times:
        // Take the best item from the priority queue, load its full part data from the database, and insert it
        // into the result list
        List<MatchedPart> matches = new ArrayList<>(maxResults);
        for (int i = 0; i < maxResults; i++) {
            CorrelationPair pair = bestParts.poll();
            Part part = database.loadPart(pair.id);
            matches.add(new MatchedPart(part, pair.correlation));
        }

        // Return the best matches. Because the priority queue was in reverse order, matches is in reverse order
        // of best matches, so needs to be reversed first.
        return reverse(matches);
    }

    /**
     * Returns a new List, which is the input list in reverse
     * @param x List to reverse
     * @return  x in reverse order
     */
    static List<MatchedPart> reverse(List<MatchedPart> x) {
        List<MatchedPart> output = new ArrayList<>(x.size());
        for (int i = 1; i <= x.size(); i++) {
            output.add(x.get(x.size() - i));
        }

        return output;
    }
}
