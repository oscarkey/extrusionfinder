package uk.ac.cam.cl.echo.extrusionfinder.server.configuration;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Manufacturer;
import uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.crawlers.SeagateCrawler;

import java.util.Map;
import java.util.HashMap;

/**
 * Static getters for manufacturers. In here, we configure which manufacturers
 * we consider in finding our extrusion parts.
 */

public class Manufacturers {

    public enum Name {
        SEAGATE
    }

    private static final Map<Name, Manufacturer> MANUFACTURERS;
    static
    {
        MANUFACTURERS = new HashMap<Name, Manufacturer>();
        MANUFACTURERS.put(Name.SEAGATE, new Manufacturer(
                "SG000",
                "SeaGate Plastics",
                "Founded in 1987",
                "http://seagateplastics.com/",
                new String[] { "http://seagateplastics.com/" },
                new SeagateCrawler())
        );

    }

    /**
     * @return  Map over all manufacturers that we consider in our part matching.
     */
    public static Map<Name, Manufacturer> getAll() {
        return MANUFACTURERS;
    }

    /**
     * @param   Name of the manufacturer company.
     * @return  Manufacturer instance of the company.
     */
    public static Manufacturer get(Name name) {
        return MANUFACTURERS.get(name);
    }
}
