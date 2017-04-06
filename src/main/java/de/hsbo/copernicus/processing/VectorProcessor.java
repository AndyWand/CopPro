/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing;

import java.io.File;
import org.esa.snap.core.datamodel.Product;

/**
 * This interface is to provide methods methods with a vector result
 *
 * @author Andreas
 */
public interface VectorProcessor extends Processor {

    @Override
    public default void compute(Product input, Product output) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
