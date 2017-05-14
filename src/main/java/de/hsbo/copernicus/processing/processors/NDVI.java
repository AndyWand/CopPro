package de.hsbo.copernicus.processing.processors;

import com.bc.ceres.core.NullProgressMonitor;
import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.ndvi.NdviOp;

/**
 *
 * @author Andreas This clase is to compute the NDVI out of two input rastert
 * only
 */
public class NDVI implements RasterProcessorInterface {

    public static final String NAME = "ndvi";

    public NDVI() {        
    }

    public void compute(Product input, Product output) {
        final NdviOp ndvi = new NdviOp();
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
