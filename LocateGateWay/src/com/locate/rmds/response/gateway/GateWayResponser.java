package com.locate.rmds.response.gateway;

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
		LocateMessage message = new LocateMessage(msgType, response);
		Channel channel = GateWayServer.channelMap.get(channelId);
		if(channel!=null&&channel.isConnected()){
			channel.write(message);
		}else{
			logger.error("The channel had been closed when write login response to client. Channel ID is "+ channelId);
		}
//		if(channelId != null && channelId.isConnected()){
//	        IoBuffer buffer = IoBuffer.allocate(2048);
//	        buffer.setAutoShrink(true);
//	        buffer.setAutoExpand(true);
//	        buffer.put(msgType);
//			String xml = response.asXML();
//	        int dataLength = 0;
//	        byte[] len ;
//	        if("true".equalsIgnoreCase(SystemProperties.getProperties(SystemProperties.USE_SOCKET_MINA))){
//	        	 dataLength = SizeCalculator.calcSize(response)+5;
//	             len = RFATypeConvert.intToByteArray1(dataLength);
//	             buffer.put(len);
//		        buffer.putObject(response);
//		      
//			    _logger.info("Send message to client:\n"+xml);
//	        }else{
//	    		byte[] xmlbyte = xml.getBytes(CHARSET);
//	    		dataLength = xmlbyte.length+5;
//		        len = RFATypeConvert.intToByteArray1(dataLength);
//		        buffer.put(len);
//				buffer.put(xmlbyte);
//	        }
//	        
//		    buffer.flip();
//		    _logger.info("Send message to client:\n"+buffer);
//		    try{
//		    	channelId.write(buffer);
//		    }catch(Exception e){
//		    	 _logger.error("Message sent failed.");
//		    	 _logger.error(e.getMessage(),e);
//		    }
//		}else{
//			String xml = response.asXML();
//		    _logger.info("Send message to client:\n"+xml);
//		    _logger.error("Client connection is closed, Can't send out message to client.");
//		}
	}
	
	
}
