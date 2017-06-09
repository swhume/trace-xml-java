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

import java.util.HashMap;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 *  yEd graph editor Trace-XML extensions. 
 *  Add XML content to the Trace-XML GraphML file to support basic yEd compatibility.
 *  @version 0.1 
*/
public class YedExt {
    private final Namespace yns = Namespace.getNamespace("y", "http://www.yworks.com/xml/graphml");
    private final Namespace ns = Namespace.getNamespace("http://graphml.graphdrawing.org/xmlns");
    

    public void addNameSpace(Element root) {
        root.addNamespaceDeclaration(yns);        
        Namespace yedNs = Namespace.getNamespace("yed", "http://www.yworks.com/xml/yed/3");
        root.addNamespaceDeclaration(yedNs);
        Namespace java = Namespace.getNamespace("java", "http://www.yworks.com/xml/yfiles-common/1.0/java");
        root.addNamespaceDeclaration(java);
        Namespace x = Namespace.getNamespace("x", "http://www.yworks.com/xml/yfiles-common/markup/2.0");
        root.addNamespaceDeclaration(x);
        Namespace sys = Namespace.getNamespace("sys", "http://www.yworks.com/xml/yfiles-common/markup/primitives/2.0");
        root.addNamespaceDeclaration(sys);
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute(new Attribute("schemaLocation", 
                "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd", xsi));
    
    }
    
    /* edge graphics needed for rendering in yEd */
    public void createEdgeGraphics(Element e, String label, String keyId) {
        e.setAttribute(new Attribute("key", keyId));
        Element polyLineEdge = new Element("PolyLineEdge", yns);
        // path sub-element of PolyLineEdge
        Element path = new Element("Path", yns);
        path.setAttribute(new Attribute("sx", "0.0"));
        path.setAttribute(new Attribute("sy", "0.0"));
        path.setAttribute(new Attribute("tx", "0.0"));
        path.setAttribute(new Attribute("ty", "0.0"));
        polyLineEdge.addContent(path);
        // LineStyle sub-element of PolyLineEdge
        Element lineStyle = new Element("LineStyle", yns);
        lineStyle.setAttribute(new Attribute("color", "#000000"));
        lineStyle.setAttribute(new Attribute("type", "line"));
        lineStyle.setAttribute(new Attribute("width", "1.0"));
        polyLineEdge.addContent(lineStyle);
        // arrows sub-element of PolyLineEdge
        Element arrows = new Element("Arrows", yns);
        arrows.setAttribute(new Attribute("source", "none"));
        arrows.setAttribute(new Attribute("target", "standard"));
        polyLineEdge.addContent(arrows);
        // EdgeLabel sub-element of PolyLineEdge
        Element edgeLabel = new Element("EdgeLabel", yns);
        edgeLabel.setAttribute(new Attribute("alignment", "center"));
        edgeLabel.setAttribute(new Attribute("configuration", "AutoFlippingLabel"));
        edgeLabel.setAttribute(new Attribute("distance", "2.0"));
        edgeLabel.setAttribute(new Attribute("fontFamily", "Dialog"));
        edgeLabel.setAttribute(new Attribute("fontSize", "10"));
        edgeLabel.setAttribute(new Attribute("fontStyle", "plain"));
        edgeLabel.setAttribute(new Attribute("hasBackgroundColor", "false"));
        edgeLabel.setAttribute(new Attribute("hasLineColor", "false"));
        edgeLabel.setAttribute(new Attribute("height", "18.7"));
        edgeLabel.setAttribute(new Attribute("modelName", "custom"));
        edgeLabel.setAttribute(new Attribute("preferredPlacement", "anywhere"));
        edgeLabel.setAttribute(new Attribute("ratio", "0.5"));
        edgeLabel.setAttribute(new Attribute("textColor", "#000000"));
        edgeLabel.setAttribute(new Attribute("visible", "true"));
        edgeLabel.setAttribute(new Attribute("width", "31.3"));
        edgeLabel.setAttribute(new Attribute("x", "15.1"));
        edgeLabel.setAttribute(new Attribute("y", "20.7"));
        edgeLabel.setText(label);
        // LabelModel sub-element of EdgeLabel        
        Element labelModel = new Element("LabelModel", yns);
        edgeLabel.addContent(labelModel);
        // SmartEdgeLabelModel sub-element of LabelModel
        Element smartEdgeLabelModel = new Element("SmartEdgeLabelModel", yns);
        smartEdgeLabelModel.setAttribute(new Attribute("autoRotationEnabled", "false"));
        smartEdgeLabelModel.setAttribute(new Attribute("defaultAngle", "0.0"));
        smartEdgeLabelModel.setAttribute(new Attribute("defaultDistance", "10.0"));
        labelModel.addContent(smartEdgeLabelModel);
        // ModelParameter sub-element of EdgeLabel
        Element modelParameter = new Element("ModelParameter", yns);
        edgeLabel.addContent(modelParameter);
        // SmartEdgeLabelModelParameter sub-element of ModelParameter
        Element smartEdgeLabelModelParam = new Element("SmartEdgeLabelModelParameter", yns);
        smartEdgeLabelModelParam.setAttribute(new Attribute("angle", "0.0"));
        smartEdgeLabelModelParam.setAttribute(new Attribute("distance", "30.0"));
        smartEdgeLabelModelParam.setAttribute(new Attribute("distanceToCenter", "true"));
        smartEdgeLabelModelParam.setAttribute(new Attribute("position", "right"));
        smartEdgeLabelModelParam.setAttribute(new Attribute("ratio", "0.5"));
        smartEdgeLabelModelParam.setAttribute(new Attribute("segment", "0"));
        modelParameter.addContent(smartEdgeLabelModelParam);
        // PreferredPlacementDescription sub-element of EdgeLabel
        Element preferredPlacement = new Element("PreferredPlacementDescriptor", yns);
        preferredPlacement.setAttribute(new Attribute("angle", "0.0"));
        preferredPlacement.setAttribute(new Attribute("angleOffsetOnRightSide", "0"));
        preferredPlacement.setAttribute(new Attribute("angleReference", "absolute"));
        preferredPlacement.setAttribute(new Attribute("angleRotationOnRightSide", "co"));
        preferredPlacement.setAttribute(new Attribute("distance", "-1.0"));
        preferredPlacement.setAttribute(new Attribute("frozen", "true"));
        preferredPlacement.setAttribute(new Attribute("placement", "anywhere"));
        preferredPlacement.setAttribute(new Attribute("side", "anywhere"));
        preferredPlacement.setAttribute(new Attribute("sideReference", "relative_to_edge_flow"));
        edgeLabel.addContent(preferredPlacement);
        // BendStyle sub-element of PolyLineEdge 
        Element bendStyle = new Element("BendStyle", yns);
        bendStyle.setAttribute(new Attribute("smooth", "false"));        
        polyLineEdge.addContent(bendStyle);
        // add PolyLineEdge to the Edge element
        e.addContent(polyLineEdge);                 
    }   
    
    /* node graphics are only needed to support yEd rendering */
    public void createNodeGraphics(Element e, String label, String nodeColor, String keyId) {
        e.setAttribute(new Attribute("key", keyId));
        Element shapeNode = new Element("ShapeNode", yns);
        Element geometry = new Element("Geometry", yns);
        geometry.setAttribute(new Attribute("height", "30.0"));
        geometry.setAttribute(new Attribute("width", "30.0"));
        geometry.setAttribute(new Attribute("x", "32.0"));
        geometry.setAttribute(new Attribute("y", "32.0"));
        shapeNode.addContent(geometry);
        Element fill = new Element("Fill", yns);
        fill.setAttribute(new Attribute("color", nodeColor));
        fill.setAttribute(new Attribute("transparent", "false"));
        shapeNode.addContent(fill);
        Element borderStyle = new Element("BorderStyle", yns);
        borderStyle.setAttribute(new Attribute("color", "#000000"));
        borderStyle.setAttribute(new Attribute("type", "line"));
        borderStyle.setAttribute(new Attribute("width", "1.0"));
        shapeNode.addContent(borderStyle);

        Element nodeLabel = new Element("NodeLabel", yns);
        nodeLabel.setAttribute(new Attribute("alignment", "center"));
        nodeLabel.setAttribute(new Attribute("autoSizePolicy", "content"));
        nodeLabel.setAttribute(new Attribute("fontFamily", "Dialog"));
        nodeLabel.setAttribute(new Attribute("fontSize", "10"));
        nodeLabel.setAttribute(new Attribute("fontStyle", "plain"));
        nodeLabel.setAttribute(new Attribute("hasBackgroundColor", "false"));
        nodeLabel.setAttribute(new Attribute("hasLineColor", "false"));
        nodeLabel.setAttribute(new Attribute("height", "18.7"));
        nodeLabel.setAttribute(new Attribute("modelName", "custom"));
        nodeLabel.setAttribute(new Attribute("textColor", "#000000"));
        nodeLabel.setAttribute(new Attribute("Visible", "true"));
        nodeLabel.setAttribute(new Attribute("width", "10.7"));
        nodeLabel.setAttribute(new Attribute("x", "9.7"));
        nodeLabel.setAttribute(new Attribute("y", "5.7"));
        nodeLabel.setText(label);
        shapeNode.addContent(nodeLabel);
        
        Element labelModel = new Element("LabelModel", yns);
        nodeLabel.addContent(labelModel);

        Element smartNodeLabelModel = new Element("SmartNodeLabelModel", yns);
        smartNodeLabelModel.setAttribute(new Attribute("distance", "4.0"));
        labelModel.addContent(smartNodeLabelModel);

        Element modelParameter = new Element("ModelParameter", yns);
        nodeLabel.addContent(modelParameter);        

        Element smartNodeLabelModelParam = new Element("SmartNodeLabelModelParameter", yns);
        smartNodeLabelModelParam.setAttribute(new Attribute("labelRatioX", "0.0"));
        smartNodeLabelModelParam.setAttribute(new Attribute("labelRatioY", "0.0"));
        smartNodeLabelModelParam.setAttribute(new Attribute("nodeRatioX", "0.0"));
        smartNodeLabelModelParam.setAttribute(new Attribute("nodeRatioY", "0.0"));
        smartNodeLabelModelParam.setAttribute(new Attribute("offsetX", "0.0"));
        smartNodeLabelModelParam.setAttribute(new Attribute("offsetY", "0.0"));
        smartNodeLabelModelParam.setAttribute(new Attribute("upX", "0.0"));
        smartNodeLabelModelParam.setAttribute(new Attribute("upY", "-1.0"));
        modelParameter.addContent(smartNodeLabelModelParam);
       
        Element shape = new Element("Shape", yns);
        shape.setAttribute(new Attribute("type", "rectangle"));
        nodeLabel.addContent(shape);
        // add all the graphic nodes and attributes to the data element        
        e.addContent(shapeNode);                 
    } 
    
    public void addYedKeys(Element root, HashMap<String, String> keyTable) {
            Element key8 = new Element("key", ns);
            key8.setAttribute(new Attribute("yfiles.type", "nodegraphics"));
            key8.setAttribute(new Attribute("for", "node"));
            key8.setAttribute(new Attribute("id", "d8"));
            root.addContent(key8);
            keyTable.put("yfiles.nodegraphics", "d8");
            
            Element key12 = new Element("key", ns);
            key12.setAttribute(new Attribute("yfiles.type", "edgegraphics"));
            key12.setAttribute(new Attribute("for", "edge"));
            key12.setAttribute(new Attribute("id", "d12"));
            root.addContent(key12);
            keyTable.put("yfiles.edgegraphics", "d12");

            Element key13 = new Element("key", ns);
            key13.setAttribute(new Attribute("yfiles.type", "portgraphics"));
            key13.setAttribute(new Attribute("for", "port"));
            key13.setAttribute(new Attribute("id", "d13"));
            root.addContent(key13);
            keyTable.put("yfiles.portgraphics", "d13");

            Element key14 = new Element("key", ns);
            key14.setAttribute(new Attribute("yfiles.type", "portgeometry"));
            key14.setAttribute(new Attribute("for", "port"));
            key14.setAttribute(new Attribute("id", "d14"));
            root.addContent(key14);
            keyTable.put("yfiles.portgeometry", "d14");

            Element key15 = new Element("key", ns);
            key15.setAttribute(new Attribute("yfiles.type", "portuserdata"));
            key15.setAttribute(new Attribute("for", "port"));
            key15.setAttribute(new Attribute("id", "d15"));
            root.addContent(key15);
            keyTable.put("yfiles.portuserdata", "d15");        
    }
}
