package com.locate.gate.hanlder;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdapterHandler extends OneToOneEncoder {
	Logger logger = Logger.getLogger(getClass());

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		Document doc = (Document) msg;
		byte[] content = null;
		try {
			content = doc.asXML().getBytes("UTF-8");
		} catch (UnsupportedEncodingException upsupportedEncodeException) {
			logger.error("Not surport encoding", upsupportedEncodeException);
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		return buffer;
	}
}
