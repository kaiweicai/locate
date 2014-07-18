package com.locate.gate.coder;

import java.nio.charset.Charset;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.locate.common.CryptSecurity;

public class EncrytDecoder extends OneToOneDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		ChannelBuffer buffer = (ChannelBuffer)msg;
		byte[] keyByte = new byte[8];
		buffer.readBytes(keyByte);
		SecretKey desKey = new SecretKeySpec(keyByte, "DES");
		String result = CryptSecurity.decryptByDES(desKey,buffer.toString(Charset.forName("UTF-8")));
		byte[] message = result.getBytes("UTF-8");
		ChannelBuffer channelBuffer = ChannelBuffers.buffer(message.length);
		channelBuffer.writeBytes(message);
		return channelBuffer;
	}
}
