xquery version "3.0";
declare namespace gml="http://graphml.graphdrawing.org/xmlns";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare option output:omit-xml-declaration "no";
declare variable $trace-doc-name as xs:string+ external;
declare variable $graph-doc-name as xs:string+ external;
declare variable $l1-doc-name as xs:string+ external;
<nodes> {
(: let $trace-doc-name := 'file:/d:/src/odm-prov/ip-map-xml/trace-nodes.xml'
let $graph-doc-name := 'file:/d:/src/odm-prov/ip-map-xml/L3-test-graph-raw.graphml'
let $l1-doc-name := 'file:/d:/src/odm-prov/ip-map-xml/xml-files.xml' :)
let $trace-doc := doc($trace-doc-name)
let $graph-doc := doc($graph-doc-name)
let $l1-doc := doc($l1-doc-name)
for $node-phase in ("ANALYSIS", "TABULATION", "DATA_COLLECTION")
    for $trace-node in distinct-values($trace-doc/nodes/node) 
      let $node-id := data($trace-node)
      for $node in $graph-doc//gml:node[@id=$node-id]
        let $node-oid := data($node/gml:data[@key="d2"])
        let $node-type := data($node/gml:data[@key="d16"])    
        let $phase := data($node/gml:data[@key="d4"])
        for $l1-node in $l1-doc//file
          let $odm-file := data($l1-node)
          where (($l1-node/@phase = $phase) and ($node-phase = $phase))
  return <node id="{$node-id}"><oid>{$node-oid}</oid><type>{$node-type}</type><phase>{$phase}</phase><file>{$odm-file}</file></node>
}</nodes>