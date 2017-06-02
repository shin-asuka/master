package com.vipkid.saml;

import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.identity.sso.saml.util.SAMLSSOUtil;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;


public class ParseMeta {

    private static String strIssuer = "Example:FrontEnd";
    private static String strNameID = "testUserID";
    private static String strNameQualifier = "Example:FrontEnd";
    // private static String strNamespace = "urn:oasis:names:tc:SAML:1.0:assertion";
    private static String strNamespace = "urn:bea:security:saml:groups";
    private static String strAttrName = "Groups";
    private static String strAuthMethod = "SunAccessManager";

    public static void main(String args[]) throws UnmarshallingException {
        String inCommonMDFile = "metadata.xml";

// Initialize the library
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

// Get parser pool manager
        BasicParserPool ppMgr = new BasicParserPool();
        ppMgr.setNamespaceAware(true);

// Parse metadata file
        InputStream in = ParseMeta.class.getResourceAsStream(inCommonMDFile);
        Document inCommonMDDoc = null;
        try {
            inCommonMDDoc = ppMgr.parse(in);
        } catch (XMLParserException e) {
            e.printStackTrace();
        }
        Element metadataRoot = inCommonMDDoc.getDocumentElement();

// Get apropriate unmarshaller
        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(metadataRoot);

        // Unmarshall using the document root element, an EntitiesDescriptor in this case
        Assertion inCommonMD = (Assertion) unmarshaller.unmarshall(metadataRoot);
        System.out.println(inCommonMD);
    }
}
