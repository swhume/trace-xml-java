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
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 *  Using the CDISC ODM-XML and Define-XML files for a full study Trace-XML uses 
 *  a Define-XML extension to generate a full life-cycle study graph that provides
 *  computable traceability. In addition to validating traceability, the Trace-XML
 *  graph can be used to query traces for a variable or to visualize traceability
 *  with a visual rendering of the graph.
 *  @version 0.1
*/
public class Tracexml {
    private final List<String> nodesAddedToRoot = new LinkedList<>();
    private static final Digraph traceGraph = new Digraph();
    private final ODMGraph odmGraph = new ODMGraph();
    private final DefineGraph defGraph = new DefineGraph();
    private static String graphOutputFileName;
    private static Boolean yEdExtensions = Boolean.FALSE;
    private static Boolean isReachable = Boolean.FALSE;
    private static Boolean isValidate = Boolean.FALSE;
    private static Boolean isDisplay = Boolean.FALSE;
    private static Boolean isUnreachable = Boolean.FALSE;
    private static String cfgFileName;
    
    /** 
     * The Tracexml application takes the following command-line arguments:
     * @param args the following command-line args are accepted by Trace-XML:
     * "cfg=path" path and name of the configuration file,
     * "unreachable" lists unreachable nodes to the console,
     * "reachable" lists reachable nodes to the console,
     * "yed" generates graphML extensions for the yEd graph editor,
     * "display" loads the unreachable nodes report into the browser,
     * "verbose" requests that the application provide additional feedback to the user,
     * "help" requests that the program display the application usage options
    */
    public static void main(String[] args) {
        Tracexml trace = new Tracexml();
        // default configuration file path and file name
        cfgFileName = trace.getConfigFileDir() + "trace-xml.cfg";
        ConfigReader.setVerbose(Boolean.FALSE);        
        /* process command line arguments */
        trace.setCommandLineOptions(args);
        /* load the configuration file options */
        trace.loadConfiguration(cfgFileName);        
        /* read the metadata files */
        //TreeMap<String, Vertex> metadata = trace.readMetadataFiles();
        /* schema validate the XML files */
        if (isValidate) trace.validateXmlFiles();        
        /* generate connected graphs from the ODM or Define-XML files containing the metadata */
        trace.processMetadataFiles(trace.readMetadataFiles());
        /* add a root node */
        traceGraph.connectRootNode();
        /* test for reachability within the new graph */
        if (isReachable) trace.runReachabilityCheck();
        /* write the graph to a graphml file */
        trace.genGraphMLFile();
        /* show the unreachable nodes in the graph */
        trace.genUnreachableList();
    }

    /* show all reachable nodes in the graph */
    private void runReachabilityCheck() {
        DFS reachable = new DFS(traceGraph, "root");
        // TODO provide alternative ways of communicating reachable nodes
        System.out.println("Reachable nodes are:");
        for (String node : reachable.marked()) {
            System.out.println(node);
        }
    }
    
    /* generate unreachable nodes and categorize these as expected or unexpected
       based on the associate value in Origin     */
    private void genUnreachableList() {
        DFS reachable = new DFS(traceGraph, "root");
        HashMap<String, String> unreached = reachable.unmarked();
        List<String> expectedUnreached = new ArrayList<>();
        List<String> retestUnreached = new ArrayList<>();
        for (String node : unreached.keySet()) {
            String origin = unreached.get(node);
            if (Origin.isOriginTraceable(origin)) {
                retestUnreached.add(node);
            } else {
                expectedUnreached.add(node + " (" + origin + ")"); 
            }    
        }
        List<String> unreachable = checkUnreachableNodes(retestUnreached, expectedUnreached); 
        if ((unreachable.size() + expectedUnreached.size()) > 0) 
            Display.showUnreachables(nodesAddedToRoot, unreachable, expectedUnreached, isDisplay, isUnreachable);        
    }
    
    /* re-test unreachable nodes to determine if there is an expected reason explainng 
    why a node is unreachable, or if the node appears to be unreachable in error */
    private List<String> checkUnreachableNodes(List<String> testList, List<String> expectedList) {
        TraceNode trc = new TraceNode();
        for (String testOID : testList) {
            // generate a graph segment to test by running a query using RunTrace
            HashMap<String, String> untraced = trc.RunTrace(testOID);
            for (String oid : untraced.keySet()) {
                String origin = untraced.get(oid);
                // if the root of the fragment is expected to be untraceable 
                if (!(Origin.isOriginTraceable(origin))) {
                    // then connect this node to the root to make traceable during re-test
                    traceGraph.connectNodeToRoot(oid); 
                    nodesAddedToRoot.add(oid);
                } 
            }
        }
        return reTestUnreachablesForOrphans(expectedList);
    }

    /* runs the re-test trace for graph fragments that are expected to be traceable
    to determine if the trace terminates in an accepted way or if the node is orphaned */
    private List<String> reTestUnreachablesForOrphans(List<String> expectedList) {
        List<String> orphans = new LinkedList<>();
        genGraphMLFile();  // re-generate the graph
        DFS reachable = new DFS(traceGraph, "root");
        HashMap<String, String> unreached = reachable.unmarked();
        for (String node : unreached.keySet()) {
            String origin = unreached.get(node);
            if (Origin.isOriginTraceable(origin)) {
                orphans.add(node + " (" + origin + ")");
            } else {
                // don't add nodes that are already on the list
                if (!expectedList.contains(node + " (" + origin + ")")) {
                    expectedList.add(node + " (" + origin + ")");
                }
            }    
        }        
        return orphans;
    }

    /* generate the GraphML file from the internal graph created from the XML files */
    private void genGraphMLFile() {
        CdiscGraphML graphML = new CdiscGraphML(graphOutputFileName, yEdExtensions);    
        graphML.createGraphMLOutput(traceGraph);
    }    

    /* build the graph using content from each ODM-XML and Define-XML file */
    private void processMetadataFiles(TreeMap<String, Vertex> metadata) {
        for (String key : metadata.keySet()) {
            String nodePhase = metadata.get(key).getPhase();
            switch (nodePhase) {
                case Phase.EHR_PHASE:
                    if (ConfigReader.getVerbose()) 
                        System.out.println("Warning: EHR graph not yet implemented (Tracexml.processMetadataFiles).");
                    break;
                case Phase.DATA_COLLECTION_PHASE:
                    if (ConfigReader.getVerbose()) 
                        System.out.println("building graph from DATA_COLLECTION metadata...");
                    odmGraph.buildODMGraph(traceGraph, metadata.get(key));
                    break;
                case Phase.TABULATION_PHASE:   
                    if (ConfigReader.getVerbose()) 
                        System.out.println("building graph from TABULATION metadata...");
                    defGraph.buildDefGraph(traceGraph, metadata.get(key));
                    break;
                case Phase.ANALYSIS_PHASE:   
                    if (ConfigReader.getVerbose()) 
                        System.out.println("building graph from ANALYSIS metadata...");
                    defGraph.buildDefGraph(traceGraph, metadata.get(key));
                    break;
                case Phase.ANALYSIS_RESULTS_PHASE:
                    if (ConfigReader.getVerbose()) 
                        System.out.println("Warning: Analysis results graph not yet implemented (Tracexml.processMetadataFiles).");
                    break;
                default:
                    System.out.println("Warning: unknown lifecycle phase: " + metadata.get(key).getPhase());
                    break;
           }
       }
    }

    /* readMetadataFiles creates the ordered set of XML files to use to generate the graph 
     * @return TreeMap containing the ordered set of XML files with a life-cycle phase key */
    private TreeMap<String, Vertex> readMetadataFiles() {
        checkForMetadataFiles();
        TreeMap<String, Vertex> vMap = new TreeMap<>();
        // set data collection node    
        Vertex dcNode = new Vertex(Phase.DATA_COLLECTION_PHASE);
        dcNode.setFileName(ConfigReader.getDataCollection());
        dcNode.setPhase(Phase.DATA_COLLECTION_PHASE);
        dcNode.setDescription(Phase.DATA_COLLECTION_PHASE_DESC);
        vMap.put(Phase.DATA_COLLECTION_PHASE_ORDER, dcNode);
        // set data tabulation node    
        Vertex dtNode = new Vertex("data-tabulation");
        dtNode.setFileName(ConfigReader.getDataTabulation());
        dtNode.setPhase(Phase.TABULATION_PHASE);
        dtNode.setDescription(Phase.TABULATION_PHASE_DESC);
        vMap.put(Phase.TABULATION_PHASE_ORDER, dtNode);
        // set data analysis node    
        Vertex daNode = new Vertex("data-analysis");
        daNode.setFileName(ConfigReader.getDataAnalysis());
        daNode.setPhase(Phase.ANALYSIS_PHASE);
        daNode.setDescription(Phase.ANALYSIS_PHASE_DESC);
        vMap.put(Phase.ANALYSIS_PHASE_ORDER, daNode);
        // save for use with XQueries
        Display.writeXmlFileList(vMap);
        return vMap;
    }        
    
    /* ensure each of the expected metadata files can be found */
    private void checkForMetadataFiles() {
        checkMetadataFileFound(ConfigReader.getDataCollection(), "Error: Missing ODM data collection file in configuration file or the file listed is not found.");
        checkMetadataFileFound(ConfigReader.getDataTabulation(), "Error: Missing SDTM Define-XML file in configuration file or the file listed is not found.");
        checkMetadataFileFound(ConfigReader.getDataAnalysis(), "Error: Missing ADaM Define-XML file in configuration file or the file listed is not found.");
    }
    
    /* performs the actual test to ensure the metadata files can be found */
    private void checkMetadataFileFound(String metadataFileName, String missingFileMsg) {
        if (metadataFileName.isEmpty() ||  !(new File(metadataFileName).isFile())) {
            System.out.println(missingFileMsg);
            System.exit(0);
        }
    }
    
    /* XML schema validation option for a future release */
    private void validateXmlFiles() {
        schemaValidateXMLFile(ConfigReader.getOdmXsdFile(), ConfigReader.getDataCollection());
        schemaValidateXMLFile(ConfigReader.getDefineXsdFile(), ConfigReader.getDataTabulation());
        schemaValidateXMLFile(ConfigReader.getDefineXsdFile(), ConfigReader.getDataAnalysis());
    }        
     
    /* calls the schema validation method for a given XML file */
    private void schemaValidateXMLFile(String xsdFile, String xmlFile) {
        if (!xsdFile.isEmpty() && (new File(xsdFile).isFile())) {
            if (!ValidateXml.validate(xmlFile, xsdFile)) {
                System.out.println(xmlFile + " failed schema validation with " + xsdFile);
                System.exit(0);
            }    
        }
    }
    
    private void setCommandLineOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String argument = args[i].toLowerCase();
            if (argument.startsWith("yed")) {
                yEdExtensions = Boolean.TRUE;
            } else if (argument.contains("validate")) {
                isValidate = Boolean.TRUE;
            } else if (argument.contains("display")) {
                isDisplay = Boolean.TRUE;
            } else if (argument.equals("reachable")) {
                isReachable = Boolean.TRUE;
            } else if (argument.contains("unreachable")) {
                isUnreachable = Boolean.TRUE;
            } else if (argument.contains("verbose")) {
                ConfigReader.setVerbose(Boolean.TRUE);
            } else if (argument.startsWith("cfg=")) {
                cfgFileName = argument.substring(argument.indexOf("=")+1); 
            } else if (argument.equals("help")) {
                usage();
                System.exit(0);                
            } else {
                System.out.println("Unknown argument in CTLoad.main: " +argument);
                usage();
                System.exit(0);                
            }
        }
        
    }
    
    private void loadConfiguration(String cfgFile) {
        ConfigReader.loadConfigProperties(cfgFile);
        graphOutputFileName = ConfigReader.getXmlPath() + ConfigReader.getL3Graph();
    }
    
    /* get the current directory of the jar file as the default for the config file */
    private String getConfigFileDir() {
        String jarPath = "";
        try {
            File jarFile = new File(Tracexml.class.getProtectionDomain().getCodeSource().get‌​Location().toURI());
            String jarFilePath = jarFile.getAbsolutePath();
            jarPath = jarFilePath.replace(jarFile.getName(), "");
        } catch (URISyntaxException ex) {
            System.out.println("Unexpected error determining the current path. Please include the config file as a command-line argument. " 
                    + ex.toString());
        }
        return jarPath;
    }
    
    /* print the usage directions that include the command-line arguments */
    private void usage() {
        System.out.println("Usage: java -jar Tracexml.jar cfg=<config file> [yed] [reachable] [unreachable] [verbose] [validate] [display] [help]");        
    }
}
