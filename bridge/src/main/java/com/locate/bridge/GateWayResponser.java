package com.locate.bridge;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.constant.SystemConstant;
import com.locate.common.datacache.GateChannelCache;
import com.locate.common.logging.err.ErrorLogHandler;
import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.XmlMessageUtil;

/**
 * 
 * @author CloudWei kaiweicai@163.com
 * create time 2014年8月20日
 * @copyRight by Author
 */
public class GateWayResponser {
	private static ErrorLogHandler errorLogHandler = ErrorLogHandler.getLogger(GateWayResponser.class);
	public static final Charset CHARSET = Charset.forName("UTF-8");
	static Logger logger = LoggerFactory.getLogger(GateWayResponser.class.getName());

	public static void sentResponseMsg(LocateUnionMessage response, Integer channelId) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		Channel channel = GateChannelCache.id2ChannelMap.get(channelId);
		if (channel != null && channel.isActive()) {
			channel.writeAndFlush(response);
		} else {
			errorLogHandler.error("The channel had been closed when write login response to client. Channel ID is " + channelId);
		}
		logger.info("downStream message is :"+response);
	}
	
	public static void sentAllChannelNews(byte msgType, Document response) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		// message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
		XmlMessageUtil.addLocateInfo(response, msgType, SystemConstant.sequenceNo.getAndIncrement(), 0);
		GateChannelCache.allChannelGroup.writeAndFlush(response);
		logger.info("downStream message is :"+response);
	}

	public static void sentMrketPriceToSubsribeChannel(LocateUnionMessage locateMessage) {
		String itemName = locateMessage.getItemName();
		ChannelGroup channelGroup = GateChannelCache.itemNameChannelGroupMap.get(itemName);
		if(channelGroup==null){
			errorLogHandler.error("channel can not find ! The itemName "+itemName);
			return;
		}
		if(channelGroup.size()==0){
			logger.warn("channel has been clean,but the ric not be register! The itemName"+itemName);
			return;
		}
		channelGroup.writeAndFlush(locateMessage);
		logger.info("send message is :" + locateMessage + " to order group" + channelGroup.name());
	}

	public static void sendSnapShotToChannel(LocateUnionMessage locatMessage, int channelId) {
		GateChannelCache.id2ChannelMap.get(channelId).writeAndFlush(locatMessage);
		logger.info("downStream message is :"+locatMessage);
	}

//	public static void sentNotiFyResponseMsg(LocateUnionMessage response, Integer channelId) {
//		Channel channel = DataBaseCache.allChannelGroup.find(channelId);
//		if (channel != null && channel.isConnected()) {
//			channel.write(response);
//		} else {
//			errorLogHandler.error("The channel had been closed when write login response to client. Channel ID is " + channelId);
//		}
//		logger.info("downStream message is :"+response);
//	}

	public static void brodcastStateResp(LocateUnionMessage responseMsg) {
		if(!GateChannelCache.allChannelGroup.isEmpty()){
			GateChannelCache.allChannelGroup.write(responseMsg);
		}else{
			logger.info("None user loginin!");
		}
	}

	public static void notifyAllCustomersStateChange(LocateUnionMessage locateMessage) {
		String itemName = locateMessage.getItemName();
		List<String> allItemNameList= new ArrayList<String>();
		allItemNameList.add(itemName);
		allItemNameList.addAll(GateChannelCache.item2derivedMap.get(itemName));
		for(String allName:allItemNameList){
			locateMessage.setItemName(allName);
			ChannelGroup channelGroup = GateChannelCache.itemNameChannelGroupMap.get(allName);
			if(channelGroup==null){
				logger.warn("channel group is null!");
				return;
			}
			if(channelGroup.size()==0){
				logger.warn("channel has been clean,but the ric not be register! The itemName"+allName);
				return;
			}
			channelGroup.write(locateMessage);
			logger.info("send message is :" + locateMessage + " to order group" + channelGroup.name());
		}
	}
}
