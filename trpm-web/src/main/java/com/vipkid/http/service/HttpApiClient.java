package com.vipkid.http.service;

import com.vipkid.http.utils.HttpClientUtils;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/16 下午3:03
 */
@Component
public class HttpApiClient {


    public String doGet(String url) {
        return HttpClientUtils.request(url);
    }

    public String doGet(String url,Map<String, String> requestParam) {
        if (requestParam != null && requestParam.size() > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("?");
            for(String key : requestParam.keySet()){
                sb.append(key).append("=").append(requestParam.get(key)).append("&");
            }
            url = url + sb.toString().substring(0,sb.length()-1);
        }
        return HttpClientUtils.request(url);
    }

    public String doPost(String url, Map<String, String> formData) {
        return HttpClientUtils.post(url, formData);
    }


    public String doPost(String url, Map<String, String> formData, Integer readTimeout) {
        return HttpClientUtils.post(url, formData, readTimeout);
    }


    public String appointHeadersPost(String url, Map<String, String> formData, Map<String, String> headers) {
        return HttpClientUtils.appointHeadersPost(url, formData, headers);
    }


    public String doPostJson(String url, String jsonData) {
        return HttpClientUtils.post(url, jsonData);
    }

    public HttpResponse doPostRESTful(String url, String jsonData) {
        return HttpClientUtils.postRESTful(url, jsonData);
    }


    public String doPostJsonWithHeader(String url, String jsonData, Map<String, String> headers) {
        return HttpClientUtils.post(url, jsonData, null, headers);
    }


    public String doTimeoutRetryPost(String url, Map<String, String> formData, int retryTimes) {
        return HttpClientUtils.timeoutRetryPost(url, formData, retryTimes);
    }


    public String doThrowTimeOutExceptionGet(String url, Integer readTimeout)
            throws SocketTimeoutException, ConnectTimeoutException {
        return HttpClientUtils.requestDealTimeOutException(url, null, readTimeout);
    }


    public String doThrowTimeOutExceptionPostJson(String url, String jsonData)
            throws SocketTimeoutException, ConnectTimeoutException {
        return HttpClientUtils.requestTimeOutPost(url, jsonData);
    }
}
