package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.metadata;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;

import java.lang.Float;
import java.lang.Integer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

/**
 * A very thin, non-transparent layer upon Jsoup. Not meant to abstract fully
 * away from Jsoup (although that would be neat), but provide helper methods
 * for extracting metadata from a web page.
 */

public class MetadataParser {

    private Document htmlDoc;
    private String url;

    /**
     * @param html  The html of the web page as a string.
     * @param url   The url of the web page.
     */
    public MetadataParser(String html, String url) {
        htmlDoc = Jsoup.parse(html, url);
        this.url = url;
    }

    /**
     * @return  The Jsoup parsed html document.
     */
    public Document getHtmlDoc() {
        return htmlDoc;
    }

    /**
     * @return  The url to the web page.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param element   The Jsoup element to be searched.
     * @param select    String CSS selector to determine searched elements.
     * @return          The inner text value of the first found result for the
     *                  given selector. If none found, the empty string.
     *                  E.g. element contains <div class=hello>This is a value</div>
     *                  and select is "div.hello" will return "This is a value".
     */
    public String selectSingleText(Element element, String select) {

        Elements found = element.select(select);
        String text = "";
        if (found != null && found.size() > 0) {
            text = found.first().ownText();
        }
        return text;
    }

    /**
     * @param element   The Jsoup element to be searched.
     * @param select    String CSS selector to determine searched elements.
     * @param attr      Name of the attribute
     * @return          The attribute value of the first found result for the
     *                  given selector. If none found, the empty string.
     *                  E.g. element contains <div data="hey">This is a value</div>
     *                  and select is "div" will return "hey".
     */
    public String selectSingleAttr(Element element, String select, String attr) {

        Elements found = element.select(select);
        String attri = "";
        if (found != null && found.size() > 0) {
            attri = found.first().attr(attr);
        }
        return attri;
    }

    /**
     * @param element   The Jsoup element to be searched.
     * @return          The first link within this element or the empty string
     *                  if none.
     */
    public String getLink(Element element) {
        return selectSingleAttr(element, "a[href]", "abs:href").toLowerCase();
    }

    /* STATIC PARSING HELPERS */

    private static final String FLOAT_FORMAT = "[0-9]*\\.?[0-9]+";
    private static final String INT_FORMAT = "[0-9]+";

    /**
     * @return  A regex string for finding floats.
     */
    public static String floatRegex() {
        return FLOAT_FORMAT;
    }

    /**
     * @return  A regex string for finding integers.
     */
    public static String intRegex() {
        return INT_FORMAT;
    }

    /**
     * @return  Float parsed from the string.
     * @throws MetadataParserException  if it was not possible to parse the string.
     */
    public static float stringToFloat(String s) throws MetadataParserException {
        try {

            return Float.parseFloat(s);

        } catch (NumberFormatException | NullPointerException e) {

            String msg = String.format(
                "Converting string to float failed. Attempted " +
                "to convert %s, failed with: %s", s, e.getMessage());
            throw new MetadataParserException(msg);

        }
    }

    /**
     * @return  Int parsed from the string.
     * @throws MetadataParserException  if it was not possible to parse the string.
     */
    public static int stringToInt(String s) throws MetadataParserException {
        try {

            return Integer.parseInt(s);

        } catch (NumberFormatException | NullPointerException e) {

            String msg = String.format(
                "Converting string to int failed. Attempted to " +
                "convert %s, failed with: %s", s, e.getMessage());
            throw new MetadataParserException(msg);

        }
    }

    /**
     * @return  Size.Unit corresponding to the string.
     */
    public static Unit stringToUnit(String s) {
        switch (s.toUpperCase()) {
            case "IN":
            case "INCH":
            case "INCHES":
                return Unit.IN;

            case "MM":
            case "MILLIMETER":
            case "MILLIMETERS":
                return Unit.MM;

            default:
                return Unit.UNKNOWN;
        }
    }

}
