package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor;

import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.IntGridImage;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.GrayscaleImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.LabImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;

import static uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.ColorSpaceConversion.toLab;

/**
 * Implementation of thresholding using L*a*b* color space;
 * currently not adaptive or hue aware.
 *
 * This produces components instead of thresholding to prticular
 * values, so the magnitude of a value only determines whether it
 * is the same threshold as another pixel, not what color it is
 * thresholded from. The exception is a threshold of -1 == 255, which
 * always represents thresholding to black.
 */
public class LabSpaceThresholder {
    // TODO: Comment
    // Requires 
    private static IntGridImage getSquareRollingSums(LabImageData lab, int meanPatchSize) {
        int width = lab.width;
        int height = lab.height;
        IntGridImage rollingSums = new IntGridImage(new int[width * height], width, height);

        assert lab.width > meanPatchSize && lab.height > meanPatchSize;

        // Get rolling luminance sums width-wise
        for (int i = 0; i < width; i++) {
            int rollingSum = 0;

            // Seed summation
            for (int j = 0; j < meanPatchSize; j++) {
                rollingSum += lab.data[lab.index(i, j)];
            }
            
            // Set start edge to snapped
            for (int j = 0; j < meanPatchSize/2; j++) {
                rollingSums.data[rollingSums.index(i, j)] = rollingSum;
            }

            // Rolling summation
            for (int j = meanPatchSize; j < height; j++) {
                rollingSum += lab.data[lab.index(i, j)];
                rollingSum -= lab.data[lab.index(i, j - meanPatchSize)];

                try {
                    rollingSums.data[rollingSums.index(i, j - meanPatchSize/2)] = rollingSum;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("!$$£$");
                }
            }

            // Set end edge to snapped
            for (int j = height - meanPatchSize/2; j < height; j++) {
                rollingSums.data[rollingSums.index(i, j)] = rollingSum;
            }
        }

        // Repeat the above but transposed and on top of rollingSums itself
        //
        // I don't see a way to deduplicate this without extra copies or
        // having stride variables, neither of which is currently a great idea.
        // I could use anonymous functions but that's a bit of a pain without Java 8.
        IntGridImage squareRollingSums = new IntGridImage(new int[width * height], width, height);

        for (int j = 0; j < height; j++) {
            int rollingSum = 0;

            // Seed summation
            for (int i = 0; i < meanPatchSize; i++) {
                rollingSum += rollingSums.data[rollingSums.index(i, j)];
            }
            
            // Set start edge to snapped
            for (int i = 0; i < meanPatchSize/2; i++) {
                squareRollingSums.data[squareRollingSums.index(i, j)] = rollingSum;
            }

            // Rolling summation
            for (int i = meanPatchSize; i < width; i++) {
                rollingSum += rollingSums.data[rollingSums.index(i, j)];
                rollingSum -= rollingSums.data[rollingSums.index(j, i - meanPatchSize)];

                rollingSums.data[rollingSums.index(j, i - meanPatchSize/2)] = rollingSum;
            }

            // Set end edge to snapped
            for (int i = width - meanPatchSize/2; i < width; i++) {
                squareRollingSums.data[squareRollingSums.index(i, j)] = rollingSum;
            }
        }

        return squareRollingSums;
    }

    // TODO: Comment
    public static GrayscaleImageData labSpaceThreshold(RGBImageData rgb, int meanPatchSize) {
        return labSpaceThreshold(toLab(rgb), meanPatchSize);
    }

    // TODO: Comment
    public static GrayscaleImageData labSpaceThreshold(LabImageData lab, int meanPatchSize) {
        int width = lab.width;
        int height = lab.height;

        if (width <= meanPatchSize || height <= meanPatchSize) {
            throw new UnsupportedOperationException(
                "Thresholding code can only handle images at least as big " +
                "as LabSpaceThresholder.meanPatchSize in each dimension."
            );
        }
        
        IntGridImage squareRollingSums = getSquareRollingSums(lab, meanPatchSize);

        GrayscaleImageData thresholds =
            new GrayscaleImageData(new byte[width * height], width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double l = lab.data[lab.index(i, j)];

                thresholds.data[thresholds.index(i, j)] = (byte)l;
            }
        }

        return thresholds;
    }

}
