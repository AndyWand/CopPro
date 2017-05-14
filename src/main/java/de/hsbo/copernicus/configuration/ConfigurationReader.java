package de.hsbo.copernicus.configuration;

import java.io.File;
import java.io.IOException;
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

    public static final String DEFAULT_CONFIGURATION_FILE = "src\\main\\resources\\configuration.xml";
    private static final String ROOT_ELEMENT = "configurations";
    private static final String GLOBAL_SEPERATOR = ":";
    private static File configurationFile;
    private static Configuration configuration;
    private static ConfigurationReader configurationReader;

    private ConfigurationReader() {
        configurationFile = new File(DEFAULT_CONFIGURATION_FILE);
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
        configuration = new Configuration();
        System.out.println("Reading file: " + configurationFile.getAbsolutePath());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (configurationFile != null) {
            try {
                doc = builder.parse(configurationFile);
            } catch (SAXException | IOException ex) {
                Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Read the document
        Element root = doc.getDocumentElement();
        String[] searchForGenStore = Configuration.SEARCH_GENERAL_STORAGE.split(GLOBAL_SEPERATOR);

        //extract the "global" -tag
        NodeList nL = root.getElementsByTagName(searchForGenStore[0]);

        /**
         * get global configs reads the "productdatastorage" -tag unt´der the "global" -tag
         *
         *
         */
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String genStore = eElement.getElementsByTagName(searchForGenStore[1]).item(0).getTextContent();
                configuration.setGeneralStorage(genStore);


            }

        }
        /**
         * extract for "decisionorder"
         */
        String[] searchForDecisionOrder = Configuration.SEARCH_DS_GENERAL_DECISIONORDER.split(GLOBAL_SEPERATOR);
        nL = root.getElementsByTagName(searchForDecisionOrder[1]);
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String order = eElement.getElementsByTagName(searchForDecisionOrder[2]).item(0).getTextContent();
                configuration.setAdapterOrder(order);
            }
        }
        
        /**
         * extract the values for "aws"
         */
        String[] searchForAwsBase = Configuration.SEARCH_AWS_BASEURL.split(GLOBAL_SEPERATOR);
        nL = root.getElementsByTagName(searchForAwsBase[1]);
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String genStore = eElement.getElementsByTagName(searchForAwsBase[2]).item(0).getTextContent();
                configuration.setAwsBaseurl(genStore);
            }
        }
        /**
         * extract the values for "SciHub"
         */
        String[] searchForScihubBase = Configuration.SEARCH_SCIHUB_BASEURL.split(GLOBAL_SEPERATOR);
        String[] searchForScihubCredentials = Configuration.SEARCH_SCIHUB_CREDENTIALS.split(GLOBAL_SEPERATOR);
        String[] searchForScihubOdataurl = Configuration.SEARCH_SCIHUB_ODATAURL.split(GLOBAL_SEPERATOR);
        nL = root.getElementsByTagName(searchForScihubBase[1]);
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String scihubBase = eElement.getElementsByTagName(searchForScihubBase[2]).item(0).getTextContent();
                String scihubOdata = eElement.getElementsByTagName(searchForScihubOdataurl[2]).item(0).getTextContent();
                String scihubCredentials = eElement.getElementsByTagName(searchForScihubCredentials[2]).item(0).getTextContent();
                configuration.setSciHubBaseurl(scihubBase);
                configuration.setSciHubOdataurl(scihubOdata);
                configuration.setSciHubCredentials(scihubCredentials);
            }
        }
        /**
         * extract the values for "code-de
         */
        //extract the "global" -tag
        String[] searchForCodedeBase = Configuration.SEARCH_CODEDE_BASEURL.split(GLOBAL_SEPERATOR);
        String[] searchForCodedeCredentials = Configuration.SEARCH_CODEDE_CREDENTIALS.split(GLOBAL_SEPERATOR);
        nL = root.getElementsByTagName(searchForCodedeBase[1]);
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String baseUrl = eElement.getElementsByTagName(searchForCodedeBase[2]).item(0).getTextContent();
                String credentials = eElement.getElementsByTagName(searchForCodedeCredentials[2]).item(0).getTextContent();
                configuration.setCodedeBaseurl(baseUrl);
                configuration.setCodedeCredentials(credentials);
            }
        }
        /**
         * extract the values for "code-de
         */
        //extract the "global" -tag
        String[] searchForSen2Cor = Configuration.SEARCH_SEN2COR.split(GLOBAL_SEPERATOR);
        nL = root.getElementsByTagName(searchForSen2Cor[0]);
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String baseUrl = eElement.getElementsByTagName(searchForSen2Cor[1]).item(0).getTextContent();
                configuration.setSen2Cor(baseUrl);
            }
        }

    }
}
