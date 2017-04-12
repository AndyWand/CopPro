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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is to download resources asychronoulsy it provides mathods to set resources which will
 * be loaded on run and offers the results at getResult
 *
 * @author Andreas Wandert
 */
class Downloader implements Runnable {

    private static final int BUFFER_SIZE = 4096;
    private String resource;
    private File result;
    private String credential;

    @Override
    public void run() {
        try {
            this.result = download(this.resource, "");
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public File download(String recource, String saveDir) throws IOException {

        //Reformat the recource string to an ascii encoded string
        URL url = new URL(recource);
        URI uri = null;
        try {
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (URISyntaxException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        recource = uri.toASCIIString();

        System.out.println("Request for download is: " + recource);

        File resultFile = null;
        final String defaultsaveDir = ".";
        if (saveDir.isEmpty()) {
            saveDir = defaultsaveDir;
        }
        String basicAuth = "Basic " + new String(java.util.Base64.getEncoder().encode(this.credential.getBytes()));
        //System.out.println("Authentication: " + basicAuth);
        url = new URL(recource);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        //Add login credentials as md5 encoded string to the HTTP header
        httpConn.setRequestProperty("Authorization", basicAuth);
        //httpConn.addRequestProperty("Authorization", "Basic YW53YTpuZGw/M2hzNyElQVQ");
        httpConn.connect();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "temp";
            String fileFormat = "xml";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
            String lengthInBytes = httpConn.getHeaderField("content-Length");
           // long length = Long.parseLong(lengthInBytes);

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName + "." + fileFormat);
            System.out.println("length in Bytes: " + lengthInBytes);
           // System.out.println("Content-Length = " + length);

            if (disposition != null) {
                //cut File format  
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                String wrongFileName = "";
                if (index > 0) {
                    wrongFileName = disposition.substring(index);
                }
                System.out.println(wrongFileName);
                String[] fileFormatArray = wrongFileName.split("\\.");

                fileFormat = fileFormatArray[fileFormatArray.length - 1].substring(0, fileFormatArray[fileFormatArray.length - 1].length() - 1);
                System.out.println(fileFormat);
            }
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String outputFile = saveDir + File.separator + fileName + "." + fileFormat;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            resultFile = new File(outputFile);
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
            throw new IOException("Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

        String[] urlsegments = recource.split("/");
        //  resultFile = new File(saveDir + "/" + urlsegments[urlsegments.length - 1]);       
        return resultFile;
    }

    /**
     *
     * @param resource
     * @param md5Credential user credential formated like <username>:<password>
     */
    public void setResource(String resource, String credential) {
        this.resource = resource;
        this.credential = credential;
    }

    /**
     *
     *
     * @return
     */
    public File getResult() {
        this.result = new File(result.getAbsoluteFile().toString().replace("\\.", ""));
        return this.result;
    }
}
