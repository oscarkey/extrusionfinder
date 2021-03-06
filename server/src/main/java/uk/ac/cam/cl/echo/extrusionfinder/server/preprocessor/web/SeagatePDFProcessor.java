package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGPathElement;
import uk.ac.cam.cl.echo.extrusionfinder.server.configuration.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides facilites for coverting a pdf to a preprocessed svg.
 *
 * @author as2388
 * @author ashleynewson
 */
public class SeagatePDFProcessor {

    private SeagatePDFProcessor() {}

    private static final Logger logger =
            LoggerFactory.getLogger(SeagatePDFProcessor.class);

    static {
        try {
            nu.pattern.OpenCV.loadShared();
        } catch (Throwable e) {
            // Assume library already loaded, and ignore the error
        }
    }

    /**
     * Converts a pdf file to a png file, removing labels and applying fill in the process.
     * <p>
     * Dear users of this class: Sorry about all the exceptions.
     * @param inputPDFPath           Address of pdf file to convert.
     * @param outputPNGPath          Address to write processed png file to
     * @throws IOException           Thrown if files could not be loaded or written
     * @throws TranscoderException   Thrown if the processed svg could not be converted to a png
     * @throws InterruptedException  Thrown if the process was unable to wait for inkscape to complete
     * @throws ProfileNotFoundException When the profile cannot be detected, possibly due to a blank
     *         or empty image, or an unsupported diagramatic representation.
     */
    public static void process(String inputPDFPath, String outputPNGPath) throws IOException, TranscoderException,
            InterruptedException, ProfileNotFoundException {
        logger.info("Processing " + inputPDFPath);

        // Convert the pdf to an svg file written to 'intermediate.svg'. This svg has yet to be cleaned
        convertPdfToSvg(inputPDFPath, "intermediate.svg");

        // Load the raw svg into an SVGDocument
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        File file = new File("intermediate.svg");
        SVGDocument svg = f.createSVGDocument(file.toURI().toString());

        // Recurse through the SVG's OM, removing nodes which look like numbers,
        // measurement lines, or arrows
        removeLabels(svg);

        // Rasterize, and write out to file system as a png.
        OutputStream os = new FileOutputStream("cleaned.png");
        try {
            PNGTranscoder transcoder = new PNGTranscoder();

            // Scale the size of the rasterized png
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, Configuration.PDF_RASTERIZATION_DIMENSIONS);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, Configuration.PDF_RASTERIZATION_DIMENSIONS);

            TranscoderInput input = new TranscoderInput(svg);
            TranscoderOutput output = new TranscoderOutput(os);
            transcoder.transcode(input, output);
        } finally {
            os.flush();
            os.close();
        }

        // Load the saved cleaned image as greyscale bytes.
        Mat rawImage = Highgui.imread("cleaned.png", -1);

        Mat croppedImage = autocrop_and_pallete(rawImage);
        Mat profile = fillProfile(croppedImage);

        Highgui.imwrite(outputPNGPath, profile);

        logger.info("Processed " + inputPDFPath);
    }

    /**
     * Converts a pdf to an svg.
     * <p>
     * Implementation notes: This is achieved by calling inkscape via its commandline interface
     * @param input                 Address of pdf to convert
     * @param output                Address of svg to write conversion to
     * @throws IOException
     * @throws InterruptedException
     */
    private static void convertPdfToSvg(String input, String output) throws IOException, InterruptedException {
        // Convert pdf with inkscape
        String inkscapeLocation = Configuration.INKSCAPE_LOCATION;
        ProcessBuilder pb = new ProcessBuilder(inkscapeLocation, "-l", output, input);
        Process p = pb.start();
        p.waitFor();
    }

    /**
     * Removes elements in the SVG's Object Model which look like arrows or measurement lines
     * @param parent Node to explore from. Typical use will be the SVG's root element when calling for the first time.
     */
    private static void removeLabels(Node parent) {
        // This loop needs to be backwards (yuck), because it deletes nodes
        for (int i = parent.getChildNodes().getLength() - 1; i >= 0; i--) {
            Node child = parent.getChildNodes().item(i);

            if (child instanceof SVGPathElement) {
                SVGPathElement element = (SVGPathElement) child;
                String styleText = element.getAttribute("style");
                if (styleText == null) {
                    removeLabels(child);
                } else if (styleText.contains("stroke-width:0.2399") ||styleText.contains("fill:#000000")) {
                    // If the path looks like a label, delete the path
                    parent.removeChild(child);
                } else {
                    // Otherwise, assume the path is a line on the extrusion. Replace its stroke thickness
                    // to thin the SVG
                    String newStyleText = styleText.replaceAll("stroke-width:[0-9]*\\.[0-9]*;", "");

                    // If newStyleText is not empty, append a semicolon.
                    if (!newStyleText.isEmpty()) {
                        newStyleText = newStyleText + ";";
                    }
                    newStyleText = newStyleText + "stroke-width:" + Configuration.PDF_THINNED_EDGE_THICKNESS;
                    element.setAttribute("style", newStyleText);

                    // Recursively explore the rest of the SVGOM
                    removeLabels(child);
                }
            } else {
                // Recursively explore the rest of the SVGOM
                removeLabels(child);
            }
        }
    }

    /**
     * Crops out all transparent space, and produces a binary 8-bit image of the input.
     *
     * @param input The extrusion diagram, currently only supports Seagate Plastics diagrams.
     * @return An 8-bit binarised image with 1 pixel thick transpareny border around diagram. The
     *         pixel values are 255 for outline and 0 for background.
     * @throws ProfileNotFoundException Thrown when the image is all transparent or empty.
     */
    private static Mat autocrop_and_pallete(Mat input) throws ProfileNotFoundException {
        int width  = input.cols();
        int height = input.rows();

        byte[] data = new byte[width * height * 4];
        input.get(0, 0, data);

        int minX = width;
        int maxX = 0;
        int minY = height;
        int maxY = 0;

        for (int y = 0, i = 3; y < height; y++) {
            for (int x = 0; x < width; x++, i += 4) {
                if (data[i] != 0) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }
        if (minX == width) {
            // There was nothing in this image.
            throw new ProfileNotFoundException();
        }

        int newWidth = maxX - minX + 3;
        int newHeight = maxY - minY + 3;
        byte[] pData = new byte[newWidth * newHeight];

        for (int y = 0; y < newHeight - 2; y++) {
            for (int x = 0; x < newWidth - 2; x++) {
                if (data[((minY + y) * width + (minX + x)) * 4 + 3] != 0) {
                    pData[(y + 1) * newWidth + (x + 1)] = (byte)255;
                }
            }
        }

        Mat cropped = new Mat(newHeight, newWidth, CvType.CV_8UC1);

        cropped.put(0, 0, pData);

        return cropped;
    }

    /**
     * Fills the perceived interior of the extrusion using the diagram's outline.
     *
     * @param input An image of the outline of the extrusion profile, where 255 represents the 
     *        outline, and 0 represents the background.
     * @return A binary image of the profile area, where 255 represents profile area, and 0 
     *         represents the background.
     */
    private static Mat fillProfile(Mat input) {
        int width  = input.cols();
        int height = input.rows();

        Mat mask = new Mat(height + 2, width + 2, CvType.CV_8UC1);
        mask.setTo(new Scalar(0));

        // Could be done more efficiently probably, but I am avoiding premature optimisation.

        Mat backgroundFill = input.clone();
        Imgproc.floodFill(backgroundFill, mask, new Point(0, 0), new Scalar(255));
        mask.setTo(new Scalar(0));

        Mat shellRemove = backgroundFill.clone();
        Imgproc.floodFill(shellRemove, mask, new Point(0, 0), new Scalar(0));
        mask.setTo(new Scalar(0));

        Mat innerFill = shellRemove.clone();
        Imgproc.floodFill(innerFill, mask, new Point(0, 0), new Scalar(255));

        Mat backgroundOnly = new Mat();
        Core.subtract(backgroundFill, input, backgroundOnly);

        Mat result = new Mat();
        Core.subtract(innerFill, backgroundOnly, result);

        return result;
    }
}
