package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGPathElement;

import javax.xml.stream.XMLStreamException;
import java.io.*;

public class SVG {
    public static void main(String[] args) throws IOException, XMLStreamException, TranscoderException,
            InterruptedException, ProfileNotFoundException {
        process();
    }

    public static void process() throws IOException, XMLStreamException, TranscoderException, InterruptedException, ProfileNotFoundException {
        convertPdfToSvg("in.pdf", "intermediate.svg");

        System.loadLibrary("opencv_java249");

        // Load SVG from hard-coded file
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        File file = new File("intermediate.svg");
        SVGDocument svg = f.createSVGDocument(file.toURI().toString());

        // Recurse through the SVG's DOM, removing nodes which look like numbers,
        // measurement lines, or arrows
        removeLabels(svg);

        // Rasterize, and write out to file system as a png
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) 4096); //scale
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) 4096);
        TranscoderInput input = new TranscoderInput(svg);
        OutputStream os = new FileOutputStream("cleaned.png");
        TranscoderOutput output = new TranscoderOutput(os);

        transcoder.transcode(input, output);
        os.flush();
        os.close();

        // Load the saved cleaned image as greyscale bytes.
        Mat rawImage = Highgui.imread("cleaned.png", -1);

        Mat croppedImage = autocrop_and_pallete(rawImage);
        Mat profile = fillProfile(croppedImage);

        Highgui.imwrite("profile.png", profile);
    }

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

    private static void convertPdfToSvg(String input, String output) throws IOException, InterruptedException {
        // Convert pdf with inkscape
        String inkscapeLocation = "inkscape";
        ProcessBuilder pb = new ProcessBuilder(inkscapeLocation, "-l", output, input);
        Process p = pb.start();
        p.waitFor();
    }

    private static void removeLabels(Node parent) {
        // This loop needs to be backwards (yuck), because it deletes nodes
        for (int i = parent.getChildNodes().getLength() - 1; i >= 0; i--) {
            Node child = parent.getChildNodes().item(i);

            if (child instanceof SVGPathElement) {
                SVGPathElement element = (SVGPathElement) child;
                String styleText = element.getAttribute("style");
                if (styleText.contains("stroke-width:0.2399") ||
                        styleText.contains("fill:#000000")) {
                    parent.removeChild(child);

                    // If the path's container element is now empty, may as well delete that too
                    if (!parent.hasChildNodes()) {
                        parent.getParentNode().removeChild(parent);
                    }
                } else {
                    removeLabels(child);
                }
            } else {
                removeLabels(child);
            }
        }
    }
}
