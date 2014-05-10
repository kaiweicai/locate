package com.locate.gate.coder;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.locate.gate.model.LocateMessage;

public class GateWayDecoder extends FrameDecoder {
	
	Logger logger = Logger.getLogger(GateWayDecoder.class);
	
	private final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
	static int MSG_HEADER_LEN = 9;// length of meessage type and data's length
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer channelBuffer) throws Exception {
//		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		if (channelBuffer.readableBytes() < 4) {
	        // The length field was not received yet - return null.
	        // This method will be invoked again when more packets are
	        // received and appended to the buffer.
			logger.error("read bytes less than 4");
			return null;
		}
		
		channelBuffer.markReaderIndex();
		
		byte msgType = channelBuffer.readByte();
		int errorCode = channelBuffer.readInt();
		int msgLength = channelBuffer.readInt();
		
		if (channelBuffer.readableBytes() < msgLength) {
			channelBuffer.resetReaderIndex();
			return null;
		}
		
		if (channelBuffer.readable()) {
			buffer.writeBytes(channelBuffer);
//			channelBuffer.readBytes(buffer, channelBuffer.readableBytes());
		}
		
		String content="";
		try{
			content = new String( buffer.readBytes(msgLength).array(),"UTF-8");
		}catch(Exception e){
			logger.error("",e.getCause());
		}
		Document doc = DocumentHelper.parseText(content);
		LocateMessage message = new LocateMessage(msgType,doc, errorCode);
		buffer.clear();
		return message;
	}

}
