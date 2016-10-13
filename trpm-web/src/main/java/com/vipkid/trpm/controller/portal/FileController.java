package com.vipkid.trpm.controller.portal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vipkid.trpm.service.portal.FileService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

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
     * Bank Info 中 ID证件上传格式限制, 只接受 .doc, .docx, .pdf, .jpg, .jpeg, .png, .bmp
     */
    private static Set<String> ACCEPT_CONTENT_TYPES = Sets.newHashSet(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/bmp");
    private static Set<String> ACCEPT_FILE_TYPES = Sets.newHashSet(
            "doc",
            "docx",
            "pdf",
            "jpeg",
            "jpg",
            "png",
            "bmp");
    //图片上传大小做个限制
    private static final long UPLOAD_SIZE_LIMIT = 10 * 1024 * 1024; //10M


    /**
     * 普通的文件上传
     *
     * @param request
     * @param response
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadNormalFile", method = RequestMethod.POST)
	public String uploadFile(MultipartHttpServletRequest request, HttpServletResponse response,
			@RequestParam("file") MultipartFile file) {

		logger.info("uploadFile");
		Map<String, Object> resultMap = Maps.newHashMap();
		
		response.setContentType("application/json");
		boolean valid = isFileTypeValid(file);
		if (!valid) {
			resultMap.put("result", false);
			resultMap.put("message", "We only accept .doc, .docx, .pdf, .jpg, .jpeg, .png, .bmp file, thanks!");
			return jsonView(response, resultMap);
		}

		long fileSize = file.getSize();
		if (fileSize > UPLOAD_SIZE_LIMIT) {
			resultMap.put("result", false);
			resultMap.put("message", "We only accept file smaller than 10M, please try again!");
			return jsonView(response, resultMap);
		}

		resultMap = fileService.uploadNormalFile(file);
		//for security consideration
		String contentType = "application/json";
		return jsonView(response, resultMap, contentType);
	}

	private static boolean isFileTypeValid(MultipartFile file) {
		if (null == file) {
			return false;
		}

		String contentType = file.getContentType();
		if (!ACCEPT_CONTENT_TYPES.contains(contentType)) {
			return false;
		}

		String fileName = file.getOriginalFilename();
		String fileType = getFileExtension(fileName);
		if (!ACCEPT_FILE_TYPES.contains(fileType)) {
			return false;
		}

		return true;
	}

	private static String getFileExtension(String fileName) {
		String fileType = null;
		if (StringUtils.isNotBlank(fileName)) {
			int lastIndex = fileName.lastIndexOf('.');
			if (lastIndex > 0) {
				fileType = fileName.substring(lastIndex + 1);
			}
		}
		return fileType;
	}

}
