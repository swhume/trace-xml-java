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

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

/**  
 * XsltTrace class transforms the XML outputs of Trace-XML
 * @version 0.1
 */
public class XsltTrace {
    private final String xmlFileNameIn;
    private final String xslFileNameIn;
    private String fileNameOut;
    
    /**
     * XsltTrace constructor
     * @param xmlFileName String of the XML file name and path to be transformed
     * @param xslFileName String name and path of the XSLT file to transform the XML
     */
    public XsltTrace(String xmlFileName, String xslFileName) {
        this.xmlFileNameIn = xmlFileName;
        this.xslFileNameIn = xslFileName;
    }
    
    /**
     * transformXMLFile executes the XSLT transformation on the XML file
     * @param fileNameOutput String containing the file name and path for saving the output
     */
    public void transformXMLFile(String fileNameOutput) {
        fileNameOut = fileNameOutput;
        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xslStream = new StreamSource(xslFileNameIn);
        StreamSource xmlIn = new StreamSource(xmlFileNameIn);
        StreamResult fileOut = new StreamResult(fileNameOut);
        try {
            Transformer transformer = factory.newTransformer(xslStream);
            transformer.transform(xmlIn, fileOut);
        } catch (TransformerException e) {
            System.out.println("Error transforming XML file. " + e.getMessage());
        }               
    }
}