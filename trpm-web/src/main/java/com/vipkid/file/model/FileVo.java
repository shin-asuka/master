/**
 * 
 */
package com.vipkid.file.model;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.collect.Lists;
import com.vipkid.file.utils.DateUtils;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.file.utils.StringUtils;

/**
 * 
 * 文件对象
 * 
 * @author zouqinghua date 2016年1月23日 下午11:31:34
 */
public class FileVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String path;
    private Long size;
    private String type;
    private Date lastModified;
    private InputStream inputStream;
    private String eTag; //数据元对象的hash值,通常为md5值
    private String uid;
    private String url;
    private Integer percentage;

    private String showSize;
    private String showDate;

    private String serverType;
    private String bucketName;
    private List<String> bucketList = Lists.newArrayList();

    public FileVo() {
        // TODO Auto-generated constructor stub
    }

    public FileVo(String name, String path, Long size, String type, Date lastModified) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.type = type;
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

   

	public String geteTag() {
		return eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
	}

	public String getShowSize() {
        return showSize;
    }

    public void setShowSize(String showSize) {
        this.showSize = showSize;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public List<String> getBucketList() {
        return bucketList;
    }

    public void setBucketList(List<String> bucketList) {
        this.bucketList = bucketList;
    }

    public static String getNameByKey(String key) {
        String name = "";
        if (!StringUtils.isEmpty(key)) {
            name = key.substring(key.lastIndexOf("/") + 1);
        }
        return name;
    }

    public static String getTypeByName(String name) {
        String type = "";
        if (!StringUtils.isEmpty(name) && name.indexOf(".") > -1) {
            type = name.substring(name.lastIndexOf("."));
        }
        return type;
    }

    public static FileVo s3ObjectSummaryToFileVo(S3ObjectSummary objectSummary){
    	FileVo fileVo = null;
        if (objectSummary != null) {
        	fileVo = new FileVo();
            String keys = objectSummary.getKey();
            String fileName = getNameByKey(keys);
            // fileVo.setoSSObject(ossObject);
            fileVo.setName(fileName);
            fileVo.setPath(keys);
            fileVo.setUid(keys);
            fileVo.setSize(objectSummary.getSize());
            fileVo.setLastModified(objectSummary.getLastModified());
            fileVo.setType(getTypeByName(fileName));
            fileVo.setInputStream(null);
            fileVo.seteTag(objectSummary.getETag());
            
            //String url = Global.getConfig("aliyuncs.fileUri") + "/" + keys;
            //url = FileUtils.EncodeURLFileName(url);

            //fileVo.setUrl(url);
            fileVo.setShowSize(FileUtils.getFormatSize(fileVo.getSize()));
            fileVo.setShowDate(DateUtils.formatDate(fileVo.getLastModified(), "yyyy-MM-dd HH:mm:ss"));
        
        }
        return fileVo;
    	
    }
    
    public static FileVo S3ObjectToFileVo(S3Object s3jObect){
    	FileVo fileVo = null;
        if (s3jObect != null) {
        	fileVo = new FileVo();
            String keys = s3jObect.getKey();
            String fileName = getNameByKey(keys);
            // fileVo.setoSSObject(ossObject);
            fileVo.setName(fileName);
            fileVo.setPath(keys);
            fileVo.setUid(keys);
            fileVo.setSize(s3jObect.getObjectMetadata().getContentLength());
            fileVo.setLastModified(s3jObect.getObjectMetadata().getLastModified());
            fileVo.setType(getTypeByName(fileName));
            fileVo.setInputStream(s3jObect.getObjectContent());
            fileVo.seteTag(s3jObect.getObjectMetadata().getETag());
            //String url = Global.getConfig("aliyuncs.fileUri") + "/" + keys;
            //url = FileUtils.EncodeURLFileName(url);

            //fileVo.setUrl(url);
            fileVo.setShowSize(FileUtils.getFormatSize(fileVo.getSize()));
            fileVo.setShowDate(DateUtils.formatDate(fileVo.getLastModified(), "yyyy-MM-dd HH:mm:ss"));
            
        }
        return fileVo;
    	
    }
    
    public static FileVo ObjectMetadataToFileVo(ObjectMetadata objectMetadata,String key){
    	FileVo fileVo = null;
        if (objectMetadata != null && StringUtils.isNotBlank(key)) {
        	fileVo = new FileVo();
            String fileName = getNameByKey(key);
            // fileVo.setoSSObject(ossObject);
            fileVo.setName(fileName);
            fileVo.setPath(key);
            fileVo.setUid(key);
            fileVo.setSize(objectMetadata.getContentLength());
            fileVo.setLastModified(objectMetadata.getLastModified());
            fileVo.setType(getTypeByName(fileName));
            fileVo.setInputStream(null);
            fileVo.seteTag(objectMetadata.getETag());
            //String url = Global.getConfig("aliyuncs.fileUri") + "/" + keys;
            //url = FileUtils.EncodeURLFileName(url);

            //fileVo.setUrl(url);
            fileVo.setShowSize(fileVo.getSize()==null?null:FileUtils.getFormatSize(fileVo.getSize()));
            if(fileVo.getLastModified()!=null){
            	fileVo.setShowDate(DateUtils.formatDate(fileVo.getLastModified(), "yyyy-MM-dd HH:mm:ss"));
                
            }
           
        }
        return fileVo;
    	
    }
}
