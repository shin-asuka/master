package com.vipkid.http.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/16 下午3:20
 */
@Component
@Lazy(false)
public class HttpUrlConstant {

    /**
     * homework服务器地址
     */
    @Value("${api.homework.server.url}")
    private String apiHomeworkServerUrl;

    /**
     * teacher-information-service服务器地址
     */
    @Value("${api.tis.server.url}")
    private String apiTisServerUrl;

    @Value("${api.gateway.server.url}")
    private String apiGatewayServerUrl;

    public String getApiHomeworkServerUrl() {
        return apiHomeworkServerUrl;
    }

    public void setApiHomeworkServerUrl(String apiHomeworkServerUrl) {
        this.apiHomeworkServerUrl = apiHomeworkServerUrl;
    }

    public String getApiTisServerUrl() {
        return apiTisServerUrl;
    }

    public void setApiTisServerUrl(String apiTisServerUrl) {
        this.apiTisServerUrl = apiTisServerUrl;
    }

    public String getApiGatewayServerUrl() {
        return apiGatewayServerUrl;
    }

    public void setApiGatewayServerUrl(String apiGatewayServerUrl) {
        this.apiGatewayServerUrl = apiGatewayServerUrl;
    }
}
