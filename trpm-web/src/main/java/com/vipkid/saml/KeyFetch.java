package com.vipkid.saml;

import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Created by LP-813 on 2017/5/25.
 */
public class KeyFetch {
    public static Credential getCredentialFromFilePath(String certPath) throws IOException, CertificateException, KeyException {
        InputStream inStream = new FileInputStream(certPath);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert =  cf.generateCertificate(inStream);
        inStream.close();

        //"Show yourself!"
        System.out.println(cert.toString());

        BasicX509Credential cred = new BasicX509Credential();
        cred.setEntityCertificate((java.security.cert.X509Certificate) cert);
        cred.setPrivateKey(null);

        return cred;

        //return (Credential) org.opensaml.xml.security.SecurityHelper.getSimpleCredential( (X509Certificate) cert, privatekey);
    }

}
