package com.vipkid.rest;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RestfulController {

    public static final String AUTOKEN = "Authorization";
    
    public static final String TEACHER = "Teacher";

    protected User getUser(HttpServletRequest request) throws IllegalArgumentException {
        Preconditions.checkArgument(request.getAttribute(AUTOKEN) != null);
        User user = (User) request.getAttribute(AUTOKEN);
        return user;
    }
    
    protected Teacher getTeacher(HttpServletRequest request) throws IllegalArgumentException {
        Preconditions.checkArgument(request.getAttribute(TEACHER) != null);
        Teacher teacher = (Teacher) request.getAttribute(TEACHER);
        return teacher;
    }
}
