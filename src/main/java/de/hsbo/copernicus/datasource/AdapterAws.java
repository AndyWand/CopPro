package de.hsbo.copernicus.datasource;

import math.geom2d.polygon.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This adapter is to provide communication to Sentinel-2 on AWS
 * <link>http://sentinel-pds.s3-website.eu-central-1.amazonaws.com/</link>
 * performs a data download without queryig or filtering because AWS doesn't
 * offer this
 *
 * @author Andreas Wandert
 */
class AdapterAws extends AbstractAdapter {

    public static final String NAME = "aws";
    private static AdapterAws instance;
    private static String baseURL = "http://sentinel-s2-l1c.s3.amazonaws.com/tiles";
    //Website:"http://sentinel-s2-l1c.s3-website.eu-central-1.amazonaws.com/tiles";

    private Calendar start, end;
    private Rectangle2D bbox;
    private HashMap additionalParameter;
    private File result;
    private static final int BUFFER_SIZE = 4096; //Buffer for download

    private AdapterAws() {
    }

    /**
     *
     * @return
     */
    public static AdapterAws getInstance() {
        if (AdapterAws.instance == null) {
            AdapterAws.instance = new AdapterAws();
            return AdapterAws.instance;
        }
        return AdapterAws.instance;
    }

    @Override
    public String query(Calendar startDate, Calendar endDate, Rectangle2D bbox,
            HashMap<String, String> additionalParameter) {
        String queryString = baseURL;
        // the following parameters need to be calulated by thransforming the
        // input parameters to UTM mGrid        

        double lat = bbox.centroid().x();
        double lon = bbox.centroid().y();
        String[] pointInMGRS = transform(lat, lon);

        // UTM Zone e.g. 10 - grid zone designator.
        String utmZone = pointInMGRS[0];
        queryString += '/' + utmZone;
        // latitude band e.g. S - latitude band are lettered C- X
        // (omitting the letters "I" and "O").
        String latitudeBand = pointInMGRS[1];
        queryString += '/' + latitudeBand;
        // square: pair of letters designating one of the
        // 100,000-meter side grid squares inside the grid
        // zone.
        String square = pointInMGRS[2];
        queryString += '/' + square;

        // perform test of 'year'
        // year should be a 4 digit number greater or equal then 2015      
        int day = startDate.get(Calendar.DAY_OF_MONTH); // day of month e.g. 
        int month = startDate.get(Calendar.MONTH);  // mouth of year e.g. 5
        int year = startDate.get(Calendar.YEAR);  // year the data was collected e.g. 2014

        if (year >= 2015 && String.valueOf(year).length() == 4) {
            queryString += '/' + String.valueOf(year);
        }

        // perform test of 'month'
        if (String.valueOf(month).length() <= 2 && String.valueOf(month).length() >= 0 && month >= 0 && month <= 12) {
            if (month == 0) {
                month = 12;
            }
            queryString += '/' + String.valueOf(month);
        } 

        // perform test of 'day'
        if (String.valueOf(day).length() >= 1 && String.valueOf(day).length() <= 2 && day >= 1 && day <= 31) {
            queryString += '/' + String.valueOf(day);
        }
        // perform test of 'sequence'
        if (additionalParameter.containsKey("sequence")) {
            queryString += '/' + additionalParameter.get("sequence");
        } else {
            queryString += '/' + "0";
        }
        if (additionalParameter.containsKey("band")) {
            queryString += '/' + additionalParameter.get("band") + ".jp2";
        }
        // return the query
        //System.out.println(queryString);
        return queryString;

    }

    /**
     * source:
     * http://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url
     * Downloads a file from a baseURL
     *
     * @param fileURL HTTP baseURL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @return
     * @throws IOException
     */
    @Override
    public File download(String fileURL, String saveDir) throws IOException {

        File result = null;
        final String defaultsaveDir = ".\\";
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
                // extracts file NAME from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            } else {
                // extracts file NAME from baseURL
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

            //result = new File(outputFile);
            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

        String[] urlsegments = fileURL.split("/");
        result = new File(saveDir + "/" + urlsegments[urlsegments.length - 1]);
        return result;
    }

    @Override
    public boolean isOnline() {
        // check if service at 'baseURL' is available
        InetAddress Ip;
        boolean flag = false;
        try {
            Ip = InetAddress.getByName("172.217.21.238");
            flag = Ip.isReachable(10);
        } catch (IOException e) {
            // TODO Auto-generated catch block

        }
        return flag;

    }

    /**
     * This Method is to transform expected input coordinates from geographic
     * lat/lon to WGS84 UTM coordinates **for internal use only**
     *
     * @param lat lat-coordinate in decimal degree
     * @param lon lon-coordinate in decimal degree
     * @return String-array format is: [0]: UTM-Zone [1]: latitude band [2]:
     * square
     *
     * uses an instance of class CoordinateConverion to cnovert decimal degree
     * into UTM MGRS licensing this method is using code from
     * https://www.ibm.com/developerworks/apps/download/index.jsp?contentid=250050&filename=j-coordconvert.zip&method=http&locale=
     */
    private String[] transform(double lat, double lon) {
        CoordinateConverter converter = new CoordinateConverter();
        String pointInMGRS = converter.latLon2MGRUTM(lat, lon);

        //Target format is: UTM-Code, Latitude-Band, Square
        String[] result = new String[3];
        //first too digits for UTM-Zone
        if (pointInMGRS.substring(0, 1).equals("0")) {
            result[0] = pointInMGRS.substring(1, 2);
        } else {
            result[0] = pointInMGRS.substring(0, 2);
        }
        //third digit for lat-band
        result[1] = pointInMGRS.substring(2, 3);
        //forth two digits for square
        result[2] = pointInMGRS.substring(3, 5);

        return result;
    }

    @Override
    public void setQuery(Calendar startDate, Calendar endDate, Rectangle2D bbox, HashMap<String, String> additionalParameter, File file) {
        this.start = startDate;
        this.end = endDate;
        this.bbox = bbox;
        this.additionalParameter = additionalParameter;
        this.result = file;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @param bbox
     * @param file
     */
    public void setQuery(Calendar startDate, Calendar endDate, Rectangle2D bbox, File file) {
        HashMap<String, String> additionalParameter = new HashMap<>();
        setQuery(startDate, endDate, bbox, additionalParameter, file);
    }

    @Override
    public void run() {
        try {
            String query = this.query(this.start, this.end, this.bbox, this.additionalParameter);
            query = "http://sentinel-s2-l1c.s3.amazonaws.com/tiles/46/J/DN/2015/8/23/0/B01.jp2";
            //System.out.println(query);
            this.result = this.download(query,"" );
            this.result = new File ("C:\\Users\\Andreas\\Downloads\\S2A_MSIL1C_20170403T104021_N0204_R008_T32ULC_20170403T104138.SAFE");
        } catch (IOException ex) {
            Logger.getLogger(AdapterAws.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public File getResult() {
        return this.result;
    }

}
