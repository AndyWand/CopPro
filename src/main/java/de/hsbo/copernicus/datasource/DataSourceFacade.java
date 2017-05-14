package de.hsbo.copernicus.datasource;

import de.hsbo.copernicus.configuration.ConfigurationReader;
import de.hsbo.copernicus.processing.Core;
import math.geom2d.polygon.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.esa.s2tbx.dataio.jp2.JP2ProductReaderPlugin;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.main.GPT;
import org.esa.snap.core.util.SystemUtils;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Andreas Wandert This Class is to present data, requests and communication of the
 * datasource package to the public. It also implements an decision algorithm to choose one of three
 * sources.
 */
public class DataSourceFacade {

    private static final String OUTPUTPRODUCT_NAME = "allInOne";

    private File resultFile;
    private Product resultProduct;
    private final ProductIOPlugInManager manager;

    public DataSourceFacade() {
        manager = ProductIOPlugInManager.getInstance();
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @param bbox
     * @param additionalParameter
     * @return
     */
    public Product request(Calendar startDate, Calendar endDate, Rectangle2D bbox, HashMap<String, String> additionalParameter) {

        AdapterFactory factory;
        factory = AdapterFactory.getInstance();
        AbstractAdapter source = null;
        try {
            source = factory.getAdapter();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        source.setQuery(startDate, endDate, bbox, additionalParameter);
        /**
         * instanciate a new Thread and use this to execute the source-Adapter
         */
        Thread t = new Thread(source);
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(DataSourceFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        resultFile = source.getResult();
        //System.out.println("Filelocation is: " + resultFile.getAbsolutePath());
        resultProduct = read(resultFile);

        return resultProduct;
    }

    /**
     * Reads a file of format JP2 into SNAPs product data model File Object needs to point to the
     * .Safe folder
     *
     * @param folder
     * @return
     *
     * this is for internal us only
     */
    protected Product read(File folder) {
        SystemUtils.init3rdPartyLibs(GPT.class);
        final String FILTER_EXPERSSION = "MTD";

        //navigate to the .xml metadata file 
        //name of xml file is e.g. MTD_MSIL1C.xml
        File[] fileList = folder.listFiles();

        File xmlFile = null;
        for (File f : fileList) {
            if (f.isFile() && f.getName().contains(FILTER_EXPERSSION)) {
                xmlFile = f;
            }
        }
        ArrayList<String> productList = readXML(xmlFile);
        Product product = null;
        JP2ProductReaderPlugin jp2ReaderPlugIn = new JP2ProductReaderPlugin();
        manager.addReaderPlugIn(jp2ReaderPlugIn);

        Product allInOne = new Product(OUTPUTPRODUCT_NAME, "JPEG-2000");
        allInOne.setFileLocation(folder);

        for (String productPath : productList) {
            try {
                String fullPathToProducts = folder + "\\" + productPath;
                product = ProductIO.readProduct(fullPathToProducts);

                ProductReader jp2Reader = ProductIO.getProductReaderForInput(fullPathToProducts);
                product = jp2Reader.readProductNodes(fullPathToProducts, null);

            } catch (IOException ex) {
                Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (String name : product.getBandNames()) {
                // Put all read bands into one product object
                Band[] bands = product.getBands();
                String nameFromProduct = "";

                //filter product name which contains "_B" to indicate an band 
                //and extract their name; example: B02
                if (product.getName().contains("_B")) {
                    String productName = product.getName().split("\\.")[0];
                    nameFromProduct = productName.substring(productName.length() - 3, productName.length());
                    Band band = bands[0];
                    band.setName(nameFromProduct);
                    allInOne.addBand(band);
                }
            }
        }
        return allInOne;
    }

    protected ArrayList<String> readXML(File xmlFile) {
        ArrayList<String> bandProducts = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            doc = builder.parse(xmlFile);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Read the document
        Element root = doc.getDocumentElement();

        //extract the "global" -tag
        NodeList nL = root.getElementsByTagName("Granule");
        /**
         * get global configs reads the "productdatastorage" -tag unt´der the "global" -tag
         *
         *
         */
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String imageFile = eElement.getElementsByTagName("IMAGE_FILE").item(0).getTextContent();

                NodeList nL2 = eElement.getElementsByTagName("IMAGE_FILE");
                for (int j = 0; j < nL2.getLength(); j++) {
                    Node n2 = nL2.item(j);
                    if (n2.getNodeType() == Node.ELEMENT_NODE) {
                        bandProducts.add(n2.getTextContent() + ".jp2");
                    }
                }
            }
        }
        return bandProducts;
    }
}
