package de.hsbo.copernicus.datasource;

import com.bc.ceres.core.ProgressMonitor;
import de.hsbo.copernicus.processing.Core;
import math.geom2d.polygon.Rectangle2D;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.esa.s2tbx.dataio.jp2.JP2ProductReaderPlugin;
import org.esa.s2tbx.dataio.jp2.JP2ProductReader;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.main.GPT;
import org.esa.snap.core.util.SystemUtils;

/**
 *
 * @author Andreas Wandert This Class is to present data, requests and communication of the
 * datasource package to the public. It also implements an decision algorithm to choose one of three
 * sources.
 */
public class DataSourceFacade {

    private File resultFile;
    private Product resultProduct;
    private final ProductIOPlugInManager manager;

    public DataSourceFacade() {
        manager = ProductIOPlugInManager.getInstance();
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @param bbox
     * @param additionalParameter
     * @return
     */
    public Product request(Calendar startDate, Calendar endDate, Rectangle2D bbox, HashMap<String, String> additionalParameter) {

        AdapterFactory factory;
        factory = AdapterFactory.getInstance();
        AbstractAdapter source = factory.getAdapter(AdapterFactory.SCIHUB);

        source.setQuery(startDate, endDate, bbox, additionalParameter);
        /**
         * instanciate a new Thread and use this to execute the source-Adapter
         */
//        Thread t = new Thread(source);
//        t.start();
//        try {
//            t.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(DataSourceFacade.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        resultFile = source.getResult();

        resultFile = new File("C:\\Users\\Andreas\\Documents\\NetBeansProjects\\Copernicus\\S2A_MSIL1C_20170104T101402_N0204_R022_T32TPT_20170104T101405.SAFE");

        System.out.println("Filelocation is: " + resultFile.getAbsolutePath());
        resultProduct = read(resultFile);

        return resultProduct;
    }

    /**
     * Reads a file of format JP2 into SNAPs product data model File Object needs to point to the
     * .Safe folder
     *
     * @param file
     * @return
     *
     * this is for internal us only
     */
    private Product read(File folder) {
        SystemUtils.init3rdPartyLibs(GPT.class);
        final String FILTER_EXPERSSION = "MTD";

        //navigate to the .xml metadata file 
        //name of xml file is e.g. MTD_MSIL1C.xml
        File[] fileList = folder.listFiles();

        File xmlFile = null;
        for (File f : fileList) {
            System.out.println("Filelist: " + f.getName());
            if (f.isFile() && f.getName().contains(FILTER_EXPERSSION)) {
                xmlFile = f;
            }
        }
        Product product = null;
        JP2ProductReaderPlugin jp2ReaderPlugIn = new JP2ProductReaderPlugin();
//        BaseProductReaderPlugin sen2ReaderPlugIn = new BaseProductReaderPlugin();
        manager.addReaderPlugIn(jp2ReaderPlugIn);
        //       manager.addReaderPlugIn(sen2ReaderPlugIn);

        try {
            File test = new File("C:\\Users\\Andreas\\Documents\\NetBeansProjects\\"
                    + "Copernicus\\S2A_MSIL1C_20170104T101402_N0204_R022_T32TPT_20170104T101405.SAFE\\GRANULE\\L1C_T32TPT_A008027_20170104T101405\\IMG_DATA\\T32TPT_20170104T101402_B02.jp2");
            // System.out.println("Path to XML file: " + xmlFile);
           // product = ProductIO.readProduct(test);
            System.out.println(test);
           // JP2OpenJPEGDriverProductReaderPlugIn readerPlugin = new JP2OpenJPEGDriverProductReaderPlugIn();
           JP2ProductReaderPlugin readerPlugin = new JP2ProductReaderPlugin();
            JP2ProductReader reader = (JP2ProductReader)readerPlugin.createReaderInstance();
            ProductReader jpReader = ProductIO.getProductReaderForInput(test);
            product = reader.readProductNodes(test, null);
            // jpReader.readBandRasterData(b01, 0, 0, 0, 0, product, ProgressMonitor.NULL);
            //System.out.println("Product is: "+ProductIO.getProductReaderForInput(product).toString());
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Product " + product);
        return product;
    }
}
