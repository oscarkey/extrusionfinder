package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.GrayscaleImageData;
import uk.ac.cam.cl.echo.extrusionfinder.server.imagedata.RGBImageData;

import java.awt.geom.Point2D;

public class PreprocessorTester {
    private PreprocessorTester() {}

    public static void detector(String inFile, String outFile) {
        System.loadLibrary(Configuration.OPENCV_LIBRARY_NAME);

        Mat in = Highgui.imread(inFile);
        byte[] inData = new byte[in.rows() * in.cols() * 3];
        in.get(0, 0, inData);

        ProfileDetector detector = new ProfileDetector();

        GrayscaleImageData outData = detector.process(new RGBImageData(inData, in.cols(), in.rows()));
        
        Mat out = new Mat(outData.height, outData.width, CvType.CV_8UC1);
        out.put(0, 0, outData.data);
        Highgui.imwrite(outFile, out);
    }

    public static void fitting(String inFile) {
        System.loadLibrary(Configuration.OPENCV_LIBRARY_NAME);

        Mat in = avgGreyscale(Highgui.imread(inFile));
        byte[] inData = new byte[in.rows() * in.cols()];
        in.get(0, 0, inData);

        ProfileFitting fitting = new ProfileFitting(new GrayscaleImageData(inData, in.cols(), in.rows()));

        Point2D centre = fitting.getCentre();
        System.out.printf("Centre: (%s, %s)\nSize: %s\n", centre.getX(), centre.getY(), fitting.getRadius());
    }

    /**
     * Converts the input image to greyscale using the averaging of all components per pixel.
     * <p>
     * For each pixel, the greyscale value is given as (pixel.red + pixel.green + pixel.blue) / 3.
     */
    private static Mat avgGreyscale(Mat input) {
        Mat output = new Mat();
        Imgproc.cvtColor(input, output, Imgproc.COLOR_RGB2GRAY);
        return output;
    }

    public static void main(String[] args) {
        detector(args[0], args[1]);
        fitting(args[1]);
    }
}
