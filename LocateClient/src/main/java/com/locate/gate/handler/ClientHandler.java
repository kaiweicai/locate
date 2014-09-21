package com.locate.gate.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

import net.sf.json.JSONObject;

import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.JsonUtil;
import com.locate.face.IBussiness;

public class ClientHandler extends StringDecoder {
	private IBussiness bussinessHandler;
	private long t0, t1;
	public ClientHandler(IBussiness bussinessHandler) {
		this.bussinessHandler = bussinessHandler;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		bussinessHandler.handleException(cause.getCause());
	}
	
//	@Override
//	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
//		bussinessHandler.handleException(e.getCause());
//	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		JSONObject transJsonObject = JSONObject.fromObject(msg);
		LocateUnionMessage myUnionMessage = JsonUtil.translateJsonToUionMessage(transJsonObject);
//		LocateUnionMessage myMessage = (LocateUnionMessage)JSONObject.toBean( transJsonObject, LocateUnionMessage.class);
		bussinessHandler.handleMessage(myUnionMessage);
		t1 = System.currentTimeMillis();
	};

//	@Override
//	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//		super.messageReceived(ctx, e);
//		ChannelBuffer channelBuffer = (ChannelBuffer) e.getMessage();
//		String msg = channelBuffer.toString(Charset.forName("UTF-8"));
//		JSONObject transJsonObject = JSONObject.fromObject(msg);
//		LocateUnionMessage myUnionMessage = JsonUtil.translateJsonToUionMessage(transJsonObject);
////		LocateUnionMessage myMessage = (LocateUnionMessage)JSONObject.toBean( transJsonObject, LocateUnionMessage.class);
//		bussinessHandler.handleMessage(myUnionMessage);
//		t1 = System.currentTimeMillis();
//	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		bussinessHandler.handleDisconnected();
	}
	
//	@Override
//	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//		bussinessHandler.handleDisconnected();
//	}
}
