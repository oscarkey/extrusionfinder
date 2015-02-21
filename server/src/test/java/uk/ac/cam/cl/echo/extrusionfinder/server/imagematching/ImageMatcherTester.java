package uk.ac.cam.cl.echo.extrusionfinder.server.imagematching;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.Zernike;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImageMatcherTester {

    double[][] imageComparisons = {
            {0.0, 0.1362300000699998, 0.21021316021322753, 0.19159809807101186, 0.2849470832595251},
            {0.0, 0.17776289731140657, 0.1725392276701063, 0.29121182103283866},
            {0.0, 0.22886727474847185, 0.15574643867962856},
            {0.0, 0.28984572143292026},
            {0.0},
    };

    @Test
    public void testDistance() {
        double[] A = {0.74, 0.62, 1.24, 0.77};
        double[] B = {0.62, 0.46, 1.07, 0.57};
        double[] C = {0.62};
        double d = ImageMatcher.distance(A, B);
        assertTrue(d == 0.33);
        try {
            ImageMatcher.distance(A, C);
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testCompare() throws IOException {
        final File directory = new File("src\\test\\java\\uk\\ac\\cam\\cl\\echo\\extrusionfinder\\server\\imagematching\\testimages1");
        int fileCount = directory.listFiles().length;
        File[] testImages = new File[fileCount];
        for (final File fileEntry : directory.listFiles()) {
            String fileName = fileEntry.getName();
            int index = Integer.parseInt(FilenameUtils.removeExtension(fileName));
            testImages[index] = fileEntry;
        }

        for (int i = 0; i < fileCount; i++) {
            String imagePath = testImages[i].getPath();
            BufferedImage image1 = ImageIO.read(new File(imagePath));
            ImageMatcher im = new ImageMatcher(image1, 10, new Point2D.Double(50.0, 50.0), 50.0);
            for (int j = i; j < fileCount; j++) {
                BufferedImage image2 = ImageIO.read(new File(testImages[j].getPath()));
                double[] zm = Zernike.zernikeMoments(image2, 10, new Point2D.Double(50.0, 50.0), 50.0);
                double similarity = im.compare(zm);
                double staticSimilarity = ImageMatcher.compare(imagePath, testImages[j].getPath(), 10, new Point2D.Double(50.0, 50.0), 50.0);
                assertTrue(similarity == imageComparisons[i][j-i]);
                assertTrue(staticSimilarity == imageComparisons[i][j-i]);
            }
        }
    }
}
