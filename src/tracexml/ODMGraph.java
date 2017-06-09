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
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

/**
 * ODMGraph class generates the directed graph from the ODM-XML metadata  
 * @version 0.1
 */
public class ODMGraph {
    private Digraph g;
    private String phase;
    private Namespace ons;    // ODM namespace
    
    /**
     * buildODMGraph Main driver for parsing the ODM-XML file and nodes on the graph
     * @param graph Digraph object that the ODM-XML nodes will be added to
     * @param metadata Vertex object that maintains the information about the ODM-XML file 
     */
    public void buildODMGraph(Digraph graph, Vertex metadata) {
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
            List<Element> formList = mdv.getChildren("FormDef", ons);
            for (Element fNode: formList) {
               // create the form node and add it to the graph
               Vertex vForm = new Vertex(fNode.getAttributeValue("OID"));
               vForm.setName(fNode.getAttributeValue("Name"));
               vForm.setPhase(phase);
               vForm.setOdmElementType("FormDef");
               vForm.setQualifiedName(vForm.getOid());
               g.addVertex(vForm);
               // assumption: each ItemGroupDef is referenced by FormDef
               List<Element> itemGroupList = fNode.getChildren("ItemGroupRef", ons);
               for (Element igNode: itemGroupList) {
                   getItemGroupDef(mdv, vForm, igNode);
               }
            }            
        } catch(JDOMException | IOException e){
            System.out.println("Error reading or parsing the ODM-XML file: " + e.getMessage());
            System.exit(0);
        }
    }
    
    /*  generates the nodes for each ItemGroup in the ODM-XML file
        assumption: each ItemDef is referenced by an ItemGroupDef or a ValueListDef */
    private void getItemGroupDef(Element mdv, Vertex vForm, Element igRefNode) {
        List<Element> igList = mdv.getChildren("ItemGroupDef", ons);
        for (Element ig : igList) {
            // create node for each ItemGroup and add to graph
            if (ig.getAttributeValue("OID").equals(igRefNode.getAttributeValue("ItemGroupOID"))) {
                Vertex vItemGroup = findOrAddVertex(ig, vForm);    
                vItemGroup.addSource(vForm);
                vForm.addTarget(vItemGroup);
                List<Element> itList = ig.getChildren("ItemRef", ons);
                for (Element it : itList) {
                    getItemDef(mdv, vItemGroup, it);
                }
            }
        }
    }
    
    private Vertex findOrAddVertex(Element ig, Vertex vForm) {
        Vertex vItemGroup;
        if (g.doesVertexExist(ig.getAttributeValue("OID"))) {
            vItemGroup = g.getVertex(ig.getAttributeValue("OID"));
            // check for duplicate node
            if (!vItemGroup.getPhase().equalsIgnoreCase(this.phase)) 
                Display.nonUniqueNodeWarning("ItemGroupDef", vItemGroup.getOid(), vItemGroup.getPhase(), this.phase);
        } else {
            vItemGroup = new Vertex(ig.getAttributeValue("OID"));
            vItemGroup.setName(ig.getAttributeValue("Name"));
            vItemGroup.setPhase(this.phase);
            vItemGroup.setOdmElementType("ItemGroupDef");
            vItemGroup.setQualifiedName(vForm.getQualifiedName() + ":" + vItemGroup.getOid());
            Element desc = ig.getChild("Description", ons);
            if (desc != null) {
                Element tt = desc.getChild("TranslatedText", ons);
                vItemGroup.setDescription(tt.getTextNormalize());
            }
            g.addVertex(vItemGroup);
        }        
        return vItemGroup;
    }
  
    /*  generates the graph nodes for each ItemDef in the ODM-XML file and links
        the Items to the related ItemGroup     */
    private void getItemDef (Element mdv, Vertex vItemGroup, Element itRefNode) {
        List<Element> itList = mdv.getChildren("ItemDef", ons);
        Vertex vItem;
        for (Element it : itList) {
            if (it.getAttributeValue("OID").equals(itRefNode.getAttributeValue("ItemOID"))) {
                if (g.doesVertexExist(it.getAttributeValue("OID"))) {
                    vItem = g.getVertex(it.getAttributeValue("OID"));
                    if (!vItem.getPhase().equalsIgnoreCase(this.phase)) 
                        Display.nonUniqueNodeWarning("ItemDef", vItem.getOid(), vItem.getPhase(), this.phase);
                } else {
                    vItem = new Vertex(it.getAttributeValue("OID"));
                    vItem.setName(it.getAttributeValue("Name"));
                    vItem.setPhase(phase);
                    vItem.setOdmElementType("ItemDef");                    
                    vItem.setQualifiedName(vItemGroup.getQualifiedName() + ":" + vItem.getOid());
                    Element desc = it.getChild("Description", ons);
                    if (desc != null) {
                        Element tt = desc.getChild("TranslatedText", ons);
                        vItem.setDescription(tt.getTextNormalize());
                    }
                    g.addVertex(vItem);
                }    
                vItem.addSource(vItemGroup);
                vItemGroup.addTarget(vItem);
            }    
        }
    }    

}
