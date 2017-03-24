package de.hsbo.copernicus.datasource;

import java.awt.Rectangle;
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

    public static File request(Calendar startDate, Calendar endDate, Rectangle bbox,
            HashMap<String, String> additionalParameter) {

        
        File file = null;
        Adapter source = AdapterFactory.getInstance().getAdapter();
        try {
            source.query(startDate, endDate, bbox, additionalParameter);
        } catch (IOException ex) {
            Logger.getLogger(DataSourceFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }    
}
