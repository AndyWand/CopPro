package de.hsbo.copernicus.datasource;

import de.hsbo.copernicus.processing.Core;
import math.geom2d.polygon.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.esa.s2tbx.dataio.jp2.JP2ProductReaderPlugin;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.datamodel.Product;

/**
 *
 * @author Andreas Wandert This Class is to present data, requests and
 * communication of the datasource package to the public. It also implements an
 * decision algorithm to choose one of three sources.
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

        source.setQuery(startDate, endDate, bbox, additionalParameter, resultFile);
        /**
         * instanciate a new Thread and use this to execute the source-Adapter
         */
        Thread t = new Thread(source);
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(DataSourceFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        resultFile = source.getResult();
        resultProduct = read(resultFile);

        return resultProduct;
    }

    /**
     *
     * @param file
     * @return
     */
    public Product read(File file) {
        Product product = null;
        JP2ProductReaderPlugin readerPlugIn = new JP2ProductReaderPlugin();
        manager.addReaderPlugIn(readerPlugIn);

        try {
            product = ProductIO.readProduct(file);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }

        return product;
    }
}
