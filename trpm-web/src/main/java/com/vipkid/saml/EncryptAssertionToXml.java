package com.vipkid.saml;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.encryption.Encrypter;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.encryption.EncryptionConstants;
import org.opensaml.xml.encryption.EncryptionException;
import org.opensaml.xml.encryption.EncryptionParameters;
import org.opensaml.xml.encryption.KeyEncryptionParameters;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xml.security.x509.X509Util;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

import java.io.IOException;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by LP-813 on 2017/5/25.
 */

public class EncryptAssertionToXml {

    public static EncryptedAssertion  encryptAssertion(Assertion assertion){

        String privateKeyPath = "D:\\keys\\smallkey";
        String publicKeyPath = "D:\\keys\\small.crt";
        // The Assertion to be encrypted
        // Assume this contains a recipient's RSA public key
        Credential keyEncryptionCredential = null;
        try {
            keyEncryptionCredential = KeyFetch.getCredentialFromFilePath(publicKeyPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyException e) {
            e.printStackTrace();
        }

        EncryptionParameters encParams = new EncryptionParameters();
        encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);

        KeyEncryptionParameters kekParams = new KeyEncryptionParameters();
        kekParams.setEncryptionCredential(keyEncryptionCredential);
        kekParams.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        KeyInfoGeneratorFactory kigf =
                Configuration.getGlobalSecurityConfiguration()
                        .getKeyInfoGeneratorManager().getDefaultManager()
                        .getFactory(keyEncryptionCredential);
        kekParams.setKeyInfoGenerator(kigf.newInstance());

        Encrypter samlEncrypter = new Encrypter(encParams, kekParams);
        samlEncrypter.setKeyPlacement(Encrypter.KeyPlacement.PEER);
        EncryptedAssertion encryptedAssertion = null;
        try {
            encryptedAssertion = samlEncrypter.encrypt(assertion);
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return encryptedAssertion;
    }
}
