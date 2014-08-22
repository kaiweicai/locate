package com.locate.gate.handler;

import java.nio.charset.Charset;

import net.sf.json.JSONObject;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.JsonUtil;
import com.locate.face.IBussiness;

public class ClientHandler extends SimpleChannelHandler {
	private IBussiness bussinessHandler;
	private long t0, t1;
	public ClientHandler(IBussiness bussinessHandler) {
		this.bussinessHandler = bussinessHandler;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		bussinessHandler.handleException(e.getCause());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		super.messageReceived(ctx, e);
		ChannelBuffer channelBuffer = (ChannelBuffer) e.getMessage();
		String msg = channelBuffer.toString(Charset.forName("UTF-8"));
		JSONObject transJsonObject = JSONObject.fromObject(msg);
		LocateUnionMessage myUnionMessage = JsonUtil.translateJsonToUionMessage(transJsonObject);
//		LocateUnionMessage myMessage = (LocateUnionMessage)JSONObject.toBean( transJsonObject, LocateUnionMessage.class);
		bussinessHandler.handleMessage(myUnionMessage);
		t1 = System.currentTimeMillis();
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		bussinessHandler.handleDisconnected();
	}
}
