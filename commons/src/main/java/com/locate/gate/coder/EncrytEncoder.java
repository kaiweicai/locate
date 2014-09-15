package com.locate.gate.coder;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.locate.common.utils.CryptSecurity;

public class EncrytEncoder extends OneToOneEncoder {
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		ChannelBuffer buffer = (ChannelBuffer)msg;
		String mes = buffer.toString(Charset.forName("UTF-8"));
		String result = CryptSecurity.encryptToDES(CryptSecurity.key,mes);
		byte[] message = result.getBytes("UTF-8");
		ChannelBuffer channelBuffer = ChannelBuffers.buffer(message.length+8);
		channelBuffer.writeBytes(CryptSecurity.key.getEncoded());
		channelBuffer.writeBytes(message);
		return channelBuffer;
	}
}
