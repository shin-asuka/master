package com.vipkid.recruitment.senddocs.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.file.model.FileUploadStatus;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.http.service.FileHttpService;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.recruitment.senddocs.service.SendDocsService;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.AwsFileUtils;

/**
 * Contract -> Send Docs -> Regular
 * <p/>
 * LifeCycle: Send Docs
 * 上传头像, 生活照, 介绍视频, 和自我介绍
 *
 * @author Austin.Cao  Date: 18/11/2016
 */
@RestController
@RestInterface(lifeCycle={TeacherEnum.LifeCycle.PUBLICITY_INFO})
@RequestMapping("/recruitment/personalinfo")
public class SendDocsController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(SendDocsController.class);

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AwsFileService awsFileService;

    @Autowired
    private FileHttpService fileHttpService;

    @Autowired
    private SendDocsService sendDocsService;

    @Autowired
    private RecruitmentService recruitmentService;


    /**
     * 进入 PersonalInfo 状态之前, 先查出之前是否有上传资料,
     * @param teacherId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryPersonalInfo", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> queryPersonalInfo(Long teacherId, HttpServletRequest request, HttpServletResponse response) {
        if (null == teacherId) {
            return ResponseUtils.responseFail("Teacher's id is empty!", this);
        }

        Map<String, Object> result = Maps.newHashMap();
        try {
            Teacher teacher = teacherService.get(teacherId);
            if (null == teacher) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return ResponseUtils.responseFail("Teacher doesn't exist", this);
            }

            //检查老师的头像/照片/视频是都都上传了? 从 TIS 获取数据
            Map<String, Object> teacherFiles = Maps.newHashMap();
            teacherFiles = fileHttpService.queryTeacherFiles(teacherId);
            String avatarUrl = (String) teacherFiles.get("avatarUrl");
            String lifePictures = (String) teacherFiles.get("lifePictures");
            String shortVideoUrl = (String) teacherFiles.get("shortVideo");
            String shortVideoStatus = (String) teacherFiles.get("shortVideoStatus");

            Map<String,Object> status = recruitmentService.getStatus(teacherId);
            if(status != null && status.size() > 0) {
                String failedReasonJson = (String) status.get("failedReason");
            }

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }

        return ResponseUtils.responseFail("Failed to submit teacher bio", this);
    }

    /**
     * only change teacher's introduction
     * 要不要检查
     *
     * @param bio      教师的 bio
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
      public Map<String, Object> submitPersonalInfo(Long teacherId, String bio, HttpServletRequest request, HttpServletResponse response) {
        if (null == teacherId || StringUtils.isEmpty(bio)) {
            return ResponseUtils.responseFail("Teacher's id and/or bio is empty!", this);
        }

        Map<String, Object> result = Maps.newHashMap();
        try {
            Teacher teacher = teacherService.get(teacherId);
            if (null == teacher) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return ResponseUtils.responseFail("Teacher doesn't exist", this);
            }

            //检查老师的头像/照片/视频是都都上传了? 从 TIS 获取数据
            Map<String, Object> teacherFiles = Maps.newHashMap();
            teacherFiles = fileHttpService.queryTeacherFiles(teacherId);
            String avatarUrl = (String) teacherFiles.get("avatarUrl");
            String lifePictures = (String) teacherFiles.get("lifePictures");
            String shortVideoUrl = (String) teacherFiles.get("shortVideo");
            String shortVideoStatus = (String) teacherFiles.get("shortVideoStatus");

            if (StringUtils.isEmpty(avatarUrl) || StringUtils.isEmpty(lifePictures)
                    || StringUtils.isEmpty(shortVideoUrl) || StringUtils.isEmpty(shortVideoStatus)) {
                return ResponseUtils.responseFail("Teacher's files do NOT exists, failed to update bio", this);
            }

            teacher.setIntroduction(bio);
            boolean ret = sendDocsService.updateTeacher(teacher);
            if (ret) {
                return ResponseUtils.responseSuccess("Modify introduction successfully!", teacherFiles);
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }

        return ResponseUtils.responseFail("Failed to submit teacher bio", this);
    }


    @ResponseBody
    @RequestMapping("/uploadAvatar")
    public Object uploadAvatar(Long teacherId, @RequestParam("file") MultipartFile file,
                               HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> result = Maps.newHashMap();
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
            String key = AwsFileUtils.getAvatarKey(fileName);
            Long fileSize = file.getSize();

            Preconditions.checkArgument(AwsFileUtils.checkAvatarFileType(fileName), "文件类型不正确，支持类型为" + AwsFileUtils.AVATAR_FILE_TYPE);
            Preconditions.checkArgument(AwsFileUtils.checkAvatarFileSize(fileSize), "文件太大，maxSize = " + AwsFileUtils.AVATAR_MAX_SIZE);

            try {
                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    String url = "http://" + bucketName + "/" + key;
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadAvatar(teacherId, url);
                    result.put("url", fileUploadStatus.getUrl());
                    result.put("status", fileUploadStatus.getStatus());
                    return ResponseUtils.responseSuccess(result);
                } else {
                    return ResponseUtils.responseFail("Failed to upload avatar to AWS.", this);
                }
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseUtils.responseFail(e.getMessage(), this);
            }
        }

        return ResponseUtils.responseFail("Failed to upload avatar!", this);
    }

    @ResponseBody
    @RequestMapping("/uploadLifePic")
    public Object uploadLifePic(Long teacherId, @RequestParam("file") MultipartFile file,
                                HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> result = Maps.newHashMap();
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
            String key = AwsFileUtils.getLifePictureKey(fileName);
            Long fileSize = file.getSize();

            Preconditions.checkArgument(AwsFileUtils.checkLifePicFileType(fileName), "文件类型不正确，支持类型为" + AwsFileUtils.LIFE_PICTURE_FILE_TYPE);
            Preconditions.checkArgument(AwsFileUtils.checkLifePicFileSize(fileSize), "文件太大，maxSize = " + AwsFileUtils.LIFE_PICTURE_MAX_SIZE);

            try {
                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    String url = "http://" + bucketName + "/" + key;
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadLifePicture(teacherId, url);
                    result.put("url", fileUploadStatus.getUrl());
                    result.put("status", fileUploadStatus.getStatus());
                    return ResponseUtils.responseSuccess(result);
                } else {
                    return ResponseUtils.responseFail("Failed to upload life picture to AWS.", this);
                }
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseUtils.responseFail(e.getMessage(), this);
            }
        }

        return ResponseUtils.responseFail("Failed to upload life picture!", this);
    }

    @ResponseBody
    @RequestMapping("/uploadVideo")
    public Object uploadVideo(Long teacherId, @RequestParam("file") MultipartFile file,
                              HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> result = Maps.newHashMap();
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
            String key = AwsFileUtils.getShortVideoKey(fileName);
            Long fileSize = file.getSize();

            Preconditions.checkArgument(AwsFileUtils.checkShortVideoFileType(fileName), "文件类型不正确，支持类型为" + AwsFileUtils.SHORT_VIDEO_FILE_TYPE);
            Preconditions.checkArgument(AwsFileUtils.checkShortVideoFileSize(fileSize), "文件太大，maxSize = " + AwsFileUtils.SHORT_VIDEO_MAX_SIZE);

            try {
                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    String url = "http://" + bucketName + "/" + key;
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadShortVideo(teacherId, url);
                    result.put("url", fileUploadStatus.getUrl());
                    result.put("status", fileUploadStatus.getStatus());
                    return ResponseUtils.responseSuccess(result);
                } else {
                    return ResponseUtils.responseFail("Failed to upload short video to AWS.", this);
                }
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseUtils.responseFail(e.getMessage(), this);
            }
        }

        return ResponseUtils.responseFail("Failed to short video picture!", this);
    }

    @ResponseBody
    @RequestMapping("/deleteAvatar")
    public Object deleteAvatar(Long teacherId, HttpServletRequest request, HttpServletResponse response) {

        if (null == teacherId) {
            return ResponseUtils.responseFail("Teacher id is null, failed to deleteAvatar", this);
        }

        boolean ret = fileHttpService.deleteAvatar(teacherId);
        if (ret) {
            return ResponseUtils.responseSuccess("Successful to delete avatar.", null);
        } else {
            return ResponseUtils.responseFail("Failed to delete avatar.", this);
        }
    }

    @ResponseBody
    @RequestMapping("/deleteLifePic")
    public Object deleteAvatar(Long teacherId, Long lifePicId, HttpServletRequest request, HttpServletResponse response) {

        if (null == teacherId) {
            return ResponseUtils.responseFail("Teacher id is null, failed to deleteLifePic", this);
        }

        boolean ret = fileHttpService.deleteLifePicture(teacherId, lifePicId);
        if (ret) {
            return ResponseUtils.responseSuccess("Successful to delete life picture.", null);
        } else {
            return ResponseUtils.responseFail("Failed to delete life picture.", this);
        }
    }

    @ResponseBody
    @RequestMapping("/deleteVideo")
    public Object deleteVideo(Long teacherId, HttpServletRequest request, HttpServletResponse response) {

        if (null == teacherId) {
            return ResponseUtils.responseFail("Teacher id is null, failed to deleteVideo", this);
        }

        boolean ret = fileHttpService.deleteShortVideo(teacherId);
        if (ret) {
            return ResponseUtils.responseSuccess("Successful to delete short video.", null);
        } else {
            return ResponseUtils.responseFail("Failed to delete short video.", this);
        }
    }

}
