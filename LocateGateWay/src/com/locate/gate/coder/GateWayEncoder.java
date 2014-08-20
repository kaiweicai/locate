package com.locate.gate.coder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;

import com.locate.common.model.LocateUnionMessage;

@Deprecated
public class GateWayEncoder extends SimpleChannelHandler {

	Logger logger = Logger.getLogger(GateWayEncoder.class);

	private final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
	int i=0;
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		buffer.clear();
		LocateUnionMessage message = (LocateUnionMessage) e.getMessage();
//		int sequenceNo = message.getSequenceNo();
//		buffer.writeInt(sequenceNo);
//		logger.info("-----------sequenceNo is --------"+sequenceNo);
//		byte msgType = message.getMsgType(); 
//		buffer.writeByte(msgType);
//		logger.info("-----------msgType is --------"+msgType);
//		int errorCode = message.getErrorCode();
//		buffer.writeInt(errorCode);
//		logger.info("-----------errorCode is --------"+errorCode);
//		int msgLength = message.getMsgLength();
//		String xml = message.getDocument().asXML();
//		byte[] content = xml.getBytes("UTF-8");
//		buffer.writeInt(msgLength);
//		logger.info("-----------msgLength is --------"+msgLength);
//		buffer.writeBytes(content);
//		ChannelBuffer buffertest=buffer.duplicate();
//		byte[] con = new byte[buffertest.capacity()];
//		buffertest.readBytes(con,0,msgLength+13);
//		Channels.write(ctx, e.getFuture(), buffer);
//		logger.info("Send message to client:\n" + new String(con,"UTF-8"));
		
	}
	
	@Override
	public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
		super.writeComplete(ctx, e);
		logger.info("write complete message number is -----------------"+i++);
	}
}
