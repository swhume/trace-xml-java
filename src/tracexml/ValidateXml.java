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

import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * ValidateXml runs schema validation on the XML files
 * @version 0.1
 */
public class ValidateXml {

    /**
     * validate is a static method that schema validates the XML input files and provides a boolean to indicate
     * if the file passed or failed validation. Error messages are also printed to the console when validation fails.
     * @param xmlPathFile String containing the path and filename to the XML file to validate
     * @param xsdPathFile String containing the path and filename to the XSD schema file used to validate the XML file
     * @return Boolean value indicating if the XML file passed or failed schema validation
     */
    public static Boolean validate(String xmlPathFile, String xsdPathFile) {
        Boolean isValid = Boolean.FALSE;

        try {
            String schemaFactoryProperty = "javax.xml.validation.SchemaFactory:" + XMLConstants.W3C_XML_SCHEMA_NS_URI;

            System.setProperty(schemaFactoryProperty, "org.apache.xerces.jaxp.validation.XMLSchemaFactory");

            DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            parserFactory.setIgnoringElementContentWhitespace(true);
            parserFactory.setIgnoringComments(true);

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Source schemaSource = new StreamSource(new File(xsdPathFile));
            Schema schema = factory.newSchema(schemaSource);
            parserFactory.setSchema(schema);
            DocumentBuilder parser = parserFactory.newDocumentBuilder();

            Validator validator = schema.newValidator();
            validator.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            validator.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);
            
            File define = new File(xmlPathFile);
            Document document = parser.parse(new File(define.getPath()));
            validator.validate(new DOMSource(document));
            isValid = Boolean.TRUE;
            if (ConfigReader.getVerbose()) System.out.println(xmlPathFile + " successfully schema validated");
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            System.out.println("Validation error: " + ex.getMessage());
        }
        return isValid;
    }
}
   