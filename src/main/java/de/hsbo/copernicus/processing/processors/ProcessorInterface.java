/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing.processors;

import org.esa.snap.core.datamodel.Product;

/**
 *
 * @author Andreas
 */
public interface ProcessorInterface {

    final static String NAME = "";

    public void compute(Product input, Product output);

}
