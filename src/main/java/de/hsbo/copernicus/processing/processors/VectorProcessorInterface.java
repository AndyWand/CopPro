package de.hsbo.copernicus.processing.processors;

import org.esa.snap.core.datamodel.Product;

/**
 * This interface is to provide methods methods with a vector result
 *
 * @author Andreas
 */
public interface VectorProcessorInterface extends ProcessorInterface {

    //for java 1.8
//    @Override
//    public default void compute(Product input, Product output) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
    //for java 1.7
    @Override
    public void compute(Product input, Product output);
    

}
