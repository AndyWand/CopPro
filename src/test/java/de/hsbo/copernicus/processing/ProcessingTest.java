package de.hsbo.copernicus.processing;

import de.hsbo.copernicus.processing.processors.correction.Corrections;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;

/**
 *
 * @author Andreas Wandert
 */
public class ProcessingTest {

    public static void main(String[] args) throws InterruptedException, IOException {
//        Rectangle2D bbox = new Rectangle2D(new Point2D(5.0873285, 45.730804), new Point2D(5.0873285, 45.730804));
//        Calendar start = new GregorianCalendar(2016, 10, 15, 0, 0);
//        System.out.println();
//
//        Calandar c = new GregorianCalendar 
//        Calendar end = new GregorianCalendar(2016, 12, 7, 0, 0);
//        HashMap<String, String> ap = new HashMap<>();
//        ap.put("credentials", "anwa:ndl?3hs7!%AT");
//        ap.put("band", "BO1");
//        //Download the data 
//        DataSourceFacade facade = new DataSourceFacade();
//        Product request = facade.request(start, end, bbox, ap);
//
//        //process them
//        File location = new File("C:\\Users\\Andreas\\Downloads\\S2A_MSIL1C_20170403T104021_N0204_R008_T32ULC_20170403T104138.SAFE");
//        location.getAbsoluteFile();
//        Sen2CorAdapter ad = new Sen2CorAdapter(location, Sen2CorAdapter.RESOLUTION_60);
//       // String query = ad.defineQuery();
//        System.out.println(location.getAbsoluteFile());
//        //System.out.println(ad.execCmd(query));
//        Thread t = new Thread(ad);
//        t.start();
//        while (t.isAlive()){
//        System.out.println(ad.getProgress());
//        }
//        t.join();
//        System.out.println(ad.getResult());
        //DataSourceFacade facade = new DataSourceFacade();
        //Product p = facade.request(start, end, bbox, ap);
        // AbstractAdapter hub = AdapterCodede.getInstance();
        // hub.setQuery(start, end, bbox, ap);
//        Thread t1 = new Thread(hub);
//        t1.start();
//        t1.join();
//        File result = hub.getResult();
       // System.out.println(p);

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
        //correctionTest();
        productReaderTest();
    }

    private static void correctionTest() {
        Corrections c = new Corrections();
        Product in  = new Product("in", Product.GEOMETRY_FEATURE_TYPE_NAME);
        File inFile = new File("C:\\Users\\Andreas\\Downloads\\S2A_MSIL1C_20170403T104021_N0204_R008_T32ULC_20170403T104138.SAFE");
        in.setFileLocation(inFile);
        Product out = new Product("out", Product.GEOMETRY_FEATURE_TYPE_NAME);
        File outFile = new File("C:\\Users\\Andreas\\Downloads\\S2A_MSIL1C_20170403T104021_N0204_R008_T32ULC_20170403T104138.SAFE\\ResultSet");
        out.setFileLocation(outFile);        
        c.compute(in,out);
    }
    
    /**
     * **
     * public int getIndex (ProcessorInterface p){ return processors. }
     *
     */
    private static void productReaderTest() {
        String file = "C:\\Users\\Andreas\\Downloads\\S2A_MSIL2A_20170403T104021_N0204_R008_T32ULC_20170403T104138.SAFE\\manifest.safe";
        Product product = null;
        try {
            product = ProductIO.readProduct(file);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(product);

    }
    
   
}
