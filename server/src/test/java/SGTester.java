import org.apache.batik.transcoder.TranscoderException;
import org.junit.Test;
import uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web.ProfileNotFoundException;
import uk.ac.cam.cl.echo.extrusionfinder.server.preprocessor.web.SVG;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * Created by Alexander Simpson on 2015-02-23.
 */
public class SGTester {
    @Test
    public void testSVG() throws InterruptedException, XMLStreamException, ProfileNotFoundException, TranscoderException, IOException {
        SVG.process();
    }
}
