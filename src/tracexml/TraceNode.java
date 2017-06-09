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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import org.basex.core.*;
import org.basex.core.cmd.XQuery;
import org.basex.io.serial.*;
import org.basex.query.*;
import org.basex.query.iter.*;
import org.basex.query.value.item.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


/**
 * TraceNode shares code with the TraceQuery application to trace a graph fragment
 * to its conclusion
 * @version 0.1
 */
public class TraceNode {
  private static final Context context = new Context();
  
  /**
   * RunTrace generates a trace for a graph fragment
   * @param nodeOID String of the starting node ID for the trace
   * @return HashMap containing the 
   */
  public HashMap<String, String> RunTrace(String nodeOID) {
    HashMap<String, String> traceMap = new HashMap<>();
    String outputFileName = ConfigReader.getXmlPath() + ConfigReader.getTraceNode();
    String graphMlL3FileName = ConfigReader.getXmlPath() + ConfigReader.getL3Graph();
    runNodeTrace(nodeOID, outputFileName, graphMlL3FileName);

    String outputUniqueFileName = ConfigReader.getXmlPath() + ConfigReader.getTraceNodeUnique();
    runNodeTraceUnique(outputFileName, outputUniqueFileName);

    String oidOutputFileName = ConfigReader.getXmlPath() + ConfigReader.getTraceNodeOid();
    String xmlFileName = ConfigReader.getXmlPath() + "xml-files.xml";
    runGetNodeOIDs(outputUniqueFileName, oidOutputFileName, graphMlL3FileName, xmlFileName);

    traceMap = runGetNodeDetails(oidOutputFileName, nodeOID);
    return traceMap;
  }

  /* get the origin for each node in the trace - used for reporting untraceables */
  private HashMap<String, String> runGetNodeDetails(String oidOutputFileName, String startNode) {
      HashMap<String, String> traced = new HashMap<>();
      try {
          String query = readFile(ConfigReader.getXqueryPath() +  "trace-node-origin.xql");
          // Iterate through all query results
          XQuery qry = new XQuery(query);
          qry.bind("trace-doc-name", oidOutputFileName);
          qry.bind("start-oid", startNode);
          String result = qry.execute(context);
          if (!(result == null || result.isEmpty() || result.equals("<nodes/>"))) {
              try {
                  SAXBuilder jdomBuilder = new SAXBuilder();
                  Document document = jdomBuilder.build(new StringReader(result));
                  Element rootElement = document.getRootElement();
                  List<Element> nodeList = rootElement.getChildren();
                  for (Element node : nodeList) {
                      String oid = node.getAttributeValue("oid");
                      String originVal = node.getAttributeValue("origin");
                      String noSrcItems = node.getAttributeValue("NoTraceItems");
                      if ("Yes".equals(noSrcItems) && "Derived".equals(originVal)) {
                          originVal = Origin.DERIVED_NO_SRC_ORIGIN;
                      }
                      traced.put(oid, originVal);
                  }
              } catch (JDOMException ex) {
                  System.out.println("Error reading results from the trace-node-origin XQuery. " + ex.getMessage());
              }
          }
      } catch (IOException ex) {
          System.out.println("Unable to locate or read the trace-node-origin.xql. " + ex.getMessage());
          System.exit(0);
      }
      return traced;
  }  
  
  /* using the results of the GraphML trace look up the nodes in the appropriate XML file including the file name and path */
  private static void runGetNodeOIDs(String nodeFileName, String oidOutputFileName, String graphMlL3FileName, String xmlFileName) {
    try{
        String query = readFile(ConfigReader.getXqueryPath() + "trace-node-oid.xql");
        // Iterate through all query results
        try(QueryProcessor proc = new QueryProcessor(query, context)) {
            // Store the pointer to the result in an iterator:
            proc.bind("trace-doc-name", nodeFileName);
            proc.bind("graph-doc-name", graphMlL3FileName);
            proc.bind("l1-doc-name", xmlFileName);
            Iter iter = proc.iter();
            // Create a serializer instance
            OutputStream os = new FileOutputStream(oidOutputFileName);
            Serializer ser = proc.getSerializer(os);
                // Iterate through all items and serialize contents
                for(Item item; (item = iter.next()) != null;) {
                    ser.serialize(item);
                }
        } catch (QueryException ex) {
            System.out.println("Error reading results from the trace-node-oid XQuery. " + ex.getMessage());
        }
    }   catch (IOException ex) {
          System.out.println("Unable write the results of trace-node-oid.xql to the file " + oidOutputFileName + ". " + ex.getMessage());
          System.exit(0);
      }
  }

    /* run the trace query on the GraphML file and return the nodes in the trace */
    private static void runNodeTrace(final String nodeOID, final String outputFileName, final String graphMlFileName) {
        try{
            // load the node trace query XQuery
            String query = readFile(ConfigReader.getXqueryPath() + "trace-node.xql");
            // Iterate through all query results
            try(QueryProcessor proc = new QueryProcessor(query, context)) {
                // Store the pointer to the result in an iterator:
                proc.bind("input", graphMlFileName);
                proc.bind("oid", nodeOID);
                Iter iter = proc.iter();
                // Create a serializer instance
                OutputStream os = new FileOutputStream(outputFileName);
                try(Serializer ser = proc.getSerializer(os)) {
                    // Iterate through all items and serialize contents
                    for(Item item; (item = iter.next()) != null;) {
                        ser.serialize(item);
                    }
                }
            } catch (QueryException ex) {
                System.out.println("Error reading results from the trace-node XQuery at " + outputFileName + ". " + ex.getMessage());
            }
        } catch (IOException ex) {
              System.out.println("Unable write the results of trace-node.xql to the file " + outputFileName + ". " + ex.getMessage());
              System.exit(0);
        }
    }

    /* filter a node list to create a list of unique nodes returned from a trace */
    private static void runNodeTraceUnique(final String inputFileName, final String outputFileName) {
        try{
            // load the node trace query XQuery
            String query = readFile(ConfigReader.getXqueryPath() + "trace-node-unique.xql");
            // Iterate through all query results
            try(QueryProcessor proc = new QueryProcessor(query, context)) {
                // Store the pointer to the result in an iterator:
                proc.bind("trace-doc-name", inputFileName);
                Iter iter = proc.iter();
                // Create a serializer instance
                OutputStream os = new FileOutputStream(outputFileName);
                try(Serializer ser = proc.getSerializer(os)) {
                    // Iterate through all items and serialize contents
                    for(Item item; (item = iter.next()) != null;) {
                        ser.serialize(item);
                    }
                }
            } catch (QueryException ex) {
                System.out.println("Error reading results from the trace-node-unique XQuery. " + ex.getMessage());
            }
        }   catch (IOException ex) {
              System.out.println("Unable to locate or read the trace-node-unique.xql. " + ex.getMessage());
              System.exit(0);
        }
    }

    /**
     * reads the XQuery file and returns the content as a string
     * @param file String contains the name and path to the XQuery file
     * @return String containing the XQuery file content (the query)
     */
  protected static String readFile(String file) {
    BufferedReader f = null;
    Boolean ioException = Boolean.FALSE;
    StringBuilder xml = new StringBuilder();
      try {
          f = new BufferedReader(new FileReader(file));
          String line;
          while((line = f.readLine()) != null)
              xml.append(line);
      } catch (FileNotFoundException ex) {
          System.out.println("Unable to locate the XQuery file " + file + ". " + ex.getMessage());
          ioException = Boolean.TRUE;
      } catch (IOException ex) {
          System.out.println("Unable to read the XQuery file " + file + ". " + ex.getMessage());
          ioException = Boolean.TRUE;
      } finally {
          try {
              if (f != null) f.close();
          } catch (IOException ex) {
              System.out.println("Unable to close the XQuery file " + file + ". " + ex.getMessage());
          } finally {
              if (ioException) System.exit(0);
          }
      }
      return xml.toString();
    }
  } 