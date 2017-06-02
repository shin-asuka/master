package com.vipkid.saml;

import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.security.x509.X509Util;
import org.opensaml.xml.util.DatatypeHelper;
import sun.misc.BASE64Decoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.*;
import java.security.cert.X509Certificate;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by LP-813 on 2017/5/26.
 */
public class X509CredentialBuilder {
    public static X509Credential getClientTLSCred(String clientTLSPrivateKeyResourceName,
                                                   String clientTLSCertificateResourceName) {
        PrivateKey privateKey = null;
        X509Certificate cert = null;
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream("D:\\keys\\server.keystore");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            ks.load(in, "vipkid".toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        try {
            cert =(X509Certificate) ks.getCertificate("serverKey");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            privateKey = (PrivateKey) ks.getKey("serverKey", "vipkid".toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        //privateKey = SecurityHelper.decodePrivateKey(privateByte,"vipkid".toCharArray());
            //cert = X509Util.decodeCertificate(DatatypeHelper.inputstreamToString(
            //       X509CredentialBuilder.class.getResourceAsStream(clientTLSCertificateResourceName),null).getBytes()).iterator().next();

        return SecurityHelper.getSimpleCredential(cert, privateKey);
    }
}
