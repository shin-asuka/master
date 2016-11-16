package com.vipkid.rest;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.entity.User;

public class RestfulController {

    public static final String AUTOKEN = "Authorization";

    protected User getUser(HttpServletRequest request) throws IllegalArgumentException {
        Preconditions.checkArgument(request.getAttribute(AUTOKEN) != null);
        User user = (User) request.getAttribute(AUTOKEN);
        return user;
    }
}
