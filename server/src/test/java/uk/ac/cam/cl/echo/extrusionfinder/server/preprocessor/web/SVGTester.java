package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web;

import org.apache.batik.transcoder.TranscoderException;
import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web.ProfileNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web.SVG;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class SVGTester {
    @Test
    public void testSVG() throws InterruptedException, XMLStreamException, ProfileNotFoundException,
            TranscoderException, IOException {
        SVG.process("in.pdf");
    }
}
