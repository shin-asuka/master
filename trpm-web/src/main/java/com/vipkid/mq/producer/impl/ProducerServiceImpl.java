package com.vipkid.mq.producer.impl;

import java.io.IOException;
import java.io.Serializable;

import javax.jms.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.*;
import org.springframework.stereotype.Component;

import com.vipkid.mq.producer.ProducerService;

@Component
public class ProducerServiceImpl implements ProducerService {

    private static final Logger logger = LoggerFactory.getLogger(ProducerServiceImpl.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * text message
     *
     * @param destination
     * @param message
     */
    @Override
    public void sendTextMessage(Destination destination, final String message) {
        logger.info("---------------生产者发了一个消息：" + message);
        jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                /*TextMessage textMessage = session.createTextMessage(message);
                textMessage.setJMSReplyTo(responseDestination);
				return textMessage;*/
//                MapMessage mapMessage = session.createMapMessage();
                return session.createTextMessage(message);
            }
        });
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * json message
     *
     * @param destination
     */
    @Override
    public <T> void sendJsonMessage(Destination destination, T t) {
        logger.info("---------------生产者发了一个消息：" + t);
        jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                String message = null;
                try {
                    message = getFeatureMapper().writeValueAsString(t);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("创建消息时候出现异常, message: {}, e: {}",t,  e);
                }
                return session.createTextMessage(message);
            }
        });
    }

    public static ObjectMapper getFeatureMapper() {
        mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_COMMENTS, true);
        return mapper;
    }

    /**
     * 测试发送消息后添加额外处理
     *
     * @param destination
     * @param message
     * @deprecated test method
     */
    @Override
    public void sendByConvertWithPostProcessor(Destination destination, String message) {
        jmsTemplate.convertAndSend(destination, message, new MessagePostProcessor() {
            @Override  //发送完消息做一些其他事情
            public Message postProcessMessage(Message message) throws JMSException {
                //TODO
                return null;
            }
        });
    }

    /**
     * 测试方法, 用不同的方式发送 ObjectMessage
     *
     * @param destination
     * @param obj
     * @deprecated
     */
    @Override
    public void sendObjectMessageWithAllType(final Destination destination, final Serializable obj) {
        //未使用MessageConverter的情况
        sendObjectMessageByCreateMessage(destination, obj);
        //使用MessageConverter的情况
        sendObjectMessageWithConvert(destination, obj);
        sendObjectMessageBySessionCallback(destination, obj);
        sendObjectMessageByProducerCallback(destination, obj);
    }

    /**
     * 推荐
     *
     * @param destination
     * @param obj
     */
    @Override
    public void sendObjectMessageWithConvert(Destination destination, Serializable obj) {
        jmsTemplate.convertAndSend(destination, obj);
    }

    @Override
    public void sendObjectMessageByCreateMessage(Destination destination, final Serializable obj) {
        jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objMessage = session.createObjectMessage(obj);
                return objMessage;
            }
        });
    }

    @Override
    public void sendObjectMessageBySessionCallback(final Destination destination, final Serializable obj) {
        jmsTemplate.execute(new SessionCallback<Object>() {
            public Object doInJms(Session session) throws JMSException {
                MessageProducer producer = session.createProducer(destination);
                Message message = session.createObjectMessage(obj);
                producer.send(message);
                return null;
            }
        });
    }

    @Override
    public void sendObjectMessageByProducerCallback(final Destination destination, final Serializable obj) {
        jmsTemplate.execute(new ProducerCallback<Object>() {
            public Object doInJms(Session session, MessageProducer producer)
                    throws JMSException {
                Message message = session.createObjectMessage(obj);
                producer.send(destination, message);
                return null;
            }
        });
    }
}
