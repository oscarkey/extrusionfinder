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
            return this.correlation - o.correlation > 0 ? 1 : -1;
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<MatchedPart> findMatches(BufferedImage inputImage, IDBManager database, int maxResults)
            throws ItemNotFoundException {


        Set<Map.Entry<String, double[]>> zernikeMoments =
                database.loadZernikeMap().getZernikeMap().entrySet();

        PriorityQueue<CorrelationPair> bestParts = new PriorityQueue<>();

        for (Map.Entry<String, double[]> moment: zernikeMoments) {
            double correlation = ImageMatcher.compare(inputImage, moment.getValue());
            bestParts.add(new CorrelationPair(moment.getKey(), correlation));
        }

        if (zernikeMoments.size() < maxResults) {
            maxResults = zernikeMoments.size();
        }

        List<MatchedPart> matches = new ArrayList<>(maxResults);
        for (int i = 0; i < maxResults; i++) {
            CorrelationPair pair = bestParts.poll();
            Part part = database.loadPart(pair.id);
            matches.add(new MatchedPart(part, pair.correlation));
        }

        return matches;
    }
}
