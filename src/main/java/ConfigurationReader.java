
import de.hsbo.copernicus.datasource.AdapterScihub;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * This class intands to read in the configuration settings from a configuration.xml ths location of
 * this file is specified in
 *
 * @author Andreas Wandert
 */
public class ConfigurationReader {

    private static File configurationFile;
    private static Configuration configuration;
    private static ConfigurationReader configurationReader;

    private ConfigurationReader() {
        configurationFile = new File("\\.");
        readConfiguration();
    }

    private ConfigurationReader(File configFileInput) {
        configurationFile = configFileInput;
        readConfiguration();

    }

    private ConfigurationReader(String pathToConfigFile) {
        configurationFile = new File(pathToConfigFile);
        readConfiguration();
    }

    public static Configuration getInstance() {
        if (configurationReader != null) {
            return configuration;
        }
        configurationReader = new ConfigurationReader();
        return configuration;
    }

    public static Configuration getInstance(File configurationFile) {
        if (configurationReader != null) {
            return configuration;
        }
        configurationReader = new ConfigurationReader(configurationFile);
        return configuration;
    }

    public static Configuration getInstance(String pathToConfigFile) {
        if (configurationReader != null) {
            return configuration;
        }
        configurationReader = new ConfigurationReader(pathToConfigFile);
        return configuration;
    }

    private static void readConfiguration() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        HashMap<String, String> result = new HashMap<>();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AdapterScihub.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (configurationFile != null) {
            try {
                doc = builder.parse(configurationFile);
            } catch (SAXException | IOException ex) {
                Logger.getLogger(AdapterScihub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Read the document
        Element root = doc.getDocumentElement();
        NodeList nL = root.getElementsByTagName("superLevelTag");
        ArrayList<String[]> productIds = new ArrayList<>();
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String tag1 = eElement.getElementsByTagName("lowerLevelTag1").item(0).getTextContent();
            }
        }
    }
}
