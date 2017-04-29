package de.hsbo.copernicus.processing;

import de.hsbo.copernicus.processing.Processors.correction.Corrections;
import de.hsbo.copernicus.processing.processors.*;

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
import org.esa.snap.core.dataio.*;
import org.esa.snap.core.dataio.rgb.ImageProductReaderPlugIn;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.s2tbx.dataio.jp2.*;
import org.openide.util.Exceptions;

/**
 * This class manages the communication to the portals and the processors. It's the main entry point
 * for all external applications. The loaded data ate managed internally in a PDM.
 * <link>https://senbox.atlassian.net/wiki/display/SNAP/Product+Data+Model</link>
 * To get a list of currently available processors use getProcessors. Or add a new processor by
 * using addProcessor.
 *
 * @author Andreas Wandert
 */
public class Core {

    /**
     * processors includes a set of available Processors idantified by an integer >1 "0" is reserved
     * for "No ProcessorInterface"
     */
    private static Core instance;
    //processors includes a set of available Processors idantified by an integer >1
    //"0" is reserved for "preprocessing only"
    private HashMap<String, ProcessorInterface> processors;
    //ProductIOManager
    private ProductIOPlugInManager manager;
    private static final String DEFAULT_PATH = "./result";
    private static final String DEFAULT_OUTPUT_FORMAT = "tiff";

    public static final String PROCESSING_NONE = "none";
    public static final String PROCESSING_CORRECTION = "correction";
    public static final String PROCESSING_NDVI = "ndvi";

    /**
     *
     * instaciate with default set of Processors 0:preprocessing only, 1:NDVI
     *
     */
    private Core() {
        initProcessors();
        ProcessorInterface pre = new Corrections();
        ProcessorInterface ndvi = new NDVI();
        processors.put(Core.PROCESSING_CORRECTION, pre);
        processors.put(Core.PROCESSING_NDVI, ndvi);
        manager = ProductIOPlugInManager.getInstance();
    }

    /**
     *
     * @param customProcessors HashMap with a set of Processors defined by the user
     *
     */
    private Core(HashMap customProcessors) {
        initProcessors();
        processors = customProcessors;
        manager = ProductIOPlugInManager.getInstance();
    }

    /**
     * Methods to ensure that Core is a Singelton Creates an instance of class core including a
     * default set of processors
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
     * Creates an instance of class core by using a HashMap of customProcessors to make them
     * available for future processing
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
    public File request(Calendar startDate, Calendar endDate, Rectangle2D bbox, HashMap additionalParameter, String type
    ) {
        DataSourceFacade facade = new DataSourceFacade();
        Product input = facade.request(startDate, endDate, bbox, additionalParameter);
        Product outputProduct = null;
        Product output = null;
        switch (type) {
            // if type is "none" return DataProduct without any processing
            case Core.PROCESSING_NONE: {
                return write(input);
            }
            case Core.PROCESSING_CORRECTION: {
                //perform preprocessing only
                ProcessorInterface pro = processors.get(Core.PROCESSING_CORRECTION);
                //perform preprocessing
                pro.compute(input, output);
                return write(output);
            }
            default: {
                ProcessorInterface pro = processors.get(Core.PROCESSING_CORRECTION);
                //perform preprocessing
                pro.compute(input, output);
                //get an other processor for processing
                if (processors.containsKey(type)) {
                    pro = processors.get(type);
                    //perform preprocessing
                    pro.compute(output, outputProduct);

                    return write(outputProduct);
                }
                return write(output);
            }
        }
    }

    /**
     * add a new processor to the processors set returns the index of the new processor
     *
     * @param newProcessor
     */
    public void addProcessor(ProcessorInterface newProcessor) {
        processors.put(newProcessor.NAME, newProcessor);
    }

    /**
     * add new processors given by a HashMap returns a list of new indexes
     *
     * @param newProcessors
     * @return
     */
    public Set<String> addProcessors(HashMap newProcessors) {
        processors.putAll(newProcessors);
        return processors.keySet();
    }

    /**
     * add new processors given by an array of Processors returns a list containing names of new processors
     *
     * @param newProcessors
     * @return
     */
    public Set<String> addProcessors(ProcessorInterface[] newProcessors) {
        Set<String> names = new HashSet();
        for (ProcessorInterface p : newProcessors) {
            processors.put(p.NAME, p);
            names.add(p.NAME);
        }
        return names;
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
     * @param type
     * @return
     */
    public ProcessorInterface getProcessor(String type) {
        return processors.get(type);
    }

    /**
     * **
     * public int getIndex (ProcessorInterface p){ return processors. }
     *
     */
    public void test() {
        String file = "deinPath";
        try {
            Product readProduct = ProductIO.readProduct(file);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @param file
     * @return
     * @deprecated
     */
    private Product read(File file) {
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

    /**
     *
     * @param product
     * @return
     *
     */
    private File write(Product product) {
        try {
            ProductIO.writeProduct(product, Core.DEFAULT_PATH, Core.DEFAULT_OUTPUT_FORMAT);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        File file = new File(Core.DEFAULT_PATH + "." + Core.DEFAULT_OUTPUT_FORMAT);
        return file;
    }

    /**
     * This method is to load a received file i to SNAPs PDM
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
