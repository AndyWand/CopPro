package de.hsbo.copernicus.processing;

import de.hsbo.copernicus.datasource.*;
import math.geom2d.polygon.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.FileLoadDescriptor;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.rgb.ImageProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.FileUtils;

/**
 * This class manages the communication to the portals and the processors. It's
 * the main entry point for all external applications. The loaded data ate
 * managed internally in a PDM.
 * <link>https://senbox.atlassian.net/wiki/display/SNAP/Product+Data+Model</link>
 * To get a list of currently available processors use getProcessors. Or add a
 * new processor by using addProcessor.
 *
 * @author Andreas Wandert
 */
public class Core {

    /**
     * processors includes a set of available Processors idantified by an
     * integer >1 "0" is reserved for "No Processor"
     */
    private static Core instance;
    //processors includes a set of available Processors idantified by an integer >1
    //"0" is reserved for "preprocessing only"
    private HashMap<Integer, Processor> processors;

    /**
     *
     * instaciate with default set of Processors 0:preprocessing only, 1:NDVI
     *
     */
    private Core() {
        initProcessors();
        Processor pre = new Corrections();
        Processor ndvi = new NDVI();
        processors.put(0, pre);
        processors.put(1, ndvi);
    }

    /**
     *
     * @param customProcessors HashMap with a set of Processors defined by the
     * user
     *
     */
    private Core(HashMap customProcessors) {
        initProcessors();
        processors = customProcessors;
    }

    /**
     * Methods to ensure that Core is a Singelton
     *
     * @return Core instance
     */
    public static Core getInstance() {
        if (instance == null) {
            instance = new Core();
            return instance;
        } else {
            return instance;
        }
    }

    /**
     *
     * @param customProcessors
     * @return
     */
    public static Core getInstance(HashMap customProcessors) {
        if (instance == null) {
            instance = new Core(customProcessors);
            return instance;
        } else {
            return instance;
        }
    }

    /**
     * this is to handle requests called by the user
     *
     * @param startDate
     * @param endDate
     * @param bbox a rectangle to specify the area of requested image
     * @param additionalParameter
     * @param type
     * @return an processed image if type is not zero else it´s a raw L1-raster
     */
    public File request(Calendar startDate, Calendar endDate, Rectangle2D bbox, HashMap additionalParameter, int type
    ) {
        DataSourceFacade facade = new DataSourceFacade();
        File input = facade.request(startDate, endDate, bbox, additionalParameter);
        File output = new File("");
        Processor pre = processors.get(0);

        // if type is -1 return DataProduct without any processing
        if (type == -1) {
            return input;
        } else if (type > 0) {
            //instanciate an input and an output file

            //call the compute method to start computation of an output file
            Processor pro = processors.get(type);
            //perform preprocessing
           //not yet supported pre.compute(this.file2pdm(input), output);
            pro.compute(this.file2pdm(output), output);

            return output;
        } else {
            //perform preprocessing only
            //not yet supported pre.compute(this.file2pdm(input), output);
            return output;
        }

    }

    /**
     * add a new processor to the processors set returns the index of the new
     * processor
     *
     * @param newProcessor
     * @return
     */
    public int addProcessor(Processor newProcessor) {
        int newIndex = processors.size();
        processors.put(newIndex, newProcessor);
        return newIndex;
    }

    /**
     * add new processors given by a HashMap returns a list of new indexes
     *
     * @param newProcessors
     * @return
     */
    public Set<Integer> addProcessors(HashMap newProcessors) {
        processors.putAll(newProcessors);
        return processors.keySet();
    }

    /**
     * add new processors given by an array of Processors return an list of new
     * indexes
     *
     * @param newProcessors
     * @return
     */
    public Set<Integer> addProcessors(Processor[] newProcessors) {
        Set<Integer> newIndexes = new HashSet();
        for (Processor p : newProcessors) {
            int newIndex = processors.size();
            processors.put(newIndex, p);
            newIndexes.add(newIndex);
        }
        return newIndexes;
    }

    /**
     *
     * @return
     */
    public HashMap getProcessors() {
        return this.processors;
    }

    /**
     *
     * @param index
     * @return
     */
    public Processor getProcessor(int index) {
        return processors.get(index);
    }

    /**
     * *
     * public int getIndex (Processor p){ return processors. }
     *
     */
    public void test() {
        String file = "deinPath";
        try {
            Product readProduct = ProductIO.readProduct(file);
//            readProduct.g
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param file
     * @return
     */
    public Product file2pdm(File file) {
        Product product = null;
        try {
            product = ProductIO.readProduct(file);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }

        return product;
    }

    /**
     * This method is to load an received file i to SNAPs PDM
     *
     * @param file
     * @return
     */
    private Product file2pdm2(File file) {
        RenderedOp sourceImage;
        sourceImage = FileLoadDescriptor.create(file.getPath(), null, true, null);
        Product product = new Product(FileUtils.getFilenameWithoutExtension(file), ImageProductReaderPlugIn.FORMAT_NAME, sourceImage.getWidth(), sourceImage.getHeight());

        return product;
    }

    private void initProcessors() {
        this.processors = new HashMap<>();
    }

}
