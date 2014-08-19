package com.locate.bridge;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

import com.locate.common.DataBaseCache;
import com.locate.common.XmlMessageUtil;
import com.locate.gate.model.LocateUnionMessage;
import com.locate.rmds.RFAServerManager;

/**
 * All responses send to customer handle by this class.
 * @author CloudWei kaiweicai@163.com
 * create time 2014��8��19��
 * @copyRight by Author
 */
public class GateWayResponser {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	static Logger logger = Logger.getLogger(GateWayResponser.class.getName());

	public static void sentResponseMsg(byte msgType, Document response, Integer channelId) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		XmlMessageUtil.addLocateInfo(response, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		Channel channel = DataBaseCache.allChannelGroup.find(channelId);
		if (channel != null && channel.isConnected()) {
			channel.write(response);
		} else {
			logger.error("The channel had been closed when write login response to client. Channel ID is " + channelId);
		}
		logger.info("downStream message is :"+response.asXML());
	}

	public static void sentAllChannelNews(byte msgType, Document response) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		// message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
		XmlMessageUtil.addLocateInfo(response, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		DataBaseCache.allChannelGroup.write(response);
		logger.info("downStream message is :"+response);
	}

	public static void sentMrketPriceToSubsribeChannel(Document response, String itemName) {
		ChannelGroup channelGroup = DataBaseCache.itemNameChannelMap.get(itemName);
		channelGroup.write(response);
		if(channelGroup.size()==0){
			logger.error("channel has been clean,but the ric not be register! The itemName"+itemName);
		}
		logger.info("send message is :" + response.asXML() + " to order group" + channelGroup.getName());
	}

	public static void sendSnapShotToChannel(byte msgType, Document response, String itemName, int channelId) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		// message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
		XmlMessageUtil.addLocateInfo(response, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		DataBaseCache.allChannelGroup.find(channelId).write(response);
		logger.info("downStream message is :"+response);
	}

	public static void sentNotiFyResponseMsg(byte msgType, Document response, Integer channelId, int errorCode) {
		XmlMessageUtil.addLocateInfo(response, msgType, RFAServerManager.sequenceNo.getAndIncrement(), errorCode);
		Channel channel = DataBaseCache.allChannelGroup.find(channelId);
		if (channel != null && channel.isConnected()) {
			channel.write(response);
		} else {
			logger.error("The channel had been closed when write login response to client. Channel ID is " + channelId);
		}
		logger.info("downStream message is :"+response);
	}

	public static void brodcastStateResp(Document responseMsg) {
		if(!DataBaseCache.allChannelGroup.isEmpty()){
			DataBaseCache.allChannelGroup.write(responseMsg);
		}else{
			logger.info("None user loginin!");
		}
	}

	public static void notifyAllCustomersStateChange(LocateUnionMessage locateMessage) {
		String itemName = locateMessage.getItemName();
		ChannelGroup channelGroup = DataBaseCache.itemNameChannelMap.get(itemName);
		channelGroup.write(locateMessage);
		if(channelGroup.size()==0){
			logger.error("channel has been clean,but the ric not be register! The itemName"+itemName);
		}
		logger.info("send message is :" + locateMessage + " to order group" + channelGroup.getName());
	}

}
