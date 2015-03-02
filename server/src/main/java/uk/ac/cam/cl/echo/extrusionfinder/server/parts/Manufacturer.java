package uk.ac.cam.cl.echo.extrusionfinder.server.parts;

import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.ExtendedCrawler;

import java.util.Arrays;

/**
 * Class for encapsulating information about extrusion manufacturers.
 */

public class Manufacturer {

    private String id;
    private String name;
    private String info;
    private String link;
    private String[] crawlerSeeds;
    private ExtendedCrawler crawler;

    /**
     * Default constructor for manufacturer.
     * @param id            The unique manufacturer-specific id.
     * @param name          Name of the manufacturer company.
     * @param info          Miscellaneous information about/description of the manufacturer.
     * @param link          Link to the manufacturer's website.
     * @param crawlerSeeds  Array of URLs to start crawling from for the website.
     * @param crawler       An instance of the crawler that can gather parts from this manufacturer.
     */
    public Manufacturer(String id, String name, String info, String link,
        String[] crawlerSeeds, ExtendedCrawler crawler) {

        this.id = id;
        this.name = name;
        this.info = info;
        this.link = link;
        this.crawlerSeeds = crawlerSeeds;
        this.crawler = crawler;
    }

    /**
     * @return  The unique id of the manufacturer.
     */
    public String getManufacturerId() {
        return id;
    }

    /**
     * @return  Name of the manufacturer company.
     */
    public String getName() {
        return name;
    }

    /**
     * @return  Miscellaneous information about the company.
     */
    public String getInfo() {
        return info;
    }

    /**
     * @return  Link to the manufacturer's website.
     */
    public String getLink() {
        return link;
    }

    /**
     * @return  Array of URLs to start crawling from for the website.
     */
    public String[] getSeeds() {
        return crawlerSeeds;
    }

    /**
     * @return  An instance of the crawler that can gather parts from this manufacturer.
     */
    public ExtendedCrawler getCrawler() {
        return crawler;
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || !(o instanceof Manufacturer)) {
            return false;
        }

        Manufacturer m = (Manufacturer) o;
        ExtendedCrawler c = m.getCrawler();
        boolean crawlerEq = (crawler == null && c == null) ||
            (crawler != null && c != null && crawler.getClass().equals(c.getClass()));

        return  crawlerEq &&
                id.equals(m.getManufacturerId()) &&
                name.equals(m.getName()) &&
                info.equals(m.getInfo()) &&
                link.equals(m.getLink()) &&
                Arrays.equals(crawlerSeeds, m.getSeeds());
    }

}
