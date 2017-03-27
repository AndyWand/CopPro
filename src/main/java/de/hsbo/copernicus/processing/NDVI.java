/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing;

import com.bc.ceres.core.ProgressMonitor;
import java.io.File;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.ndvi.NdviOp;

/**
 *
 * @author Andreas This clase is to compute the NDVI out of two input rastert
 * only
 */
public class NDVI implements RasterProcessor {

    public static final String name = "ndvi";

    public void compute(Product input, File output) {
        NdviOp n = new NdviOp();
        //TODO Read file into a Product Reader
        //pick proper bands from the product 
        //pass this to computeTile
        

       // n.computeTile(targetBand, targetTile, ProgressMonitor.NULL);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
