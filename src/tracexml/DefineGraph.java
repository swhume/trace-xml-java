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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

/**
 * Generates a graph from the Define-XML metadata. Assumes that the XML files are loaded in life-cycle phase order.
 * Assumes that OIDs are unique within a submission
 * @version 0.1
 */
public class DefineGraph {
    private Digraph g;
    private String phase;
    private HashMap<String, Vertex> notFoundItems = new HashMap<>(); 
    private Namespace ons;    // ODM namespace
    private final Namespace dns = Namespace.getNamespace("def", "http://www.cdisc.org/ns/def/v2.0");
    private final Namespace tns = Namespace.getNamespace("trc", "http://www.cdisc.org/ns/trace/v1.0");
    
    /** 
     * buildDefGraph Drives the implementation of a digraph based on the contents of Define-XML
     * @param graph Digraph object to add the Define-XML nodes to
     * @param metadata Vertex object that contains information needed on the Define-XML file
     */
    public void buildDefGraph(Digraph graph, Vertex metadata) {
        g = graph;
        phase = metadata.getPhase();
        try {
            SAXBuilder jdomBuilder = new SAXBuilder();
            File inputFile = new File(metadata.getFileName());            
            Document document = jdomBuilder.build(inputFile);
            Element root = document.getRootElement();
            ons = root.getNamespace();
            Element study = root.getChild("Study", ons);
            Element mdv = study.getChild("MetaDataVersion", ons);
            List<Element> igList = mdv.getChildren("ItemGroupDef", ons);
            for (Element igNode: igList) {
               // create the item group node and add it to the graph
               Vertex vItemGroup = new Vertex(igNode.getAttributeValue("OID"));
               vItemGroup.setName(igNode.getAttributeValue("Name"));
               vItemGroup.setPhase(phase);
               // origin for a node that represents a collection of variables
               vItemGroup.setOriginType("Collection");
               vItemGroup.setOdmElementType("ItemGroupDef");
               vItemGroup.setQualifiedName(vItemGroup.getOid()); 
               
               Element desc = igNode.getChild("Description", ons);
               if (desc != null) {
                  Element tt = desc.getChild("TranslatedText", ons);
                  vItemGroup.setDescription(tt.getTextNormalize());
               }   
               g.addVertex(vItemGroup);
               List<Element> itemList = igNode.getChildren("ItemRef", ons);
               for (Element itNode: itemList) {
                   getItemDef(mdv, vItemGroup, itNode);
               }
            }
        // re-check for the missing nodes (e.g. could reference a source in the same file that wasn't processed yet)
        reCheckMissingNodes();
        } catch(JDOMException e){
            System.out.println("Unable to parse the Define-XML file " + metadata.getFileName() + ". " + e.getMessage());
            System.exit(0);
        } catch(IOException ioe){
            System.out.println("Unable to open the Define-XML file " + metadata.getFileName() + ". " + ioe.getMessage());
            System.exit(0);
        }
    }

    /* re-test all nodes listed as not found */
    private void reCheckMissingNodes() {
        for (String itemOID : notFoundItems.keySet()) {
            Vertex findVertex = g.getVertex(itemOID);
            if (findVertex == null) {
                System.out.println("Bad OID reference: " + itemOID + ". Unable to find source item in DefineGraph.lookupSrcNode");
            } else {
                String qualifiedName = findVertex.getQualifiedName();
                Vertex vSi = lookupSrcNodeByName(itemOID, qualifiedName);
                if (vSi != null) {
                    Vertex vTarget = notFoundItems.get(itemOID);
                    vTarget.addSource(vSi);
                    vSi.addTarget(vTarget);
                } else {
                    System.out.println("Unable to find source item " + itemOID + " DefineGraph.lookupSrcNode");
                }
            }
        }                        
    }
    
    /* generate graph nodes for the ItemDefs for an ItemGroup - also processes ItemDefs associated with ValueLists */
    private void getItemDef (Element mdv, Vertex vItemGroup, Element itRefNode) {
        List<Element> itList = mdv.getChildren("ItemDef", ons);
        for (Element it : itList) {
            // an itemDef must exist for each itemRef
            if (it.getAttributeValue("OID").equals(itRefNode.getAttributeValue("ItemOID"))) {
                Vertex vItem = findOrAddVertex(vItemGroup, it);    
                vItem.addSource(vItemGroup);
                String valueListOID = getValueListOID(it);
                String methodOID = getItemMethodOID(itRefNode);
                // assumption: item's have methods or VLM, not both (methods may be on VLM items)
                if (methodOID.length() > 0) {
                    addMethod(methodOID, vItem, mdv, it);
                } else if (valueListOID.length() > 0) {
                    // included in this method as makes a recursive call
                    List<Element> valueList = mdv.getChildren("ValueListDef", dns);
                    for (Element vlm : valueList) {
                        if (vlm.getAttributeValue("OID").equals(valueListOID)) {
                            List<Element> itRefList = vlm.getChildren("ItemRef", ons);
                            for (Element itRef : itRefList) {
                                getItemDef(mdv, vItem, itRef);
                            }
                        }
                    }
                } else {    
                    addSourceItems(it, vItem);
                }
                vItemGroup.addTarget(vItem);
            }    
        }  
    }

    // an itemDef must exist for each itemRef - so find existing node or add one
    private Vertex findOrAddVertex(Vertex vItemGroup, Element it) {
        Vertex vItem;
        if (g.doesVertexExist(it.getAttributeValue("OID"))) {
            vItem = g.getVertex(it.getAttributeValue("OID"));
            if (!vItem.getPhase().equalsIgnoreCase(this.phase)) 
                Display.nonUniqueNodeWarning("ItemDef", vItem.getOid(), vItem.getPhase(), this.phase);
        } else {
            // add new Vertex to graph
            vItem = addNewVertex(vItemGroup, it);
        }  
        return vItem;
    }
    
    // a method may be referenced by multiple ItemRefs with different sources - TODO clean up redundancy
    private void addMethod(String methodOID, Vertex vItem, Element mdv, Element it) {
        if (!g.doesVertexExist(methodOID)) {
            Vertex methodNode = getMethodDef(mdv, methodOID);
            if (methodNode != null) {
                methodNode.setOriginType("Method");
                addSourceItems(it, methodNode);
                vItem.addSource(methodNode);
                methodNode.addTarget(vItem);
                g.addVertex(methodNode);
            }
         } else {
            // do not add the same method twice
            Vertex methodNode = g.getVertex(methodOID);
            addSourceItems(it, methodNode);
            vItem.addSource(methodNode);
            methodNode.addTarget(vItem);                        
            if (!methodNode.getPhase().equalsIgnoreCase(this.phase)) 
                Display.nonUniqueNodeWarning("MethodDef", methodNode.getOid(), methodNode.getPhase(), this.phase);
        }        
    }

    private Vertex addNewVertex(Vertex vItemGroup, Element it) {
        Vertex vItem = new Vertex(it.getAttributeValue("OID"));
        vItem.setName(it.getAttributeValue("Name"));
        vItem.setPhase(phase);
        vItem.setQualifiedName(vItemGroup.getQualifiedName() + ":" + vItem.getOid());
        vItem.setOdmElementType("ItemDef");
        Element desc = it.getChild("Description", ons);
        if (desc != null) {
            Element tt = desc.getChild("TranslatedText", ons);
            vItem.setDescription(tt.getTextNormalize());
        }
        Element origin = it.getChild("Origin", dns);
        if (origin != null) {
            String originType = origin.getAttributeValue("Type");
            if (originType == null || originType.isEmpty()) {
                System.out.println("Error: Origin missing the Type attribute. Please fix the schema validation errors.");
                originType = "None";
            }
            String originNoInput = origin.getAttributeValue("NoTraceItems", tns);
            if ("Yes".equals(originNoInput)) {
                originType = Origin.DERIVED_NO_SRC_ORIGIN;
            }
            vItem.setOriginType(originType);
        }     
        g.addVertex(vItem);
        return vItem;
    }
    
    /* add source items to nodes that will be used to create edges */    
    private void addSourceItems(Element it, Vertex vTarget) {
        Element origin = it.getChild("Origin", dns);
        if (origin == null) return;
        String originType = origin.getAttributeValue("Type");
        // ASSUMPTION: only providing sources for specified Origin Types
        // TODO encapsulate origin types with sources check rule
        if (originType != null && Origin.isOriginSourceItems(originType)) {
            Element src = origin.getChild("Trace", tns);
            if (src == null) return;                  
            List<Element> srcList = src.getChildren("TraceItem", tns);
            if (srcList == null) return;  
            for (Element si : srcList) {
                Vertex vSi = lookupSrcNode(si);
                if (vSi != null) {
                    vTarget.addSource(vSi);
                    vSi.addTarget(vTarget);
                } else {
                    // nodes that reference a source in the same file may not be processed yet - recheck after all are completed
                    if (ConfigReader.getVerbose()) System.out.println("Unable to find source node: " + si.getAttributeValue("ItemOID"));
                    notFoundItems.put(si.getAttributeValue("ItemOID"), vTarget);
                }                
            }
        }
    }
        
    /* ASSUMPTION: OIDs are unique within a submission */
    private Vertex lookupSrcNodeByName(String itemOID, String qualifiedName) {
        Vertex vSrc = null;
        if (g.getKeys().contains(itemOID)) {
            Vertex v = g.getVertex(itemOID);
            if (qualifiedName != null && !qualifiedName.isEmpty()) {
                if (v.getQualifiedName().equals(qualifiedName)) {
                    vSrc = v;
                }
            } else {
                    vSrc = v;
            }
        }
        return vSrc;  
    }

    /* ASSUMPTION: OIDs are unique within a submission */
    private Vertex lookupSrcNode(Element si) {
        String itemOID = si.getAttributeValue("ItemOID");
        String itemGroupOID = si.getAttributeValue("ItemGroupOID");
        String formOID = si.getAttributeValue("FormOID");
        String qualifiedName = buildQualifiedName(itemOID, formOID, itemGroupOID);
        Vertex vSrc = lookupSrcNodeByName(itemOID, qualifiedName);
        return vSrc;
    }    

    /* build name from full ODM hierachy to identify specific sources from a set of possible sources */
    private String buildQualifiedName(String itemOID, String formOID, String itemGroupOID) {
        String qualifiedName = "";
        if (itemGroupOID != null && !itemGroupOID.isEmpty()) {
            qualifiedName = itemGroupOID + ":" + itemOID;
       }
       if (formOID != null && !formOID.isEmpty()) {
           qualifiedName = formOID + ":" + qualifiedName;
       } 
        return qualifiedName;
    }
    
    private String getValueListOID(Element itNode) {
        String valueListOID = "";
        try {
            Element valueListRef = itNode.getChild("ValueListRef", dns);
            valueListOID = valueListRef.getAttributeValue("ValueListOID");
        } catch(Exception e){
            if (itNode != null) {
                String vlName = itNode.getAttributeValue("Name");
                if (vlName != null) {
                    if (ConfigReader.getVerbose()) System.out.println("No value list in the ItemDef " + vlName);
                } else {
                    if (ConfigReader.getVerbose()) System.out.println("No value list in the ItemDef - value list name is not found.");                    
                } 
            }
        } 
        return valueListOID;
    }
        
    private Vertex getMethodDef(Element mdv, String methodOID) {
        Vertex vMethod = null;
        List<Element> mList = mdv.getChildren("MethodDef", ons);
        // native JDOM searching is faster than XPath
        for (Element m : mList) {
            if (m.getAttributeValue("OID").equals(methodOID)) {
                vMethod = new Vertex(methodOID);
                vMethod.setName(m.getAttributeValue("Name"));
                vMethod.setMethodType(m.getAttributeValue("Type"));
                vMethod.setOdmElementType("MethodDef");
                vMethod.setPhase(phase);
                Element desc = m.getChild("Description", ons);
                // ASSUMPTION: only English for prototype - expand for multi-language
                Element tt = desc.getChild("TranslatedText", ons);
                // ASSUMPTION: use query to lookup the FormalExpression if needed
                vMethod.setDescription(tt.getTextNormalize());
                break;
            }    
        }
        if (vMethod == null) {
            System.out.println("Unable to find method " + methodOID + ".");
        }
        return vMethod;
    }
        
    private String getItemMethodOID(Element itRefNode) {
        List<Attribute> itemAttributes = itRefNode.getAttributes();
        String methodName = "";
        for (Attribute attr : itemAttributes) {
            if (attr.getName().equalsIgnoreCase("MethodOID")) {
                methodName = attr.getValue();
                break;
            }
        }
        return methodName;
    }
}
