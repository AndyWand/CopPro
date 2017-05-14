package de.hsbo.copernicus.processing.processors;

import org.esa.snap.core.datamodel.Product;

/**
 *
 * @author Andreas
 */
public interface RasterProcessorInterface extends ProcessorInterface {

    /**
     *
     * @param input
     * @param output
     */
    @Override
    public void compute(Product input, Product output);
}
