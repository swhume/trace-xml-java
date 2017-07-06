xquery version "3.0";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare option output:omit-xml-declaration "no";
declare function local:findFilters($input as xs:string) as xs:string* {
  let $trace-doc := doc($input)
  for $trace-node in $trace-doc/nodes/node 
    let $filter-seq := string-join((data($trace-node/traceig), data($trace-node/traceform)), ' ') 
    for $filter in tokenize($filter-seq, '\s')
      where string-length($filter) > 0
      return $filter
};
declare function local:matchFilters($filter as xs:string*, $phase as xs:string?, 
    $type as xs:string?, $oid as xs:string) as xs:string* {
  let $utype := lower-case($type)  
  for $f in $filter              
    let $match-oid := $f
    where (($phase = "ANALYSIS") or
           ($utype = "itemdef") or
           ($utype = "methoddef") or
           ($oid = $filter))
  return $match-oid
};
declare variable $trace-doc-name as xs:string external;  
(: let $trace-doc-name := 'file:/Users/shume/Documents/Temp/trace-node-details.xml' :) 
let $filters := local:findFilters($trace-doc-name) 
let $trace-doc := doc($trace-doc-name)
return <nodes> {
for $trace-node in $trace-doc/nodes/node
  let $node-type := data($trace-node/@type)
  let $phase := data($trace-node/phase) 
  let $node-oid := data($trace-node/oid)
  where ((count(local:matchFilters($filters, $phase, $node-type, $node-oid)) > 0) or
         (count($filters) = 0)) 
    return $trace-node
} </nodes>  
