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

public class TestDatasource {

    public static void mainoff(String[] args) throws UnknownHostException, IOException, FactoryException, TransformException, InterruptedException {

        Rectangle2D bbox = new Rectangle2D(new Point2D(-4.53, 29.85), new Point2D(-4.53, 29.85));
        //System.out.println(bbox.vertices());        
        //AdapterCodede a = (AdapterCodede)AdapterCodede.getInstance();
        //System.out.println();        

        //Test AWS-Adapter  
        //AdapterAws aws = AdapterAws.getInstance();
        AbstractAdapter codede = AdapterCodede.getInstance();
        Calendar start = new GregorianCalendar(2016, 10, 8, 0, 0);
        //Calandar c = new GregorianCalendar 
        Calendar end = new GregorianCalendar(2016, 11, 7, 0, 0);
        GregorianCalendar diff = new GregorianCalendar();
        long timediff = end.getTimeInMillis() - start.getTimeInMillis();
        diff.setTimeInMillis(timediff);
        HashMap h = new HashMap();
        h.put("band", "B01");
        h.put("credentials", "YW53YTpuZGw/M2hzNyElQVQ=");

        codede.setQuery(start, end, bbox, h);
        Thread t1 = new Thread(codede);
        t1.start();
        t1.join();
        System.out.println(codede.getResult());
        //System.out.println(diff.get(Calendar.DAY_OF_MONTH)+"-"+diff.get(Calendar.MONTH)+"-"+diff.get(Calendar.YEAR));

        //File file = aws.request(start,end,bbox,h);
        //System.out.println(file.getPath());
        // Core c = Core.getInstance();        
        // File result = c.request(start, end, bbox, h, "correction");
        // System.out.println(result.getPath());
        //
//        aws.download("http://sentinel-s2-l1c.s3.amazonaws.com/tiles/10/S/DG/2015/12/7/0/B01.jp2",
//                "D:\\"); //System.out.println(aws.isOnline()); //InetAddress Ip =
//        InetAddress.getByName("http://sentinel-s2-l1c.s3-website.eu-central-1.amazonaws.com/#tiles");
        //System.out.println(Ip);
        // String b = AdapterCodede.handleXML(inputLine);
    }

    public static void main(String[] args) throws UnknownHostException, IOException, FactoryException, TransformException, InterruptedException {
        Downloader d = new Downloader();
        HashMap<String,String> parameterValues = new HashMap<>();
        parameterValues.put("bbox", "8.334,2324.424,424.424,244.49");        
        parameterValues.put("httpAccept=", "application/atom+xml");
        System.out.println(d.encode(parameterValues));
    }
}
