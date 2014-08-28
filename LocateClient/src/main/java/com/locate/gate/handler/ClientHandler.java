package com.locate.gate.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;
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
		bussinessHandler.handleException(cause.getCause());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		JSONObject transJsonObject = JSONObject.fromObject(msg);
		LocateUnionMessage myUnionMessage = JsonUtil.translateJsonToUionMessage(transJsonObject);
		bussinessHandler.handleMessage(myUnionMessage);
		t1 = System.currentTimeMillis();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		bussinessHandler.handleDisconnected();
	}
	
}
