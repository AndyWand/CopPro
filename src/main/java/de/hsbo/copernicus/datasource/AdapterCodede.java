package de.hsbo.copernicus.datasource;

import math.geom2d.polygon.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.geom2d.Point2D;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import de.hsbo.copernicus.configuration.*;

/**
 * This adapter is to request and download Sentinel-2 Products from Code-DE
 * <link>https://code-de.org/</link>
 * It requests products by using OpenSearch and downloads it as a file Parallel downloads are
 * limited to 2 or 4
 * <p>
 * Mandatory parameters: startDate, endDate bbox optional as String:String in
 * additionalParameter-HashMap cloudCover startRecord maximumRecords
 *
 * @author Andreas
 */
class AdapterCodede extends AbstractAdapter {

    /**
     * Attributes predifined by the abstract class AbstractAdapter
     */   
    private static AbstractAdapter instance;  
    
    //Available Sensor types
    public static final String SENSOR_TYPE_SOUND = "LIMB";
    public static final String SENSOR_TYPE_RADAR = "RADAR";
    public static final String SENSOR_TYPE_OPTICAL = "OPTICAL";
    public static final String SENSOR_TYPE_ALTIMETRY = "ALTIMETRIC";
    public static final String SENSOR_TYPE_ATHMOSPHERIC = "ATHMOSPHERIC";

    private String sensorType = SENSOR_TYPE_OPTICAL;
    private Calendar start, end;
    private Rectangle2D bbox;
    private HashMap additionalParameter;
    private File result;    
    private final String credentials;

    /**
     * Attributes nessesary to perform a proper query only in this adapter
     */
    private static final String httpAccept = "httpAccept=application/atom+xml";
    private static final String parentIdentifier = "parentIdentifier=EOP:CODE-DE:S2_MSI_L1C";
    private static String startDateString = "startDate=2017-02-09T10:09:55.000Z";
    private static String endDateString = "endDate=2017-02-19T10:09:55.000Z";
    private static String bboxString = "bbox=7.1,51.3,7.4,51.4";

    /**
     * Dafault Constuctor
     */
    private AdapterCodede() {     
        super.name = "codede";
        Configuration config = ConfigurationReader.getInstance();
        super.baseUrl = config.getCodedeBaseurl();
        this.credentials = config.getCodedeCredentials();
    }

    /**
     * indirect constructor to ensure that there is only one instance of this class (Singleton)
     *
     * @return
     */
    public static AbstractAdapter getInstance() {
        if (AdapterCodede.instance == null) {
            AdapterCodede.instance = new AdapterCodede();
            return instance;
        }
        return instance;
    }

    public String buildQueryString(Calendar startDate, Calendar endDate, Rectangle2D bbox) {
        String result = "";
        //If there is a passed date, put it into the query string
        if (!(startDate.compareTo(new GregorianCalendar(0, 0, 0)) == 0)) {
            Integer startYear = startDate.get(Calendar.YEAR);
            Integer startMonth = startDate.get(Calendar.MONTH);
            Integer startDay = startDate.get(Calendar.DAY_OF_MONTH);
            Integer startHour = startDate.get(Calendar.HOUR);
            Integer startMinute = startDate.get(Calendar.MINUTE);
            Integer startSecond = startDate.get(Calendar.SECOND);

            Integer endYear = endDate.get(Calendar.YEAR);
            Integer endMonth = endDate.get(Calendar.MONTH);
            Integer endDay = endDate.get(Calendar.DAY_OF_MONTH);
            Integer endHour = endDate.get(Calendar.HOUR);
            Integer endMinute = endDate.get(Calendar.MINUTE);
            Integer endSecond = endDate.get(Calendar.SECOND);

            //startDate components as strings
            String startMonthString = startMonth.toString();
            String startDayString = startDay.toString();
            String startHourString = startHour.toString();
            String startMinuteString = startMinute.toString();
            String startSecondString = startSecond.toString();

            //components of endDate as strings
            String endMonthString = endMonth.toString();
            String endDayString = endDay.toString();
            String endHourString = endHour.toString();
            String endMinuteString = endMinute.toString();
            String endSecondString = endSecond.toString();

            /**
             * reformat the Stringrepresentations of the date to fit them in to the template
             * YY-MM-DDThh:mm:ss.sssZ
             *
             */
            //refomat startDate components
            if (startMonth < 10) {
                startMonthString = "0" + startMonthString;
            }
            if (startDay < 10) {
                startDayString = "0" + startDayString;
            }
            if (startHour == 0) {
                startHourString = "00";
            }
            if (startMinute == 0) {
                startMinuteString = "00";
            }
            if (startSecond == 0) {
                startSecondString = "00.000";
            }
            //reformat endDate components
            if (endMonth < 10) {
                endMonthString = "0" + endMonthString;
            }
            if (endDay < 10) {
                endDayString = "0" + endDayString;
            }
            if (endHour == 0) {
                endHourString = "00";
            }
            if (endMinute == 0) {
                endMinuteString = "00";
            }
            if (endSecond == 0) {
                endSecondString = "00.000";
            }
            startDateString = "startDate=" + startYear + "-" + startMonthString + "-" + startDayString + "T"
                    + startHourString + ":" + startMinuteString + ":" + startSecondString + "Z";
            endDateString = "endDate=" + endYear + "-" + endMonthString + "-" + endDayString + "T"
                    + endHourString + ":" + endMinuteString + ":" + endSecondString + "Z";

            result = baseUrl + httpAccept + "&" + parentIdentifier + "&" + startDateString + "&"
                    + endDateString;
            return result;
        } else {
            //if there ia a passed bbox, calculate the center and put it into the query string
            if (!bbox.isEmpty()) {
                result = "bbox=" + bbox2String(bbox);
            } else {
            }
            result += " AND " + "sensorType=" + this.sensorType;
        }
        return result;
    }

    private String bbox2String(Rectangle2D bbox) {
        // if bbox is a point it'll be converted to just X/Y
        // a polygon is convertes to a series of points
        // "'bbox': '7.1,51.3,7.4,51.4'";
        if (!bbox.vertex(0).contains(bbox.vertex(2))) {
            String localBboxString = "bbox=";
            Collection<Point2D> c = bbox.vertices();
            int i = 0;
            for (Point2D p : c) {
                if (i > 0) {
                    localBboxString += "," + p.x() + " " + p.y();
                    i++;
                } else {
                    localBboxString += p.x() + " " + p.y();
                    i++;
                }
            }
            localBboxString += "'";
            return localBboxString;
        } else {
            Point2D point = bbox.vertex(0);
            String bboxString = point.x() + ", " + point.y() + "'";
            return bboxString;
        }
    }

    public String query(Calendar startDate, Calendar endDate, Rectangle2D bbox,
            HashMap<String, String> additionalParameter) throws IOException {

        File xml;// = new File(".\\");
        // transform input parameter into a valid request string        

        //define optional parameters
        final String startRecord;
        final String maximumRecords;
        final String cloudCover;

        //set optional parameters
        if (additionalParameter.containsKey("startRecord")) {
            startRecord = "startRecord=" + additionalParameter.get("startRecord");
        } else {
            // startRecord = "startRecord=1";
        }
        if (additionalParameter.containsKey("maximumRecords")) {
            maximumRecords = "maximumRecords=" + additionalParameter.get("maximumRecords");
        } else {
            // maximumRecords = "maximumRecords=10";
        }
        //set optinal Parameter "cloudCover"
        if (additionalParameter.containsKey("cloudCover")) {
            //TODO: add addional verification, if "cloudCover" is well formed
            cloudCover = "cloudCover=" + additionalParameter.get("cloudCover");
        } else {
            //  cloudCover = "cloudCover=[0,20]";
        }

        String queryString = buildQueryString(startDate, endDate, bbox);// + "&" + cloudCover + "&" + startRecord + "&" + maximumRecords;

        // request CODE-DE via Opensearch       
        xml = download(queryString, "");
        handleXML(xml);

        return "";
    }

    static String handleXML(File inputLine) {
        // receive XML response and filter result

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
        }
        Document xmlDoc = null;
        try {
            xmlDoc = builder.parse(inputLine);
        } catch (SAXException | IOException e) {
            // TODO Auto-generated catch block
        }
        xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}entry");
        String id = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}id").item(0).toString();
        String title = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}title").item(0).toString();
        String updated = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}updated").item(0).toString();
        String date = xmlDoc.getElementsByTagName("{http://purl.org/dc/elements/1.1/}date'").item(0).toString();
        String polygon = xmlDoc.getElementsByTagName("{http://www.georss.org/georss}polygon").item(0).toString();
        String published = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}published").item(0)
                .toString();
        String identifier = xmlDoc.getElementsByTagName("{http://purl.org/dc/elements/1.1/}identifier").item(0)
                .toString();
        String link = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}link").item(0).toString();

        return link;
    }
    
    public File download(String recource, String saveDir) throws IOException {
        Downloader d = new Downloader();
        d.setResource(recource, credentials);
        Thread t1 = new Thread(d);
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return d.getResult();

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setQuery(Calendar startDate, Calendar endDate, Rectangle2D bbox, HashMap<String, String> additionalParameter) {
        this.start = startDate;
        this.end = endDate;
        this.bbox = bbox;
        this.additionalParameter = additionalParameter;
    }

    @Override
    public void run() {
        String recource = "";
        try {
            recource = query(this.start, this.end, this.bbox, this.additionalParameter);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        try {
            //TODO specify passed String parameters
            this.result = download(recource, "");
        } catch (IOException ex) {
            Logger.getLogger(AdapterCodede.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public File getResult() {
        return this.result;
    }
    
    @Override
    public String getName(){
        return super.name;
    }

}
