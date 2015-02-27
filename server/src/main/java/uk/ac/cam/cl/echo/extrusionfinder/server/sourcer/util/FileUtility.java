package uk.ac.cam.cl.echo.extrusionfinder.server.sourcer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.ConnectException;
 
/**
 * Utility class for various file system related methods.
 */
public class FileUtility {

    private static final int BUFFER_SIZE = 16384;

    private FileUtility() {}
 
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @return  Path to saved file
     * @throws IOException if writing to file system failed
     * @throws ConnectException if the http url connection failed
     * slightly modified from:
     * http://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url
     */
    public static String downloadFile(String fileURL, String saveDir)
        throws IOException, ConnectException {

        fileURL = fileURL.replace(" ", "%20");

        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
 
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String fileName = "";
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = FileUtility.createPath(saveDir, fileName);

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();

            httpConn.disconnect();
            return saveFilePath;

        } else {

            String msg = "No file to download. Server replied HTTP code: " +
                responseCode + " on url " + fileURL;
            httpConn.disconnect();
            throw new ConnectException(msg);

        }
    }

    /**
     * Creates directory if it doesn't already exist.
     * @param path  Path to new directory.
     */
    public static void makeDir(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * Delete a directory or file (recursive)
     * Source: http://stackoverflow.com/a/3775718
     * @param path  Path to directory/file to be deleted.
     */
    public static void delete(String path) {
        delete(new File(path));
    }

    /**
     * @param path  File object of directory/file to be deleted recursively.
     */
    private static void delete(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {

                    if(files[i].isDirectory()) {
                        delete(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        path.delete();
    }

    /**
     * Creates a path from a directory and a file name.
     * @param dir   Directory name
     * @param file  File name
     * @return Path to file inside directory
     */
    public static String createPath(String dir, String file) {
        return String.format("%s%s%s", dir, File.separator, file);
    }
}
