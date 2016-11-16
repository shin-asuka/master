package com.vipkid.trpm.dao;


/**
 * 在国籍中处理转换 
 * 2016-05-25 teacher portal中personal info
 */

public class NationalityTransfer {
    
    /**转化国籍*/
    public static String getNationality(String country){
        if("United States".equals(country)){
            country = Country.USA.toString();
        }else if("Canada".equals(country)){
            country = Country.CANADA.toString();
        }else if("Estonia".equals(country)){
            country = Country.ESTONIA.toString();
        }else if("Australia".equals(country)){
            country = Country.AUSTRALIA.toString();
        }else if("New Zealand".equals(country)){
            country = Country.NEW_ZEALAND.toString();
        }else if("Jamaica".equals(country)){
            country = Country.JAMAICA.toString();
        }else if("Dominican Republic".equals(country)){
            country = Country.THE_DOMINICAN_REPUBLIC.toString();
        }else if("United Kingdom".equals(country)){
            country = Country.UK.toString();
        }
        return country;
    }
    
    /**还原国籍*/
    public static String getRestoreNationality(String country){
        if(Country.USA.toString().equals(country)){
            country = "United States";
        }else if(Country.CANADA.toString().equals(country)){
            country = "Canada";
        }else if(Country.ESTONIA.toString().equals(country)){
            country = "Estonia";
        }else if(Country.AUSTRALIA.toString().equals(country)){
            country = "Australia";
        }else if(Country.NEW_ZEALAND.toString().equals(country)){
            country = "New Zealand";
        }else if(Country.JAMAICA.toString().equals(country)){
            country = "Jamaica";
        }else if(Country.THE_DOMINICAN_REPUBLIC.toString().equals(country)){
            country = "Dominican Republic";
        }else if(Country.UK.toString().equals(country)){
            country = "United Kingdom";
        }
        return country;
    }
    
    public enum Country {
        USA, // 美国
        CANADA, // 加拿大
        ESTONIA, // 爱莎尼亚
        AUSTRALIA, // 澳大利亚
        NEW_ZEALAND, // 新西兰
        JAMAICA, // 牙买加
        THE_DOMINICAN_REPUBLIC, // 多米尼加共和国
        UK, //英国
        OTHER // 其他
    }
    
}
