package com.vipkid.saml;

import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collections;

/**
 * Created by LP-813 on 2017/5/24.
 */
public class KryptoUtil {
    public PrivateKey getStoredPrivateKey(String privateKeyFilePath){
        // Open an input stream on the keystore file
        String cerFileName = "d:/certA.cer";
        String p12FileName = "d:/certA.p12";
        String pfxPassword = "vipkid";

        InputStream fis = null;
        try {
            fis = new FileInputStream(p12FileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Create a keystore object
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        // Load the file into the keystore
        try {
            keyStore.load(fis, pfxPassword.toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        String aliaesName = "small";
        PrivateKey priKey = null;
        try {
            priKey = (PrivateKey) (keyStore.getKey(aliaesName, null));
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        System.out.println("private key:/n" + priKey);

        // public key
        InputStream is = null;
        try {
            is = new FileInputStream(cerFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("x509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        Certificate cerCert = null;
        try {
            cerCert = cf.generateCertificate(is);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        System.out.println("public key:/n" + cerCert);
        return null;
    }

    public static KeyInfo getKeyInfo(){
        KeyInfoFactory factory = KeyInfoFactory.getInstance("DOM");
        KeyInfo keyInfo = factory.newKeyInfo
                (Collections.singletonList(factory.newKeyName("small")));
        return null;
    }
}
