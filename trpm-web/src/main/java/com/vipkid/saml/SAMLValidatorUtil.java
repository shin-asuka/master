package com.vipkid.saml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.core.util.AnonymousSessionUtil;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.sso.saml.util.SAMLSSOUtil;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class SAMLValidatorUtil {

    private static Log log = LogFactory.getLog(SAMLValidatorUtil.class);

    /**
     * Extract SAML query string from URL
     *
     * @param url
     * @return query string
     */
    public static String getQueryString(String url) {
        String[] temp = url.split("\\?");
        if (temp != null && temp.length > 1) {
            return temp[1];
        }
        return null;
    }

    /**
     * Get SAML request form URL
     *
     * @param url
     * @return encoded SAML request
     * @throws UnsupportedEncodingException
     */
    public static String getSAMLRequestFromURL(String url) throws UnsupportedEncodingException {
        String decodedURL = java.net.URLDecoder.decode(url, "UTF-8");
        String[] temp = decodedURL.split("\\?");
        if (temp != null && temp.length > 1) {
            String[] parameters = temp[1].split("&");
            if (parameters != null) {
                for (String parameter : parameters) {
                    if (parameter.contains("SAMLRequest")) {
                        String[] keyValuePair = parameter.split("=");
                        return keyValuePair != null && keyValuePair.length > 1 ? keyValuePair[1]
                                : null;
                    }
                }
            }
        }
        return null;
    }

    public static Map<String, String> getUserClaimValues(String username, String[] requestedClaims, String profile)
            throws IdentityException {
        try {
            UserStoreManager userStroreManager =
                    AnonymousSessionUtil.getRealmByUserName(SAMLSSOUtil.getRegistryService(),
                            SAMLSSOUtil.getRealmService(),
                            username).getUserStoreManager();
            username = MultitenantUtils.getTenantAwareUsername(username);
            return userStroreManager.getUserClaimValues(username, requestedClaims, profile);
        } catch (UserStoreException e) {
            log.error("Error while retrieving claims values", e);
            throw new IdentityException("Error while retrieving claims values", e);
        } catch (CarbonException e) {
            log.error("Error while retrieving claims values", e);
            throw new IdentityException(
                    "Error while retrieving claim values",
                    e);
        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

}

