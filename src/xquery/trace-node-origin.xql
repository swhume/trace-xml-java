xquery version "3.0";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare namespace odm="http://www.cdisc.org/ns/odm/v1.3";
declare namespace def="http://www.cdisc.org/ns/def/v2.0";
declare namespace trc="http://www.cdisc.org/ns/trace/v1.0";
declare namespace xlink="http://www.w3.org/1999/xlink";
declare namespace xs="http://www.w3.org/2001/XMLSchema";
declare option output:omit-xml-declaration "no";
declare variable $trace-doc-name as xs:string+ external;
declare variable $start-oid as xs:string+ external;
<nodes> {
let $trace-doc := doc($trace-doc-name)
for $trace-node in $trace-doc/nodes/node 
  let $node-oid := data($trace-node/oid)
  let $node-type := data($trace-node/type)
  let $node-file := data($trace-node/file) 
  let $phase := data($trace-node/phase) 
  let $node-id := data($trace-node/@id)
  let $odm-doc := doc($node-file)
  where (($node-type = "ItemDef") and ($node-oid != $start-oid))
  return <node oid='{$node-oid}' origin='{data($odm-doc//odm:ItemDef[@OID=$node-oid]/def:Origin/@Type)}'
  NoTraceItems='{data($odm-doc//odm:ItemDef[@OID=$node-oid]/def:Origin/@trc:NoTraceItems)}'/>     
} </nodes>  