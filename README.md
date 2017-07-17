# trace-xml

## Introduction
This repository contains the Trace-XML prototype source code, examples, and sample configuration file. It also contains the Trace-XML extension
to Define-XML v2.0. The schema extension includes the referenced Define-XML v2.0.0 and ODM v1.3.2 schemas since these must be 
present for the extension to work properly.

## Getting Started
[The Trace-XML documentation is available in the CDISC wiki.](https://wiki.cdisc.org/display/~shume@cdisc.org/Trace-XML)

The documentation includes a getting started guide, an overview slide deck, and the schema extension documentation.

## Trace-Query
The Trace-Query prototype application exists in a separate repository within the Trace-XML project. The shared XQuery source, examples, and config 
file exist in the Trace-XML project only. Trace-Query executes variable traces based on the GraphML-based traceability graph created by 
Trace-XML.

## Binaries
The Trace-XML and Trace-Query lib files are available for download on the CDISC wiki. 

Lib files inlcuded:
* BaseX851.jar
* jdom-2.0.6.jar
* jdom-2.0.6-contrib.jar
* jdom-2.0.6-javadoc.jar
* jdom-2.0.6-junit.jar

Trace-XML jar files included:
* Tracexml.jar
* TraceQuery.jar
