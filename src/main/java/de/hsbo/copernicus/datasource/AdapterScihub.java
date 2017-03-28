package de.hsbo.copernicus.datasource;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This adapter provides query and download of Sentinel-2 products from
 * Scientific Data Hub (SciHub) by ESA.
 *
 * @author Andreas Wandert
 */
public class AdapterScihub extends Adapter {

    // This URL is for ODATA-Hub
    private static final String baseURL = "https://scihub.copernicus.eu/apihub/";
    //For the Open Search API use "https://scihub.copernicus.eu/dhus/search" instead
    private static Adapter instance;
    public static final String name = "scihub";
    private Calendar start, end;
    private Rectangle2D bbox;
    private HashMap additionalParameter;
    private File result;

    /**
     * This is a List of available parameter to query a product: For geometric
     * filtering q=foodprint:"INtersects(Point)"
     * q=foodprint:"Intersects(POLYGON((Point coordinates in decimal degees)))"
     * For time filtering q=ingestiondate:[], beginposition, endposition
     * platformname platformname:Sentinel-2 Others: cloudcoverage,
     * swathidentifier, producttype, orbitdirection, orbitnumber, filename for
     * more see:
     * https://scihub.copernicus.eu/twiki/do/view/SciHubUserGuide/3FullTextSearch
     */
    private AdapterScihub() {
    }

    public static Adapter getInstance() {
        if (AdapterScihub.instance == null) {
            instance = new AdapterScihub();
            return instance;
        }
        return instance;
    }

    public String query(Calendar startDate, Calendar endDate, Rectangle2D bbox,
            HashMap<String, String> additionalParameter) {

        //extract date and time
        final Integer startYear = startDate.get(Calendar.YEAR);
        final Integer startMonth = startDate.get(Calendar.MONTH);
        final Integer startDay = startDate.get(Calendar.DAY_OF_MONTH);
        final Integer startHour = startDate.get(Calendar.HOUR);
        final Integer startMinute = startDate.get(Calendar.MINUTE);
        final Integer startSecond = startDate.get(Calendar.SECOND);

        final Integer endYear = endDate.get(Calendar.YEAR);
        final Integer endMonth = endDate.get(Calendar.MONTH);
        final Integer endDay = endDate.get(Calendar.DAY_OF_MONTH);
        final Integer endHour = endDate.get(Calendar.HOUR);
        final Integer endMinute = endDate.get(Calendar.MINUTE);
        final Integer endSecond = endDate.get(Calendar.SECOND);
        // TODO Auto-generated method stub

        return null;
    }

    public void download() {
        // TODO Auto-generated method stub

    }

    public boolean isOnline() {
        // check if service at 'baseURL' is available
        InetAddress Ip;
        boolean flag = false;
        try {
            Ip = InetAddress.getByName(baseURL);
            System.out.println(Ip);
            flag = Ip.isReachable(10);

            System.out.println(flag);
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

    @Override
    public File download(String fileURL, String saveDir) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setQuery(Calendar startDate, Calendar endDate, Rectangle2D bbox, HashMap<String, String> additionalParameter, File file) {
        this.start = startDate;
        this.end = endDate;
        this.bbox = bbox;
        this.additionalParameter = additionalParameter;
        this.result = file;
    }

    @Override
    public void run() {
        try {
            this.result = download("", "");
        } catch (IOException ex) {
            Logger.getLogger(AdapterScihub.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File getResult() {
        return this.result;
    }

}
