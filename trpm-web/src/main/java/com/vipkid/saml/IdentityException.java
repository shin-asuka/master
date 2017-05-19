package com.vipkid.saml;

/**
 * Created by LP-813 on 2017/5/19.
 */
/**
 * Used for creating checked internal/server exceptions that can be handled.
 */
public class IdentityException extends Exception {

    private static final long serialVersionUID = 3061847849910145257L;

    public IdentityException(String messaage) {
        super(messaage);
    }

    public IdentityException(String messaage, Throwable e) {
        super(messaage, e);
    }
}
