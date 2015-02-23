package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer;

import org.junit.Test;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.MongoDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.database.IDBManager;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.*;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

/* Unit tests the part sourcer.
 * Cannot test main() because it relies on other static methods within the same
 * class, which is currently not testable with PowerMock/PowerMockito.
 *
 * Note that the staticness is necessary because PartSourcer contains an
 * entrypoint (main function) to let it be run on the commandline.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, CrawlControllerFactory.class})
public class PartSourcerTester {

    /**
     * Tests the updateDatabase() method, which starts all the crawlers and
     * saves all the found parts to the database.
     * NOTE: uses mockito!
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateDatabase() throws CrawlerException {

        // create mock controller<extendedcrawler>
        MongoDBManager dbManager = mock(MongoDBManager.class);
        Controller<ExtendedCrawler> c1 = mock(Controller.class);
        Controller<ExtendedCrawler> c2 = mock(Controller.class);

        // create list containing only the above controller
        Collection<Controller<? extends ExtendedCrawler>> controllers =
            new ArrayList<Controller<? extends ExtendedCrawler>>();
        controllers.add(c1);
        controllers.add(c2);

        // create mock of parts returned by the both controllers when run
        Collection<Part> fakeparts = new ArrayList<Part>();
        fakeparts.add(mock(Part.class));
        when(c1.start()).thenReturn(fakeparts);
        when(c2.start()).thenReturn(fakeparts);

        // run the method!
        PartSourcer.updateDatabase(dbManager, controllers);

        // assert that the required methods are invoked
        verify(c1).start();
        verify(c2).start();
        verify(dbManager, times(2)).savePart(any(Part.class));
    }

    /**
     * Tests the getControllers() method which gets all the crawler config
     * options and the configured crawlers, and initialises controllers for
     * each.
     * NOTE: uses powermock! (due to mocking static methods)
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testGetControllers() throws Exception{

        mockStatic(Configuration.class);
        mockStatic(CrawlControllerFactory.class);

        // our dummy/mocked objects
        ExtendedCrawler crawler = createMock(ExtendedCrawler.class);
        Collection crawlers = new ArrayList<ExtendedCrawler>();
        crawlers.add(crawler);
        CrawlController crawlcontroller = createMock(CrawlController.class);

        // expect calls to configuration
        expect(Configuration.getCrawlStorageFolder()).andReturn("foo");
        expect(Configuration.getMaxCrawlDepth()).andReturn(1);
        expect(Configuration.getMaxCrawlPages()).andReturn(2);
        expect(Configuration.getCrawlers()).andReturn(crawlers);

        // expect constructors/factory calls for controller and crawlcontroller
        expect(CrawlControllerFactory.get("foo",1,2)).andReturn(crawlcontroller);
        expect(crawler.getSeeds()).andReturn(new String[] {"bar"});
        crawlcontroller.addSeed("bar");
        expectLastCall().once();

        replayAll();

        // check that the call returned a collection of one crawler as expected
        Collection<Controller<? extends ExtendedCrawler>> cs =
            PartSourcer.getControllers();
        assertTrue(cs.size() == 1);

        verifyAll();

    }

}
