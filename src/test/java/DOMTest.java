
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
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
 */ /**
 *
 * @author Andreas Wandert Link zum download
 * https://scihub.copernicus.eu/dhus/odata/v1/Products('5d9c44e9-6ae2-40c4-9dda-46e7e12b5ab8')/
 * Nodes('S1A_IW_SLC__1SSV_20161207T013515_20161207T013552_014267_017143_DFE1.SAFE')/
 * Nodes('measurement')/
 * Nodes('s1a-iw1-slc-vv-20161207t013517-20161207t013550-014267-017143-001.tiff')
 */

public class DOMTest {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("./testXML.xml"));
        //Read the document
        Element root = doc.getDocumentElement();
        NodeList nL = root.getElementsByTagName("m:properties");
        ArrayList<String[]> productIds = new ArrayList<String[]>();
        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) n;
                String id = eElement.getElementsByTagName("d:Id").item(0).getTextContent();
                String name = eElement.getElementsByTagName("d:Name").item(0).getTextContent();
                String[] listelement = {id, name};
                productIds.add(listelement);
            }

        }
        //System.out.println(root.getNodeName());

        //Ausgabe
        for (String[] st : productIds) {
            System.out.println(st[0] + ", " + st[1]);
            productIds.get(productIds.size() - 1);
        }
        System.out.println(Arrays.toString(productIds.get(productIds.size() - 1)));
    }
/**
 * 
 * @param tag1 entry
 * @param tag2 title
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws IOException 
 */
    public void handleXML(String tag1, String tag2) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("./measurement.xml"));
        Element root = doc.getDocumentElement();
        NodeList nL = root.getElementsByTagName(tag1);

        for (int i = 0; i < nL.getLength(); i++) {
            Node n = nL.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) n;
                String id = e.getElementsByTagName(tag2).item(0).getTextContent();
                
            }
        }
    }

    public void buildString(String[] id) {
        String s = "https://scihub.copernicus.eu/dhus/odata/v1/Products('" + id[0] + ")/Nodes('" + id[1] + ".SAFE')/Nodes('measurement/Nodes('";
    }
}
