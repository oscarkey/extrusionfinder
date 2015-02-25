package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Manufacturers;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Manufacturers.Name;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size;
import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Manufacturer;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.util.MetadataParser;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.util.MetadataParserException;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Seagate Plastics specific crawler (part sourcer).
 */

public class SeagateCrawler extends ExtendedCrawler {

    /* We are only interested in entering pages with these file endings */
    private final static Pattern FILTERS =
        Pattern.compile(".*(\\.html)$");

    /* This pattern is used to find bad Seagate links (see details at usage) */
    private static final Pattern FAULTY_PDF_FILTER =
        Pattern.compile(".*[a-zA-Z0-9].pdf");

    /* Patterns for extracting metadata.
     * 0: 3.4 DIA. MM
     * 1: 1.2 OD X 2.3 ID IN
     * 2: 0.125 X 3.0 IN
     * 3: 0.325 MM
     */
    private static final String F = MetadataParser.floatRegex();
    private static final Pattern[] METADATA_FILTERS = new Pattern[] {
            Pattern.compile("(("+ F +")( )DIA. (MM|IN)) (.*)"),
            Pattern.compile("(("+ F +") OD [xX] ("+ F +") ID (MM|IN)) (.*)"),
            Pattern.compile("(("+ F +") [xX] ("+ F +") (MM|IN)) (.*)"),
            Pattern.compile("(("+ F +")( )(MM|IN)) (.*)")
    };

    /* This is the set that we manipulate via side effects.
     * Note that if used, it HAS to be assigned with configure method.
     */
    private static Set<Part> parts;

    /* Vendor specific unique id */
    private String manufacturerId;

    /* We want to keep our search within this domain. */
    private String domain;

    /**
     * Empty constructor for the crawler.
     * Manufacturer id and start link is fetched from Manufacturers; cannot be
     * done statically as the Manufacturers class uses the crawlers itself.
     */
    public SeagateCrawler() {

        Manufacturer m = Manufacturers.get(Name.SEAGATE);
        if (m != null) {
            manufacturerId = m.getManufacturerId();
            domain = m.getLink();
        } else {
            manufacturerId = "";
            domain = "";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Set<Part> parts) {
        this.parts = parts;
    }

    /**
     * Filters out the pages we want to visit.
     * @param referringPage The parent page of this URL
     * @param url           The current URL that we want to check
     * @return              True if the URL should be visited
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {

        String href = url.getURL().toLowerCase();
        return FILTERS.matcher(href).matches() && (href.startsWith(domain));
    }

    /**
     * Process the page, namely get information about parts.
     * @param page  The fetched page
     */
    @Override
    public void visit(Page page) {

        String url = page.getWebURL().getURL();

        // parsedata can be html, plaintext or binary. We only want html, since
        // we need both images, links, and metadata.
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

            String html = htmlParseData.getHtml();
            MetadataParser mp = new MetadataParser(html, url);
            extractData(mp);
            System.out.println(url);
        }
    }

    /**
     * Extracts metadata from the page. Add found parts to the internal list
     * of parts.
     */
    private void extractData(MetadataParser mp) {

        Elements productNodes = mp.getHtmlDoc().getElementsByClass("node");
        for (Element productNode : productNodes) {

            // get the product code
            String productId = mp.selectSingleText(productNode, "div.product-name");
            if (productId.isEmpty()) {
                continue;
            }
            productId = productId.toUpperCase();

            // get the link to the product, deal with seagate's link problems
            String link = mp.getLink(productNode);
            link = fixLink(link);

            // get the image of the product
            String image = mp.selectSingleAttr(productNode, "img[src]", "abs:src");
            image = image.toLowerCase();

            // get the metadata of the product
            String description =
                mp.selectSingleText(productNode, "div.product-description");
            Size size = extractSize(description);

            if (parts != null) {
                Part p = new Part(manufacturerId, productId, link, image, size,
                    description);
                parts.add(p);
                System.out.println(p);
            }
        }
    }

    /**
     * regex hack to deal with seagate's bad links
     * assumption: all seagate's pdf links follow the format
     * http://seagateplastics.com/stock_plastics_catalog/
     * images_catalog/XYZ pdf (1).pdf
     * where XYZ is the product id. Some of their own (!) urls on their
     * website are wrong and don't include the " pdf (1)" at the
     * end ... This is a "temporary" solution.
     */
    private String fixLink(String link) {

        if (FAULTY_PDF_FILTER.matcher(link).matches()) {
            return link.replaceFirst(".pdf", " pdf (1).pdf");
        }
        return link;
    }

    /**
     * Given the description on the Seagate website, use standard regex
     * patterns to attempt to extract the numbers and units.
     * @return  Size of the part. If none could be parsed, a size with 0x0
     *          dimensions and unknown unit.
     */
    private Size extractSize(String description) {

        for (Pattern pattern : METADATA_FILTERS) {

            Matcher match = pattern.matcher(description);
            if (match.find()) {

                // group2 is first dim, group3 is second dim, group4 is unit
                Float dim1 = null;
                Float dim2 = null;
                Unit unit = Unit.UNKNOWN;

                try {
                    unit = MetadataParser.stringToUnit(match.group(4));
                    dim1 = MetadataParser.stringToFloat(match.group(2));
                    dim2 = MetadataParser.stringToFloat(match.group(3));

                } catch (MetadataParserException e) {
                    // ignore, they're set to null anyway
                }
                return new Size(dim1, dim2, unit);
            }
        }
        return new Size();
    }
}
