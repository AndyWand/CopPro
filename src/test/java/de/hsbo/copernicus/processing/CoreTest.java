/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.copernicus.processing;

import de.hsbo.copernicus.datasource.TestDatasource;
import de.hsbo.copernicus.processing.processors.correction.Corrections;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.esa.snap.core.datamodel.Product;

import org.junit.Assert;
import org.openide.util.Exceptions;

/**
 *
 * @author Andreas Wandert
 */
public class CoreTest {

    public static void main(String[] args) throws IOException {
//       requestTest();
        writeTest();
        //  authorizationTest();

    }

    public static void requestTest() {
        Core core = Core.getInstance();
        Calendar startDate = new GregorianCalendar(2017, 01, 01, 0, 0, 0), endDate = new GregorianCalendar(2017, 05, 05, 0, 0, 0);
        String bbox = "51.8500|7.6300|51.8500|7.6300";
        HashMap<String, String> additionalParameter = new HashMap<>();

        core.request(startDate, endDate, bbox, additionalParameter, Core.PROCESSING_CORRECTION);
    }

    public static void writeTest() {
        Core core = Core.getInstance();
        Product product = TestDatasource.readTest();

        //pass product to processor 'Correction'
        Corrections corrections = new Corrections();
        //overrite the input product
        corrections.compute(product, product);

        Assert.assertNotNull(product);

        if (product != null) {
            System.out.println("Writing...");
            File out = core.write(product);

            System.out.println("Path to output Product is: " + out.getAbsolutePath());
        } else {
            System.out.println("'Product' is null");
        }
    }

    private static void authorizationTest() throws IOException {
        URL url = new URL("https://scihub.copernicus.eu/dhus/search?q=footprint:" + "\\\"+\"Intersects(51.85, 7.63)\" AND platformname:Sentinel-2");

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        if (httpConn != null) {
            String credentials = "Basic YW53YTpuZGw/M2hzNyElQVQ=";
            System.out.println(credentials);
            try {
                httpConn.setRequestMethod("GET");
            } catch (ProtocolException ex) {
                Exceptions.printStackTrace(ex);
            }
            httpConn.setDoOutput(true);
            httpConn.setRequestProperty("Authorization", "Basic " + credentials);
            httpConn.addRequestProperty("Bla", "Blubb");
//        httpConn.addRequestProperty("Authorization", "Basic YW53YTpuZGw/M2hzNyElQVQ");
            System.out.println(httpConn.getRequestProperties());

            try {
                httpConn.connect();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            System.out.println("HttpConnection is null!!!");
        }
    }
}
