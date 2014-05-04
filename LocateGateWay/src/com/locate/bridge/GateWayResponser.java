package com.locate.bridge;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.channel.Channel;

import com.locate.gate.GateWayServer;
import com.locate.gate.model.LocateMessage;
import com.reuters.rfa.omm.OMMMsg;

/**
 * RFA 通过该程序将消息发送到gateway.
 * @author cloud wei
 *
 */
public class GateWayResponser {
	
	public static final Charset CHARSET = Charset.forName("UTF-8");
	static Logger logger = Logger.getLogger(GateWayResponser.class.getName());
	
	public static void sentResponseMsg(byte msgType,Document response,Integer channelId){
		LocateMessage message = new LocateMessage(msgType, response, 0);
		Channel channel = GateWayServer.allChannelGroup.find(channelId);
		if(channel!=null&&channel.isConnected()){
			channel.write(message);
		}else{
			logger.error("The channel had been closed when write login response to client. Channel ID is "+ channelId);
		}
	}
	
	public static void sentAllChannelNews(byte msgType,Document response){
		LocateMessage message = new LocateMessage(msgType, response, 0);
		GateWayServer.allChannelGroup.write(message);
	}
	
	public static void sentMrketPriceToSubsribeChannel(byte msgType,Document response,String itemName){
		LocateMessage message = new LocateMessage(msgType, response, 0);
		GateWayServer.itemNameChannelMap.get(itemName).write(message);
	}
	
	
	public static void sentNotiFyResponseMsg(byte msgType,Document response,Integer channelId,int errorCode){
		LocateMessage message = new LocateMessage(msgType, response, errorCode);
		Channel channel = GateWayServer.allChannelGroup.find(channelId);
		if(channel!=null&&channel.isConnected()){
			channel.write(message);
		}else{
			logger.error("The channel had been closed when write login response to client. Channel ID is "+ channelId);
		}
	}
}
