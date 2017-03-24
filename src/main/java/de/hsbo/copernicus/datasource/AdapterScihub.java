package de.hsbo.copernicus.datasource;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.HashMap;

public class AdapterScihub extends Adapter {

    // This URL is for ODATA-Hub
    private static final String baseURL = "https://scihub.copernicus.eu/apihub/";
    //For the Open Search API use "https://scihub.copernicus.eu/dhus/search" instead
    private static Adapter instance;
    public static final String name ="scihub";
    
    /**This is a List of available parameter to query a product:
     * For geometric filtering
     * q=foodprint:"INtersects(Point)"
     * q=foodprint:"Intersects(POLYGON((Point coordinates in decimal degees)))"
     * For time filtering
     * q=ingestiondate:[], beginposition, endposition 
     * platformname
     * platformname:Sentinel-2
     * Others:
     * cloudcoverage, swathidentifier, producttype, orbitdirection, orbitnumber, filename
     * for more see: https://scihub.copernicus.eu/twiki/do/view/SciHubUserGuide/3FullTextSearch
     */

    private AdapterScihub() {
    }

    public static Adapter getInstance() {
        if (AdapterScihub.instance == null) {
            instance = new AdapterScihub();
        }
        return AdapterScihub.instance;
    }

    public String query(Calendar startDate, Calendar endDate, Rectangle bbox,
            HashMap<String, String> additionalParameter) {
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
    public File request(Calendar startDate, Calendar endDate, Rectangle bbox, HashMap<String, String> additionalParameter) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
