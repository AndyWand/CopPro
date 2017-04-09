/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing.Processors.correction;

import static de.hsbo.copernicus.processing.Processors.correction.Sen2CorAdapter.ATHMOSPERIC_GIPP;
import static de.hsbo.copernicus.processing.Processors.correction.Sen2CorAdapter.RESOLUTION_10;
import de.hsbo.copernicus.datasource.DataSourceFacade;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import math.geom2d.Point2D;
import math.geom2d.polygon.Rectangle2D;
import org.esa.snap.core.datamodel.Product;

/**
 *
 * @author Andreas Wandert
 */
public class Test {

    public static void main(String[] args) throws InterruptedException, IOException {
//        Rectangle2D bbox = new Rectangle2D(new Point2D(5.0873285, 45.730804), new Point2D(5.0873285, 45.730804));
//        Calendar start = new GregorianCalendar(2016, 10, 15, 0, 0);
//        System.out.println();
//
//        //Calandar c = new GregorianCalendar 
//        Calendar end = new GregorianCalendar(2016, 12, 7, 0, 0);
//        HashMap<String, String> ap = new HashMap<>();
//        ap.put("band", "BO1");
//        //Download the data 
//        DataSourceFacade facade = new DataSourceFacade();
//        Product request = facade.request(start, end, bbox, ap);
//
//        //process them
        File location = new File("C:\\Users\\Andreas\\Downloads\\S2A_MSIL1C_20170403T104021_N0204_R008_T32ULC_20170403T104138.SAFE");
        location.getAbsoluteFile();
        Sen2CorAdapter ad = new Sen2CorAdapter(location, Sen2CorAdapter.RESOLUTION_60);
       // String query = ad.defineQuery();
        System.out.println(location.getAbsoluteFile());
        //System.out.println(ad.execCmd(query));
        Thread t = new Thread(ad);
        t.start();
        while (t.isAlive()){
        System.out.println(ad.getProgress());
        }
        t.join();
        System.out.println(ad.getResult());
//        String cmd = "L2A_Process --help";
//        Process runtime = Runtime.getRuntime().exec(cmd);
//        InputStream input = runtime.getInputStream();
//        InputStreamReader inreader = new InputStreamReader(input);
//        BufferedReader buffreader = new BufferedReader(inreader);
//        java.util.Scanner s = new java.util.Scanner(input).useDelimiter("\\A");
//        String st;
//        while ((st=buffreader.readLine())!=null) {
//            System.out.println(s.hasNext() ? s.next() : "");
//            System.out.println();
//        }
    }
}
