package uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web;

import org.apache.batik.transcoder.TranscoderException;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author as2388
 */
public class SeagatePDFProcessorTester {

    /**
     * Performs an integration test by processing 'in.pdf' to a nice 'profile.png' output file.
     * If either inkscape or opencv is misconfigured, this test will fail.
     * @throws InterruptedException
     * @throws XMLStreamException
     * @throws ProfileNotFoundException
     * @throws TranscoderException
     * @throws IOException
     */
//    @Test
    public void testSVG() throws InterruptedException, XMLStreamException, ProfileNotFoundException,
            TranscoderException, IOException {
        SeagatePDFProcessor.process("src/test/resources/in.pdf", "profile.png");
    }
}
