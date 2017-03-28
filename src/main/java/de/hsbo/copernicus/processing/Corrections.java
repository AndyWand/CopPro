/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing;

import java.io.File;
import org.esa.snap.core.datamodel.Product;

/**
 *
 * @author Andreas
 */
public class Corrections implements RasterProcessor {

    public Corrections(){
        
    }
    
    @Override
    public void compute(Product input, File output) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
