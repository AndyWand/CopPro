package de.hsbo.copernicus.processing.processors.correction;

import de.hsbo.copernicus.processing.processors.*;
import org.esa.snap.core.datamodel.Product;
import org.openide.util.Exceptions;

import static java.lang.System.out;

/**
 *
 * @author Andreas
 */
public class Corrections implements RasterProcessorInterface {

    public Corrections() {
    }

    @Override
    public synchronized void compute(Product input, Product output) {
        //perform correstions, use sen2core; pass location of input product
        Sen2CorAdapter sen2cor = new Sen2CorAdapter(input.getFileLocation());
        //either extract result or pass corrected scene to caller 
        Thread t1 = new Thread(sen2cor);
        t1.start();
        Double lastProgress = 0.0;
        while (!sen2cor.isDone()) {
            try {
                this.wait(100);
                if (sen2cor.getProgress()!= lastProgress) {
                    out.println("Progress: " + sen2cor.getProgress());
                    lastProgress = sen2cor.getProgress();
                }
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        try {
            t1.join();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }

        sen2cor.getResult();
        //TODO: load result into PDM
        //output = sen2cor.getResult();

    }

}
