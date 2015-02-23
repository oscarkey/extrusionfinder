package uk.ac.cam.cl.echo.extrusionfinder.server.orchestration;

import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.*;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.ItemNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagematching.ImageMatcher;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.MatchedPart;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.ZernikeMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Tests for class ExtrusionFinder
*
* @author as2388
*/
@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageMatcher.class)
public class ExtrusionFinderTester {

    /**
     * Tests ExtrusionFinder.findMatches() loads the 3 closest matches from a database, and
     * returns them as a List of MatchedParts. Tests zernke maps with 0 elements, lt 3 elements, and gt 3 elements
     * *
     * @throws ItemNotFoundException
     */
    @Test
    public void testFindMatches() throws ItemNotFoundException {
        List<MatchedPart> matchedParts = testFindMatchesWorker(new double[]{0.1, 0.0, 0.5, 0.7, 0.4, 1.0, 0.15, 0.05});
        assertTrue(matchedParts.size() == 3);
        assertTrue(matchedParts.get(0).equals(new MatchedPart(
                new Part("p", "1", "l", "il"), 0.0
        )));
        assertTrue(matchedParts.get(1).equals(new MatchedPart(
                new Part("p", "7", "l", "il"), 0.05
        )));
        assertTrue(matchedParts.get(2).equals(new MatchedPart(
                new Part("p", "0", "l", "il"), 0.1
        )));

        matchedParts = testFindMatchesWorker(new double[]{0.1, 0.0});
        assertTrue(matchedParts.size() == 2);
        assertTrue(matchedParts.get(0).equals(new MatchedPart(
                new Part("p", "1", "l", "il"), 0.0
        )));
        assertTrue(matchedParts.get(1).equals(new MatchedPart(
                new Part("p", "0", "l", "il"), 0.1
        )));

        matchedParts = testFindMatchesWorker(new double[]{});
        assertTrue(matchedParts.size() == 0);
    }

    private List<MatchedPart> testFindMatchesWorker(double[] confidences) throws ItemNotFoundException {
        int maxResults = 3;

        Map<String, double[]> zernikeMomentsMap = new HashMap<>();
        IDBManager database = createMock(IDBManager.class);
        ImageMatcher imageMatcher = EasyMock.createMock(ImageMatcher.class);

        for (int i = 0; i < confidences.length; i++) {
            double[] values = {(double) i};
            zernikeMomentsMap.put("p" + i, values);
            expect(imageMatcher.compare(values)).andReturn(confidences[i]);
            expect(database.loadPart("p" + i)).andReturn(new Part("p", Integer.toString(i), "l", "il"));
        }

        ZernikeMap zernikeMap = createMock(ZernikeMap.class);
        expect(database.loadZernikeMap()).andReturn(zernikeMap);
        expect(zernikeMap.getZernikeMap()).andReturn(zernikeMomentsMap);

        replay(ImageMatcher.class);
        replay(database);
        EasyMock.replay(imageMatcher);
        replay(zernikeMap);

        return ExtrusionFinder.findMatches(imageMatcher, database, maxResults);
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor constructor = ExtrusionFinder.class.getDeclaredConstructor();
        Assert.assertTrue("Constructor is not private", Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
