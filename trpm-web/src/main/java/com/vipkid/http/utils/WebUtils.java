/**
 *
 */
package com.vipkid.http.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.vipkid.http.vo.HttpResult;

/**
 * http请求客户端工具类
 *
 * @author zouqinghua
 * @date 2016年3月11日 上午10:31:48
 *
 */
public class WebUtils {

    private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);
    private static final String DEFAULT_CHARSET = "UTF8";

    private static final int STATUS_SUCCESS = 200;

    private static final int DEFAULT_TIMEOUT = 2 * 1000;
    private static final RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
            .setConnectionRequestTimeout(DEFAULT_TIMEOUT).setConnectTimeout(DEFAULT_TIMEOUT)
            .setSocketTimeout(DEFAULT_TIMEOUT).build();

    // cookie
    private static final String COOKIE_NAME = "Cookie";
    private static final String COOKIE_SPLIT = ";";

    public static HttpResult post(String url) {
        return post(url, null, null, null);
    }

    public static HttpResult post(String url, Map<String, String> params) {
        return post(url, params, null, null);
    }

    public static HttpResult post(String url, Map<String, String> params, Map<String, String> heads) {
        return post(url, params, heads, null);
    }

    /**
     *
     * @param url
     * @param params
     * @param heads
     * @param cookies
     * @return
     */
    public static HttpResult post(String url, Map<String, String> params, Map<String, String> heads,
                                  Map<String, String> cookies) {
        logger.info("HTTP Post data,url = "+url+",params = "+params+",heads = "+heads+",cookies = "+cookies);
        HttpResult result = new HttpResult();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);

            // 设置参数
            List<NameValuePair> paramsList = ParamsToNameValuePair(params);
            if (CollectionUtils.isNotEmpty(paramsList)) {
                httpPost.setEntity(new UrlEncodedFormEntity(paramsList, Charset.forName(DEFAULT_CHARSET)));
            }
            // 设置cookie
            String cookie = cookiesToString(cookies);
            if (StringUtils.isNotBlank(cookie)) {
                httpPost.addHeader(COOKIE_NAME, cookie);
            }
            // 设置Header
            if (heads != null) {
                for (String key : heads.keySet()) {
                    httpPost.addHeader(key, heads.get(key));
                }
            }

            CloseableHttpClient httpclient = HttpClients.createDefault();
            response = httpclient.execute(httpPost);
            logger.info("Post data,response status line = "+response.getStatusLine() );
            if (STATUS_SUCCESS != response.getStatusLine().getStatusCode()) {
                logger.info("httpPost 网路请求服务端异常  " + response.getStatusLine());
                result.setMessage(response.getStatusLine().toString());
                result.setStatus(HttpResult.STATUS_NETWORK_ERROR);
            } else {
                HttpEntity entity = response.getEntity();
                result.setMessage(response.getStatusLine().toString());
                result.setStatus(HttpResult.STATUS_SUCCESS);
                result.setResponse(EntityUtils.toString(entity));
                logger.info("httpPost 网路请求成功  " + response.getStatusLine());
            }
        } catch (Exception e) {
            logger.error("httpPost 网路请求失败 ,url = "+url+", params = "+params+", e= "+e );
            result.setMessage("httpPost 网路请求失败 " + e.getMessage());
            result.setStatus(HttpResult.STATUS_NETWORK_FAIL);
            result.setException(e);
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("关闭输出流时出错，url = "+url+", e=", e);
                }
            }
        }

        return result;
    }

    /**
     * params 由map转换成NameValuePair格式数据
     *
     * @param params
     * @return
     */
    public static List<NameValuePair> ParamsToNameValuePair(Map<String, String> params) {
        List<NameValuePair> paramsList = Lists.newArrayList();
        if (params != null && !params.isEmpty()) {
            for (String k : params.keySet()) {
                paramsList.add(new BasicNameValuePair(k, params.get(k)));
            }
        }
        return paramsList;
    }

    /**
     * cookies 由map转换成http Cookie 数据格式
     *
     * @param cookies
     * @return
     */
    public static String cookiesToString(Map<String, String> cookies) {
        List<String> cookieList = Lists.newArrayList();
        if (cookies != null && !cookies.isEmpty()) {
            for (String key : cookies.keySet()) {
                String cookie = new StringBuilder(key).append("=\"").append(cookies.get(key)).append("\"").toString();
                cookieList.add(cookie);
            }
        }
        return StringUtils.join(cookieList, COOKIE_SPLIT);
    }

    public static String simpleGet(String url) {
        logger.info("get data,url = "+url );
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(DEFAULT_REQUEST_CONFIG);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            response = httpclient.execute(httpGet);
            logger.info("get data,response status line = "+response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String rt = EntityUtils.toString(entity);
            if(HttpStatus.OK.value()!=response.getStatusLine().getStatusCode()){
                logger.error("http get error =  "+ rt);
                throw new Exception(rt);
            }
            return rt;
        } catch (Exception e) {
            logger.error("get data error,url = "+url+" e= "+e);
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("关闭输出流时出错，url = "+url+" e= "+e);
                }
            }
        }
        return null;
    }

    public static String postNameValuePair(String url, Object object) {
//    	JSONObject json = JsonUtils.toJSONObject(object);
        String json = JsonUtils.toJSONString(object);

        logger.info("Post data,url = {},params = {}", url, json);
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            Map<String, Object> map = JsonUtils.parseJsonToHttpParams(JsonUtils.parseObject(json));
//            Map<String, Object> map = MapUtils.parseJsonToMap(json);
            List<NameValuePair> paramsList = Lists.newArrayList();
            if( json != null){
//				for (String key : json.keySet()) {
//					String value = json.getString(key);
//					paramsList.add(new BasicNameValuePair(key, value));
//				}
                for (String key : map.keySet()) {
                    String value = map.get(key)==null?null:map.get(key).toString();
                    paramsList.add(new BasicNameValuePair(key, value));
                }
            }
            logger.info("Post data map,url = {},params = {}", url, map);
            //httpPost.addHeader("Authorization", UserUtils.getAuthorization());
            httpPost.setEntity(new UrlEncodedFormEntity(paramsList, Charset.forName(DEFAULT_CHARSET)));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            response = httpclient.execute(httpPost);
            logger.info("Post data,response status line = {}",response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String rt = EntityUtils.toString(entity);
            return rt;
        } catch (Exception e) {
            logger.error("Post data error,url = {},params = {}",url,json,e);
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("关闭输出流时出错，url = {}",url,e);
                }
            }
        }
        return null;
    }

    public static String postJSON(String url, JSONObject json) {
        logger.info("Post data,url = {},params = {}", url, json);
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(json.toString(),"utf-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            logger.info("Post data map,url = {},params = {}", url, json);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            response = httpclient.execute(httpPost);
            logger.info("Post data,response status line = {}",response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String rt = EntityUtils.toString(entity);
            return rt;
        } catch (Exception e) {
            logger.error("Post data error,url = {},params = {}",url,json,e);
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("关闭输出流时出错，url = {}",url,e);
                }
            }
        }
        return null;
    }
}
