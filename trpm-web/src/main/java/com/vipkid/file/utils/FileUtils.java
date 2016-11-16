package com.vipkid.file.utils;

import java.math.BigDecimal;


/**
 * 
 * @author zouqinghua 
 * date 2016年1月23日 下午11:38:42
 */
public class FileUtils {


    /**
     * 名称转码
     * 
     * @param url URL
     * @return
     */
    public static String EncodeURLFileName(String url) {
        String newUrl = "";
        if (!StringUtils.isEmpty(url)) {
            String path = url.substring(0, url.lastIndexOf("/") + 1);
            String outName = url.substring(url.lastIndexOf("/") + 1);
            newUrl = path + Encodes.urlEncode(outName);
        }
        return newUrl;
    }

    /**
     * 修复路径，将 \\ 或 / 等替换为 File.separator
     * 
     * @param path
     * @return
     */
    public static String path(String path) {
        String p = StringUtils.replace(path, "\\", "/");
        p = StringUtils.join(StringUtils.split(p, "/"), "/");
        if (!StringUtils.startsWithAny(p, "/") && StringUtils.startsWithAny(path, "\\", "/")) {
            p += "/";
        }
        if (!StringUtils.endsWithAny(p, "/") && StringUtils.endsWithAny(path, "\\", "/")) {
            p = p + "/";
        }
        return p;
    }

    public static String filePath(String path) {
        String newPath = "/" + path(path);
        if ("/".equals(newPath.substring(newPath.length() - 1))) {
            newPath = newPath.substring(0, newPath.length() - 1);
        }
        return newPath;
    }

    public static String getFormatSize(Long size) {
        double kiloByte = size / 1024.0;
        if (kiloByte < 1) {
            return size + "Byte(s)";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
    
    public static String getFileType(String fileName){
    	String type = "";
    	if(StringUtils.isNotBlank(fileName)){
    		try {
    			type = fileName.substring(fileName.lastIndexOf(".")+1);
    			type = type.toLowerCase(); //类型小写
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return type;
    }
}
