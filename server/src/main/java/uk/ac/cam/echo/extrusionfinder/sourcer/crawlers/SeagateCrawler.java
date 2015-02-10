package uk.ac.cam.echo.extrusionfinder.sourcer.crawlers;

import uk.ac.cam.echo.extrusionfinder.parts.Part;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Seagate Plastics specific crawler (part sourcer).
 * PERHAPS INCLUDE A NOTION OF A VISITED PAGE, TO AVOID DUPLICATES ??
 */
public class SeagateCrawler extends ExtendedCrawler {

    /* This is where we start our search */
    public static final String[] SEEDS = { "http://seagateplastics.com/" };

    /* We are not interested in entering pages with these file endings */
    private final static Pattern FILTERS =
        Pattern.compile(".*(\\.(css|js|gif|jpe?g|png|mp3|mp3|zip|gz|pdf))$");

    /* This is the stream that we manipulate via side effects.
     * Note that if used, it HAS to be initialised with configure method.
     */
    private static Stream.Builder<Part> parts;

    @Override
    public void configure(Stream.Builder<Part> prts) {
        parts = prts;
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

            // TODO: read the table data to get metadata
            String html = htmlParseData.getHtml();

            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            for (WebURL webUrl : links) {
                String internalUrl = webUrl.getURL().toLowerCase();
                if (internalUrl.endsWith("pdf") && parts != null) {

                    // note: until we know what the product id is, item id
                    // is the url, because that is guaranteed unique.
                    parts.add(new Part("1",internalUrl, internalUrl, "image"));

                }
            }
        }
    }
}
