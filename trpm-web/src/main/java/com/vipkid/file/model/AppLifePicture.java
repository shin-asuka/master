package com.vipkid.file.model;

import java.io.Serializable;

/**
     * TIS 返回的 lifePicture 数据结构
     */
    public class AppLifePicture implements Serializable {

        private static final long serialVersionUID = 599275727559781725L;

        private Long id;    //生活照文件记录ID
        private String url; //生活照url


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }