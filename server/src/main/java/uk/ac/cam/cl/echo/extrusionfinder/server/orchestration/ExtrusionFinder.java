package uk.ac.cam.cl.echo.extrusionfinder.server.orchestration;

import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagematching.ImageMatcher;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * @author as2388
 */
public class ExtrusionFinder implements IExtrusionFinder {

    class CorrelationPair implements Comparable<CorrelationPair> {
        private final String id;
        private final double correlation;

        CorrelationPair(String id, double correlation) {
            this.id = id;
            this.correlation = correlation;
        }

        @Override
        public int compareTo(CorrelationPair o) {
            return this.correlation - o.correlation > 0 ? -1 : 1;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<MatchedPart> findMatches(BufferedImage inputImage, IDBManager database, int maxResults)
            throws ItemNotFoundException {
        // TODO: call preprocessor to clean up inputImage before proceeding

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
        for (Map.Entry<String, double[]> moment: zernikeMoments) {
            double correlation = ImageMatcher.compare(inputImage, moment.getValue());
            bestParts.add(new CorrelationPair(moment.getKey(), correlation));

            if (bestParts.size() > maxResults) bestParts.poll();
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
    private List<MatchedPart> reverse(List<MatchedPart> x) {
        List<MatchedPart> output = new ArrayList<>(x.size());
        for (int i = 1; i <= x.size(); i++) {
            output.add(x.get(x.size() - i));
        }

        return output;
    }
}
