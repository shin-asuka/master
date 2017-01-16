package com.vipkid.trpm.controller;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@PreAuthorize("fullyAuthenticated")
public abstract class AbstractController {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    protected static boolean IS_PRODUCTION = true;

    static {
        try {
            IS_PRODUCTION = PropertyConfigurer.booleanValue("env.production");
        } catch (Exception e) {
            logger.info("Current evn is production");
        }
    }

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
  //add a method for setting customized contentType
	protected String jsonView(HttpServletResponse response, Map<String, Object> model, String contentType) {
		try {
			String jsonString = JsonTools.getJson(model);

			if (StringUtils.isNotBlank(contentType)) {
				response.setContentType(contentType);
			}
			PrintWriter writer = response.getWriter();
			writer.print(jsonString);
		} catch (Exception e) {
			throw new RuntimeException("Writer json string exception.", e);
		}

		return nullView();
	}
}
