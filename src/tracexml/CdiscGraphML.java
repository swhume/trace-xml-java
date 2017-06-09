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

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.XMLOutputter;

/**
 *  CdiscGraphML generates a GraphML version of the internal graph built from
 *  the ODM-XML and Define-XML files.
 *  @version 0.1 
*/
public class CdiscGraphML {
    private String xmlFileName = "";
    private final Namespace ns = Namespace.getNamespace("http://graphml.graphdrawing.org/xmlns");
    private Document document;
    private HashMap<String, String> keyTable = new HashMap<>();
    private boolean yedExtension;
    private YedExt yed;

    /** CdiscGraphML constructor
     * @param graphMLFileName String containing the GraphML output filename and path
     * @param yedExt Boolean that indicates whether or not to add yEd extensions 
     */
    public CdiscGraphML(String graphMLFileName, boolean yedExt) {
        this.xmlFileName = graphMLFileName;
        this.yedExtension = yedExt;
        if (this.yedExtension) yed = new YedExt();
    }
    
    /**  createGraphMLOutput executes the generation of the GraphML file 
     * @param g the Digraph object of the graph built using the ODM and Define-XML files
     */
    public void createGraphMLOutput(Digraph g) {
        Element root = createRootNode();
        document = new Document(root);
        createKeyDefinitions(root);
        createGraph(root, g);        
    }
   
    /* main driver for building the graph nodes and edges */
    private void createGraph(Element root, Digraph g) {
        Element graph = new Element("graph", ns);
        graph.setAttribute(new Attribute("edgedefault", "directed"));
        graph.setAttribute(new Attribute("id", "G"));
        root.addContent(graph); 
        // add graph nodes and edges
        createGraphNodes(graph, g);
        createGraphEdges(graph, g);
        writeGraphMLFile();
    }
    
    /* build the edges connecting linked nodes in the graph */
    private void createGraphEdges(Element graph, Digraph g) {
        Integer edgeCount = 0;
        for (String key : g.getKeys()) {
            Vertex v = g.getVertex(key);
            for (Vertex source: v.getSource()) {
                if (!source.getOid().equals("root"))
                    createNewEdge(source, v, graph, edgeCount++);                
            }        
        }
    }

    /* generate an new edge to connect two nodes */
    private void createNewEdge(Vertex source, Vertex target, Element graph, Integer edgeCount) {
        Element edge = new Element("edge", ns);       
        edge.setAttribute(new Attribute("id", "e" + edgeCount.toString()));
        if (source.getNodeId().length() < 1)
            System.out.println("Warning: edge node is missing the source CdiscGraphML.createNewEdge");
        edge.setAttribute(new Attribute("source", source.getNodeId()));
        edge.setAttribute(new Attribute("target", target.getNodeId()));               
        graph.addContent(edge); 
        Element d1 = new Element("data", ns);                               
        d1.setAttribute(new Attribute("key", keyTable.get("edge.description")));
        d1.setText("maps-to");
        edge.addContent(d1); 
        // add node graphics for needed to support yEd
        if (yedExtension) {
            Element d2 = new Element("data", ns);                               
            yed.createEdgeGraphics(d2, edgeCount.toString(), keyTable.get("yfiles.edgegraphics"));
            edge.addContent(d2);
        }
    }
    
    /* build a node for each vertex in the graph */
    private void createGraphNodes(Element graph, Digraph g) {
        Integer nodeCount = 0;
        String formatCode = getFormatForLength(g);
        for (String key : g.getKeys()) {
            // leave the root node out of the graph
            if (key.equals("root")) continue;
            Vertex v = g.getVertex(key);
            
            Element node = new Element("node", ns);       
            node.setAttribute(new Attribute("id", "n" + nodeCount.toString()));
            graph.addContent(node); 
            // add node description as Vertex oid
            Element d1 = new Element("data", ns);                               
            d1.setAttribute(new Attribute("key", keyTable.get("node.description")));
            // length of node id determined by the number of nodes in the graph
            String formatCount = String.format(formatCode, nodeCount);
            d1.setText(formatCount + " " +  v.getOid());
            node.addContent(d1); 
            // add node elementType as Vertex odmElementType
            Element d2 = new Element("data", ns);                               
            d2.setAttribute(new Attribute("key", keyTable.get("node.elementType")));
            d2.setText(v.getOdmElementType());
            node.addContent(d2); 
            // add node lifecycleStage as Vertex odmElementType
            Element d3 = new Element("data", ns);                               
            d3.setAttribute(new Attribute("key", keyTable.get("node.lifecycleStage")));
            d3.setText(v.getPhase());
            node.addContent(d3); 
            // add node name as Vertex name
            Element d4 = new Element("data", ns);                               
            d4.setAttribute(new Attribute("key", keyTable.get("node.name")));
            d4.setText(v.getName());
            node.addContent(d4); 
            // add node url as Vertex filename
            Element d5 = new Element("data", ns);                               
            d5.setAttribute(new Attribute("key", keyTable.get("node.url")));
            // TODO change this attribute to be the OID
            d5.setText(v.getOid());
            node.addContent(d5); 
            // add node graphics for yEd
            if (yedExtension) {
                Element d8 = new Element("data", ns);                               
                yed.createNodeGraphics(d8, nodeCount.toString(), getNodeColor(v), keyTable.get("yfiles.nodegraphics"));
                node.addContent(d8);
            }
            
            // add detailed description as Vertex description
            Element d17 = new Element("data", ns);                               
            d17.setAttribute(new Attribute("key", keyTable.get("node.detailedDesc")));
            d17.setText(v.getDescription());
            node.addContent(d17); 
            
            v.setNodeId("n" + nodeCount.toString());
            nodeCount++;
        }
    }
            
    /* write the GraphML XML to a file */
    private void writeGraphMLFile() {
        try {
            XMLOutputter xmlOutput = new XMLOutputter();
            //xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, new FileWriter(xmlFileName));
            if (ConfigReader.getIsFixInvalidByte1()) 
                Display.cleanupXmlFile(xmlFileName);
        } catch (IOException io) {
            System.out.println(io.getMessage());
        }    
    }
        
    /* create the root element for the GraphML file */
    private Element createRootNode() {
        Element root = new Element("graphml", ns);
        if (this.yedExtension) yed.addNameSpace(root);
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.addNamespaceDeclaration(xsi);
        return root;
    }

    
    private void createKeyDefinitions(Element root) {
        Element key0 = new Element("key", ns);
        key0.setAttribute(new Attribute("attr.name", "description"));
        key0.setAttribute(new Attribute("attr.type", "string"));
        key0.setAttribute(new Attribute("for", "graph"));
        key0.setAttribute(new Attribute("id", "d0"));
        root.addContent(key0);
        keyTable.put("graph.description", "d0");

        Element key1 = new Element("key", ns);
        key1.setAttribute(new Attribute("attr.name", "level"));
        key1.setAttribute(new Attribute("attr.type", "int"));
        key1.setAttribute(new Attribute("for", "graph"));
        key1.setAttribute(new Attribute("id", "d1"));
        root.addContent(key1);
        keyTable.put("graph.level", "d1");
        
        Element key2 = new Element("key", ns);
        key2.setAttribute(new Attribute("attr.name", "url"));
        key2.setAttribute(new Attribute("attr.type", "string"));
        key2.setAttribute(new Attribute("for", "node"));
        key2.setAttribute(new Attribute("id", "d2"));
        root.addContent(key2);
        keyTable.put("node.url", "d2");
        
        Element key3 = new Element("key", ns);
        key3.setAttribute(new Attribute("attr.name", "description"));
        key3.setAttribute(new Attribute("attr.type", "string"));
        key3.setAttribute(new Attribute("for", "node"));
        key3.setAttribute(new Attribute("id", "d3"));
        root.addContent(key3);
        keyTable.put("node.description", "d3");

        Element key4 = new Element("key", ns);
        key4.setAttribute(new Attribute("attr.name", "lifecycleStage"));
        key4.setAttribute(new Attribute("attr.type", "string"));
        key4.setAttribute(new Attribute("for", "node"));
        key4.setAttribute(new Attribute("id", "d4"));
        root.addContent(key4);
        keyTable.put("node.lifecycleStage", "d4");

        Element key5 = new Element("key", ns);
        key5.setAttribute(new Attribute("attr.name", "name"));
        key5.setAttribute(new Attribute("attr.type", "string"));
        key5.setAttribute(new Attribute("for", "node"));
        key5.setAttribute(new Attribute("id", "d5"));
        root.addContent(key5);
        keyTable.put("node.name", "d5");
                                
        Element key9 = new Element("key", ns);
        key9.setAttribute(new Attribute("yfiles.type", "resources"));
        key9.setAttribute(new Attribute("for", "graphml"));
        key9.setAttribute(new Attribute("id", "d9"));
        root.addContent(key9);
        keyTable.put("yfiles.resources", "d9");

        Element key10 = new Element("key", ns);
        key10.setAttribute(new Attribute("attr.name", "url"));
        key10.setAttribute(new Attribute("attr.type", "string"));
        key10.setAttribute(new Attribute("for", "edge"));
        key10.setAttribute(new Attribute("id", "d10"));
        root.addContent(key10);
        keyTable.put("edge.url", "d10");

        Element key11 = new Element("key", ns);
        key11.setAttribute(new Attribute("attr.name", "description"));
        key11.setAttribute(new Attribute("attr.type", "string"));
        key11.setAttribute(new Attribute("for", "edge"));
        key11.setAttribute(new Attribute("id", "d11"));
        root.addContent(key11);
        keyTable.put("edge.description", "d11");
        
        if (yedExtension) yed.addYedKeys(root, keyTable);

        Element key16 = new Element("key", ns);
        key16.setAttribute(new Attribute("attr.name", "elementType"));
        key16.setAttribute(new Attribute("attr.type", "string"));
        key16.setAttribute(new Attribute("for", "node"));
        key16.setAttribute(new Attribute("id", "d16"));
        root.addContent(key16);
        keyTable.put("node.elementType", "d16");
        
        Element key17 = new Element("key", ns);
        key17.setAttribute(new Attribute("attr.name", "detailedDesc"));
        key17.setAttribute(new Attribute("attr.type", "string"));
        key17.setAttribute(new Attribute("for", "node"));
        key17.setAttribute(new Attribute("id", "d17"));
        root.addContent(key17);
        keyTable.put("node.detailedDesc", "d17");
    }    
    
    private String getFormatForLength(Digraph g) {
        return "%0" + g.vertexCount().toString().length() + "d";
    }

    private String getNodeColor(Vertex v) {
        return getNodeTypeColor(v.getOdmElementType());
    }
    
    private String getNodeTypeColor(String nodeType) {
        String nodeTypeColor;
        switch (nodeType) {
            case "FormDef":
                nodeTypeColor = "#c0c0c0";
                break;
            case "ItemGroupDef":
                nodeTypeColor = "#44aa00";
                break;
            case "ItemDef":
                nodeTypeColor = "#ffcc00";
                break;
            case "MethodDef":
                nodeTypeColor = "#00ccff";
                break;
            default:
                nodeTypeColor = "#00ffff";
                break;
        }
        return nodeTypeColor;
    }
}
