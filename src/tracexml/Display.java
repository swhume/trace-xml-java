/*
 * Copyright 2017 Sam Hume.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tracexml;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.util.List;
import java.util.TreeMap;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Display class contains static methods used to generate output listings, reports, and console messages
 * @version 0.1
*/
public class Display {
    
    /** 
     * showUnreachables generates listings and reports of the unreachable nodes in the graph
     * @param nodesAddedToRoot List of strings with the IDs of nodes added to the root for re-testing
     * @param unreachable List of IDs for nodes that are un-reachable, but should be reachable
     * @param expectedUnreached List of IDs for nodes that are un-reachable, and this is an expected state
     * @param isShowResult Boolean that indicates whether or not to automatically load an HTML unreachable report
     * @param isListUnreachables Boolean that determines if the unreachables should be listed to the console
     */
    public static void showUnreachables(List<String> nodesAddedToRoot, List<String> unreachable, List<String> expectedUnreached, Boolean isShowResult, Boolean isListUnreachables) {
        // run the xslt to transform the node details file into an html trace visualization
        String xsltFileName = ConfigReader.getXmlPath() + ConfigReader.getUnreachableXsl();
        String htmlFileName = ConfigReader.getXmlPath() + ConfigReader.getUnreachableHtml();
        String xmlFileName  = ConfigReader.getXmlPath() + ConfigReader.getUnreachableXml();
        Element root = new Element("unreachable");
        Document document = new Document(root);        
        try {
            addUnexpectedUnreachables(root, unreachable, isListUnreachables);
            addExpectedUnreachables(root, expectedUnreached);            
            // generate the xml output file
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, new FileWriter(xmlFileName));
            // create the html file and display it
            XsltTrace xslTrace = new XsltTrace(xmlFileName, xsltFileName);
            xslTrace.transformXMLFile(htmlFileName);
            if (isShowResult) DisplayHtmlPage(htmlFileName);
            if (ConfigReader.getVerbose()) System.out.println("Nodes added to the root are:");
            for (String addedNode : nodesAddedToRoot) {
                if (ConfigReader.getVerbose()) System.out.println(addedNode);
            }        
            generateTabDelimitedOutput(xmlFileName);
        } catch (IOException io) {
            System.out.println("Error generating list of unreachable nodes. " + io.getMessage());
        }    
    }
    
    private static void addExpectedUnreachables(Element root, List<String> expectedUnreached) {
        if (ConfigReader.getVerbose()) System.out.println("Nodes expected to be unreachable are:");
        for (String expectedNode : expectedUnreached) {
            if (ConfigReader.getVerbose()) System.out.println(expectedNode);
            Element node = new Element("node");
            String origin = expectedNode.substring(expectedNode.indexOf("(")+1,expectedNode.indexOf(")"));
            String nodeId = expectedNode.substring(0, expectedNode.indexOf("(")-1);
            node.setAttribute(new Attribute("oid", nodeId));
            node.setAttribute(new Attribute("origin", origin));
            node.setAttribute(new Attribute("expected", "yes"));
            root.addContent(node); 
        }        
    }

    private static void addUnexpectedUnreachables(Element root, List<String> unreachable, Boolean isListUnreachables) {
        if (ConfigReader.getVerbose() || isListUnreachables) System.out.println("Unreachable nodes are:");
        for (String orphanNode : unreachable) {
            if (ConfigReader.getVerbose()) System.out.println(orphanNode);
            Element node = new Element("node");
            String origin = orphanNode.substring(orphanNode.indexOf("(")+1,orphanNode.indexOf(")"));
            String nodeId = orphanNode.substring(0, orphanNode.indexOf("(")-1);
            node.setAttribute(new Attribute("oid", nodeId));
            node.setAttribute(new Attribute("origin", origin));
            node.setAttribute(new Attribute("expected", "no"));
            root.addContent(node); 
            if (isListUnreachables) System.out.println(nodeId);
        }
        
    }
    
    /* generates the tab delimited output of the same content included in the HTML version */
    private static void generateTabDelimitedOutput(String xmlFileName) {
        String xsltFileName = ConfigReader.getXmlPath() + ConfigReader.getUnreachableTextXsl();
        String textFileName = ConfigReader.getXmlPath() + ConfigReader.getUnreachableText();
        XsltTrace xslTrace = new XsltTrace(xmlFileName, xsltFileName);
        xslTrace.transformXMLFile(textFileName);
    }
    
    /* loads the HTML unreachable report in the default browser */
    private static void DisplayHtmlPage(String htmlFileName) {
        try {
            File fileOut = new File(htmlFileName);
            URI fileUri = fileOut.toURI();
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(fileUri);
        }
        catch (Exception e) {
            System.out.println("Unable to load HTML file in browser. " + e.getMessage());
        }        
    }

    /**
     * writeXMLFIleList saves a list of XML files generated for XQuery look-ups
     * @param vMap a TreeMap of Vertexes that contain the XML file names retrieved by phase
     */
    public static void writeXmlFileList(TreeMap<String, Vertex> vMap) {
        try {
            String xmlFileName = ConfigReader.getXmlPath() + "xml-files.xml";
            Element root = new Element("files");
            Document document = new Document(root);
            for (String phase : vMap.keySet()) {
                Element elem = new Element("file");
                elem.setAttribute(new Attribute("phase", vMap.get(phase).getPhase()));
                elem.addContent(vMap.get(phase).getFileName()); 
                root.addContent(elem);
            }
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, new FileWriter(xmlFileName));
        } catch (IOException ex) {
            System.out.println("Error saving xml files for xquery use. " + ex.getMessage());
        }    
    }
    
    /** 
     * cleanupXmlFile is used to eliminate an UTF-8 generation bug in the XML writer library
     * @param xmlFile String with the path and filename of the GraphML file to cleanup
     */
    public static void cleanupXmlFile(String xmlFile) {
        Writer writer = null;
        try{
            String output = TraceNode.readFile(xmlFile);
            //force to convert UTF-8 standard will address this issue Invalid byte 1 of 1-byte UTF-8 sequence
            String s = new String(output.trim().getBytes(),"UTF-8");
            writer = new BufferedWriter(new FileWriter(xmlFile));
            writer.write(s);
        }   catch (UnsupportedEncodingException ex) {
            System.out.println("Encoding error in the xml file " + xmlFile + ", " + ex.getMessage());            
        }   catch (IOException ex) { 
            System.out.println("Error reading or writing the xml file " + xmlFile + ", " + ex.getMessage());
        } finally {
            try { 
                writer.close();
            } catch (IOException ex) {
                if (ConfigReader.getVerbose()) System.out.println("Error closing xml file " + xmlFile + ". " + ex.getMessage());
            }
        }
        
    }

    /**
     * nonUniqueNodeWarning prints a warning if a node generated from ODM or Define-XML already exists - a node with the same name 
     * @param nodeType String with the type of node - element type from the ODM or Define-XML file
     * @param oid String the element OID from the ODM or Define-XML file
     * @param origPhase String indicating the life-cycle phase where the node was originally used
     * @param currentPhase String the current life-cycle phase where a duplicate version of the original node was found
    */
    public static void nonUniqueNodeWarning(String nodeType, String oid, String origPhase, String currentPhase ) {
        System.out.println("Warning: " + nodeType + " with OID " + oid + " does not appear to be unique. "
                + " Nodes with this OID are found in " + origPhase + " and " + currentPhase);
    }
}
