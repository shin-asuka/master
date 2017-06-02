package com.vipkid.saml;

import org.apache.xml.security.Init;
import org.apache.xml.security.utils.Base64;
import org.opensaml.saml2.core.StatusResponseType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.*;
import org.wso2.carbon.identity.base.IdentityException;

import javax.xml.namespace.QName;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;

/**
 * Created by LP-813 on 2017/5/26.
 */
public class SignatureBuilder {
    public static StatusResponseType doSignResponse(StatusResponseType response, String signatureAlgorithm, X509Credential cred) throws IdentityException {
        try {
            Signature e = (Signature)buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
            e.setSigningCredential(cred);
            e.setSignatureAlgorithm(signatureAlgorithm);
            e.setCanonicalizationAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");

            try {
                KeyInfo signatureList = (KeyInfo)buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
                X509Data marshallerFactory = (X509Data)buildXMLObject(X509Data.DEFAULT_ELEMENT_NAME);
                X509Certificate marshaller = (X509Certificate)buildXMLObject(X509Certificate.DEFAULT_ELEMENT_NAME);
                String value = Base64.encode(cred.getEntityCertificate().getEncoded());
                marshaller.setValue(value);
                marshallerFactory.getX509Certificates().add(marshaller);
                signatureList.getX509Datas().add(marshallerFactory);
                e.setKeyInfo(signatureList);
            } catch (CertificateEncodingException var9) {
                throw new IdentityException("errorGettingCert");
            }

            response.setSignature(e);
            ArrayList signatureList1 = new ArrayList();
            signatureList1.add(e);
            MarshallerFactory marshallerFactory1 = Configuration.getMarshallerFactory();
            Marshaller marshaller1 = marshallerFactory1.getMarshaller(response);
            marshaller1.marshall(response);
            Init.init();
            Signer.signObjects(signatureList1);
            return response;
        } catch (Exception var10) {
            throw new IdentityException("Error while signing the SAML Response message.", var10);
        }
    }

    private static XMLObject buildXMLObject(QName objectQName) throws IdentityException {
        XMLObjectBuilder builder = Configuration.getBuilderFactory().getBuilder(objectQName);
        if(builder == null) {
            throw new IdentityException("Unable to retrieve builder for object QName " + objectQName);
        } else {
            return builder.buildObject(objectQName.getNamespaceURI(), objectQName.getLocalPart(), objectQName.getPrefix());
        }
    }
}
