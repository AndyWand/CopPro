/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.datasource;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is to download resources asychronoulsy
 * it provides mathods to set resources which will be loaded on run 
 * and offers the results at getResult
 * @author Andreas Wandert
 */
public class Downloader implements Runnable {

    private static final int BUFFER_SIZE = 4096;
    private String resource;
    private File result;

    @Override
    public void run() {
        try {
            this.result = download(this.resource, "");
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public File download(String recource, String saveDir) throws IOException {

        File resultFile = null;
        final String defaultsaveDir = "./";
        if (saveDir.isEmpty()) {
            saveDir = defaultsaveDir;
        }

        URL url = new URL(recource);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        httpConn.addRequestProperty("Authorization", "Basic YW53YTpuZGw/M2hzNyElQVQ");

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            } else {
                // extracts file name from baseURL
                fileName = recource.substring(recource.lastIndexOf("/") + 1, recource.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String outputFile = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            //result = new File(outputFile);
            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

        String[] urlsegments = recource.split("/");
        resultFile = new File(saveDir + "/" + urlsegments[urlsegments.length - 1]);
        return resultFile;
    }

    /**
     *
     * @param resource
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     *
     *
     * @return 
     */
    public File getResult() {
        return this.result;
    }
}
