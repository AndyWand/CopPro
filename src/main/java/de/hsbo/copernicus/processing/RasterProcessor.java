/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing;

import java.io.File;

/**
 *
 * @author Andreas
 */
public interface RasterProcessor extends Processor{
    
    public void compute(File input, File output);
}