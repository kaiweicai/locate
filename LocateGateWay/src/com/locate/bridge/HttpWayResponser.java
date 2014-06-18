package com.locate.bridge;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.springframework.stereotype.Service;

import com.locate.common.DataBaseCache;
import com.locate.common.JsonUtil;
import com.locate.common.XmlMessageUtil;
import com.locate.gate.model.LocateMessage;
import com.locate.gate.server.GateWayServer;
import com.locate.rmds.RFAServerManager;
import com.reuters.rfa.omm.OMMMsg;

/**
 * RFA 通过该程序将消息发送到gateway.
 * 
 * @author cloud wei
 * 
 */
public class HttpWayResponser {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	static Logger logger = Logger.getLogger(HttpWayResponser.class.getName());

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
		Channel channel = DataBaseCache.allChannelGroup.find(channelId);
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
		DataBaseCache.allChannelGroup.write(buffer);
		logger.info("downStream message is :"+content);
	}

	public static void sentMrketPriceToSubsribeChannel(Document response, String itemName) {
		byte[] content = null;
		try {
			content = response.asXML().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Not surport encoding",e);
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		ChannelGroup channelGroup=DataBaseCache.itemNameChannelMap.get(itemName);
		channelGroup.write(buffer);
		for(Iterator<Channel> channelIterator= channelGroup.iterator();channelIterator.hasNext() ;){
			Channel channel= channelIterator.next();
			logger.info("send follow message to " + channel.getRemoteAddress());
		}
		logger.info("downStream message is :"+response.asXML());
	}

	public static void sentwebResponse2Channel(byte msgType, Document doc, String itemName,HttpRequest request) {
		// 解析Connection首部，判断是否为持久连接
		boolean keepAlive = isKeepAlive(request);
		
		// Build the response object.
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setStatus(HttpResponseStatus.OK);
		// 服务端可以通过location首部将客户端导向某个资源的地址。
		// response.addHeader("Location", uri);
		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
		}
		// 得到客户端的cookie信息，并再次写到客户端
		String cookieString = request.getHeader(COOKIE);
		if (cookieString != null) {
			CookieDecoder cookieDecoder = new CookieDecoder();
			Set<Cookie> cookies = cookieDecoder.decode(cookieString);
			if (!cookies.isEmpty()) {
				CookieEncoder cookieEncoder = new CookieEncoder(true);
				for (Cookie cookie : cookies) {
					cookieEncoder.addCookie(cookie);
				}
				response.addHeader(SET_COOKIE, cookieEncoder.encode());
			}
		}
		
		
		ChannelGroup channelGroup = DataBaseCache.recipientsMap.get(itemName);
		XmlMessageUtil.addLocateInfo(doc, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		String content = null;
		byte[] result = null;
		try {
			content = doc.asXML();
			result = JsonUtil.getJSONFromXml(content).toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Not surport encoding",e);
		}
		ChannelGroupFuture futhure=null;
		try {
			long fileLength = result.length;
			response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(fileLength));
			ChannelBuffer buffer = ChannelBuffers.buffer(result.length);
			buffer.writeBytes(result);
			channelGroup.write(buffer);
			logger.info("downStream message is :"+content);
			// 这里又要重新温习下http的方法，head方法与get方法类似，但是服务器在响应中只返回首部，不会返回实体的主体部分
//			if (!request.getMethod().equals(HttpMethod.HEAD)) {
//				channelGroup.write(new ChunkedInput(result, 0, fileLength, 8192));// 8kb
//			}
		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			if (keepAlive) {
				response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
			}
			if (!keepAlive) {
				futhure.addListener(new ChannelGroupFutureListener() {
					@Override
					public void operationComplete(ChannelGroupFuture future) throws Exception {
						future.getGroup().close();
					}
				});
			}
		}
	}
	
	public static void writeWebSocket(byte msgType, Document doc, String itemName, HttpRequest request) {

		ChannelGroup channelGroup = DataBaseCache.recipientsMap.get(itemName);
		XmlMessageUtil.addLocateInfo(doc, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		String content = null;
		String result = null;
		content = doc.asXML();
		result = JsonUtil.getJSONFromXml(content).toString();
		ChannelGroupFuture futhure = null;
		try {
			channelGroup.write(new TextWebSocketFrame(result));
			logger.info("downStream message is :" + content);
			// 这里又要重新温习下http的方法，head方法与get方法类似，但是服务器在响应中只返回首部，不会返回实体的主体部分
			// if (!request.getMethod().equals(HttpMethod.HEAD)) {
			// channelGroup.write(new ChunkedInput(result, 0, fileLength,
			// 8192));// 8kb
			// }
		} catch (Exception e2) {
			e2.printStackTrace();
		}
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
		Channel channel = DataBaseCache.allChannelGroup.find(channelId);
		if (channel != null && channel.isConnected()) {
			channel.write(buffer);
		} else {
			logger.error("The channel had been closed when write login response to client. Channel ID is " + channelId);
		}
		logger.info("downStream message is :"+content);
	}

	public static void brodcastStateResp(Document responseMsg) {
		if(!DataBaseCache.allChannelGroup.isEmpty()){
			byte[] content = null;
			try {
				content = responseMsg.asXML().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("Not surport encoding",e);
			}
			ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
			buffer.writeBytes(content);
			DataBaseCache.allChannelGroup.write(buffer);
		}else{
			logger.info("None user loginin!");
		}
	}

}
