package de.hsbo.copernicus.datasource;

import math.geom2d.polygon.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author Andreas
 */
public abstract class AbstractAdapter implements Runnable {

    /**
     * instance is an object of class DataSource to ensure that there is only one instance of each
     * adapter existing
     */
    public String name;
    private static AbstractAdapter instance;
    public String baseUrl;
    private static File result;

    /**
     * Method to test waether a Portal is available
     *
     * @return
     */
    public boolean isOnline() {
        // check if service at 'baseURL' is available
        String[] urlArray1 = baseUrl.split("//");
        String[] urlArray2 = urlArray1[1].split("/");
        try {
            InetAddress.getByName(urlArray2[0]).isReachable(3000); //Replace with your name
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @param bbox
     * @param additionalParameter
     *
     */
    public abstract void setQuery(Calendar startDate, Calendar endDate, Rectangle2D bbox,
            HashMap<String, String> additionalParameter);

    /**
     * Starts an execution
     */
    @Override
    public abstract void run();

    /**
     *
     * @return file: file object passed in setQuery
     */
    public abstract File getResult();

    /**
     *
     * @return Name string of the associated adapter
     */
    public abstract String getName();

}
