package com.locate.bridge;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

import com.locate.common.DataBaseMap;
import com.locate.common.XmlMessageUtil;
import com.locate.gate.GateWayServer;
import com.locate.gate.model.LocateMessage;
import com.locate.rmds.RFAServerManager;
import com.reuters.rfa.omm.OMMMsg;

/**
 * RFA 通过该程序将消息发送到gateway.
 * 
 * @author cloud wei
 * 
 */
public class GateWayResponser {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	static Logger logger = Logger.getLogger(GateWayResponser.class.getName());

	public static void sentResponseMsg(byte msgType, Document response, Integer channelId) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		XmlMessageUtil.addLocateInfo(response, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		byte[] content = null;
		try {
			content = response.asXML().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Not surport encoding",e);
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		Channel channel = DataBaseMap.allChannelGroup.find(channelId);
		if (channel != null && channel.isConnected()) {
			channel.write(buffer);
		} else {
			logger.error("The channel had been closed when write login response to client. Channel ID is " + channelId);
		}
		logger.info("downStream message is :"+response.asXML());
	}

	public static void sentAllChannelNews(byte msgType, Document response) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		// message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
		XmlMessageUtil.addLocateInfo(response, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		byte[] content = null;
		try {
			content = response.asXML().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Not surport encoding",e);
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		DataBaseMap.allChannelGroup.write(buffer);
		logger.info("downStream message is :"+content);
	}

	public static void sentMrketPriceToSubsribeChannel(byte msgType, Document response, String itemName) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		// message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
		XmlMessageUtil.addLocateInfo(response, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		byte[] content = null;
		try {
			content = response.asXML().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Not surport encoding",e);
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		ChannelGroup channelGroup=DataBaseMap.itemNameChannelMap.get(itemName);
		channelGroup.write(buffer);
		for(Iterator<Channel> channelIterator= channelGroup.iterator();channelIterator.hasNext() ;){
			Channel channel= channelIterator.next();
			logger.info("send follow message to " + channel.getRemoteAddress());
		}
		logger.info("downStream message is :"+response.asXML());
	}

	public static void sentInitialToChannel(byte msgType, Document response, String itemName, int channelId) {
		// LocateMessage message = new LocateMessage(msgType, response, 0);
		// message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
		XmlMessageUtil.addLocateInfo(response, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		byte[] content = null;
		try {
			content = response.asXML().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Not surport encoding",e);
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		DataBaseMap.allChannelGroup.find(channelId).write(buffer);
		logger.info("downStream message is :"+content);
	}

	public static void sentNotiFyResponseMsg(byte msgType, Document response, Integer channelId, int errorCode) {
		// LocateMessage message = new LocateMessage(msgType, response,
		// errorCode);
		// message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
		XmlMessageUtil.addLocateInfo(response, msgType, RFAServerManager.sequenceNo.getAndIncrement(), errorCode);
		byte[] content = null;
		try {
			content = response.asXML().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Not surport encoding",e);
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		Channel channel = DataBaseMap.allChannelGroup.find(channelId);
		if (channel != null && channel.isConnected()) {
			channel.write(buffer);
		} else {
			logger.error("The channel had been closed when write login response to client. Channel ID is " + channelId);
		}
		logger.info("downStream message is :"+content);
	}

	public static void brodcastStateResp(Document responseMsg) {
		if(!DataBaseMap.allChannelGroup.isEmpty()){
			byte[] content = null;
			try {
				content = responseMsg.asXML().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("Not surport encoding",e);
			}
			ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
			buffer.writeBytes(content);
			DataBaseMap.allChannelGroup.write(buffer);
		}else{
			logger.info("None user loginin!");
		}
	}

}
