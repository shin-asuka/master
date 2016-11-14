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

    protected SimpleDateFormat localoutputFormat = new SimpleDateFormat("MMMM d, yyyy @ h:mm a", Locale.US);
    protected String[] dateStr = new String[]{
            " 0th,"," 1st,"," 2nd,"," 3rd,"," 4th,"," 5th,"," 6th,"," 7th,"," 8th,"," 9th,",
            " 10th,"," 11th,"," 12th,"," 13th,"," 14th,"," 15th,"," 16th,"," 17th,"," 18th,"," 19th,"," 20th,",
            " 21st,"," 22nd,"," 23rd,"," 24th,"," 25th,"," 26th,"," 27th,"," 28th,"," 29th,"," 30th,"," 31st,"};

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
