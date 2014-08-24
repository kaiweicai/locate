package com.locate.bridge;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

import com.locate.common.DataBaseCache;
import com.locate.common.SystemConstant;
import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.XmlMessageUtil;
import com.locate.rmds.RFAServerManager;

/**
 * 
 * @author CloudWei kaiweicai@163.com
 * create time 2014年8月20日
 * @copyRight by Author
 */
public class GateWayResponser {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	static Logger logger = Logger.getLogger(GateWayResponser.class.getName());

	public static void sentResponseMsg(LocateUnionMessage response, Integer channelId) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		Channel channel = DataBaseCache.allChannelGroup.find(channelId);
		if (channel != null && channel.isConnected()) {
			channel.write(response);
		} else {
			logger.error("The channel had been closed when write login response to client. Channel ID is " + channelId);
		}
		logger.info("downStream message is :"+response);
	}

	public static void sentAllChannelNews(byte msgType, Document response) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		// message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
		XmlMessageUtil.addLocateInfo(response, msgType, SystemConstant.sequenceNo.getAndIncrement(), 0);
		DataBaseCache.allChannelGroup.write(response);
		logger.info("downStream message is :"+response);
	}

	public static void sentMrketPriceToSubsribeChannel(LocateUnionMessage locateMessage) {
		String itemName = locateMessage.getItemName();
		ChannelGroup channelGroup = DataBaseCache.itemNameChannelMap.get(itemName);
		channelGroup.write(locateMessage);
		if(channelGroup.size()==0){
			logger.error("channel has been clean,but the ric not be register! The itemName"+itemName);
		}
		logger.info("send message is :" + locateMessage + " to order group" + channelGroup.getName());
	}

	public static void sendSnapShotToChannel(LocateUnionMessage locatMessage, int channelId) {
		DataBaseCache.allChannelGroup.find(channelId).write(locatMessage);
		logger.info("downStream message is :"+locatMessage);
	}

//	public static void sentNotiFyResponseMsg(LocateUnionMessage response, Integer channelId) {
//		Channel channel = DataBaseCache.allChannelGroup.find(channelId);
//		if (channel != null && channel.isConnected()) {
//			channel.write(response);
//		} else {
//			logger.error("The channel had been closed when write login response to client. Channel ID is " + channelId);
//		}
//		logger.info("downStream message is :"+response);
//	}

	public static void brodcastStateResp(LocateUnionMessage responseMsg) {
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
