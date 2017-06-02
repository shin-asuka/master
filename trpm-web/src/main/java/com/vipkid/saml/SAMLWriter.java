package com.vipkid.saml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;
import org.wso2.carbon.identity.core.model.SAMLSSOServiceProviderDO;

/**
 * This is a demo class which creates a valid SAML 2.0 Assertion.
 */
public class SAMLWriter {

    public static Assertion AssertionBuild() {
        try {
            SAMLInputContainer input = new SAMLInputContainer();
            input.strIssuer = "vipkid-teacher-sso";
            input.strNameID = "yangchao@vipkid.com.cn";
            input.strNameQualifier = "Influitive-AdvocateHub";
            input.email = "yangchao@vipkid.com.cn";
            input.samlConsumerUrl = "https://vipkiddemo.influitive.com/saml/consume";
            input.inResponseTo = "_330f4e90-2994-0135-1f2a-22000b97c7aa";//SAMLRequest的唯一ID，实时变化

            Map customAttributes = new HashMap();
            customAttributes.put("FirstName", "Chao");
            customAttributes.put("LastName", "Yang");
            customAttributes.put("Email","yangchao@vipkid.com.cn");

            input.attributes = customAttributes;

            Assertion assertion = SAMLWriter.buildDefaultAssertion(input);
            AssertionMarshaller marshaller = new AssertionMarshaller();
            Element plaintextElement = marshaller.marshall(assertion);
            String originalAssertionString = XMLHelper.nodeToString(plaintextElement);

            System.out.println("Assertion String: " + originalAssertionString);

            // TODO: now you can also add encryption....
            return assertion;
        } catch (MarshallingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static XMLObjectBuilderFactory builderFactory;

    public static XMLObjectBuilderFactory getSAMLBuilder() throws ConfigurationException{

        if(builderFactory == null){
            // OpenSAML 2.3
            DefaultBootstrap.bootstrap();
            builderFactory = Configuration.getBuilderFactory();
        }

        return builderFactory;
    }

    /**
     * Builds a SAML Attribute of type String
     * @param name
     * @param value
     * @param builderFactory
     * @return
     * @throws ConfigurationException
     */
    public static Attribute buildStringAttribute(String name, String value, XMLObjectBuilderFactory builderFactory) throws ConfigurationException
    {
        SAMLObjectBuilder attrBuilder = (SAMLObjectBuilder) getSAMLBuilder().getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
        Attribute attrFirstName = (Attribute) attrBuilder.buildObject();
        attrFirstName.setName(name);

        // Set custom Attributes
        XMLObjectBuilder stringBuilder = getSAMLBuilder().getBuilder(XSString.TYPE_NAME);
        XSString attrValueFirstName = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        attrValueFirstName.setValue(value);

        attrFirstName.getAttributeValues().add(attrValueFirstName);
        return attrFirstName;
    }

    /**
     * Helper method which includes some basic SAML fields which are part of almost every SAML Assertion.
     *
     * @param input
     * @return
     */
    public static Assertion  buildDefaultAssertion(SAMLInputContainer input)
    {
        try
        {
            // Create the NameIdentifier
            SAMLObjectBuilder nameIdBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(NameID.DEFAULT_ELEMENT_NAME);
            NameID nameId = (NameID) nameIdBuilder.buildObject();
            nameId.setValue(input.getStrNameID());
            nameId.setNameQualifier(input.getStrNameQualifier());
            nameId.setFormat(NameID.EMAIL);
            nameId.setValue(input.getEmail());

            // Create the SubjectConfirmation

            SAMLObjectBuilder confirmationMethodBuilder = (SAMLObjectBuilder)  SAMLWriter.getSAMLBuilder().getBuilder(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
            SubjectConfirmationData confirmationMethod = (SubjectConfirmationData) confirmationMethodBuilder.buildObject();
            DateTime now = new DateTime();
            confirmationMethod.setNotOnOrAfter(now.plusDays(2));
            confirmationMethod.setRecipient(input.getSamlConsumerUrl());

            SAMLObjectBuilder subjectConfirmationBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
            SubjectConfirmation subjectConfirmation = (SubjectConfirmation) subjectConfirmationBuilder.buildObject();
            subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
            subjectConfirmation.setSubjectConfirmationData(confirmationMethod);

            // Create the Subject
            SAMLObjectBuilder subjectBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Subject.DEFAULT_ELEMENT_NAME);
            Subject subject = (Subject) subjectBuilder.buildObject();

            subject.setNameID(nameId);
            subject.getSubjectConfirmations().add(subjectConfirmation);

            // Create Authentication Statement
            SAMLObjectBuilder authStatementBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(AuthnStatement.DEFAULT_ELEMENT_NAME);
            AuthnStatement authnStatement = (AuthnStatement) authStatementBuilder.buildObject();
            //authnStatement.setAuthenticationMethod(strAuthMethod);
            DateTime now2 = new DateTime();
            authnStatement.setAuthnInstant(now2);

            SAMLObjectBuilder authContextBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(AuthnContext.DEFAULT_ELEMENT_NAME);
            AuthnContext authnContext = (AuthnContext) authContextBuilder.buildObject();

            SAMLObjectBuilder authContextClassRefBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
            AuthnContextClassRef authnContextClassRef = (AuthnContextClassRef) authContextClassRefBuilder.buildObject();
            authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified"); // TODO not sure exactly about this

            authnContext.setAuthnContextClassRef(authnContextClassRef);
            authnStatement.setAuthnContext(authnContext);

            // Builder Attributes
            SAMLObjectBuilder attrStatementBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(AttributeStatement.DEFAULT_ELEMENT_NAME);
            AttributeStatement attrStatement = (AttributeStatement) attrStatementBuilder.buildObject();

            // Create the attribute statement
            Map<String,String> attributes = input.getAttributes();
            if(attributes != null){
                Set<String> keySet = attributes.keySet();
                for (String key : keySet)
                {
                    Attribute attrFirstName = buildStringAttribute(key, (String)attributes.get(key), getSAMLBuilder());
                    attrStatement.getAttributes().add(attrFirstName);
                }
            }

            SAMLObjectBuilder conditionsBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
            Conditions conditions = (Conditions) conditionsBuilder.buildObject();
            conditions.setNotBefore(new DateTime());
            conditions.setNotOnOrAfter(new DateTime().plusDays(2));
            // Create Issuer
            SAMLObjectBuilder issuerBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
            Issuer issuer = (Issuer) issuerBuilder.buildObject();
            issuer.setValue(input.getStrIssuer());

            // Create the assertion
            SAMLObjectBuilder assertionBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Assertion.DEFAULT_ELEMENT_NAME);
            Assertion assertion = (Assertion) assertionBuilder.buildObject();
            assertion.setIssuer(issuer);
            assertion.setIssueInstant(now);
            assertion.setID(UUID.randomUUID().toString());
            assertion.setVersion(SAMLVersion.VERSION_20);
            assertion.setSubject(subject);
            assertion.getAuthnStatements().add(authnStatement);
            assertion.getAttributeStatements().add(attrStatement);
            assertion.setConditions(conditions);

            return assertion;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static class SAMLInputContainer
    {

        private String strIssuer;
        private String strNameID;
        private String strNameQualifier;
        private String sessionId;
        private int maxSessionTimeoutInMinutes = 2880; // default is 15 minutes
        private String email;
        private Map attributes;
        private String samlConsumerUrl;
        private String inResponseTo;
        /**
         * Returns the strIssuer.
         *
         * @return the strIssuer
         */
        public String getStrIssuer()
        {
            return strIssuer;
        }

        /**
         * Sets the strIssuer.
         *
         * @param strIssuer
         *            the strIssuer to set
         */
        public void setStrIssuer(String strIssuer)
        {
            this.strIssuer = strIssuer;
        }

        /**
         * Returns the strNameID.
         *
         * @return the strNameID
         */
        public String getStrNameID()
        {
            return strNameID;
        }

        /**
         * Sets the strNameID.
         *
         * @param strNameID
         *            the strNameID to set
         */
        public void setStrNameID(String strNameID)
        {
            this.strNameID = strNameID;
        }

        /**
         * Returns the strNameQualifier.
         *
         * @return the strNameQualifier
         */
        public String getStrNameQualifier()
        {
            return strNameQualifier;
        }

        /**
         * Sets the strNameQualifier.
         *
         * @param strNameQualifier
         *            the strNameQualifier to set
         */
        public void setStrNameQualifier(String strNameQualifier)
        {
            this.strNameQualifier = strNameQualifier;
        }

        /**
         * Sets the attributes.
         *
         * @param attributes
         *            the attributes to set
         */
        public void setAttributes(Map attributes)
        {
            this.attributes = attributes;
        }

        /**
         * Returns the attributes.
         *
         * @return the attributes
         */
        public Map getAttributes()
        {
            return attributes;
        }

        /**
         * Sets the sessionId.
         * @param sessionId the sessionId to set
         */
        public void setSessionId(String sessionId)
        {
            this.sessionId = sessionId;
        }

        /**
         * Returns the sessionId.
         * @return the sessionId
         */
        public String getSessionId()
        {
            return sessionId;
        }

        /**
         * Sets the maxSessionTimeoutInMinutes.
         * @param maxSessionTimeoutInMinutes the maxSessionTimeoutInMinutes to set
         */
        public void setMaxSessionTimeoutInMinutes(int maxSessionTimeoutInMinutes)
        {
            this.maxSessionTimeoutInMinutes = maxSessionTimeoutInMinutes;
        }

        /**
         * Returns the maxSessionTimeoutInMinutes.
         * @return the maxSessionTimeoutInMinutes
         */
        public int getMaxSessionTimeoutInMinutes()
        {
            return maxSessionTimeoutInMinutes;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSamlConsumerUrl() {
            return samlConsumerUrl;
        }

        public void setSamlConsumerUrl(String samlConsumerUrl) {
            this.samlConsumerUrl = samlConsumerUrl;
        }

        public String getInResponseTo() {
            return inResponseTo;
        }

        public void setInResponseTo(String inResponseTo) {
            this.inResponseTo = inResponseTo;
        }
    }

}