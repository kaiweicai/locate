package com.locate.gate.coder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.locate.gate.model.LocateMessage;

public class GateWayEncoder extends SimpleChannelHandler {

	Logger logger = Logger.getLogger(GateWayEncoder.class);

	private final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		buffer.clear();
		LocateMessage message = (LocateMessage) e.getMessage();
		buffer.writeInt(message.getSequenceNo());
		buffer.writeByte(message.getMsgType());
		buffer.writeInt(message.getErrorCode());
		String xml = message.getDocument().asXML();
		byte[] content = xml.getBytes("UTF-8");
		int dataLength = content.length;
		buffer.writeInt(dataLength);
		buffer.writeBytes(content);
		Channels.write(ctx, e.getFuture(), buffer);
		logger.info("Send message to client:\n" + message);
	}
}
