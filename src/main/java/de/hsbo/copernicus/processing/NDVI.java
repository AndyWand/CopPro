/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing;

import com.bc.ceres.core.NullProgressMonitor;
import com.bc.ceres.core.ProgressMonitor;
import java.io.File;
import java.io.Writer;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.ndvi.NdviOp;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.rgb.ImageProductReader;
import org.esa.s2tbx.dataio.jp2.JP2ProductReaderPlugin;
import org.esa.s2tbx.dataio.jp2.*;
import org.esa.snap.core.dataio.ProductWriter;

/**
 *
 * @author Andreas This clase is to compute the NDVI out of two input rastert
 * only
 */
public class NDVI implements RasterProcessor {

    public static final String NAME = "ndvi";

    public NDVI() {

    }

    public void compute(Product input, Product output) {
        NdviOp ndvi = new NdviOp();
        //TODO Read file into a Processor
        ndvi.setSourceProduct(input);
        //pick proper bands from the product        
        Product target = new Product("ndvi", "indexRaster");
        ndvi.setTargetProduct(target);
        ProgressMonitor monitor = new NullProgressMonitor() {
            @Override
            public void done() {
                Product result = ndvi.getTargetProduct();
            }
        };
        ndvi.doExecute(monitor);

        //pass this to computeTile
        //write the target product into a file object
        output = target;

    }

}
