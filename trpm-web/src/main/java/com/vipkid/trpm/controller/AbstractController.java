package com.vipkid.trpm.controller;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.community.config.PropertyConfigurer;
import org.community.tools.JsonTools;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("fullyAuthenticated")
public abstract class AbstractController {

    protected static final int LINE_PER_PAGE = PropertyConfigurer.intValue("page.linePerPage");

    protected String nullView() {
        return null;
    }

    protected String jsonView() {
        return "template/jsonView";
    }

    /* JSON视图，在前端用ajaxForm提交表单时使用（修复IE提示JSON保存的问题） */
    protected String jsonView(HttpServletResponse response, Map<String, Object> model) {
        try {
            String jsonString = JsonTools.getJson(model);

            response.setContentType("text/html;charset=" + StandardCharsets.UTF_8.name());
            PrintWriter writer = response.getWriter();
            writer.print(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Writer json string exception.", e);
        }

        return nullView();
    }

}
