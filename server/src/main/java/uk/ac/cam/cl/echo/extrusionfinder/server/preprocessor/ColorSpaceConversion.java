package uk.ac.cam.echo.extrusionfinder.preprocessor;

import java.lang.Math;

import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.LabImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.XYZImageData;

/**
 * Collection of static colorspace conversion methods.
 * Includes pairwise conversions between RGB, XYZ and L*a*b*.
 */
public class ColorSpaceConversion {

    // The CIE XYZ tristimulus values of the reference white point for D6500
    // wikipedia.org/wiki/Illuminant_D65#Definition
    private static final double X_N = 0.95047;
    private static final double Y_N = 1.00000;
    private static final double Z_N = 1.08833;


    // Converting to L*a*b* by going through of XYZ seems
    // to be recommended by every online source I've found

    /**
     * Convert RGBImageData to LabImageData, preserving color.
     *
     * @param rgb Input image
     * @return Output image of the same color (barring rounding error)
     *         in L*a*b* format.
     */
    public static LabImageData toLab(RGBImageData rgb) {
        return toLab(toXYZ(rgb));
    }

    /**
     * Convert LabImageData to RGBImageData, preserving color.
     *
     * @param lab Input image
     * @return Output image of the same color (barring rounding error)
     *         in RGB format.
     */
    public static RGBImageData toRGB(LabImageData lab) {
        return toRGB(toXYZ(lab));
    }


    /**
     * The f(t) function from wikipedia.org/wiki/Lab_color_space#Forward_transformation.
     */
    private static double f(double t) {
        if (t > 6*6*6 / (29*29*29D)) {
            return Math.cbrt(t);
        } else {
            return 29*29 / (3 * 6*6D) * t + 4/29D;
        }
    }

    /**
     * The f⁻¹(t) function from wikipedia.org/wiki/Lab_color_space#Reverse_transformation
     */
    private static double f_inv(double t) {
        if (t > 6 / 29D) {
            return t*t*t;
        } else {
            return 3 * 6*6 / (29*29D) * (t - 4/29D);
        }
    }


    /**
     * Convert XYZImageData to LabImageData, preserving color.
     *
     * @param lab Input image
     * @return Output image of the same color (barring rounding error)
     *         in Lab format.
     */
    public static LabImageData toLab(XYZImageData xyz) {
        LabImageData lab = new LabImageData(new double[xyz.data.length], xyz.width, xyz.height);

        for (int i = 0; i < xyz.width; i++) {
            for (int j = 0; j < xyz.height; j++) {
                int index = 3 * (j * xyz.width + i);

                double x = xyz.data[index + 0];
                double y = xyz.data[index + 1];
                double z = xyz.data[index + 2];

                // wikipedia.org/wiki/Lab_color_space#Forward_transformation
                double fx = f(x / X_N);
                double fy = f(y / Y_N);
                double fz = f(z / Z_N);

                double l = lab.data[index + 0] = 116 * fy - 16;
                double a = lab.data[index + 1] = 500 * (fx - fy);
                double b = lab.data[index + 2] = 200 * (fy - fz);
            }
        }

        return lab;
    }

    /**
     * Convert LabImageData to XYZImageData, preserving color.
     *
     * @param lab Input image
     * @return Output image of the same color (barring rounding error)
     *         in XYZ format.
     */
    public static XYZImageData toXYZ(LabImageData lab) {
        XYZImageData xyz = new XYZImageData(new double[lab.data.length], lab.width, lab.height);

        for (int i = 0; i < lab.width; i++) {
            for (int j = 0; j < lab.height; j++) {
                int index = 3 * (j * lab.width + i);

                double l = lab.data[index + 0];
                double a = lab.data[index + 1];
                double b = lab.data[index + 2];

                // wikipedia.org/wiki/Lab_color_space#Reverse_transformation
                double l_norm = (l + 16) / 116;

                double x = xyz.data[index + 0] = X_N * f_inv(l_norm + a / 500);
                double y = xyz.data[index + 1] = Y_N * f_inv(l_norm);
                double z = xyz.data[index + 2] = Z_N * f_inv(l_norm - b / 200);
            }
        }

        return xyz;
    }


    private static final double A = 0.055;

    /**
     * The C_srgb(C_linear) function from
     * wikipedia.org/wiki/SRGB#The_forward_transformation_.28CIE_xyY_or_CIE_XYZ_to_sRGB.29
     */
    private static double to_c_srgb(double c_linear) {
        if (c_linear <= 0.0031308) {
            return 12.92 * c_linear;
        } else {
            return (1 + A) * Math.pow(c_linear, 1 / 2.4) - A;
        }
    }

    /**
     * The C_linear(C_srgb) function from
     * wikipedia.org/wiki/SRGB#The_reverse_transformation
     */
    private static double to_c_linear(double c_srgb) {
        if (c_srgb <= 0.04045) {
            return c_srgb / 12.92;
        } else {
            return Math.pow((c_srgb + A) / (1 + A), 2.4);
        }
    }

    /**
     * Convert XYZImageData to RGBImageData, preserving color.
     *
     * @param lab Input image
     * @return Output image of the same color (barring rounding error)
     *         in RGB format.
     */
    public static RGBImageData toRGB(XYZImageData xyz) {
        RGBImageData rgb = new RGBImageData(new byte[xyz.data.length], xyz.width, xyz.height);

        for (int i = 0; i < xyz.width; i++) {
            for (int j = 0; j < xyz.height; j++) {
                int index = 3 * (j * xyz.width + i);

                double x = xyz.data[index + 0];
                double y = xyz.data[index + 1];
                double z = xyz.data[index + 2];

                // wikipedia.org/wiki/SRGB#The_forward_transformation_.28CIE_xyY_or_CIE_XYZ_to_sRGB.29
                double r_linear = + 3.2406 * x - 1.5372 * y - 0.4986 * z;
                double g_linear = - 0.9689 * x + 1.8758 * y + 0.0415 * z;
                double b_linear = + 0.0557 * x - 0.2040 * y + 1.0570 * z;

                // "the usual technique is to multiply by 255 and round to an integer" - Wikipedia
                // Note that byte is signed, but wraparound makes this "work"
                byte r = rgb.data[index + 0] = (byte)(255 * to_c_srgb(r_linear) + 0.5);
                byte g = rgb.data[index + 1] = (byte)(255 * to_c_srgb(g_linear) + 0.5);
                byte b = rgb.data[index + 2] = (byte)(255 * to_c_srgb(b_linear) + 0.5);
            }
        }

        return rgb;
    }

    /**
     * Convert RGBImageData to XYZImageData, preserving color.
     *
     * @param lab Input image
     * @return Output image of the same color (barring rounding error)
     *         in XYZ format.
     */
    public static XYZImageData toXYZ(RGBImageData rgb) {
        XYZImageData xyz = new XYZImageData(new double[rgb.data.length], rgb.width, rgb.height);

        for (int i = 0; i < rgb.width; i++) {
            for (int j = 0; j < rgb.height; j++) {
                int index = 3 * (j * rgb.width + i);

                byte r = rgb.data[index + 0];
                byte g = rgb.data[index + 1];
                byte b = rgb.data[index + 2];

                // wikipedia.org/wiki/SRGB#The_reverse_transformation
                double r_lin = to_c_linear((r & 0xFF) / 255.0);
                double g_lin = to_c_linear((g & 0xFF) / 255.0);
                double b_lin = to_c_linear((b & 0xFF) / 255.0);

                double x = xyz.data[index + 0] = + 0.4124 * r_lin + 0.3576 * g_lin + 0.1805 * b_lin;
                double y = xyz.data[index + 1] = + 0.2126 * r_lin + 0.7152 * g_lin + 0.0722 * b_lin;
                double z = xyz.data[index + 2] = + 0.0193 * r_lin + 0.1192 * g_lin + 0.9505 * b_lin;
            }
        }

        return xyz;
    }
}
