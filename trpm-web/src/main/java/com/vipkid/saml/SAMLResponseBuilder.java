package com.vipkid.saml;

import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Date;

import com.vipkid.enums.TeacherEnum;
import org.apache.xml.security.Init;
import org.apache.xml.security.utils.Base64;
import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml2.core.impl.StatusMessageBuilder;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.encryption.EncryptionConstants;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.*;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.model.SAMLSSOServiceProviderDO;
import org.wso2.carbon.identity.sso.saml.SAMLSSOConstants;
import org.wso2.carbon.identity.sso.saml.builders.SignKeyDataHolder;
import org.wso2.carbon.identity.sso.saml.builders.signature.DefaultSSOSigner;
import org.wso2.carbon.identity.sso.saml.util.SAMLSSOUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

/**
 * Created by LP-813 on 2017/5/25.
 */
public class SAMLResponseBuilder {

    /**
     * Build SAML response using IdP configuration & user name
     *
     * @param ssoIdPConfigs
     * @param userName
     * @return SAML Response object
     * @throws IdentityException
     */
    String privateKeyPath = "server.keystore";
    String certificatePath = "server.cer";
    public Response buildSAMLResponse(SAMLSSOServiceProviderDO ssoIdPConfigs, String userName)
            throws IdentityException {
        Response response = new org.opensaml.saml2.core.impl.ResponseBuilder().buildObject();
        response.setID(java.util.UUID.randomUUID().toString());
        response.setDestination(ssoIdPConfigs.getAssertionConsumerUrl());
        response.setStatus(buildStatus(SAMLSSOConstants.StatusCodes.SUCCESS_CODE, null));
        response.setVersion(SAMLVersion.VERSION_20);
        DateTime issueInstant = new DateTime();
        response.setIssueInstant(issueInstant);
        Assertion assertion = SAMLWriter.AssertionBuild();
        if (ssoIdPConfigs.isDoEnableEncryptedAssertion()) {
            String domainName = MultitenantUtils.getTenantDomain(userName);
            String alias = ssoIdPConfigs.getCertAlias();
            if (alias != null) {
                EncryptedAssertion encryptedAssertion =
                        SAMLSSOUtil.setEncryptedAssertion(assertion,
                                EncryptionConstants.ALGO_ID_DIGEST_SHA256,
                                alias,
                                domainName);
                response.getEncryptedAssertions().add(encryptedAssertion);
            }
        } else {
            response.getAssertions().add(assertion);
        }
        if (ssoIdPConfigs.isDoSignResponse()) {
            X509Credential x509 = X509CredentialBuilder.getClientTLSCred(privateKeyPath, certificatePath);
            response =(Response) SignatureBuilder.doSignResponse(response,SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1,x509);
        }
        return response;
    }


    private Status buildStatus(String status, String statMsg) {

        Status stat = new StatusBuilder().buildObject();

        // Set the status code
        StatusCode statCode = new StatusCodeBuilder().buildObject();
        statCode.setValue(status);
        stat.setStatusCode(statCode);

        // Set the status Message
        if (statMsg != null) {
            StatusMessage statMesssage = new StatusMessageBuilder().buildObject();
            statMesssage.setMessage(statMsg);
            stat.setStatusMessage(statMesssage);
        }

        return stat;
    }


}
