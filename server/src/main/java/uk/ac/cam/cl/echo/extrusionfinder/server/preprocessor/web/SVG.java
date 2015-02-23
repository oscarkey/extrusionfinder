package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGPathElement;

import javax.xml.stream.XMLStreamException;
import java.io.*;

public class SVG {
    public static void main(String[] args) throws IOException, XMLStreamException, TranscoderException,
            InterruptedException {
        process();
    }

    public static void process() throws IOException, XMLStreamException, TranscoderException, InterruptedException {
        // Convert pdf with inkscape
        String inkscapeLocation = "C:\\Program Files\\Inkscape\\inkscape.exe";
        ProcessBuilder pb = new ProcessBuilder(inkscapeLocation, "-l", "intermediate.svg", "in.pdf");
        Process p = pb.start();
        p.waitFor();

        // Load SVG from hard-coded file
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        File file = new File("intermediate.svg");
        SVGDocument svg = f.createSVGDocument(file.toURI().toString());

        // Recurse through the SVG's DOM, removing nodes which look like numbers,
        // measurement lines, or arrows
        removeLabels(svg);

        // TODO: fill

        // Rasterize, and write out to file system as a png
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) 5000); //scale
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) 5000);
        TranscoderInput input = new TranscoderInput(svg);
        OutputStream os = new FileOutputStream("out.png");
        TranscoderOutput output = new TranscoderOutput(os);

        transcoder.transcode(input, output);
        os.flush();
        os.close();
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
