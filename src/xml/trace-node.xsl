<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" encoding="utf-8" doctype-system="http://www.w3.org/TR/html4/strict.dtd"
    doctype-public="-//W3C//DTD HTML 4.01//EN" version="4.0"/>
  
  <!-- Global Variables -->  
  <xsl:variable name="g_TraceVariableName" select="/nodes/node[1]/name"/>
  <xsl:variable name="g_TraceVariableOID" select="/nodes/node[1]/oid"/>
  <xsl:variable name="g_TraceVariableType" select="/nodes/node[1]/@type"/>
  <xsl:variable name="g_TraceVariablePhase" select="/nodes/node[1]/phase"/>
    
  <!-- ***************************************************************** -->
  <!-- Create the HTML Header                                            -->
  <!-- ***************************************************************** -->
  <xsl:template match="/">
    <xsl:param name="rowNum"/>
    <html lang="en">
      <head>
        <meta http-equiv="Content-Script-Type" content="text/javascript"/>
        <meta http-equiv="Content-Style-Type" content="text/css"/>
        <title> Trace for <xsl:value-of select="/nodes/node[1]/@type"/> element <xsl:value-of select="/nodes/node[1]/name"/> with OID 
          <xsl:value-of select="/nodes/node[1]/oid"/></title>
        <xsl:call-template name="GenerateCSS"/>
      </head>
      <body>
        <table summary="Data Definition Tables">
          <caption class="header">Trace for <xsl:value-of select="$g_TraceVariableType"/> Element Named <xsl:value-of select="$g_TraceVariableName"/>   
            (<xsl:value-of select="$g_TraceVariablePhase"/>)</caption>
          <xsl:for-each select="/nodes/node">
            <xsl:call-template name="ItemGroupDefs">
              <xsl:with-param name="rowNum" select="position()"/>
            </xsl:call-template>
          </xsl:for-each>
        </table>
      </body>
    </html>
  </xsl:template>

      <!-- **************************************************** -->
      <!-- Template: ItemGroupDefs                              -->
      <!-- **************************************************** -->
      <xsl:template name="ItemGroupDefs">
        <xsl:param name="rowNum"/>
        <xsl:choose>
          <xsl:when test="@type='ItemDef'">
            <tr class="header">
              <th scope="col">#</th>
              <th scope="col">Node ID (OID)</th>
              <th scope="col">Phase</th>
              <th scope="col">Name</th>
              <th scope="col">Type</th>
              <th scope="col">Description</th>
              <th scope="col">Origin</th>
              <th scope="col">DataType</th>
              <th scope="col">Length</th>
              <th scope="col">CodeList</th>
              <th scope="col">ValueList</th>
              <th scope="col">Comment</th>
            </tr>
            <tr>                  
              <td><xsl:value-of select="$rowNum"/></td>
              <td><xsl:value-of select="@id"/> <p>(<xsl:value-of select="./oid"/>)</p></td>
              <td><xsl:value-of select="./phase"/></td>
              <td><xsl:value-of select="./name"/></td>
              <td><xsl:value-of select="@type"/></td>
              <td><xsl:value-of select="./desc"/></td>
              <td><xsl:value-of select="./origin"/></td>
              <td><xsl:value-of select="./datatype"/></td>
              <td><xsl:value-of select="./length"/></td>
              <td><xsl:value-of select="./codelist"/></td>
              <td><xsl:value-of select="./valuelist"/></td>
              <td><xsl:value-of select="./comment"/></td>
            </tr>
          </xsl:when>
          <xsl:when test="@type='ItemGroupDef'">
            <tr class="header">
              <th scope="col">#</th>
              <th scope="col">Node ID (OID)</th>
              <th scope="col">Phase</th>
              <th scope="col">Name</th>
              <th scope="col">Type</th>
              <th scope="col">Description</th>
              <th scope="col">Domain</th>
              <th scope="col">Purpose</th>
              <th scope="col">Class</th>
              <th scope="col">Structure</th>
              <th scope="col">SAS Dataset</th>
              <th scope="col">Comment</th>
            </tr>
            <tr>                              
              <td><xsl:value-of select="$rowNum"/></td>
              <td><xsl:value-of select="@id"/> <p>(<xsl:value-of select="./oid"/>)</p></td>
              <td><xsl:value-of select="./phase"/></td>
              <td><xsl:value-of select="./name"/></td>
              <td><xsl:value-of select="@type"/></td>
              <td><xsl:value-of select="./desc"/></td>
              <td><xsl:value-of select="./domain"/></td>
              <td><xsl:value-of select="./purpose"/></td>
              <td><xsl:value-of select="./class"/></td>
              <td><xsl:value-of select="./structure"/></td>
              <td><xsl:value-of select="./sasdataset"/></td>
              <td><xsl:value-of select="./comment"/></td>
            </tr>
        </xsl:when>
        <xsl:when test="@type='MethodDef'">
          <tr class="header">
            <th scope="col">#</th>
            <th scope="col">Node ID (OID)</th>
            <th scope="col">Phase</th>
            <th scope="col">Name</th>
            <th scope="col">Type</th>
            <th scope="col" colspan="2">Description</th>
            <th scope="col" colspan="2">Formal Expression Context</th>
            <th scope="col" colspan="2">Formal Expression</th>
            <th scope="col">Comment</th>
          </tr>
          <tr>                              
            <td><xsl:value-of select="$rowNum"/></td>
            <td><xsl:value-of select="@id"/> <p>(<xsl:value-of select="./oid"/>)</p></td>
            <td><xsl:value-of select="./phase"/></td>
            <td><xsl:value-of select="./name"/></td>
            <td><xsl:value-of select="@type"/></td>
            <td colspan="2"><xsl:value-of select="./desc"/></td>
            <td colspan="2"><xsl:value-of select="./fexcontext"/></td>
            <td colspan="2"><xsl:value-of select="./fex"/></td>
            <td><xsl:value-of select="./comment"/></td>
          </tr>
        </xsl:when>
          <xsl:when test="@type='FormDef'">
            <tr class="header">
              <th scope="col">#</th>
              <th scope="col">Node ID (OID)</th>
              <th scope="col">Phase</th>
              <th scope="col">Name</th>
              <th scope="col">Type</th>
              <th scope="col">Repeating</th>
              <th scope="col" colspan="6">Description</th>
            </tr>
            <tr>                              
              <td><xsl:value-of select="$rowNum"/></td>
              <td><xsl:value-of select="@id"/> <p>(<xsl:value-of select="./oid"/>)</p></td>
              <td><xsl:value-of select="./phase"/></td>
              <td><xsl:value-of select="./name"/></td>
              <td><xsl:value-of select="@type"/></td>
              <td><xsl:value-of select="./repeating"/></td>
              <td colspan="6"><xsl:value-of select="./desc"/></td>
            </tr>
          </xsl:when>
          <xsl:otherwise>
            <tr><td colspan="12">Unknown element type <xsl:value-of select="@type"/> for node id
              <xsl:value-of select="@id"/> and oid <xsl:value-of select="./oid"/></td></tr>
          </xsl:otherwise>        
        </xsl:choose>
      </xsl:template>
  
<!-- ************************************************************* -->
<!-- Generate CSS                                                  -->
<!-- ************************************************************* -->
<xsl:template name="GenerateCSS">
<style type="text/css">
  body{
  background-color:#FFFFFF;
  font-family:Verdana, Arial, Helvetica, sans-serif;        
  font-size:62.5%;
  margin:0;
  padding:30px;        
  }
  
  h1{
  font-size:1.6em;
  margin-left:0;
  font-weight:bolder;
  text-align:left;
  color:#800000;
  }
  
  ul{
  margin-left:0px;
  }
  
  a{
  color:#0000FF;
  text-decoration:underline;
  }
  a.visited{
  color:#551A8B;
  text-decoration:underline;
  }
  a:hover{
  color:#FF9900;
  text-decoration:underline;
  }
  a.tocItem{
  color:#004A95;
  text-decoration:none;
  margin-top:2px;
  font-size:1.4em;
  }
  a.tocItem.level2{
  margin-left:15px;
  }
  
  #menu{
  position:fixed;
  left:0px;
  top:10px;
  width:20%;
  height:96%;
  bottom:0px;
  overflow:auto;
  background-color:#FFFFFF;
  color:#000000;
  border:0px none black;
  text-align:left;
  white-space:nowrap;
  }
  
  .hmenu li{
  list-style:none;
  line-height:200%;
  padding-left:0;
  }
  .hmenu ul{
  padding-left:14px;
  margin-left:0;
  }
  .hmenu-item{
  }
  .hmenu-submenu{
  }
  .hmenu-bullet{
  float:left;
  width:16px;
  color:#AAAAAA;
  font-size:1.2em;
  }
  
  #main{
  position:absolute;
  left:22%;
  top:0px;
  overflow:auto;
  color:#000000;
  background-color:#FFFFFF;
  }
  
  #main .docinfo{
  width:95%;
  text-align:right;
  padding: 0px 5px;
  }
  
  div.containerbox{
  padding:0px;
  margin:10px auto;
  border:0px solid #999;
  page-break-after:always;
  }
  
  div.codelist{
  page-break-after:avoid;
  }
  
  table{
  width:95%;
  border-spacing:4px;
  border:1px solid #000000;
  background-color:#EEEEEE;
  margin-top:5px;
  border-collapse:collapse;
  padding:5px;
  empty-cells:show;
  }
  
  table caption{
  border:0px solid #999999;
  left:20px;
  font-size:1.4em;
  font-weight:bolder;
  color:#800000;
  margin:10px auto;
  text-align:left;
  }
  
  table caption .dataset{
  font-weight:normal;
  }
  
  table caption.header{
  font-size:1.6em;
  margin-left:0;
  font-weight:bolder;
  text-align:left;
  color:#800000;
  }
  
  table tr{
  border:1px solid #000000;
  }
  
  table tr.header{
  background-color:#6699CC;
  color:#FFFFFF;
  font-weight:bold;
  }
  
  table th{
  font-weight:bold;
  vertical-align:top;
  text-align:left;
  padding:5px;
  border:1px solid #000000;
  font-size:1.3em;
  }
  
  table td{
  vertical-align:top;
  padding:5px;
  border:1px solid #000000;
  font-size:1.2em;
  line-height:150%;
  }
  
  table th.codedvalue{
  width:20%;
  }
  table th.length{
  width:7%;
  }
  table td.datatype{
  text-align:center;
  }
  table td.number{
  text-align:right;
  }
  .tablerowodd{
  background-color:#FFFFFF;
  }
  .tableroweven{
  background-color:#E2E2E2;
  }
  
  .linebreakcell{
  vertical-align:top;
  margin-top:3px;
  margin-bottom:3px;
  }
  
  .nci, .extended{
  font-style:italic;
  }
  .super{
  vertical-align:super;
  }
  .footnote{
  font-size:1.2em;
  }
  
  .standard{
  font-size:1.6em;
  font-weight:bold;
  text-align:left;
  padding:15px;
  margin-left:20px;
  margin-top:40px;
  margin-right:20px;
  margin-bottom:20px;
  color:#800000;
  border:0px;
  }
  
  .study{
  font-size:1.6em;
  font-weight:bold;
  text-align:left;
  padding:0px;
  margin-left:0px;
  margin-top:00px;
  margin-right:0px;
  margin-bottom:0px;
  color:#800000;
  border:0px none;
  }
  
  .linktop{
  font-size:1.2em;
  margin-top:5px;
  }
  .documentinfo, .stylesheetinfo{
  font-size:1.2em;
  }
  
  .invisible{
  display:none;
  }
  
  span.error{
  width:95%;
  font-size:1.6em;
  font-weight: bold;	
  padding:5px;
  color:#FF0000;
  border-spacing:4px;
  border:2px solid #FF0000;
  }
  td.error{
  color:#FF0000;
  }
  
  .arm-table{ background-color:#ececec;}
  
  table th.label{
  width:13%;
  }
  .arm{
  margin-top:5px;
  margin-bottom:5px;
  margin-left:5px;
  margin-right:0;
  background-color:#C0C0C0;
  width:97%;
  }
  
  .title{ margin-left:5pt; }
  
  p.summaryresult{ margin-left:15px; margin-top:5px; margin-bottom:5px;}
  p.parameter{ margin-top:5px; margin-bottom:5px;}
  p.analysisvariable{ margin-top:5px; margin-bottom:5px;}
  .datareference{ margin-top:5px; margin-bottom:5px;}
  tr.analysisresult{ background-color:#6699CC; color:#FFFFFF; font-weight:bold; border:1px solid black;}
  
  .code-context{
  padding:5px 0px;
  }
  .coderef{
  font-size:1.2em;
  line-height:150%;
  padding:5px;
  }
  .code{
  font-family:"Courier New", monospace, serif;
  font-size:1.2em;
  line-height:150%;
  white-space:pre;
  display:block;
  vertical-align:top;
  padding:5px;
  }
  
  
  dl.multiple-table
  {
  width:95%;
  padding: 5px 0px;
  font-size:0.8em;
  color:#000000;
  }

  dl.multiple-table dt
  {
  clear: left;
  float: left;
  width: 200px;
  margin: 0;
  padding: 5px 5px 5px 0px;
  font-weight: bold;
  }
  
  dl.multiple-table dd
  {
  margin-left: 210px;
  padding: 5px;
  font-weight: normal;
  }
  
  @media print{
  
  body, h1, table caption, table caption.header{
  color:#000000;
  }
  
  a:link,
  a:visited{
  background:transparent;
  text-decoration:none;
  color:#000000;
  }
  a.external:link:after,
  #main a:visited:after{
  content:" &lt;" attr(href) "&gt; ";
  font-size:90%;
  text-decoration:none;
  font-weight:bold;
  color:#808080;
  }
    
  table{
  border-width:2px;
  }
  
  #menu,
  .linktop, .stylesheetinfo{
  display:none !important;
  width:0px;
  }
  #main{
  left:0px;
  }
 
 }
</style>
</xsl:template>
</xsl:stylesheet>
