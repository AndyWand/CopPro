package de.hsbo.copernicus.datasource;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

public class AdapterAws extends Adapter {

    public static final String name = "aws";
    private static Adapter instance;
    private static String baseURL = "http://sentinel-s2-l1c.s3-website.eu-central-1.amazonaws.com/";

    private AdapterAws() {
    }

    public static Adapter getInstance() {
        if (AdapterAws.instance == null) {
            AdapterAws.instance = new AdapterAws();
        }
        return AdapterAws.instance;
    }

    /**
     * these are all the necessary parameters to request an aws resource.
     */
    private int UTM_Zone; // e.g. 10 - grid zone designator.
    private int latitude_band; // e.g. S - latitude band are lettered C- X
    // (omitting the letters "I" and "O").
    private String square; // pair of letters designating one of the
    // 100,000-meter side grid squares inside the grid
    // zone.
    private static Integer year; // year the data was collected e.g. 2014
    private static Integer month; // mouth of year e.g. 5
    private static Integer day; // day of month e.g. 10
    private static Integer sequence;

    private static char[] possibleLatBands = {'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W'};
    private static final int BUFFER_SIZE = 4096;

    @Override
    public String query(Calendar startDate, Calendar endDate, Rectangle bbox,
            HashMap<String, String> additionalParameter) {
        // the following parameters need to be calulated by thransforming the
        // input parameters to UTM mGrid
        //
        final Integer utmZone = 0;
        final Integer latitude_band = 0;
        final String square = "";
        sequence = Integer.parseInt(additionalParameter.get("sequence"));

        String queryString = "";

        // Validate passed parameters
        // and construct a valid query by using the available parameters
        if (utmZone <= 60 && utmZone >= 1) {
            queryString += "#tiles/" + utmZone;
        }
        for (char c : possibleLatBands) {
            if (latitude_band == c) {
                queryString += '/' + latitude_band;
            }
        }

        // create a HashMap for all possible values of the first character in
        // square string
        HashMap<Integer, char[]> planSquares = new HashMap<Integer, char[]>();
        char line1[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        char line2[] = {'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R'};
        char line3[] = {'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        planSquares.put(2, line2);
        planSquares.put(1, line1);
        planSquares.put(0, line3);

        // perform test of 'square'
        // test length
        if (square.length() == 2) {
            // test if first digit is valid
            for (char c : planSquares.get(square.charAt(0) % 3)) {
                if (c == square.charAt(0)) {

                    // test if secon ddigit is valid
                }
            }

        }
        // perform test of 'year'
        // year should be a 4 digit number greater or equal then 2015

        year = startDate.get(Calendar.YEAR);
        month = startDate.get(Calendar.MONTH);
        day = startDate.get(Calendar.DAY_OF_MONTH);

        if (year > 2015 && String.valueOf(year).length() == 4) {
            queryString += '/' + year;
        }

        // perform test of 'month'
        if (String.valueOf(month).length() <= 2 && String.valueOf(month).length() >= 1 && month >= 1 && month <= 12) {
            queryString += '/' + month;
        }

        // perform test of 'day'
        if (String.valueOf(day).length() <= 1 && String.valueOf(month).length() >= 2 && day >= 1 && day <= 31) {
            queryString += '/' + day;
        }
        // perform test of 'sequence'
        if (String.valueOf(sequence).length() >= 1) {
            queryString += '/' + sequence;
        }
        // return the baseURL to query
        return queryString;

    }

    /**
     * source:
     * http://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url
     * Downloads a file from a baseURL
     *
     * @param fileURL HTTP baseURL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    @Override
    public File download(String fileURL, String saveDir) throws IOException {

        File result = null;
        final String defaultsaveDir = "./resultDataset";
        if (saveDir.isEmpty()) {
            saveDir = defaultsaveDir;
        }

        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

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
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
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

            result = new File(outputFile);
            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

        return result;
    }

    @Override
    public boolean isOnline() {
        // check if service at 'baseURL' is available
        InetAddress Ip;
        boolean flag = false;
        try {
            Ip = InetAddress.getByName("172.217.21.238");
            System.out.println(Ip);
            flag = Ip.isReachable(10);

            System.out.println(flag);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return flag;

    }

    public File request(Calendar startDate, Calendar endDate, Rectangle bbox,
            HashMap<String, String> additionalParameter) throws IOException {
        if (isOnline()) {
            String fileURL = query(startDate, endDate, bbox, additionalParameter);
            return this.download(fileURL, "");
        } else {
            return null;
        }
    }

}
