xquery version "3.0";
declare namespace gml="http://graphml.graphdrawing.org/xmlns";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare option output:omit-xml-declaration "no";
declare function local:replace-first( $arg as xs:string?, $pattern as xs:string, $replacement as xs:string ) as xs:string {
   replace($arg, concat('(^.*?)', $pattern), concat('$1',$replacement))
 } ;
declare function local:right-trim( $arg as xs:string? ) as xs:string {
   replace($arg,'\s+$','')
 } ;
declare function local:findSources($input as xs:string, $node-id as xs:string?) as xs:string*
{
   let $doc := doc($input)
   let $source := data($doc//gml:edge[@target=$node-id]/@source)
    return <node>{$source}
         { 
             for $src-seq in $source
               for $src in local:findSources($input, $src-seq)
             return $src
         }
    </node>
};
declare function local:findNodeSources($input as xs:string, $node-oid as xs:string?) as xs:string?
{
let $doc := doc($input)
for $node in $doc//gml:node/gml:data[@key="d2"]
  let $node-id := data($node/parent::node()/@id) 
  where data($node) = $node-oid 
  return for $src in local:findSources($input, $node-id) 
             return <node>{$src}</node>
};
declare function local:addTraceNode($input as xs:string, $node-oid as xs:string?, $source-node-ids as xs:string?) as xs:string?
{
let $doc := doc($input)
for $node in $doc//gml:node/gml:data[@key="d2"]
  let $node-id := data($node/parent::node()/@id) 
  let $seq := concat($node-id, $source-node-ids)
  where data($node) = $node-oid 
  return $seq
};
  declare variable $oid as xs:string+ external;
  declare variable $input as xs:string+ external;
  let $source-node-ids := local:findNodeSources($input, $oid)
  let $full-source-node-ids := local:addTraceNode($input, $oid, $source-node-ids)
  return <nodes> {
     for $id in tokenize(local:replace-first($full-source-node-ids, 'n', ''), "n")
        return <node>{if ($id) then concat('n', local:right-trim($id)) else $id}</node>
   }
</nodes>
