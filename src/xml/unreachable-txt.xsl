<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    version="2.0">
    <xsl:output method="text" encoding="utf-8"/> 
    <xsl:strip-space elements="*" />
    
    <xsl:template match="/">
        <!-- header row -->        
        <xsl:text>#</xsl:text> 
        <xsl:text>&#x9;</xsl:text>
        <xsl:text>Unreachable OID</xsl:text> 
        <xsl:text>&#x9;</xsl:text>
        <xsl:text>Origin</xsl:text> 
        <xsl:text>&#x9;</xsl:text>
        <xsl:text>Expected?</xsl:text> 
        <xsl:text>&#x9;</xsl:text>
        <xsl:text>&#10;</xsl:text>    
        <xsl:for-each select="/unreachable/node">
           <xsl:call-template name="node">
               <xsl:with-param name="rowNum" select="position()"/>
               <xsl:with-param name="oid" select="@oid"/>
               <xsl:with-param name="origin" select="@origin"/>
               <xsl:with-param name="expected" select="@expected"/>
           </xsl:call-template>
        </xsl:for-each>
     </xsl:template>
    
    <xsl:template name="node">
        <xsl:param name="rowNum"/>
        <xsl:param name="oid"/>
        <xsl:param name="origin"/>
        <xsl:param name="expected"/>        
        <xsl:value-of select="$rowNum"/>
        <xsl:text>&#x9;</xsl:text>
        <xsl:value-of select="$oid"/>
        <xsl:text>&#x9;</xsl:text>
        <xsl:value-of select="$origin"/>
        <xsl:text>&#x9;</xsl:text>
        <xsl:value-of select="$expected"/>
        <xsl:text>&#10;</xsl:text>    
    </xsl:template>    
</xsl:stylesheet>