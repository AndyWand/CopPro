package de.hsbo.copernicus.deployment.wps;

/**
 * Copyright (C) 2013 by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24 48155 Muenster, Germany info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under the terms of the GNU
 * General Public License version 2 as published by the Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY OF
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program (see
 * gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite
 * 330, Boston, MA 02111-1307, USA or visit the Free Software Foundation web page,
 * http://www.fsf.org.
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.wps.io.data.IData;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.ExceptionReport;

import de.hsbo.copernicus.processing.Core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.File;


/**
 * This algorithm creates a convex hull of a Geotools FeatureCollection
 *
 * @author BenjaminPross
 *
 */
public class ProcessingDemo extends AbstractSelfDescribingAlgorithm {

    static final String INPUT = "InputParameter" ;
    static final String STARTDATE= "startdate" ;
    static final String ENDDATE = "enddate" ;
    static final String BBOX = "bbox" ;
    static final String OUTPUT = "Local Path to Result";

    @Override
    public Class<?> getInputDataType(String identifier) {
        if (identifier.equalsIgnoreCase(INPUT)) {
            return LiteralStringBinding.class;
            //return GTVectorDataBinding.class;
        }
        return null;
    }

    @Override
    public Class<?> getOutputDataType(String identifier) {
        if (identifier.equalsIgnoreCase(OUTPUT)) {
            return LiteralStringBinding.class;
            //return GTVectorDataBinding.class;
        }
        return null;
    }

    @Override
    public List<String> getInputIdentifiers() {
        List<String> list = new ArrayList<String>();
        list.add(INPUT);
        return list;
    }

    @Override
    public List<String> getOutputIdentifiers() {
        List<String> list = new ArrayList<String>();
        list.add(OUTPUT);
        return list;
    }

    @Override
    public Map<String, IData> run(Map<String, List<IData>> inputData)
            throws ExceptionReport {

        //check that all necessary inputdata is present. 
        if (inputData == null || !inputData.containsKey(INPUT)) {
            throw new RuntimeException(
                    "Error while allocating input parameters");
        }
        List<IData> dataList = inputData.get(INPUT);
        if (dataList == null || dataList.size() != 1) {
            throw new RuntimeException(
                    "Error while allocating input parameters");
        }
        //extract "startDate", "endDate" and "bbox" from the request string
        IData firstInputData = (IData)dataList.get(0);
        String datastring = ((LiteralStringBinding) firstInputData).getPayload();       
        
        String[] dataArray = datastring.split(",");
        //order of parameters is :startDate,endDate,bbox
        String start = dataArray[0];
        String end = dataArray[1];
        DateFormat formatter = new SimpleDateFormat("YYYY/MM/DD:hh:mm:ss.sss");
        Date date = null;
        try {
            date = formatter.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);

        try {
            date = formatter.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(date);
        
       String bbox = dataArray[2];
//        String[] bboxArray = bbox.split("|");
//        Point2D p1 = new Point2D(Double.parseDouble(bboxArray[0].split(",")[0]), Double.parseDouble(bboxArray[0].split(",")[1]));
//        Point2D p2 = new Point2D(Double.parseDouble(bboxArray[1].split(",")[0]), Double.parseDouble(bboxArray[1].split(",")[1]));
//        Rectangle2D rectangle = new Rectangle2D(p1, p2);
        HashMap<String, String> ap = new HashMap();
        Core core = Core.getInstance();
        java.io.File resultFile = core.request(startDate, endDate, bbox, ap, Core.PROCESSING_NONE);

        //create a feature collection and put the resultfeature in it. 
        class Out implements IData {

            File file;

            public Out(File file) {
                this.file = file;
            }

            @Override
            public Object getPayload() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Class<?> getSupportedClass() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        }
        IData fOut = new Out(resultFile);
        // to create the standard output hashmap which holds the name
        //of the output identifier and an IData object.In our case we create a GTVectordataBinding. 
        HashMap<String, IData> result = new HashMap<>();
        result.put(OUTPUT, fOut);
        return result;
    }

}
