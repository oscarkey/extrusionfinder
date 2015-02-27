package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.util;

import uk.ac.cam.cl.echo.extrusionfinder.server.parts.Size.Unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.ConnectException;
import java.lang.reflect.Constructor;

import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.anyObject;

/**
 * Unit tests the metadata parser (using EasyMock and PowerMock).
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileUtility.class })
public class FileUtilityTester {

    @Test
    public void testConstructor() throws Exception {

        Constructor constructor = FileUtility.class.getDeclaredConstructor();
        Object c = constructor.newInstance();
        assertTrue(c instanceof FileUtility);

    }

    /**
     * Tests downloading of file
     */
    @Test
    public void testDownloadFileSuccess() throws Exception {

        final String url = "http://extrusionfinder.com/file.png";
        final String dir = "saveme";
        final String savedTo = "saveme/file.png";

        URL urll = createMockAndExpectNew(URL.class, url);
        HttpURLConnection http = createMock(HttpURLConnection.class);
        InputStream input = createMock(InputStream.class);

        expect(urll.openConnection()).andReturn(http);
        expect(http.getResponseCode()).andReturn(HttpURLConnection.HTTP_OK);
        expect(http.getHeaderField("Content-Disposition")).andReturn(null);

        expect(http.getInputStream()).andReturn(input);
        FileOutputStream output = createMockAndExpectNew(FileOutputStream.class, savedTo);

        expect(input.read(anyObject())).andReturn(-1);
        input.close();
        expectLastCall().times(1);

        output.close();
        expectLastCall().times(1);

        http.disconnect();
        expectLastCall().times(1);

		replayAll();

        // call the getter
        String path = FileUtility.downloadFile(url, dir);

        // check that we got the mocked crawlcontroller
        assertEquals(path, savedTo);

		verifyAll();
    }


    /**
     * Tests exception thrown if http did not connect successfully.
     */
    @Test(expected = ConnectException.class)
    public void testDownloadFileFailure() throws Exception {

        final String url = "http://extrusionfinder.com/file.png";
        final String dir = "saveme";
        final String savedTo = "saveme/file.png";

        URL urll = createMockAndExpectNew(URL.class, url);
        HttpURLConnection http = createMock(HttpURLConnection.class);
        expect(urll.openConnection()).andReturn(http);

        // this time return not found
        expect(http.getResponseCode()).andReturn(HttpURLConnection.HTTP_NOT_FOUND);
        http.disconnect();
        expectLastCall().times(1);

		replayAll();

        // call the getter
        String path = FileUtility.downloadFile(url, dir);

        // check that we got the mocked crawlcontroller
        assertEquals(path, savedTo);

		verifyAll();
    }

    /**
     * Tests creation of file path, creation of directory, and recursive
     * deletion of directory.
     */
    @Test
    public void testFileCreationDeletion() throws IOException {
        final String path = "fileutilitytest";
        final String filename = "file.txt";
        File f = new File(path);

        // create the directory
        FileUtility.makeDir(path);
        assertTrue(f.exists());

        // create a file and put it in the directory
        String newfile = FileUtility.createPath(path, filename);
        File g = new File(newfile);
        g.createNewFile();
        assertTrue(g.exists());

        // delete the directory recursively
        FileUtility.delete(path);
        assertTrue(!f.exists());
        assertTrue(!g.exists());
    }
}
