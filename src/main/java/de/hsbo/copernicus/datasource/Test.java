package de.hsbo.copernicus.datasource;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Test{

	public static void main(String[] args) throws UnknownHostException, IOException {
		//Test AWS-Adapter
		Adapter aws;
            aws = AdapterAws.getInstance();
                aws.download("http://sentinel-s2-l1c.s3.amazonaws.com/tiles/10/S/DG/2015/12/7/0/B01.jp2", "D:\\");
		System.out.println(aws.isOnline());
                //InetAddress Ip = InetAddress.getByName("http://sentinel-s2-l1c.s3-website.eu-central-1.amazonaws.com/#tiles");
                //System.out.println(Ip);

                
               
	}
        

}
