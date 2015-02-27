package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.util;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.*;
import static org.easymock.EasyMock.expect;

/**
 * Unit tests the metadata parser (using EasyMock and PowerMock).
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Jsoup.class })
public class MetadataParserTester {

    private final String html = "<p>valid html</p>";
    private final String url = "http://url";
    private final MetadataParser mp = new MetadataParser(html, url);

    /**
     * Test constructor of metadata parser.
     */
    @Test
    public void testConstructor() {
        final String html = "test";
        final String url = "testy mctest";

        mockStatic(Jsoup.class);

        Document doc = createMock(Document.class);
        expect(Jsoup.parse(html, url)).andReturn(doc);

        replayAll();

        MetadataParser mp = new MetadataParser(html,url);
        assertEquals(doc, mp.getHtmlDoc());
        assertEquals(url, mp.getUrl());

        verifyAll();
    }

    /**
     * Test select single text (this is mostly asserting that it calls the
     * expected Jsoup methods).
     */
    @Test
    public void testSelectText() {

        final Element arg1 = createMock(Element.class);
        final String arg2 = "some selector";
        final String res = "what's up";

        Elements es = createMock(Elements.class);
        Element e = createMock(Element.class);

        expect(arg1.select(arg2)).andReturn(es);
        expect(es.size()).andReturn(1);
        expect(es.first()).andReturn(e);
        expect(e.ownText()).andReturn(res);

        replayAll();

        String found = mp.selectSingleText(arg1, arg2);
        assertEquals(found, res);

        verifyAll();
    }

    /**
     * Test select single attribute (this is mostly asserting that it calls the
     * expected Jsoup methods).
     */
    @Test
    public void testSelectAttr() {

        final Element arg1 = createMock(Element.class);
        final String arg2 = "some selector";
        final String arg3 = "some attribute";
        final String res = "giraffe";

        Elements es = createMock(Elements.class);
        Element e = createMock(Element.class);

        expect(arg1.select(arg2)).andReturn(es);
        expect(es.size()).andReturn(2);
        expect(es.first()).andReturn(e);
        expect(e.attr(arg3)).andReturn(res);

        replayAll();

        String found = mp.selectSingleAttr(arg1, arg2, arg3);
        assertEquals(found, res);

        verifyAll();
    }

    /**
     * Test that it returns empty string if no result found.
     */
    @Test
    public void testEmptyAttr() {

        final Element arg1 = createMock(Element.class);
        final String arg2 = "some selector";
        final String arg3 = "some attribute";

        Elements es = createMock(Elements.class);
        Element e = createMock(Element.class);

        expect(arg1.select(arg2)).andReturn(es);
        expect(es.size()).andReturn(0);

        replayAll();

        String found = mp.selectSingleAttr(arg1, arg2, arg3);
        assertEquals(found, "");

        verifyAll();
    }

    /**
     * Tests string to float parsing.
     */
    @Test
    public void testFloat() throws MetadataParserException {

        String f1 = "0.1";
        String f2 = "0.0f";
        String f3 = "945.1";
        String f4 = "-3.11";
        String f5 = "3.11s";

        assertTrue(MetadataParser.stringToFloat(f1) == 0.1f);
        assertTrue(MetadataParser.stringToFloat(f2) == 0.0f);
        assertTrue(MetadataParser.stringToFloat(f3) == 945.1f);
        assertTrue(MetadataParser.stringToFloat(f4) == -3.11f);

        try {
            MetadataParser.stringToFloat(f5);
            fail("testFloat() failed to throw metadataparserexception");
        } catch (MetadataParserException e) { }
    }


    /**
     * Tests string to int parsing.
     */
    @Test
    public void testInt() throws MetadataParserException {

        String i1 = "42";
        String i2 = "0";
        String i3 = "-2";
        String i4 = "434.0";

        assertEquals(MetadataParser.stringToInt(i1), 42);
        assertEquals(MetadataParser.stringToInt(i2), 0);
        assertEquals(MetadataParser.stringToInt(i3), -2);

        try {
            MetadataParser.stringToInt(i4);
            fail("testInt() failed to throw metadataparserexception");
        } catch (MetadataParserException e) { }
    }


    /**
     * Tests string to unit parsing.
     */
    @Test
    public void testUnit() {

        String u1 = "MM";
        String u2 = "MilliMeters";
        String u3 = "inch";
        String u4 = "feet";

        assertEquals(MetadataParser.stringToUnit(u1), Unit.MM);
        assertEquals(MetadataParser.stringToUnit(u2), Unit.MM);
        assertEquals(MetadataParser.stringToUnit(u3), Unit.IN);
        assertEquals(MetadataParser.stringToUnit(u4), Unit.UNKNOWN);

    }
}
