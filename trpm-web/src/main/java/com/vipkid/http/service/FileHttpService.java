package com.vipkid.http.service;

import java.io.Serializable;
import java.util.HashMap;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.util.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.file.model.FileUploadStatus;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;

import com.vipkid.http.vo.HttpResult;
import com.vipkid.http.vo.TeacherFile;

/**
 * 包装 Teacher Information Service 中的文件接口
 * @author Austin.Cao  Date: 19/11/2016
 */
public class FileHttpService extends HttpBaseService {

    private static final Logger logger = LoggerFactory.getLogger(FileHttpService.class);

    private String s3BucketName;

    public String getS3BucketName() {
        return s3BucketName;
    }

    public void setS3BucketName(String s3BucketName) {
        this.s3BucketName = s3BucketName;
    }

    private static final String FILE_SERVICE_ROOT_URL = "/api/pc/teacher";
    private static final String UPLOAD_AVATAR = "/transformAvatar";
    private static final String UPLOAD_LIFE_PIC = "/transformLifePicture";
    private static final String UPLOAD_SHORT_VIDEO = "/transformShortVideo";

    private static final String DELETE_AVATAR = "/deleteAvatar";
    private static final String DELETE_LIFE_PIC = "/deleteLifePicture";
    private static final String DELETE_SHORT_VIDEO = "/deleteShortVideo";

    private static final String QUERY_TEACHER_FILES = "/queryMediaFiles";


    public FileUploadStatus uploadAvatar(Long teacherId, String s3UrlOfAvatar) {

        String url = getFileServiceUrl() + UPLOAD_AVATAR;
        FileUploadStatus fileUploadStatus = uploadFile(url, teacherId, s3UrlOfAvatar);

        logger.info("Call {} and get response: {}", url, fileUploadStatus);
        return fileUploadStatus;
    }


    public FileUploadStatus uploadLifePicture(Long teacherId, String s3UrlOfLifePic) {

        String url = getFileServiceUrl() + UPLOAD_LIFE_PIC;
        FileUploadStatus fileUploadStatus = uploadFile(url, teacherId, s3UrlOfLifePic);

        logger.info("Call {} and get response: {}", url, fileUploadStatus);
        return fileUploadStatus;
    }

    public FileUploadStatus uploadShortVideo(Long teacherId, String s3UrlOfVideo) {

        String url = getFileServiceUrl() + UPLOAD_SHORT_VIDEO;
        FileUploadStatus fileUploadStatus = uploadFile(url, teacherId, s3UrlOfVideo);

        logger.info("Call {} and get response: {}", url, fileUploadStatus);
        return fileUploadStatus;
    }


    public boolean deleteAvatar(Long teacherId) {
        if (null == teacherId) {
            logger.error("teacherId is null, failed to deleteAvatar!");
            return false;
        }

        String url = getFileServiceUrl() + DELETE_AVATAR;

        HashMap<String, String> params = Maps.newHashMap();
        params.put("teacherId", teacherId.toString());
        boolean isSuccessful = deleteFile(url, params);
        logger.info("deleteAvatar of {} with {}.", teacherId, isSuccessful);

        return isSuccessful;
    }

    public boolean deleteLifePicture(Long teacherId, Long fileId) {
        if (null == teacherId || null == fileId) {
            logger.error("teacherId or fileId is null, failed to deleteLifePicture!");
            return false;
        }

        String url = getFileServiceUrl() + DELETE_LIFE_PIC;

        HashMap<String, String> params = Maps.newHashMap();
        params.put("teacherId", teacherId.toString());
        params.put("id", fileId.toString());
        boolean isSuccessful = deleteFile(url, params);
        logger.info("deleteLifePicture of {} for teacher {} with {}.", fileId, teacherId, isSuccessful);

        return isSuccessful;
    }

    public boolean deleteShortVideo(Long teacherId) {
        if (null == teacherId) {
            logger.error("teacherId is null, failed to deleteShortVideo!");
            return false;
        }

        String url = getFileServiceUrl() + DELETE_SHORT_VIDEO;

        HashMap<String, String> params = Maps.newHashMap();
        params.put("teacherId", teacherId.toString());
        boolean isSuccessful = deleteFile(url, params);
        logger.info("deleteShortVideo of {} with {}.", teacherId, isSuccessful);

        return isSuccessful;
    }

    /**
     * 从 TIS 获取教师的 生活照/短视频 信息, 希望头像也可以获取到
     *
     * @param teacherId
     * @return
     */
    public TeacherFile queryTeacherFiles(Long teacherId) {

        logger.info("获取老师文件信息 queryTeacherFiles teacherId = {}",teacherId);

        TeacherFile teacherFile = new TeacherFile(teacherId);
        //Map<String, Object> result = Maps.newHashMap();
        if(null == teacherId) {
            return teacherFile;
        }

        String url = getFileServiceUrl() + QUERY_TEACHER_FILES + "?teacherId=" + teacherId;

        try {
            String httpResult = WebUtils.simpleGet(url);

            String responseBody = httpResult;
            logger.info("Call {} and get {}", url, responseBody);

            if (responseBody != null) {
                JsonNode response = JsonUtils.parseObject(responseBody);
                if (isSuccessResponse(response)) {
                    JsonNode data = response.get("data");
                    if (null != data) {

                        /*String lifePicturesJson = data.getString("lifePictures");
                        String avatarUrl = data.getString("avatar");
                        String shortVideoStr = data.getString("shortVideo");

                        List<AppLifePicture> lifePictures = JsonUtils.toBeanList(lifePicturesJson, AppLifePicture.class);
                        AppVideo shortVideo = JsonUtils.toBean(shortVideoStr, AppVideo.class);

                        teacherFile.setAvatar(avatarUrl);
                        teacherFile.setLifePictures(lifePictures);
                        teacherFile.setShortVideo(shortVideo);*/

                        teacherFile = JsonUtils.toBean(data.toString(), TeacherFile.class);
                    }
                } else {
                    String errorMessage = response.get("errMsg").asText();
                    logger.error("Call {} and get error message: {}", errorMessage);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to call %s TIS!", url), e);
            e.printStackTrace();
        }

        return teacherFile;
    }

    public String getFileServiceUrl() {
        return super.getServerAddress() + FILE_SERVICE_ROOT_URL;
    }

    private AppFile buildAppFile(Long teacherId, String s3FileKey) {
        AppFile appFile = new AppFile();
        appFile.setTeacherId(teacherId);
        appFile.setBucketName(s3BucketName);
        appFile.setKey(s3FileKey);
        return appFile;
    }

    /**
     * 按照不同的 upload URL 来上传教师的头像/生活照/短视频
     *
     * @param uploadUrl
     * @param teacherId
     * @param s3FileUrl
     * @return
     */
    private FileUploadStatus uploadFile(String uploadUrl, Long teacherId, String s3FileUrl) {

        FileUploadStatus fileUploadStatus = null;
        if (StringUtils.isEmpty(uploadUrl) || StringUtils.isEmpty(s3FileUrl) || null == teacherId) {
            return fileUploadStatus;
        }

        AppFile appFile = buildAppFile(teacherId, s3FileUrl);
        String url = uploadUrl;

        try {
            String responseBody = WebUtils.postNameValuePair(url, appFile);
            logger.info("Call {} and get {}", url, responseBody);

            if (responseBody != null) {
                JsonNode response = JsonUtils.parseObject(responseBody);
                if (isSuccessResponse(response)) {
                    JsonNode data = response.get("data");
                    if (null != data) {
                        Long id = null;
                        if(null != data.get("id")){
                            id = data.get("id").asLong();
                        }

                        Integer status = null ;
                        if(null != data.get("status")){
                            status = data.get("status").asInt();//1 成功，2 失败
                        }

                        String fileUrl = null;
                        if(null !=data.get("url")){
                            data.get("url").textValue();
                        }

                        fileUploadStatus = new FileUploadStatus();
                        fileUploadStatus.setId(id);
                        fileUploadStatus.setStatus(status);
                        fileUploadStatus.setUrl(fileUrl);
                    }
                } else {
                    String errorMessage = response.get("errMsg").asText();
                    logger.error("Call {} and get error message: {}", errorMessage);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to call %s TIS!", url), e);
            e.printStackTrace();
        }

        return fileUploadStatus;
    }

    /**
     * 按照不同的 delete URL 来删除教师的头像/生活照/短视频
     *
     * @param deleteUrl TIS 中的 delete 接口
     * @param params    接口所需的参数: 教师 Id and/or fileId
     * @return
     */
    private boolean deleteFile(String deleteUrl, Map<String, String> params) {
        if (StringUtils.isEmpty(deleteUrl)) {
            return false;
        }

        String url = deleteUrl;
        try {
            HttpResult httpResult = WebUtils.post(deleteUrl, params);

            String responseBody = (String) httpResult.getResponse();
            logger.info("Call {} and get {}", url, responseBody);

            if (responseBody != null) {
                 JsonNode response = JsonUtils.parseObject(responseBody);
                if (isSuccessResponse(response)) {
                    JsonNode data = response.get("data");
                    if (null != data) {
                        Integer status = data.get("status").asInt();//1 成功，0 失败
                        if (status == 1) {
                            return true;
                        }
                    }
                } else {
                    String errorMessage = response.get("errMsg").asText();
                    logger.error("Call {} and get error message: {}", errorMessage);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to call %s TIS!", url), e);
            e.printStackTrace();
        }

        return false;
    }


    private boolean isSuccessResponse(JsonNode response) {

        if (null != response) {
            Boolean ret = response.get("ret").asBoolean();
            if (ret) {
                return true;
            }
        }

        return false;
    }

    /**
     * 发送给 TIS upload 的文件上传参数
     */
    private static class AppFile implements Serializable {

        private static final long serialVersionUID = 2223242733111132024L;

        private Long teacherId;     //教师ID
        private String bucketName;  //文件所在bucketName
        private String key;         //文件key
        private Integer sort;       //所在位置

        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }

        @JsonIgnore
        public String getUrl() {
            String url = "";
            if (StringUtils.isNotBlank(this.bucketName)
                    && StringUtils.isNotBlank(this.key)
                    ) {
                url = "https://" + this.bucketName + "/" + this.key;
            }
            return url;
        }
    }
}
