package com.vipkid.mq.producer;

import java.io.Serializable;

import javax.jms.Destination;

public interface ProducerService {

	/**
	 * 发送文本消息
	 * @param destination
	 * @param message
	 */
	public void sendTextMessage(Destination destination, String message);

	/**
	 * 发送JSON数据
	 * @param destination
	 * @param t
	 */
	public <T> void sendJsonMessage(Destination destination, T t);

	/**
	 * 测试发送消息后添加额外处理
	 * @param destination
	 * @param message
	 */
	public void sendByConvertWithPostProcessor(Destination destination, String message);

	/**
	 * 测试方法, 用不同的方式发送
	 * @param destination
	 * @param obj
	 */
	public void sendObjectMessageWithAllType(Destination destination, Serializable obj);

	/**
	 * 发送消息，数据格式序列化对象
	 * @param destination
	 * @param obj
	 */
	public void sendObjectMessageWithConvert(Destination destination, Serializable obj);

	public void sendObjectMessageByCreateMessage(Destination destination, Serializable obj);

	public void sendObjectMessageBySessionCallback(Destination destination, Serializable obj);

	public void sendObjectMessageByProducerCallback(Destination destination, Serializable obj);
}
