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
public abstract class Adapter implements Runnable {

    /**
     * instance is an object of class DataSource to ensure that there is only
     * one instance of each adapter existing
     */
    private static Adapter instance;
    private static String baseURL;
    public static final String NAME = "";
    public static File result;

    /**
     * *
     * Method to query for datasats in a specific area, period of time, sensor
     * TODO: bbox should be a Geometry Object with spatial reference system
     *
     * @param startDate Calendar object of type GregorianCalendar
     * @param endDate   Calendar object of type GregorianCalendar
     * @param bbox Recangle of type math.geom2d.polygon.Rectangle2D;
     * @param additionalParameter HashMap for optional parameter
     * @return
     * @throws java.io.IOException
     */
    protected abstract String query(Calendar startDate, Calendar endDate, Rectangle2D bbox,
            HashMap<String, String> additionalParameter) throws IOException;

    /**
     * *
     * Method to request the actual set of images
     */
    abstract File download(String fileURL, String saveDir) throws IOException;

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
     * @param file
     */
    public abstract void setQuery(Calendar startDate, Calendar endDate, Rectangle2D bbox,
            HashMap<String, String> additionalParameter, File file);

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
