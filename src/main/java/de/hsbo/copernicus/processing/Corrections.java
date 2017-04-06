/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing;

import org.esa.snap.core.datamodel.Product;
//import sen2core
/**
 *
 * @author Andreas
 */
public class Corrections implements RasterProcessor {

    public Corrections(){
        
    }
    
    @Override
    public void compute(Product input, Product output) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //load Product
    //get nessesary bands from product to perform corrections
    //perform correstions, use sen2core
    //either extract result or pass corrected scene to caller 
    }
    
}
