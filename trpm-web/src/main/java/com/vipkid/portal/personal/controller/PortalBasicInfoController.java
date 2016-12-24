package com.vipkid.portal.personal.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.file.model.FileUploadStatus;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.http.service.FileHttpService;
import com.vipkid.portal.personal.service.PortalBasicInfoService;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.dto.PersonlInfoDto;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.util.AwsFileUtils;

@RestController
@RestInterface(lifeCycle = LifeCycle.REGULAR)
@RequestMapping("/portal/personal")
public class PortalBasicInfoController extends RestfulController{
	
	private static Logger logger = LoggerFactory.getLogger(PortalBasicInfoController.class);

    @Autowired
    private AwsFileService awsFileService;
    
    @Autowired
    private FileHttpService fileHttpService;
    
    @Autowired
    private PortalBasicInfoService portalBasicInfoService;
	
    /**
     * 个人基本信息上传
     * @param file
     * @param request
     * @param response
     * @return
     */
	@ResponseBody
    @RequestMapping("/uploadAvatar")
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
		
        Map<String, Object> result = Maps.newHashMap();
        
        if (file != null) {
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                fileName = AwsFileUtils.reNewFileName(fileName);
            }
            String bucketName = AwsFileUtils.getAwsBucketName();
            String key = AwsFileUtils.getAvatarKey(fileName);
            Long fileSize = file.getSize();

            try {
                Preconditions.checkArgument(AwsFileUtils.checkAvatarFileType(fileName), "文件类型不正确，支持类型为" + AwsFileUtils.AVATAR_FILE_TYPE);
                Preconditions.checkArgument(AwsFileUtils.checkAvatarFileSize(fileSize), "文件太大，maxSize = " + AwsFileUtils.AVATAR_MAX_SIZE);

                Teacher teacher = getTeacher(request);
                if (null == teacher) {
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    return  ApiResponseUtils.buildErrorResp(-2, "This account does not exist.");
                }
                Long teacherId = teacher.getId();
                logger.info("Upload avatar for {}!", teacherId);
                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadAvatar(teacherId, key);
                    result.put("url", fileUploadStatus.getUrl());
                    logger.info("Successful to upload avatar for {}!", teacherId);
                    ApiResponseUtils.buildSuccessDataResp(result);
                } else {
                    logger.error("Failed to upload avatar for {}!", teacherId);
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    return  ApiResponseUtils.buildErrorResp(-3, "Upload failed!  Please try again.");
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.error("Upload avatar with Exception", e);
                return ApiResponseUtils.buildErrorResp(-4, "Upload failed!  Please try again.");
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                logger.error("Upload avatar with Exception", e);
                return ApiResponseUtils.buildErrorResp(-5, "Upload failed!  Please try again.");
            }
        }
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ApiResponseUtils.buildErrorResp(-6, "Upload failed!  Please try again.");
    }
	
	
	@ResponseBody
	@RequestMapping(value = "/basicInfo", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String,Object> getBasicInfo(HttpServletRequest request, HttpServletResponse response){
		try{
			PersonlInfoDto result = portalBasicInfoService.getBasicInfo(getTeacher(request),getUser(request));
			return ApiResponseUtils.buildSuccessDataResp(result);
        } catch (IllegalArgumentException e) {
        	logger.error("Exception", e);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ApiResponseUtils.buildErrorResp(-4,e.getMessage());
        } catch (Exception e) {
        	logger.error("Exception", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(-5,e.getMessage());
        }
	}
	
	@ResponseBody
	@RequestMapping(value = "/basicInfo", method = RequestMethod.PUT, produces = RestfulConfig.JSON_UTF_8)
	public Map<String,Object> updateBasicInfo(@RequestBody PersonlInfoDto bean, HttpServletRequest request, HttpServletResponse response){
		try{
            List<Result> list = ValidateUtils.checkBean(bean,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(0, "reslult:"+list.get(0).getName() + "," + list.get(0).getMessages());
            }
            
			Map<String,Object> result = portalBasicInfoService.updateBasicInfo(getTeacher(request),getUser(request),bean);
			if(ReturnMapUtils.isFail(result)){
				 response.setStatus(HttpStatus.FORBIDDEN.value());
			}
			return ApiResponseUtils.buildSuccessDataResp(result);
        } catch (IllegalArgumentException e) {
        	logger.error("Exception", e);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ApiResponseUtils.buildErrorResp(-4,e.getMessage());
        } catch (Exception e) {
        	logger.error("Exception", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(-5,e.getMessage());
        }
	}
}
