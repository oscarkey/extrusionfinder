package uk.ac.cam.cl.echo.extrusionfinder.server.imagematching;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.GrayscaleImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.zernike.Zernike;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImageMatcherTester {

    //Euclidean distances between Zernike moments of the images in the folder testimages1
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
        final File directory = new File("src/test/java/uk/ac/cam/cl/echo/extrusionfinder/server/imagematching/testimages1");
        int fileCount = directory.listFiles().length;
        if (fileCount == 0) {
            return;
        }
        File[] testImages = new File[fileCount];
        for (final File fileEntry : directory.listFiles()) {
            String fileName = fileEntry.getName();
            int index = Integer.parseInt(FilenameUtils.removeExtension(fileName));
            testImages[index] = fileEntry;
        }

        for (int i = 0; i < fileCount; i++) {
            String imagePath = testImages[i].getPath();

            BufferedImage baseImage1 = ImageIO.read(new File(imagePath));
            byte[] baseImage1Data;
            if (baseImage1.getType() != BufferedImage.TYPE_BYTE_GRAY) {
                BufferedImage t = new BufferedImage(baseImage1.getWidth(), baseImage1.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                t.getGraphics().drawImage(baseImage1, 0, 0, null);
                baseImage1Data = ((DataBufferByte) t.getData().getDataBuffer()).getData();
            } else {
                baseImage1Data = ((DataBufferByte) baseImage1.getData().getDataBuffer()).getData();
            }
            GrayscaleImageData baseImage2 = new GrayscaleImageData(baseImage1Data, baseImage1.getWidth(), baseImage1.getTileHeight());

            ImageMatcher im1 = new ImageMatcher(baseImage1, 10, new Point2D.Double(50.0, 50.0), 50.0);
            ImageMatcher im2 = new ImageMatcher(baseImage2, 10, new Point2D.Double(50.0, 50.0), 50.0);

            for (int j = i; j < fileCount; j++) {
                BufferedImage image = ImageIO.read(new File(testImages[j].getPath()));
                double[] zm = Zernike.zernikeMoments(image, 10, new Point2D.Double(50.0, 50.0), 50.0);

                double similarity1 = im1.compare(zm);
                double similarity2 = im2.compare(zm);
                double staticSimilarity = ImageMatcher.compare(imagePath, testImages[j].getPath(), 10, new Point2D.Double(50.0, 50.0), 50.0);

                assertTrue(similarity1 == imageComparisons[i][j-i]);
                assertTrue(similarity2 == imageComparisons[i][j-i]);
                assertTrue(staticSimilarity == imageComparisons[i][j-i]);
            }
        }
    }

    /*@Test
    public void compareAllWithOne() throws IOException {
        final File directory = new File("src/test/java/uk/ac/cam/cl/echo/extrusionfinder/server/imagematching/testimages2");
        final File[] files = directory.listFiles();

        ImageMatcher im = new ImageMatcher(ImageIO.read(files[0]), 12, new Point2D.Double(50.0, 50.0), 50.0);

        System.out.println("Comparing with: " + files[0].getName());

        for (final File fileEntry : directory.listFiles()) {
            BufferedImage image = ImageIO.read(fileEntry);
            double[] zm = Zernike.zernikeMoments(image, 12, new Point2D.Double(50.0, 50.0), 50.0);
            System.out.print(fileEntry.getName() + ": ");
            System.out.print(im.compare(zm));
            System.out.println();
        }
    }*/
}
