package com.vipkid.trpm.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * 在国籍中处理转换 
 * 2016-05-25 teacher portal中personal info
 */

public class NationalityTransfer {
    
    private static Map<String,String> NATION_MAP_FROM_DB = new HashMap<String, String>() {
        
        /**
         * 
         */
        private static final long serialVersionUID = 6528083946235930844L;

        //
        {
            put("CANADA","Canadian");
            put("USA","American");
        }
    };
    
    private static Map<String,String> NATION_MAP_TO_DB = new HashMap<String, String>() {
        /**
         * 
         */
        private static final long serialVersionUID = 2909018804526386204L;

        //
        {
            put("Canadian","CANADA");
            put("American","USA");
        }
    };
    
    public static String nationalityToDB(String strNationality) {
        String strNation = "USA";
        try {
            String strResult = NATION_MAP_TO_DB.get(strNationality);
            return null != strResult ? strResult : strNation;
        } catch (Exception e) {
            
        }
        return strNation;
    }
    
    public static String nationalityFromDB(String strNationality) {
        String strNation = "American";
        try {
            String strResult = NATION_MAP_FROM_DB.get(strNationality);
            return null != strResult ? strResult : strNation;
        } catch (Exception e) {
            
        }
        return strNation;
    }
    
}
