package de.hsbo.copernicus.datasource;

import de.hsbo.copernicus.processing.Core;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import math.geom2d.polygon.Rectangle2D;
import math.geom2d.Point2D;

public class Test {

    public static void main(String[] args) throws UnknownHostException, IOException, FactoryException, TransformException {
        Point2D p1 = new Point2D(21.309444, -148.916861);
        Point2D p2 = new Point2D(21.309444, -147.916861);
        Rectangle2D bbox = new Rectangle2D(p1, p2);
        
       //System.out.println(bbox.vertices());
        
        AdapterCodede a = (AdapterCodede)AdapterCodede.getInstance();
        System.out.println();
        

        //Test AWS-Adapter  
        //AdapterAws aws = AdapterAws.getInstance();
        Calendar start = new GregorianCalendar(2016,10, 8, 0, 0);
        //Calandar c = new GregorianCalendar 
        Calendar end = new GregorianCalendar(2016, 12, 7, 0, 0);
        GregorianCalendar diff = new GregorianCalendar();
        long timediff = end.getTimeInMillis() - start.getTimeInMillis();
        diff.setTimeInMillis(timediff);
        HashMap h = new HashMap();
        h.put("band","B01");
        System.out.println(diff.get(Calendar.DAY_OF_MONTH)+"-"+diff.get(Calendar.MONTH)+"-"+diff.get(Calendar.YEAR));
        
        
                
        //File file = aws.request(start,end,bbox,h);
        //System.out.println(file.getPath());
        
        Core c = Core.getInstance();
        //File result = c.request(start, end, bbox, h, 0);
          //      System.out.println(result.getPath());
         //
//        aws.download("http://sentinel-s2-l1c.s3.amazonaws.com/tiles/10/S/DG/2015/12/7/0/B01.jp2",
//                "D:\\"); //System.out.println(aws.isOnline()); //InetAddress Ip =
//        InetAddress.getByName("http://sentinel-s2-l1c.s3-website.eu-central-1.amazonaws.com/#tiles");
        //System.out.println(Ip);
        // String b = AdapterCodede.handleXML(inputLine);
    }

}
