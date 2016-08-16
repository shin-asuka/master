/**
 * 
 */
package com.vipkid.http.service;

/**
 * @author zouqinghua
 * @date 2016年3月11日 上午11:16:46
 *
 */
public class HttpBaseService {

    public String serverAddress;

    public HttpBaseService() {

    }

    public HttpBaseService(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

}
