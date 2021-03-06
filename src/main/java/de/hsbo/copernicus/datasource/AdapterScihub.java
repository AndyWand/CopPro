package de.hsbo.copernicus.datasource;

import math.geom2d.polygon.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import math.geom2d.Point2D;

import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.hsbo.copernicus.configuration.*;

/**
 * This adapter provides query and download of Sentinel-2 products from Scientific Data Hub (SciHub)
 * by ESA.
 *
 * @author Andreas Wandert
 */
class AdapterScihub extends AbstractAdapter {

    private static final String TAG_VALUE_ID = "str";
    private static final String ATTRIBUTE_VALUE_ID = "uuid";
    private static final String ATTRIBUTE_VALUE_IDENTIFIER = "identifier";
    private static final String TAG_VALUE_NAME = "str";
    private static final String TAG_VALUE_ENTRY = "entry";
    public static final String PLATFORM_S1 = "Sentinel-1";
    public static final String PLATFORM_S2 = "Sentinel-2";
    public static final String PLATFORM_S3 = "Sentinel-3";
    public static final String PLATFORM_S4 = "Sentinel-4";
    public static final String PLATFORM_S5 = "Sentinel-5";
    private static AbstractAdapter instance;
    //For the Open Search API use "https://scihub.copernicus.eu/apihub/" instead
    private final String odataUrl;
    private Calendar start, end;
    private Rectangle2D bbox;
    private HashMap additionalParameter;
    private File result;
    private String credentials;
    private String platform = "Sentinel-2";

    /**
     * This is a List of available parameter to query a product: For geometric filtering
     * q=foodprint:"Intersects(Point)" q=foodprint:"Intersects(POLYGON((Point coordinates in decimal
     * degees)))" For time filtering q=ingestiondate:[], beginposition, endposition platformname
     * platformname:Sentinel-2 Others: cloudcoverage, swathidentifier, producttype, orbitdirection,
     * orbitnumber, filename for more see:
     * https://scihub.copernicus.eu/twiki/do/view/SciHubUserGuide/3FullTextSearch
     */
    private AdapterScihub() {
        super.name = "scihub";
        Configuration config = ConfigurationReader.getInstance();
        super.baseUrl = config.getSciHubBaseurl();
        this.odataUrl = config.getSciHubOdataurl();
        this.credentials = config.getSciHubCredentials();       
    }

    public static AbstractAdapter getInstance() {
        if (AdapterScihub.instance == null) {
            instance = new AdapterScihub();
            return instance;
        }
        return instance;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @param bbox
     * @param additionalParameter bandnumber
     * @return
     */
    public String query(Calendar startDate, Calendar endDate, Rectangle2D bbox,
            HashMap<String, String> additionalParameter) {

        File xml = new File("./");

        //TODO build the query string
        String toRequest = baseUrl + "?q=" + buildQueryString(startDate, endDate, bbox);
        //send request to SciHub OpenSearch API
        try {
            xml = download(toRequest, "");
        } catch (IOException ex) {
            Logger.getLogger(AdapterScihub.class.getName()).log(Level.SEVERE, null, ex);
        }
        //filter UUID and Product name from responded XML file
        HashMap<String, String> tagValues = null;
        try {
            tagValues = handleXML(xml, TAG_VALUE_ENTRY, TAG_VALUE_NAME, TAG_VALUE_ID, ATTRIBUTE_VALUE_IDENTIFIER, ATTRIBUTE_VALUE_ID, ATTRIBUTE_VALUE_ID, ATTRIBUTE_VALUE_IDENTIFIER);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        //build a new request string
        /* https://scihub.copernicus.eu/dhus/odata/v1/Products('5d9c44e9-6ae2-40c4-9dda-46e7e12b5ab8')/
 * Nodes('S1A_IW_SLC__1SSV_20161207T013515_20161207T013552_014267_017143_DFE1.SAFE')/
 * Nodes('measurement')/
 * Nodes('s1a-iw1-slc-vv-20161207t013517-20161207t013550-014267-017143-001.tiff')
         */      
        toRequest = odataUrl + "/Products('" + tagValues.get(ATTRIBUTE_VALUE_ID) + "')/$value";//Nodes('" + tagValues.get(ATTRIBUTE_VALUE_IDENTIFIER) + "')/Nodes";

        //use this for an other request
        try {            
            xml = download(toRequest, "");
        } catch (IOException ex) {
            Logger.getLogger(AdapterScihub.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //filter raster name from the responded XML
            //<title> type="text">s1a-iw1-slc-vv-20161207t013517-20161207t013550-014267-017143-001.tiff</title>
            tagValues = handleXML(xml, "entry", "title", "", "", "", "", "");
            //TODO "title" has an attribute type="text"
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        //cut ".tiff" and the image number eg: 001 from the result number
        String s = tagValues.get("title").split(".")[0];
        String productName = s.substring(s.length() - 4, s.length() - 1);

        //take the product name and add the choosen number from users input and ".tiff"
        productName += additionalParameter.get("bandnumber") + "." + tagValues.get("title").split(".")[1];

        //build an other request string by using UUID, Product name and image name
        toRequest += "/Nodes('measurement')/Nodes('" + productName + "')";

        return toRequest;
    }

    /**
     *
     *
     * @param xmlFile xml file to extract the specified to from
     * @param tagToFilter tag tag2 to filter
     *
     * @return array of strings with tag values in
     */
    private HashMap<String, String> handleXML(File xmlFile, String superLevelTag, String lowerLevelTag1, String lowerLevelTag2, String attributeOfTag1, String attributeOfTag2, String attributeValue1, String attributeValue2) throws FileNotFoundException, IOException {
      
        String[] resultArray = new String[2];
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        HashMap<String, String> result = new HashMap<>();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AdapterScihub.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            doc = builder.parse(xmlFile);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(AdapterScihub.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Read the document
        Element root = doc.getDocumentElement();
        NodeList nL = root.getElementsByTagName(superLevelTag);
        ArrayList<String[]> productIds = new ArrayList<>();
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element superElement = (Element) n;
                Element eElement;
                String tag1 = superElement.getElementsByTagName(lowerLevelTag1).item(0).getTextContent();

                String attribute1 = "", attribute2 = "";
                //filter for 1st attribute
                if (!attributeOfTag1.isEmpty() && !attributeOfTag1.isEmpty()) {
                    NodeList lowerLevel1nl = superElement.getElementsByTagName(lowerLevelTag1);
                    for (int j = 0; j < lowerLevel1nl.getLength(); j++) {
                        eElement = (Element) lowerLevel1nl.item(j);
                        //attribute1 = eElement.getAttribute(attributeOfTag1);
                        //result.put(attributeValue1, attribute1);

                        String valueOfAttribute1 = eElement.getAttributes().getNamedItem("name").getNodeValue();
                        if (valueOfAttribute1.equalsIgnoreCase(attributeValue1)) {
                            attribute1 = eElement.getTextContent();
                        }
                        if (valueOfAttribute1.equalsIgnoreCase(attributeValue2)) {
                            attribute2 = eElement.getTextContent();
                        }
                    }
                }
                String[] listelement = {attribute1, tag1};
                productIds.add(listelement);
            }

        }
        //do a size check
        if (!productIds.isEmpty()) {
            resultArray = productIds.get(productIds.size() - 1);
            result.put(attributeValue1, resultArray[0]);
            result.put(attributeValue2, resultArray[1]);
        } else {
            System.out.println("SciHub-Adapter: productids: size is zero");
        }
        return result;
    }

    /**
     *
     */
    private String buildQueryString(Calendar startDate, Calendar endDate, Rectangle2D bbox) {
        String result = "";

        //If there is a passed date, put it into the query string
        if (!(startDate.compareTo(new GregorianCalendar(0, 0, 0)) == 0)) {

            //extract date and time
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

        } else {
            //TODO use time for query
        }
        //if there ia a passed bbox, calculate the center and put it into the query string
        if (!bbox.isEmpty()) {
            result = "footprint:\"Intersects(" + bbox2String(bbox) + ")\"";
        } else {
        }
        result += " AND " + "platformname:" + this.platform;
        return result;
    }

    private String bbox2String(Rectangle2D bbox) {
        // if bbox is a point it'll be converted to just X/Y
        // a polygon is convertes to a series of points
        if (!bbox.vertex(0).contains(bbox.vertex(2))) {
            String bboxString = "POLYGON((";
            Collection<Point2D> c = bbox.vertices();
            int i = 0;
            for (Point2D p : c) {
                if (i > 0) {
                    bboxString += "," + p.x() + " " + p.y();
                    i++;
                } else {
                    bboxString += p.x() + " " + p.y();
                    i++;
                }
            }
            bboxString += "))";
            return bboxString;
        } else {
            Point2D point = bbox.vertex(0);
            String bboxString = point.x() + ", " + point.y();
            return bboxString;
        }

    }

    private String data2String(Calendar start, Calendar end) {
        //as a result the query string for time should bw formetde like 
        //ingestiondate:[NOW-1Day TO NOW]
        String dateQuery = "ingestiondate:["; //if startend end date are equal or end is zero
        if (start.compareTo(end) == 0 || end.compareTo(new GregorianCalendar(0, 0, 0)) == 0) {
            //put just one date into the query string
            //calculate the difference between now and start date
            Calendar now = new GregorianCalendar();

            int yearsDiff = now.get(Calendar.YEAR) - start.get(Calendar.YEAR);

            //put year,month or year diff into query string depening on which is greater
            //Years
            if (yearsDiff < 0) {
                dateQuery += "[NOW-" + yearsDiff + 1 + "YEARS TO NOW]";
            } else {
                //Month
                int monthDiff = now.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                if (monthDiff < 0) {
                    dateQuery += "[NOW-" + monthDiff + 1 + "MONTH TO NOW]";
                } else {
                    //Days
                    int daysDiff = now.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);
                    if (daysDiff < 0) {
                        dateQuery += "[NOW-" + daysDiff + 1 + "DAYS TO NOW]";

                    } else {
                        //Hours
                    }
                }
            }
        } else {
            //else put a period of time into the query
        }
        return dateQuery;
    }

    public synchronized File download(String fileURL, String saveDir) throws IOException {
        Downloader d = new Downloader();      
        d.setResource(fileURL, this.credentials);
        Thread t = new Thread(d);
        t.start();
        try {
            t.join();

        } catch (InterruptedException ex) {
            Logger.getLogger(AdapterScihub.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return d.getResult();
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @param bbox
     * @param additionalParameter has to contain "credantials" formated like <username>:<password>
     * in one string optional parameters: bandnumber
     *
     */
    @Override
    public void setQuery(Calendar startDate, Calendar endDate, Rectangle2D bbox, HashMap<String, String> additionalParameter) {
        this.start = startDate;
        this.end = endDate;
        this.bbox = bbox;
        this.additionalParameter = additionalParameter;        
    }

    @Override
    public void run() {
        String resource = query(this.start, this.end, this.bbox, this.additionalParameter);
        try {
            this.result = download(resource, "");

        } catch (IOException ex) {
            Logger.getLogger(AdapterScihub.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public File getResult() {
        return this.result;
    }

    private void setPlattformName(String platformname) {
        this.platform = platformname;
    }

    @Override
    public String getName() {
        return super.name;
    }
}
