package de.hsbo.copernicus.processing;

import de.hsbo.copernicus.datasource.DataSourceFacade;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;

/**
 * This class manages the communication to the portals and the processors. It's
 * the main entry point for all external applications. The loaded data ate
 * managed internally in a PDM.
 * <link>https://senbox.atlassian.net/wiki/display/SNAP/Product+Data+Model</link>
 * To get a list of currently available processors use getProcessors.
 * Or add a new processor by using addProcessor.
 * 
 * @author Andreas Wandert
 */
public class Core {

    /**
     * processors includes a set of available Processors idantified by an
     * integer >1 "0" is reserved for "No Processor"
     */
    private Core instance;
    //processors includes a set of available Processors idantified by an integer >1
    //"0" is reserved for "No Processor"
    HashMap<Integer, Processor> processors;

    //instaciate with default set of Processors
    //0:None, 1:NDVI
    private Core() {
        Processor ndvi = new NDVI();
        processors.put(1, ndvi);
    }

    /**
     *
     * @param customProcessors HashMap with a set of Processors defined by the
     * user
     *
     */
    private Core(HashMap customProcessors) {
        processors = customProcessors;
    }

    /**
     * Methods to ensure that Core is a Singelton
     *
     * @return Core instance
     */
    public Core getInstance() {
        if (instance == null) {
            return new Core();
        } else {
            return this;
        }
    }

    public Core getInstance(HashMap customProcessors) {
        if (instance == null) {
            return new Core(customProcessors);
        } else {
            return this;
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
    public File request(Calendar startDate, Calendar endDate, Rectangle bbox, HashMap additionalParameter, int type
    ) {
        File input = DataSourceFacade.request(startDate, endDate, bbox, additionalParameter);
        File output;
        if (type > 0) {
            //instanciate an input and an output file

            output = new File("");
            //call the compute method to start computation of an output file
            Processor pro = processors.get(type);
            // pro.compute(input, output);

            return output;
        } else {
            return input;
        }

    }

    /**
     * add a new processor to the processors set returns the index of the new
     * processor
     */
    public int addProcessor(Processor newProcessor) {
        int newIndex = processors.size();
        processors.put(newIndex, newProcessor);
        return newIndex;
    }

    /**
     * add new processors given by a HashMap returns a list of new indexes
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

    public HashMap getProcessors() {
        return this.processors;
    }

    public Processor getProcessor(int index) {
        return processors.get(index);
    }

    /**
     * should be implemetd in later iterations
     *
     * @param p
     * @return
     */
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

    public void loadDataToPDM(File file) {
        Product product;
        try {
            product = ProductIO.readProduct(file);
        } catch (IOException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }

        return;
    }

}
