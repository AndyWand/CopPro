/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing;

/**
 *
 * @author Andreas
 */
@Deprecated
public class ProcessorFactory {

    public ProcessorInterface makeRasterProcessor(String type) {

        if (type == "ndvi") {
            ProcessorInterface a = new NDVI();

            return a;
        } else {
            return null;
        }
    }

}
