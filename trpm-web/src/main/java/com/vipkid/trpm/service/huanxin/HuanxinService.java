package com.vipkid.trpm.service.huanxin;

import com.google.api.client.util.Maps;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.http.service.HttpApiClient;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.trpm.dao.TeacherDao;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2017/1/11 下午3:33
 */
@Service
public class HuanxinService {

    private Logger logger = LoggerFactory.getLogger(HuanxinService.class);

    @Autowired
    private HttpApiClient httpApiClient;

    @Autowired
    private TeacherDao teacherDao;

    private final static String URL = "https://a1.easemob.com/513276871/teachertest/users";

    public boolean signUpHuanxin(String userName, String password) {
        try {

            Map userAndPassword = Maps.newHashMap();
            userAndPassword.put("username", userName);
            userAndPassword.put("password", password);
            String jsonData = JsonUtils.toJSONString(userAndPassword);
            HttpResponse response = httpApiClient.doPostRESTful(URL, jsonData);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                logger.info("HuanxinService signUpHuanxin success!userName={}", userName);
                return true;
            } else {
                String content = EntityUtils.toString(response.getEntity());
                Map<String, String> contentMap = JsonUtils.toBean(content, Map.class);
                logger.error(
                    "HuanxinService signUpHuanxin fail!userName={},response status ={},error={}",
                    userName, statusCode, contentMap.get("error"));
            }

        } catch (Exception e) {
            logger.error("HuanxinService signUpHuanxin happens error ", e);
        }

        return false;
    }

    public List<String> findAllRegularButNoHuanxinId() {
        return teacherDao.findAllRegularButNoHuanxinId();
    }
}
