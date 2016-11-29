package com.vipkid.http.utils;

import org.apache.commons.collections.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/16 下午2:51
 */
public class HttpClientUtils {


    /**
     * 实现描述：返回请求的内容，支持默认的字符集，如果httpEntity没有指定的话
     *
     * @author steven
     * @version v1.0.0
     * @see
     * @since 2016/11/16 下午2:51
     */
    private static class CharsetableResponseHandler implements ResponseHandler<String> {

        private String defaultEncoding;

        public CharsetableResponseHandler(String defaultEncoding) {
            this.defaultEncoding = defaultEncoding;
        }

        @Override
        public String handleResponse(HttpResponse response) throws IOException {
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
            return entity == null ? null : EntityUtils.toString(entity, defaultEncoding);
        }
    }

    private static HttpClient CLIENT;

    private static final int CONNECTION_TIMEOUT = 1000;

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private static final int READ_TIMEOUT = 10000;

    private static final int DEFAULT_MAX_PER_ROUTE = 100;

    private static final int MAX_TOTAL = 400;

    static {
        LayeredConnectionSocketFactory ssl = null;

        final SSLContext sslcontext;
        try {
            sslcontext = SSLContexts.custom().useTLS().loadTrustMaterial(
                    KeyStore.getInstance(KeyStore.getDefaultType()), new TrustStrategy() {

                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            return true;
                        }
                    }).build();
            ssl = new SSLConnectionSocketFactory(sslcontext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        final Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", ssl)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(sfr);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        connectionManager.setMaxTotal(MAX_TOTAL);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIMEOUT).setConnectTimeout(
                CONNECTION_TIMEOUT).build();//设置请求和传输超时时间

        CLIENT = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connectionManager).build();

    }

    private static String getDefaultEncoding(String defaultEncoding) {
        return StringUtils.isEmpty(defaultEncoding) ? Consts.UTF_8.name() : defaultEncoding;
    }

    /**
     * 请求特定的url提交表单，使用post方法，返回响应的内容
     *
     * @param url
     * @param formData 表单的键值对
     * @return
     */
    public static String post(String url, Map<String, String> formData, Integer readTimeout) {
        return HttpClientUtils.post(url, formData, null, readTimeout);
    }

    /**
     * 请求特定的url提交表单，使用post方法，返回响应的内容
     *
     * @param url
     * @param formData 表单的键值对
     * @return
     */
    public static String post(String url, Map<String, String> formData) {
        return HttpClientUtils.post(url, formData, null, null);
    }

    /**
     * 请求特定的url提交表单，使用post方法，返回响应的内容，超时重试指定次数
     *
     * @param url
     * @param formData
     * @param retryTimes
     * @return
     */
    public static String timeoutRetryPost(String url, Map<String, String> formData, int retryTimes) {
        if (retryTimes < 0) {
            throw new IllegalArgumentException("retry times must great than or equal 0");
        }
        SocketTimeoutException ste = null;
        for (int i = 0; i <= retryTimes; i++) {
            try {
                String result = postWithTimeoutException(url, formData, null,null, null);
                if (i > 0) {
                    logger.warn("post [{}] retry {} times", url, i);
                }
                return result;
            } catch (SocketTimeoutException e) {
                ste = e;
            }
        }
        logger.error("post [{}] timeout, retry {} times", url, retryTimes, ste);
        return null;
    }

    /**
     * 指定headers的post请求
     * @param url
     * @param formData
     * @param headers
     * @return
     */
    public static String appointHeadersPost(String url, Map<String, String> formData,Map<String,String> headers) {
        try {
            return postWithTimeoutException(url, formData, null,headers, null);
        } catch (SocketTimeoutException e) {
            HttpClientUtils.logger.error(String.format("post [%s] timeout", url), e);
            return null;
        }
    }

    /**
     * 请求特定的url提交表单，使用post方法，返回响应的内容
     *
     * @param url
     * @param formData        表单的键值对
     * @param defaultEncoding 处理 form encode 的编码，以及作为 contentType 的默认编码
     * @return
     */
    public static String post(String url, Map<String, String> formData, String defaultEncoding, Integer readTimeout) {
        try {
            return postWithTimeoutException(url, formData, defaultEncoding, null, readTimeout);
        } catch (SocketTimeoutException e) {
            HttpClientUtils.logger.error(String.format("post [%s] timeout", url), e);
            return null;
        }
    }

    private static String postWithTimeoutException(String url, Map<String, String> formData, String defaultEncoding,Map<String, String> headers, Integer readTimeout)
            throws SocketTimeoutException {
        defaultEncoding = HttpClientUtils.getDefaultEncoding(defaultEncoding);

        HttpPost post = new HttpPost(url);
        if (readTimeout != null && readTimeout > 0) {
            RequestConfig.Builder configBuilder = null;
            if(post.getConfig()!=null){
                configBuilder=RequestConfig.copy(post.getConfig());
            }else {
                configBuilder=RequestConfig.custom();
            }
            configBuilder.setSocketTimeout(readTimeout);
            post.setConfig(configBuilder.build());
        }
        String content = null;
        List<NameValuePair> nameValues = new ArrayList<NameValuePair>();
        if (formData != null && !formData.isEmpty()) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                nameValues.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                post.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValues, defaultEncoding);
            post.setEntity(formEntity);
            content = HttpClientUtils.CLIENT.execute(post, new CharsetableResponseHandler(defaultEncoding));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unsupported Encoding " + defaultEncoding, e);
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (Exception e) {
            HttpClientUtils.logger.error(String.format("post [%s] happens error ", url), e);
        }
        return content;
    }

    /**
     * 请求特定的url提交Json字符串，使用post方法，返回响应的内容
     *
     * @param url
     * @param jsonData
     * @return
     */
    public static String post(String url, String jsonData){
        return HttpClientUtils.post(url, jsonData, null, null);
    }

    public static String post(String url, String jsonData, Map<String, String> headers) {
        return HttpClientUtils.post(url, jsonData, null, headers);
    }

    /**
     * 请求特定的url提交Json字符串，使用post方法，返回响应的内容
     *
     * @param url
     * @param jsonData
     * @return
     */
    public static String post(String url, String jsonData, String defaultEncoding, Map<String, String> headers) {
        defaultEncoding = HttpClientUtils.getDefaultEncoding(defaultEncoding);
        String content = null;
        try {
            HttpPost post = new HttpPost(url);
            if(org.apache.commons.collections.MapUtils.isNotEmpty(headers)){
                for(String key : headers.keySet()){
                    post.setHeader(key, headers.get(key));
                }
            }
            StringEntity entity = new StringEntity(jsonData, defaultEncoding);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(entity);
            HttpResponse response = HttpClientUtils.CLIENT.execute(post);
            content = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            HttpClientUtils.logger.error(String.format("post [%s] happens error ", url), e);
        }
        return content;
    }

    /**
     * 上传文件方法
     *
     * @param url
     * @param key
     * @param files
     * @return
     */
    public static String postFile(String url, String key, List<File> files) {
        HttpPost post = new HttpPost(url);
        String content = null;

        MultipartEntity multipartEntity = new MultipartEntity();
        for (File file : files) {
            multipartEntity.addPart(key, new FileBody(file));
        }
        try {
            post.setEntity(multipartEntity);
            HttpResponse response = HttpClientUtils.CLIENT.execute(post);
            content = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            HttpClientUtils.logger.error(String.format("post [%s] happens error ", url), e);
        }

        return content;
    }

    /**
     * 请求特定的url，返回响应的内容
     *
     * @param url
     * @return
     */
    public static String request(String url) {
        return HttpClientUtils.request(url, null, null);
    }

    /**
     * 请求特定的url，返回响应的内容
     *
     * @param url
     * @param readTimeout
     * @return
     */
    public static String request(String url, Integer readTimeout) {
        return HttpClientUtils.request(url, null, readTimeout);
    }

    /**
     * 请求特定的url，返回响应的内容
     *
     * @param url
     * @param defaultEncoding 如果返回的 contentType 中没有指定编码，则使用默认编码
     * @param readTimeout:    socket timeout
     * @return
     */
    public static String request(String url, String defaultEncoding, Integer readTimeout) {
        defaultEncoding = HttpClientUtils.getDefaultEncoding(defaultEncoding);

        HttpGet get = new HttpGet(url);
        if (readTimeout != null && readTimeout > 0) {
            RequestConfig.Builder configBuilder = null;
            if(get.getConfig()!=null){
                configBuilder=RequestConfig.copy(get.getConfig());
            }else {
                configBuilder=RequestConfig.custom();
            }
            configBuilder.setSocketTimeout(readTimeout);
            get.setConfig(configBuilder.build());
        }
        String content = null;
        try {
            content = HttpClientUtils.CLIENT.execute(get, new CharsetableResponseHandler(defaultEncoding));
        } catch (Exception e) {
            HttpClientUtils.logger.error(String.format("request [%s] happens error ", url), e);
        }
        return content;
    }

    /**
     * 请求特定的url，返回响应的内容
     *
     * @param url
     * @param defaultEncoding 如果返回的 contentType 中没有指定编码，则使用默认编码
     * @param readTimeout:    socket timeout
     * @return
     * @throws SocketTimeoutException
     * @throws ConnectTimeoutException
     */
    public static String requestDealTimeOutException(String url, String defaultEncoding, Integer readTimeout)
            throws SocketTimeoutException, ConnectTimeoutException {
        defaultEncoding = HttpClientUtils.getDefaultEncoding(defaultEncoding);

        HttpGet get = new HttpGet(url);
        if (readTimeout != null && readTimeout > 0) {
            RequestConfig.Builder configBuilder = null;
            if(get.getConfig()!=null){
                configBuilder=RequestConfig.copy(get.getConfig());
            }else {
                configBuilder=RequestConfig.custom();
            }
            configBuilder.setSocketTimeout(readTimeout);
            get.setConfig(configBuilder.build());
        }
        String content = null;
        try {
            content = HttpClientUtils.CLIENT.execute(get, new CharsetableResponseHandler(defaultEncoding));
        } catch(SocketTimeoutException e){
            HttpClientUtils.logger.error(String.format("requestDealTimeOutException [%s] happens error ", url), e);
            throw  e;
        } catch(ConnectTimeoutException e){
            HttpClientUtils.logger.error(String.format("requestDealTimeOutException [%s] happens error ", url), e);
            throw  e;
        } catch (Exception e) {
            HttpClientUtils.logger.error(String.format("requestDealTimeOutException [%s] happens error ", url), e);
        }finally {
            if(get!=null){
                get.releaseConnection();
            }
        }
        return content;
    }

    /**
     * 对url解码
     *
     * @param str    需要解码的字符串
     * @param encode 编码，如 GBK, UTF-8 等
     * @return 如果解码出错，会传回 null；如果 str 为空，则也返回 null
     */
    public static String urlDecode(String str, String encode) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        String result = null;
        try {
            result = URLDecoder.decode(str, encode);
        } catch (UnsupportedEncodingException e) {
            HttpClientUtils.logger.error("urldecode error for {} with encode {}", str, encode);
        }
        return result;
    }

    /**
     * post请求，上传文件，请求参数为文件名加文件内容
     *
     * @param url               请求的url
     * @param multipartFileList 上传文件列表
     * @return
     */
    //    public static String postFile(String url, List<MultipartFile> multipartFileList, Integer readTimeOut) {
    //        HttpPost httppost = new HttpPost(url);
    //        String content = null;
    //        //1.设置超时时间
    //        if (readTimeOut != null && readTimeOut > 0) {
    //            RequestConfig.Builder configBuilder = null;
    //            if(httppost.getConfig()!=null){
    //                configBuilder=RequestConfig.copy(httppost.getConfig());
    //            }else {
    //                configBuilder=RequestConfig.custom();
    //            }
    //            configBuilder.setSocketTimeout(readTimeOut);
    //            httppost.setConfig(configBuilder.build());
    //        }
    //
    //        //2.组装请求参数
    //        MultipartEntity reqEntity = new MultipartEntity(
    //                HttpMultipartMode.BROWSER_COMPATIBLE, null,
    //                Charset.forName("UTF-8"));
    //        try {
    //            for (MultipartFile multipartFile : multipartFileList) {
    //                File tempFile = FileUtils.touch(multipartFile.getOriginalFilename()+String.valueOf(System.currentTimeMillis()));
    //                multipartFile.transferTo(tempFile);
    //                reqEntity.addPart(multipartFile.getOriginalFilename(), new FileBody(tempFile));
    //            }
    //            httppost.setEntity(reqEntity);
    //
    //            //3.获取响应结果
    //            HttpResponse response = HttpClientUtils.CLIENT.execute(httppost);
    //            content = EntityUtils.toString(response.getEntity());
    //        } catch (IOException e) {
    //            HttpClientUtils.logger.error("postFileForResponse error " + e);
    //        }
    //        return content;
    //    }


    /**
     * 对特定的url的post请求，返回响应体
     *
     * @param url      请求的url
     * @param formData 请求参数,转化为json
     * @return
     */
    //    public static HttpResponse postMapToJsonForResponse(String url, Map<String, Object> formData) {
    //        String encoding = HttpClientUtils.getDefaultEncoding(null);
    //
    //        HttpPost post = new HttpPost(url);
    //        String param = JacksonUtils.marshalToString(formData);
    //        List<NameValuePair> nameValues = new ArrayList<NameValuePair>();
    //        nameValues.add(new BasicNameValuePair("param", param));
    //        try {
    //            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValues, encoding);
    //            post.setEntity(formEntity);
    //
    //            RequestConfig.Builder configBuilder = null;
    //            if(post.getConfig()!=null){
    //                configBuilder=RequestConfig.copy(post.getConfig());
    //            }else {
    //                configBuilder=RequestConfig.custom();
    //            }
    //            configBuilder.setSocketTimeout(100000);
    //            post.setConfig(configBuilder.build());
    //
    //            return HttpClientUtils.CLIENT.execute(post);
    //        } catch (UnsupportedEncodingException e) {
    //            throw new RuntimeException("unsupported Encoding " + encoding, e);
    //        } catch (SocketTimeoutException e) {
    //            logger.error("error when postForResponse " + e);
    //        } catch (Exception e) {
    //            HttpClientUtils.logger.error(String.format("post [%s] happens error ", url), e);
    //        }
    //        return null;
    //    }

    /**
     * 对特定的url的post请求，返回响应体
     *
     * @param url      请求的url
     * @param formData 请求参数
     * @return
     */
    public static HttpResponse postMapForResponse(String url, Map<String, String> formData) {
        String encoding = HttpClientUtils.getDefaultEncoding(null);

        HttpPost post = new HttpPost(url);
        List<NameValuePair> nameValues = new ArrayList<NameValuePair>();
        if (formData != null && !formData.isEmpty()) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                nameValues.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValues, encoding);
            post.setEntity(formEntity);
            return HttpClientUtils.CLIENT.execute(post);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unsupported Encoding " + encoding, e);
        } catch (SocketTimeoutException e) {
            logger.error("error when postForResponse " + e);
        } catch (Exception e) {
            HttpClientUtils.logger.error(String.format("post [%s] happens error ", url), e);
        }
        return null;
    }

    /**
     * 对特定的url的post请求，返回响应体
     *
     * @param url      请求的url
     * @return
     */
    public static HttpResponse get(String url) {
        HttpGet get = new HttpGet(url);
        try {
            return HttpClientUtils.CLIENT.execute(get);
        } catch (SocketTimeoutException e) {
            logger.error("error when get " + e);
        } catch (Exception e) {
            HttpClientUtils.logger.error(String.format("get [%s] happens error ", url), e);
        }
        return null;
    }

    private HttpClientUtils() {
    }

    /**
     * 请求特定的url提交Json字符串，使用post方法，返回响应的内容
     *
     * @param url
     * @param jsonData
     * @return
     */
    public static String requestTimeOutPost(String url, String jsonData)
            throws SocketTimeoutException, ConnectTimeoutException{
        String defaultEncoding = HttpClientUtils.getDefaultEncoding(null);
        String content = null;
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonData, defaultEncoding);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(entity);
            HttpResponse response = HttpClientUtils.CLIENT.execute(post);
            content = EntityUtils.toString(response.getEntity());
        } catch(SocketTimeoutException e){
            HttpClientUtils.logger.error(String.format("requestTimeOutPost [%s] happens error ", url), e);
            throw  e;
        } catch(ConnectTimeoutException e){
            HttpClientUtils.logger.error(String.format("requestTimeOutPost [%s] happens error ", url), e);
            throw  e;
        } catch (Exception e) {
            HttpClientUtils.logger.error(String.format("requestTimeOutPost [%s] happens error ", url), e);
        }finally {
            if(post!=null){
                post.releaseConnection();
            }
        }
        return content;
    }
}
