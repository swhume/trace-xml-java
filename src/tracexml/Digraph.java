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
import java.util.Set;

/**
 * The Digraph class represents the directed graph built from the ODM and Define-XML files 
 * @version 0.1
 */
public class Digraph {
    private Vertex root;
    private HashMap<String, Vertex> digraph = new HashMap<>();
    private HashMap<String, Integer> phaseCount = new HashMap<>();
    
    /** 
     * Digraph constructor 
     */
    public Digraph() {
        root = new Vertex("root");
        digraph.put("root", root);
        initializePhaseCount();
    }
    
    /**
     * addVertex Inserts a Vertex object into the graph if it's not already there
     * @param node  Vertex object to be inserted into the graph
     */
    public void addVertex(Vertex node) {
        // do not add duplicate nodes
        if (doesVertexExist(node.getOid())) {
            System.out.println("Duplicate node found (" + node.getOid() + ") and will not be added to the graph again.");
            return;    
        }
        digraph.put(node.getOid(), node);
        incrementPhaseCount(node.getPhase());
    }
    
    private void incrementPhaseCount(String phase) {
        Integer pCount = phaseCount.get(phase);
        pCount++;
        phaseCount.replace(phase, pCount);
    }
    
    /**
     * connectRoodNode Connects a root node to the graph */
    public void connectRootNode() {
        String start = getFirstPhase();
        setFirstPhase(start);
    }

    /* used to determine the point where the lifecycle and graph start */    
    private String getFirstPhase() {
        String phase = Phase.DATA_COLLECTION_PHASE;
        if (phaseCount.get(Phase.EHR_PHASE) > 0) {
            phase = Phase.EHR_PHASE;
        } else if (phaseCount.get(Phase.DATA_COLLECTION_PHASE) > 0) {
            phase = Phase.DATA_COLLECTION_PHASE;    
        } else if (phaseCount.get(Phase.TABULATION_PHASE) > 0) {
            phase = Phase.TABULATION_PHASE;    
        } else {
            System.out.println("Warning: expected starting phase not found (Digraph.getFirstPhase).");
        }
        return phase;
    }
    
    private void setFirstPhase(String startPhase) {
        switch (startPhase) {
            case Phase.EHR_PHASE:
                System.out.println("EHR set to system start phase");
                break;
            case Phase.DATA_COLLECTION_PHASE:
                connectRootToFormDef();
                break;
            case Phase.TABULATION_PHASE:   
                connectRootToItemGroupDef(Phase.TABULATION_PHASE);
                break;
            case Phase.ANALYSIS_PHASE:   
                System.out.println("Analysis set to system start phase");
                break;
            case Phase.ANALYSIS_RESULTS_PHASE:
                System.out.println("Warning: analysis results incorrectly set to system start phase");
                break;
            default:
                System.out.println("Warning: unknown phase inccorectly set to system start phase");
                break;
        }
    }

    /* ASSUMPTION: data collection forms are the starting point for the graph */
    private void connectRootToFormDef() {
        for (String key : digraph.keySet()) {
            Vertex v = getVertex(key);
            if (v.getOdmElementType().equals("FormDef")) {
                v.addSource(root);
                root.addTarget(v);
            }
        }
    }
    
    /**
     * connectNodeToRoot adds root as the source of the node and add the node as a target of root
     * @param nodeOID String that contains the identifying OID for the node
     */
    public void connectNodeToRoot(String nodeOID) {
        Vertex v = getVertex(nodeOID);
        v.addSource(root);
        root.addTarget(v);
    }
    
    private void connectRootToItemGroupDef(String phase) {
        for (String key : digraph.keySet()) {
            Vertex v = getVertex(key);
            if (v.getPhase().equals(phase) && v.getOdmElementType().equals("ItemGroupDef")) {
                v.addSource(root);
                root.addTarget(v);
            }
        }
    }

    /**
     * dropVertex removes a node from the graph
     * @param oid String containing the OID key for the node to remove
     */
    public void dropVertex(String oid) {
        if (oid != null && !oid.isEmpty()) {
            digraph.remove(oid);
        }
    }
 
    /**
     * getVertex returns a Vertex or node given the key or node OID
     * @param key String key for the Vertex or node
     * @return the Vertex that matches the provided key, otherwise null
     */
    public Vertex getVertex(String key) {
        if (key != null && !digraph.containsKey(key)) {
            System.out.println("OID " + key + " not found.");
            return null;
        }
        return digraph.get(key);
    }
    
    /**
     * getKeys returns the set of keys for the nodes in the current graph
     * @return Set of String keys for the nodes in the digraph
     */
    public Set<String> getKeys() {
        return digraph.keySet();
    }
    
    /**
     * doesVertex exist returns a boolean value that indicates if the key of a node exists in the graph
     * @param key String key for the node or Vertex that is being tested for existence in the graph
     * @return boolean that indicates if the Vertex or node exists in the graph
     */
    public boolean doesVertexExist(String key) {
        return digraph.containsKey(key);
    }
    
    /** 
     * vertexCount returns the total number of nodes or Vertexes in the graph
     * @return Integer that indicates the number of nodes or Vertexes in the graph
     */
    public Integer vertexCount() {
        return digraph.size();
    }

    private void initializePhaseCount() {
        phaseCount.put(Phase.EHR_PHASE, 0);
        phaseCount.put(Phase.DATA_COLLECTION_PHASE, 0);
        phaseCount.put(Phase.TABULATION_PHASE, 0);
        phaseCount.put(Phase.ANALYSIS_PHASE, 0);
        phaseCount.put(Phase.ANALYSIS_RESULTS_PHASE, 0);
    }
}
