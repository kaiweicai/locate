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
	
//	private final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer channelBuffer) throws Exception {
		if (channelBuffer.readableBytes() < 4) {
			logger.error("read bytes less than 4");
			return null;
		}
//		if (channelBuffer.readable()) {
//			channelBuffer.readBytes(this.buffer, channelBuffer.readableBytes());
//		}
		
		byte msgType = channelBuffer.readByte();
		int msgLength = channelBuffer.readInt();
		String content = new String( channelBuffer.readBytes(msgLength).array(),"UTF-8");
		Document doc = DocumentHelper.parseText(content);
		LocateMessage message = new LocateMessage(msgType,doc);
//		buffer.clear();
		return message;
	}

}
