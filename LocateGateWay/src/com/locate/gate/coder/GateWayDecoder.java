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
import com.locate.rmds.RFAServerManager;

public class GateWayDecoder extends FrameDecoder {
	
	Logger logger = Logger.getLogger(GateWayDecoder.class);
	
	private final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
	static int MSG_HEADER_LEN = 13;// length of meessage type and data's length
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer channelBuffer) throws Exception {
//		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		if (channelBuffer.readableBytes() < 13) {
	        // The length field was not received yet - return null.
	        // This method will be invoked again when more packets are
	        // received and appended to the buffer.
			logger.error("read bytes less than 13");
			return null;
		}
		
		
		channelBuffer.markReaderIndex();
		int sequenceNo = channelBuffer.readInt();
		logger.info("-----------readerIndex is --------"+channelBuffer.readerIndex());
		byte msgType = channelBuffer.readByte();
		logger.info("-----------readerIndex is --------"+channelBuffer.readerIndex());
		int errorCode = channelBuffer.readInt();
		logger.info("-----------readerIndex is --------"+channelBuffer.readerIndex());
		int msgLength = channelBuffer.readInt();
		logger.info("-----------readerIndex is --------"+channelBuffer.readerIndex());
		logger.info("-----------sequenceNo is --------"+sequenceNo);
		logger.info("-----------msgType is --------"+msgType);
		logger.info("-----------errorCode is --------"+errorCode);
		logger.info("-----------msgLength is --------"+msgLength);
		if(msgLength>20000){
			logger.error("mesge lenght too big, mesg length is "+msgLength);
			logger.debug(msgLength);
		}
		if (channelBuffer.readableBytes() < msgLength) {
			channelBuffer.resetReaderIndex();
			return null;
		}
		
		if (channelBuffer.readable()) {
			buffer.writeBytes(channelBuffer,msgLength);
//			channelBuffer.readBytes(buffer, channelBuffer.readableBytes());
		}
		
		String content="";
		try{
			content = new String( buffer.readBytes(msgLength).array(),"UTF-8");
		}catch(Exception e){
			logger.error("",e.getCause());
		}
		Document doc=null;
		try{
			doc = DocumentHelper.parseText(content);
		}catch(Exception e){
			e.printStackTrace();
		}
		LocateMessage message = new LocateMessage(msgType,doc, errorCode);
		message.setSequenceNo(sequenceNo);
		buffer.clear();
		return message;
	}

}
