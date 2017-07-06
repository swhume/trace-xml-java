<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">

    <xsl:template match="/">
        <html lang="en">
            <head>
                <meta http-equiv="Content-Style-Type" content="text/css"/>
                <title>Unreachable Nodes</title> 
                <xsl:call-template name="GenerateCSS"/>                
            </head>
            <body>
                <table summary="Unreachable Nodes">
                    <caption class="header">Unreachable Nodes</caption>
                    <tr class="header">
                        <th scope="col">#</th>
                        <th scope="col">Element OID</th>
                        <th scope="col">Origin</th>
                        <th scope="col">Expected?</th>
                    </tr>
                    <xsl:for-each select="/unreachable/node">
                        <xsl:call-template name="node">
                            <xsl:with-param name="rowNum" select="position()"/>
                            <xsl:with-param name="oid" select="@oid"/>
                            <xsl:with-param name="origin" select="@origin"/>
                            <xsl:with-param name="expected" select="@expected"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template name="node">
        <xsl:param name="rowNum"/>
        <xsl:param name="oid"/>
        <xsl:param name="origin"/>
        <xsl:param name="expected"/>        
        <xsl:element name="tr">            
            <xsl:call-template name="rowClass">
                <xsl:with-param name="rowNum" select="position()"/>
            </xsl:call-template>
        </xsl:element>
        <td><xsl:value-of select="$rowNum"/></td>
        <td><xsl:value-of select="$oid"/></td>
        <td><xsl:value-of select="$origin"/></td>
        <td><xsl:value-of select="$expected"/></td>
    </xsl:template>
    
    <xsl:template name="rowClass">
        <xsl:param name="rowNum"/>       
        <xsl:attribute name="class">
            <xsl:choose>
                <xsl:when test="$rowNum mod 2 = 0">
                    <xsl:text>tableroweven</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>tablerowodd</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>
    
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
            
        </style>
    </xsl:template>
</xsl:stylesheet>