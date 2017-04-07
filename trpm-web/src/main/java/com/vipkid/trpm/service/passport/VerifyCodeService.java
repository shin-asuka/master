package com.vipkid.trpm.service.passport;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.google.api.client.util.Maps;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.proxy.RedisProxy;

/**
 * 验证码服务
 */
@Service
public class VerifyCodeService {
    private static final Logger logger = LoggerFactory.getLogger(VerifyCodeService.class);

    @Resource
    private RedisProxy redisProxy;

    public Map<String, String> getVerifyCode() throws IOException {
        Map<String, String> map = Maps.newHashMap();
        try {
            String key = UUID.randomUUID().toString();
            Cage cage = new GCage();
            String code = cage.getTokenGenerator().next();
            code = code.substring(0, 4);
            String redisKey = String.format(ApplicationConstant.RedisConstants.IMAGE_CODE_KEY, key);
            redisProxy.setex(redisKey, ApplicationConstant.RedisConstants.IMAGE_CODE_INVALID_SEC, code);
            logger.info("图片验证码 key = {}, code = {} ", key, code);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            cage.draw(code, bs);
            // 创建编码对象
            Base64.Encoder base64 = Base64.getEncoder();
            logger.info("create verify code end,code = {}", code);
            map.put("key", key);
            map.put("value", "data:image/jpg;base64," + base64.encodeToString(bs.toByteArray()));
        } catch (IOException e) {
            logger.error("生成验证码时出错", e);
        }
        return map;
    }

    public boolean checkVerifyCode(String key, String verifyCode) {
        try {
            String redisKey = String.format(ApplicationConstant.RedisConstants.IMAGE_CODE_KEY, key);
            String redisValue = redisProxy.get(redisKey);
            if (StringUtils.isEmpty(redisValue)) {
                logger.warn("图片验证码不存在，key = {}", key);
                return false;
            } else if (!StringUtils.equalsIgnoreCase(verifyCode, redisValue)) {
                logger.warn("输入的图片验证码不正确，key = {},value = {},verifyCode = {}", redisKey, redisValue, verifyCode);
                return false;
            }
            logger.info("验证码验证通过");
            redisProxy.del(redisKey);
            return true;
        } catch (Exception e) {
            logger.error("校验验证码出错,key = {},verifyCode = {}", key, verifyCode, e);
            return false;
        }
    }
}
