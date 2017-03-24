/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.datasource;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author Andreas
 */
public abstract class Adapter {

    /**
     * instance is an object of class DataSource to ensure that there is only
     * one instance of each adapter existing
     */
    private static Adapter instance;
    private static String baseURL;
    public static final String name = "";

    /**
     * *
     * Method to query for datasats in a specific area, period of time, sensor
     * TODO: bbox should be a Geometry Object with spatial reference system
     */
    protected abstract String query(Calendar startDate, Calendar endDate, Rectangle bbox,
            HashMap<String, String> additionalParameter) throws IOException;

    /**
     * *
     * Method to request the actual set of images
     */
    abstract File download(String fileURL, String saveDir) throws IOException;

    /**
     * *
     * ask a specific datasource whether its online or not
     *
     * @param startDate
     * @param endDate
     * @param bbox
     * @return
     */
    abstract public File request(Calendar startDate, Calendar endDate, Rectangle bbox,
            HashMap<String, String> additionalParameter) throws IOException;

    abstract public boolean isOnline();
}
