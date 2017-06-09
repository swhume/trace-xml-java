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

import java.util.ArrayList;
import java.util.List;

/**
 * Vertex class represents the node objects that comprise the graph generated from
 * the ODM-XML and Define-XML metadata files
 * @version 0.1
*/
public class Vertex {
    private List<Vertex> source = new ArrayList<>();  
    private List<Vertex> target = new ArrayList<>();
    private String phase = "";
    private String name = "";
    private String qualifiedName;
    private String oid;
    private String nodeId = "";
    private String odmElementType = "";
    private String fileName = "";
    private String description = "";
    private String methodType = "";
    private String originType = "";
    private String transformStageStart;
    private String transformStageFinish;
    
    /** 
     * Vertex constructor with element Name and OID parameters
     * @param name String containing the name of the node typically taken from the XML element
     * @param oid  String containing the OID identifier of the node typically taken from the XML element
     */
    public Vertex(String name, String oid) {
        this.name = name;
        this.oid = oid;
    }

    /** 
     * Vertex constructor with the element OID as a parameter
     * @param oid  String containing the OID identifier of the node typically taken from the XML element.
     */
    public Vertex(String oid) {
        this.oid = oid;
    }

    /**
     * addSource adds a graph source node to a Vertex
     * @param node the source node to add to the vertex
     */
    public void addSource(Vertex node) {
        if (node == null) {
            if (ConfigReader.getVerbose()) System.out.println("Unable to addSource for null node in Vertex.");
            return;
        }
        // each node source should exist only once (should be unique); multiple unique source are ok
        if (!vertexExists(node, source)) {
            this.source.add(node);
        } else {
            if (ConfigReader.getVerbose()) System.out.println("Source already exists for node: " + node.getOid());
        }
    }
    
    /**
     * addTarget adds a graph target node to a Vertex
     * @param node the target node to add to the Vertex
     */
    public void addTarget(Vertex node) {
        if (node == null) {
            if (ConfigReader.getVerbose()) System.out.println("Unable to addTarget for null node in Vertex.");
            return;
        }
        // each node target should exist only once (should be unique); multiple unique targets are ok
        if (!vertexExists(node, target)) {
            this.target.add(node);
        } else {
            if (ConfigReader.getVerbose()) System.out.println("Target already exists for node: " + node.getOid());            
        }
    }
    
    /* tests to determine if the Vertex exists in a list of Vertexes */
    private Boolean vertexExists(Vertex node, List<Vertex> vList) {
        return vList.stream().anyMatch(o -> o.getOid().equalsIgnoreCase(node.getOid()));
    }
        
    /**
     * getter for the Vertex name
     * @return String name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * getter for the Vertex OID
     * @return String OID
     */
    public String getOid() {
        return this.oid;
    } 
    
    /**
     * getter for the identifier (node id) for use in the GraphML graph
     * @return String nodeId for the Vertex for use in the GraphML graph
     */
    public String getNodeId() {
        return this.nodeId;
    }
    
    /**
     * getter for the ODM element type represented by the Vertex
     * @return String containing the ODM element type 
     */
    public String getOdmElementType() {
        return this.odmElementType;
    }
    
    /**
     * getter for the Vertex Define-XML def:Origin type
     * @return String with the Origin type
     */
    public String getOriginType() {
        return originType;
    }
    
    /**
     * setter for Vertex Origin type from Define-XML def:Origin
     * @param originType String containing the Origin type from Define-XML
     */
    public void setOriginType(String originType) {
        this.originType = originType;
    }
    
    /**
     * setter for the fileName that contains the metadata used to generate the Vertex
     * @param fileName String file name of the Define-XML or ODM file
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * getter for the file name where the metadata for the Vertex originated
     * @return String file name for the Define-XML or ODM file
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * getter for a list of Vertexes that are a source for this Vertex within the graph
     * @return List of source Vertexes or nodes
     */
    public List<Vertex> getSource() {
        return source;
    }

    /**
     * getter for a list of Vertexes that are a target for this Vertex within the graph
     * @return List of target Vertexes or nodes
     */
    public List<Vertex> getTarget() {
        return target;
    }

    /**
     * getter for the life-cycle phase for the Vertex (e.g. Data Collection, Tabulation, Analysis)
     * @return String representing the life-cycle phase of the Vertex
     */
    public String getPhase() {
        return phase;
    }

    /**
     * getter for the type of Method; assigned where the element type is a MethodDef
     * @return String containing the type of method
     */
    public String getMethodType() {
        return methodType;
    }

    /**
     * getter for the description of the Vertex or element from ODM or Define-XML
     * @return String with a description of the Vertex
     */
    public String getDescription() {
        return description;
    }

    /**
     * getter for the life-cycle stage that provides the input to a method
     * @return String containing the life-cycle for the input of a method
     */
    public String getTransformStageStart() {
        return transformStageStart;
    }

    /**
     * getter for the life-cycle stage that provides the output of a method
     * @return String containing the life-cycle for the output of a method
     */
    public String getTransformStageFinish() {
        return transformStageFinish;
    }

    /**
     * setter for the life-cycle phase for the Vertex (e.g. Data Collection, Tabulation, Analysis)
     * @param phase String representing the life-cycle phase of the Vertex
     */
    public void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * setter for the name of the Vertex
     * @param name String name of the Vertex
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * setter the type of method element represented by the Vertex
     * @param methodType String containing the type of MethodDef element
     */
    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    /**
     * setter for the Vertex OID taken from the ODM or Define-XML OID values
     * @param oid String OID set from the ODM or Define-XML OID value
     */
    public void setOid(String oid) {
        this.oid = oid;
    }
    
    /**
     * setter for the node ID used in the GraphML version of the graph
     * @param id String id used as the node ID in the GraphML graph
     */
    public void setNodeId(String id) {
        this.nodeId = id;
    }

    /**
     * setter for the description of the Vertex or node
     * @param description String containing the Vertex or node description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * setter for the type of element in the ODM XML file
     * @param odmType String containing the ODM element type
     */
    public void setOdmElementType(String odmType) {
        this.odmElementType = odmType;
    }

    /**
     * setter for the life-cycle phase that provides the input to a method
     * @param transformStageStart String containing the life-cycle phase
     */
    public void setTransformStageStart(String transformStageStart) {
        this.transformStageStart = transformStageStart;
    }

    /**
     * setter for the life-cycle phase that contains the output of a method
     * @param transformStageFinish String containing the life-cycle phase
     */
    public void setTransformStageFinish(String transformStageFinish) {
        this.transformStageFinish = transformStageFinish;
    }    

    /**
     * getter for the fully qualified node name used to identify nodes across ODM and Define-XML files
     * @return String containing the a unique fully qualified name
     */
    public String getQualifiedName() {
        return qualifiedName;
    }

    /**
     * setter for the fully qualified node name used to identify nodes across ODM and Define-XML files
     * @param qualifiedName String containing the unique fully qualified node name
     */
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    } 
}
