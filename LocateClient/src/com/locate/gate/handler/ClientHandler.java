package com.locate.gate.handler;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.locate.face.BussinessInterface;

public class ClientHandler extends SimpleChannelHandler {
	private BussinessInterface bussinessHandler;
	private long t0, t1;
	public ClientHandler(BussinessInterface bussinessHandler) {
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
		bussinessHandler.handleMessage(msg);
		t1 = System.currentTimeMillis();
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		bussinessHandler.handleDisconnected();
	}
}
