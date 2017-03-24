package de.hsbo.copernicus.datasource;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.net.*;
import java.io.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class AdapterCodede extends Adapter {

    /**
     * Attributes predifined by the abstract class Adapter
     */
    private static String baseURL = "https://code-de.org/opensearch/request/?";
    private static Adapter instance;
    public static final String name = "Codede";

    /**
     * Attributes nesseary to perform a proper query only in this adapter
     */
    private static String httpAccept = "'httpAccept': 'application/atom+xml'";
    private static String parentIdentifier = "'parentIdentifier': 'EOP:CODE-DE:S2_MSI_L1'C";
    private static String startDateString = "'startDate': '2017-02-09T10:09:55.000Z'";
    private static String endDateString = "";
    private static String bboxString = "'bbox': '7.1,51.3,7.4,51.4'";

    private AdapterCodede() {

    }

    public static Adapter getInstance() {
        if (AdapterCodede.instance == null) {
            AdapterCodede.instance = new AdapterCodede();
        }
        return AdapterCodede.instance;
    }

    public String query(Calendar startDate, Calendar endDate, Rectangle bbox,
            HashMap<String, String> additionalParameter) throws IOException {

        // transform input parameter into a valid request string
        final Integer startYear = startDate.get(Calendar.YEAR);
        final Integer startMonth = startDate.get(Calendar.MONTH);
        final Integer startDay = startDate.get(Calendar.DAY_OF_MONTH);
        // TODO: add Time

        final Integer endYear = endDate.get(Calendar.YEAR);
        final Integer endMonth = endDate.get(Calendar.MONTH);
        final Integer endDay = endDate.get(Calendar.DAY_OF_MONTH);
        // TODO: add Time

        startDateString = "startDate:" + startYear + "-" + startMonth + "-" + startDay + "T00:00:00.000Z";
        endDateString = "endDate:" + endYear + "-" + endMonth + "-" + endDay + "T00:00:00.000Z";
        bboxString = bbox.toString();

        final String QueryString = baseURL + httpAccept + "&" + parentIdentifier + "&" + startDateString + "&"
                + endDateString + "&" + bboxString;

        // request CODE-DE via Opensearch
        URL request;
        request = new URL(QueryString);

        URLConnection yc = request.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
        }
        in.close();

        handleXML(inputLine);

        return "";
    }

    private static void handleXML(String inputLine) {
        // receive XML response and filter result

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Document xmlDoc = null;
        try {
            xmlDoc = builder.parse(inputLine);
        } catch (SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}entry");
        final String id = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}id").item(0).toString();
        final String title = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}title").item(0).toString();
        final String updated = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}updated").item(0).toString();
        final String date = xmlDoc.getElementsByTagName("{http://purl.org/dc/elements/1.1/}date'").item(0).toString();
        final String polygon = xmlDoc.getElementsByTagName("{http://www.georss.org/georss}polygon").item(0).toString();
        final String published = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}published").item(0)
                .toString();
        final String identifier = xmlDoc.getElementsByTagName("{http://purl.org/dc/elements/1.1/}identifier").item(0)
                .toString();
        final String link = xmlDoc.getElementsByTagName("{http://www.w3.org/2005/Atom}link").item(0).toString();

    }

    /**
     * *
     * ask a specific datasource whether its online or not
     */
    public boolean isOnline() {
        // check if service at 'URL' is available
        InetAddress Ip;
        boolean flag = false;
        try {
            Ip = InetAddress.getByName(baseURL);
            flag = Ip.isReachable(10);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (flag) {
            return true;
        } else {
            return false;
        }

    }

    public File request(Calendar startDate, Calendar endDate, Rectangle bbox,
            HashMap<String, String> additionalParameter) throws IOException {
        if (this.isOnline()) {
            //TODO specify passed String parameters
            return download(new String(), new String());
        }
        return null;
    }

    @Override
    public File download(String fileURL, String saveDir) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
