package uk.ac.cam.cl.echo.extrusionfinder.server.configuration;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

/**
 * @author as2388
 */
public class ConfigurationTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor constructor = Configuration.class.getDeclaredConstructor();
        assertTrue("Constructor is not private", Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
