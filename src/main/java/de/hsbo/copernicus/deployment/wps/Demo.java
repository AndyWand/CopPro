/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.deployment.wps;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import math.geom2d.Point2D;
import org.n52.wps.io.data.IData;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.server.ExceptionReport;
import math.geom2d.polygon.Rectangle2D;

/**
 *
 * @author Andreas Wandert
 */
public class Demo extends AbstractSelfDescribingAlgorithm {

    @Override
    public List<String> getInputIdentifiers() {

        List<String> list = new ArrayList();
        list.add("STARTDATE");
        list.add("ENDDATE");
        list.add("BBOX");
        return list;
    }

    @Override
    public List<String> getOutputIdentifiers() {
        List<String> list = new ArrayList<>();
        list.add("raster");
        return list;
    }

    @Override
    public Class<?> getInputDataType(String id) {
        if (id.equalsIgnoreCase("STARTDATE")) {
            return null; //TBD  
        }
        return null;
    }

    @Override
    public Class<?> getOutputDataType(String id) {
        if (id.equalsIgnoreCase("raster")) {
            return null;//TBD
        }
        return null;
    }

    @Override
    public Map<String, IData> run(Map<String, List<IData>> inputData) throws ExceptionReport {
        Map<String, IData> result = new HashMap<>();
        if (inputData == null || !inputData.containsKey("STARTDATE")) {
            throw new RuntimeException("Error while allocating input parameters");
        }
        List<IData> dataList = inputData.get("STARTDATE");
        if (dataList == null || dataList.size() != 1) {
            throw new RuntimeException("Error while allocating input parameters");
        }

        //extract the data collection
        //STARTDATE
        String start = dataList.get(0).toString();
        DateFormat formatter = new SimpleDateFormat("YYYY/MM/DD:hh:mm:ss.sss");
        Date date = null;
        try {
            date = formatter.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);

        //ENDDATE
        if (!inputData.containsKey("ENDDATE")) {
            throw new RuntimeException("Error while allocating input parameters");
        }
        dataList = inputData.get("ENDDATE");
        if (dataList == null || dataList.size() != 1) {
            throw new RuntimeException("Error while allocating input parameters");
        }
        String end = dataList.get(0).toString();
        try {
            date = formatter.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(date);

        //BBOX
        if (!inputData.containsKey("BBOX")) {
            throw new RuntimeException("Error while allocating input parameters");
        }
        dataList = inputData.get("BBOX");
        if (dataList == null || dataList.size() != 1) {
            throw new RuntimeException("Error while allocating input parameters");
        }

        Rectangle2D bbox = new Rectangle2D();
        for (int i = 0; i < dataList.size(); i++) {
            Double x = Double.parseDouble(dataList.get(i).toString().split(",")[0]);
            Double y = Double.parseDouble(dataList.get(i).toString().split(",")[0]);
            bbox.setVertex(0, new Point2D(x, y));
        }

        
        
        return result;
    }

}
