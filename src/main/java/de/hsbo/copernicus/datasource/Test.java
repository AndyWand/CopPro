package de.hsbo.copernicus.datasource;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class Test {

    public static void main(String[] args) throws UnknownHostException, IOException, FactoryException, TransformException {

        //Test AWS-Adapter
        AdapterAws aws = AdapterAws.getInstance();
        Calendar start = new GregorianCalendar(2016,10, 8, 0, 0);
        //Calandar c = new GregorianCalendar 
        Calendar end = new GregorianCalendar(0, 0, 0, 0, 0);
        HashMap h = new HashMap();
        h.put("band","B01");
        Rectangle2D bbox = new Rectangle2D.Double(21.309444, -147.916861, 100, 100);
                
        File file = aws.request(start,end,bbox,h);
        System.out.println(file.getPath());
         //
//        aws.download("http://sentinel-s2-l1c.s3.amazonaws.com/tiles/10/S/DG/2015/12/7/0/B01.jp2",
//                "D:\\"); //System.out.println(aws.isOnline()); //InetAddress Ip =
//        InetAddress.getByName("http://sentinel-s2-l1c.s3-website.eu-central-1.amazonaws.com/#tiles");
        //System.out.println(Ip);
        // String b = AdapterCodede.handleXML(inputLine);
    }

}
