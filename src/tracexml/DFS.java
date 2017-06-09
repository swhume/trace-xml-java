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

import java.util.Collection;
import java.util.HashMap;

/**
 * DFS Depth-First Search class - searches the trace graph to establish node reachability
 * @version 0.1
*/
public class DFS {
    private HashMap<String, Boolean> marked;
    private Digraph graph;
    
    /**
     * DFS constructor that initiates a DFS on an internal digraph build from ODM and Define-XML
     * @param graph Digraph object that contains the graph to search
     * @param startOid String with the OID of the start node for the search
     */
    public DFS(Digraph graph, String startOid) {
        this.graph = graph;
        marked = new HashMap<>();
        Vertex start = graph.getVertex(startOid);
        for (Vertex v : start.getTarget()) {
            if (!marked.containsKey(start.getOid())) {
                directedDFS(graph, v);
            }
        }    
    }

    /* main recursive search algorithm */
    private void directedDFS(Digraph G, Vertex v) { 
        marked.put(v.getOid(), true);
        for (Vertex n : v.getTarget()) {
            if (!marked.containsKey(n.getOid())) {
                directedDFS(graph, n);
            }
        }    
    }
    
    /**
     * marked returns a list of keys of every node that's marked. Marked nodes
     * were reached during the DFS.
     * @return Collection of String keys of the set of marked nodes that were visited during the DFS 
    */
    public  Collection<String> marked() {
        return marked.keySet();
    }

    /**
     * unmarked generates the set of unmarked nodes that were not visited during the DFS 
     * @return HashMap HashMap of unmarked node String OIDs and and String Origins
    */
    public HashMap<String, String> unmarked() {
        HashMap<String, String> unmarked = new HashMap<>();
        for (String key : graph.getKeys()) {
            Vertex v = graph.getVertex(key);
            if (!marked.containsKey(v.getOid()) && !v.getOid().equals("root")) {
                unmarked.put(v.getOid(), v.getOriginType());
            }
        }
        return unmarked;
    }    
}
