/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.datasource;

import math.geom2d.polygon.Rectangle2D;
import java.io.File;
import java.io.IOException;
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
    private static AbstractAdapter instance;
    private static String baseURL; 
    private static File result;    
    
    
     /**
     * Method to test waether a Portal is available
     *
     * @return
     */
    abstract public boolean isOnline();

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

}
