package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor;

import java.util.Arrays;

import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.LabImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.XYZImageData;
import static uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.ColorSpaceConversion.toLab;
import static uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.ColorSpaceConversion.toRGB;
import static uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.ColorSpaceConversion.toXYZ;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class ColorSpaceConversionTester {
    @Test
    public void testRgbXyzRoundTrip() {
        int width = 64 * 64 * 64;
        RGBImageData rgb = new RGBImageData(new byte[width * 3], width, 1);

        for (int i = 0; i < width; i++) {
            rgb.data[3 * i + 0] = (byte)(i >>  0);
            rgb.data[3 * i + 1] = (byte)(i >>  8);
            rgb.data[3 * i + 2] = (byte)(i >> 16); 
        }

        XYZImageData xyz = toXYZ(rgb);
        RGBImageData cycled = toRGB(xyz);

        assertTrue(rgb.width == cycled.width);
        assertTrue(rgb.height == cycled.height);
        assertTrue(Arrays.equals(rgb.data, cycled.data));
    }

    @Test
    public void testRgbLabRoundTrip() {
        int width = 64 * 64 * 64;
        RGBImageData rgb = new RGBImageData(new byte[width * 3], width, 1);

        for (int i = 0; i < width; i++) {
            rgb.data[3 * i + 0] = (byte)((i >>  0) * 4);
            rgb.data[3 * i + 1] = (byte)((i >>  6) * 4);
            rgb.data[3 * i + 2] = (byte)((i >> 12) * 4); 
        }

        RGBImageData cycled = toRGB(toLab(rgb));

        assertTrue(rgb.width == cycled.width);
        assertTrue(rgb.height == cycled.height);
        assertTrue(Arrays.equals(rgb.data, cycled.data));
    }
}
