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
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

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

    private File download(String recource, String saveDir) throws IOException {
        String SEPERATOR = "\\?";

        //Reformat the recource string to an ascii encoded string
        String[] splitedRecource = recource.split(SEPERATOR);
        URL url = new URL(recource);
        //new URL(splitedRecource[0] + "?" + URLEncoder.encode(splitedRecource[1], "UTF-8"));
        URI uri = null;
        try {
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (URISyntaxException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        recource = uri.toASCIIString();
        System.out.println("Downloader:encoded URL: " + recource);
        File resultFile = null;
        final String defaultSaveDir = ".";
        if (saveDir.isEmpty()) {
            saveDir = defaultSaveDir;
        }
        String basicAuth = "Basic " + new String(java.util.Base64.getEncoder().encode(this.credential.getBytes()));
        //System.out.println("Authentication: " + basicAuth);
        //url = new URL(recource);
        System.out.println("Downloader:URL: " + url);
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

    public HashMap<String, String> encode(HashMap<String, String> parameterValues) {
        HashMap<String, String> resultMap = new HashMap<>();
        Object[] keys = parameterValues.keySet().toArray();
        Object[] values = parameterValues.values().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            String value = (String) values[i];
            try {
                resultMap.put(key, URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
        return resultMap;
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
