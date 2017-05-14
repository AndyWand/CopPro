package de.hsbo.copernicus.datasource;

import de.hsbo.copernicus.datasource.AbstractAdapter;
import de.hsbo.copernicus.datasource.AbstractAdapter;
import de.hsbo.copernicus.datasource.AdapterCodede;
import de.hsbo.copernicus.datasource.AdapterCodede;
import de.hsbo.copernicus.datasource.AdapterFactory;
import de.hsbo.copernicus.datasource.AdapterFactory;
import de.hsbo.copernicus.datasource.DataSourceFacade;
import de.hsbo.copernicus.datasource.DataSourceFacade;
import de.hsbo.copernicus.datasource.Downloader;
import de.hsbo.copernicus.datasource.Downloader;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import math.geom2d.polygon.Rectangle2D;
import math.geom2d.Point2D;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.openide.util.Exceptions;

public class TestDatasource {

    public static void mainoff(String[] args) throws UnknownHostException, IOException, FactoryException, TransformException, InterruptedException {

        Rectangle2D bbox = new Rectangle2D(new Point2D(-4.53, 29.85), new Point2D(-4.53, 29.85));

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
    }

    public static void main(String[] args) throws UnknownHostException, IOException, FactoryException, TransformException, InterruptedException {
//        adapterFactoryTest();
//        downloaderTest();
        facadeTest();
//        productReadTest();
    }

    public static void downloaderTest() {
        System.out.println("\n####Testing Downloader#####");
        System.out.println("\n##Encoding###");
        Downloader d = new Downloader();
        HashMap<String, String> parameterValues = new HashMap<>();
        parameterValues.put("bbox", "8.334,2324.424,424.424,244.49");
        parameterValues.put("httpAccept=", "application/atom+xml");
        System.out.println(d.encode(parameterValues));
    }

    public static void adapterFactoryTest() {
        System.out.println("####Testing AdapterFactory#####\n");
        System.out.println("##Get Adapter by order###");
        AbstractAdapter adapter = null;
        AdapterFactory f = null;
        try {
            f = AdapterFactory.getInstance();
            adapter = f.getAdapter();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        System.out.println(adapter.getName());

        System.out.println("\n##Get Adapater by type name ###");
        System.out.print("Sentinel-2 on AWS: ");
        if (f.getAdapter(AdapterFactory.AWS) != null) {
            System.out.println("passed!!");
        }
        System.out.print("CODE-DE: ");
        if (f.getAdapter(AdapterFactory.CODEDE) != null) {
            System.out.println("passed!!");
        }
        System.out.print("SciHub: ");
        if (f.getAdapter(AdapterFactory.SCIHUB) != null) {
            System.out.println("passed!!");
        }
    }

    private static void facadeTest() {
        DataSourceFacade facade = new DataSourceFacade();
        File safeFolder = new File("C:\\Users\\Andreas\\Downloads\\S2A_MSIL1C_20170403T104021_N0204_R008_T32ULC_20170403T104138.SAFE");
        Product product = facade.read(safeFolder);
        Band[] bands = product.getBands();
        for (Band band : bands) {
            System.out.println("Bandname: " + band.getName());
        }
        System.out.println("Product location is: " + product.getFileLocation());

        System.out.println(product);
    }

    public static Product readTest() {
        DataSourceFacade facade = new DataSourceFacade();
        File safeFolder = new File("C:\\Users\\Andreas\\Downloads\\S2A_MSIL1C_20170403T104021_N0204_R008_T32ULC_20170403T104138.SAFE");
        System.out.println("Reading product...");
        Product product = facade.read(safeFolder);               

        return product;
    }

    private static void productReadTest() {
        DataSourceFacade facade = new DataSourceFacade();
        ArrayList list = facade.readXML(new File("C:\\Users\\Andreas\\Downloads\\S2A_MSIL1C_20170403T104021_N0204_R008_T32ULC_20170403T104138.SAFE\\MTD_MSIL1C.xml"));
//        System.out.println("Bandlist: "+list);
    }
}
