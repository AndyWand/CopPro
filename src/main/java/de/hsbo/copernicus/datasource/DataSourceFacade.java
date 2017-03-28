package de.hsbo.copernicus.datasource;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andreas Wandert This Class is to present data, requests and
 * communication of the datasource package to the public. It also implements an
 * decision algorithm to choose one of three sources.
 */
public class DataSourceFacade {

    private File result;

    /**
     *
     * @param startDate
     * @param endDate
     * @param bbox
     * @param additionalParameter
     * @return
     */
    public File request(Calendar startDate, Calendar endDate, Rectangle2D bbox,
            HashMap<String, String> additionalParameter) {

        AdapterFactory factory;
        factory = AdapterFactory.getInstance();
        Adapter source = factory.getAdapter(AdapterFactory.AWS);

        source.setQuery(startDate, endDate, bbox, additionalParameter, result);
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
        result = source.getResult();

        return result;
    }

}
