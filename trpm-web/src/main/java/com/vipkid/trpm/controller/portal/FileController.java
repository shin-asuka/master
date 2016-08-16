package com.vipkid.trpm.controller.portal;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.vipkid.trpm.service.portal.FileService;

/**
 * 文件上传 controller
 * @author vipkid
 *
 */

@Controller
public class FileController extends AbstractPortalController {

    private Logger logger = LoggerFactory.getLogger(FileController.class);
    
    @Resource
    private FileService fileService;
    
    /**
     * 普通的文件上传
     * @param request
     * @param response
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadNormalFile", method = RequestMethod.POST)
    public String uploadFile(MultipartHttpServletRequest request, HttpServletResponse response,
                                @RequestParam("file") MultipartFile file) {
        logger.info("uploadFile");
        
        Map<String,Object> map = fileService.uploadNormalFile(file);

        return jsonView(response, map);
    }

}
