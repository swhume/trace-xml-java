xquery version "3.0";
declare namespace output = "http://www.w3.org/2010/xslt-xquery-serialization";
declare namespace odm="http://www.cdisc.org/ns/odm/v1.3";
declare namespace def="http://www.cdisc.org/ns/def/v2.0";
declare namespace trc="http://www.cdisc.org/ns/trace/v1.0";
declare namespace xlink="http://www.w3.org/1999/xlink";
declare namespace xs="http://www.w3.org/2001/XMLSchema";
declare option output:omit-xml-declaration "no";
declare variable $trace-doc-name as xs:string external;
<nodes> {
let $trace-doc := doc($trace-doc-name)
for $trace-node in $trace-doc/nodes/node 
  let $node-oid := data($trace-node/oid)
  let $node-type := data($trace-node/type)
  let $node-file := data($trace-node/file) 
  let $phase := data($trace-node/phase) 
  let $node-id := data($trace-node/@id)
  let $odm-doc := doc($node-file)
  let $elem-def := switch ($node-type)
    case "ItemDef" return $odm-doc//odm:ItemDef[@OID=$node-oid]
    case "ItemGroupDef" return $odm-doc//odm:ItemGroupDef[@OID=$node-oid]
    case "FormDef" return  $odm-doc//odm:FormDef[@OID=$node-oid]
     default return  $odm-doc//odm:MethodDef[@OID=$node-oid]
  let $elem-desc := switch ($node-type)
    case "ItemDef" return <node id='{$node-id}' type='ItemDef'>
            <oid>{$node-oid}</oid>
            <name>{data($elem-def/@Name)}</name>
            <desc>{data($elem-def/odm:Description/odm:TranslatedText)}</desc>
            <phase>{$phase}</phase>
            <datatype>{data($elem-def/@DataType)}</datatype>
            <length>{data($elem-def/@Length)}</length>
            <sasname>{data($elem-def/@SASFieldName)}</sasname>
            <sigdigits>{data($elem-def/@SignificantDigits)}</sigdigits>
            <dispformat>{data($elem-def/@def:DisplayFormat)}</dispformat>
            <question>{data($elem-def/odm:Question/odm:TranslatedText)}</question>
            <codelist>{data($odm-doc//odm:CodeList[@OID=data($elem-def/odm:CodeListRef/@CodeListOID)]/@Name)}</codelist>
            <origin>{data($elem-def/def:Origin/@Type)}</origin>
            <traceig>{distinct-values(data($elem-def/def:Origin/trc:Trace/trc:TraceItem/@ItemGroupOID))}</traceig>
            <traceform>{distinct-values(data($elem-def/def:Origin/trc:Trace/trc:TraceItem/@FormOID))}</traceform>
            <valuelist>{data($elem-def/def:ValueListRef/@ValueListOID)}</valuelist>
            <comment>{data($odm-doc//def:CommentDef[@OID=data($elem-def/@def:CommentOID)]/odm:Description/odm:TranslatedText)}</comment>
        </node>     
    case "ItemGroupDef" return <node id='{$node-id}' type='ItemGroupDef'>
            <oid>{$node-oid}</oid>
            <name>{data($elem-def/@Name)}</name>
            <desc>{data($elem-def/odm:Description/odm:TranslatedText)}</desc>
            <phase>{$phase}</phase>
            <domain>{data($elem-def/@Domain)}</domain>
            <refdata>{data($elem-def/@IsReferenceData)}</refdata>
            <sasdataset>{data($elem-def/@SASDatasetName)}</sasdataset>
            <purpose>{data($elem-def/@Purpose)}</purpose>
            <structure>{data($elem-def/@def:Structure)}</structure>
            <class>{data($elem-def/@def:Class)}</class>            
            <comment>{data($odm-doc//def:CommentDef[@OID=data($elem-def/@def:CommentOID)]/odm:Description/odm:TranslatedText)}</comment>
        </node>
    case "FormDef" return <node id='{$node-id}' type='FormDef'>
            <oid>{$node-oid}</oid>
            <name>{data($elem-def/@Name)}</name>
            <desc>{data($elem-def/odm:Description/odm:TranslatedText)}</desc>
            <phase>{$phase}</phase>            
            <repeating>{data($elem-def/@Repeating)}</repeating>
        </node>
    default return
        <node id='{$node-id}' type='MethodDef'>
            <oid>{$node-oid}</oid>
            <name>{data($elem-def/@Name)}</name>
            <type>{data($elem-def/@Type)}</type>
            <desc>{data($elem-def/odm:Description/odm:TranslatedText)}</desc>
            <phase>{$phase}</phase>
            <fexcontext>{data($elem-def/odm:FormalExpression/@Context)}</fexcontext>
            <fex>{data($elem-def/odm:FormalExpression)}</fex>
        </node>
     
  return $elem-desc
} </nodes>  