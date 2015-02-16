package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Part;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Stream.Builder;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;


/**
 * Seagate Plastics specific crawler (part sourcer).
 */

public class SeagateCrawler extends ExtendedCrawler {

    /* Vendor specific unique id */
    public static final String VENDOR_ID = "1";

    /* This is where we start our search */
    private static final String[] SEEDS = { "http://seagateplastics.com/" };

    /* We are not interested in entering pages with these file endings */
    private final static Pattern FILTERS =
        Pattern.compile(".*(\\.(css|js|gif|jpe?g|png|mp3|mp3|zip|gz|pdf))$");

    /* detects if a pdf uses the wrong format (see usage for explanation) */
    private final static Pattern FAULTY_PDF_FILTER =
        Pattern.compile(".*[a-zA-Z0-9].pdf");

    /* separates size from name in seagate description */
    private final static Pattern METADATA_FILTER =
        Pattern.compile("(.*([0-9]| MM| ID| OD| IN)) (.*)");

    /* This is the stream that we manipulate via side effects.
     * Note that if used, it HAS to be assigned with configure method.
     */
    private static Builder<Part> parts;

    @Override
    public void configure(Builder<Part> parts) {
        this.parts = parts;
    }

    @Override
    public String[] getSeeds() {
        return SEEDS;
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
        return !FILTERS.matcher(href).matches() && (href.startsWith(SEEDS[0]));
    }

    /**
     * Process the page, namely get information about parts.
     * @param page  The fetched page
     */
    @Override
    public void visit(Page page) {

        String url = page.getWebURL().getURL();

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

            String html = htmlParseData.getHtml();
            Document htmlDoc = Jsoup.parse(html, url);
            extractData(htmlDoc);
        }
    }

    /* Extracts metadata from the page.
     * TODO: generalise this to something that can be used by other vendors
     * TODO: seagateplastics.com/Stock_Plastics_Catalog/catalog/plastic_tubes_misc.html
     *       (ie. extrusions that all have the same shape - circular - but no image)
     */
    private void extractData(Document htmlDoc) {

        Elements productNodes = htmlDoc.getElementsByClass("node");
        for (Element productNode : productNodes) {

            // get the product code
            Elements product = productNode.select("div.product-name");;
            if (product == null || product.size() == 0) {
                continue;
            }
            String productId = product.first().ownText();

            // get the link to the product
            Elements links = productNode.select("a[href]");
            String link = "";
            if (!(links == null || links.size() == 0)) {

                link = links.first().attr("abs:href").toLowerCase();

                // regex hack to deal with seagate's bad links
                // assumption: all seagate's pdf links follow the format
                // http://seagateplastics.com/stock_plastics_catalog/
                // images_catalog/XYZ pdf (1).pdf
                // where XYZ is the product id. Some of the urls on their
                // website are wrong and don't include the " pdf (1)" at the
                // end ... This is a temporary solution.
                if (FAULTY_PDF_FILTER.matcher(link).matches()) {
                    link = link.replaceFirst(".pdf", " pdf (1).pdf");
                }
            }

            // get the image of the product
            Elements images = productNode.select("img[src]");
            String image = "";
            if (!(images == null || images.size() == 0)) {
                image = images.first().attr("abs:src").toLowerCase();
            }

            if (parts != null) {
                parts.accept(new Part(VENDOR_ID, productId, link, image, size,
                    description));
            }
        }
    }
}
