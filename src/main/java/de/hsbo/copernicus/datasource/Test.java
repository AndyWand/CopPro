package de.hsbo.copernicus.datasource;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class Test {

    public static void main(String[] args) throws UnknownHostException, IOException, FactoryException, TransformException {

        //Test AWS-Adapter
        AdapterAws aws = AdapterAws.getInstance();
        String[] result = aws.transform(21.309444, -157.916861);
        System.out.println(result[0]+","+result[1]+","+result[2]); //
//        aws.download("http://sentinel-s2-l1c.s3.amazonaws.com/tiles/10/S/DG/2015/12/7/0/B01.jp2",
//                "D:\\"); //System.out.println(aws.isOnline()); //InetAddress Ip =
//        InetAddress.getByName("http://sentinel-s2-l1c.s3-website.eu-central-1.amazonaws.com/#tiles");
        //System.out.println(Ip);
        // String b = AdapterCodede.handleXML(inputLine);
    }

}
