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
