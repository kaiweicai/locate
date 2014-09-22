package com.locate.gate.coder;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.locate.common.utils.CryptSecurity;

public class EncrytDecoder extends MessageToMessageDecoder<ByteBuf> {

//	@Override
//	protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
//		ChannelBuffer buffer = (ChannelBuffer)msg;
//		byte[] keyByte = new byte[8];
//		buffer.readBytes(keyByte);
//		SecretKey desKey = new SecretKeySpec(keyByte, "DES");
//		String result = CryptSecurity.decryptByDES(desKey,buffer.toString(Charset.forName("UTF-8")));
//		byte[] message = result.getBytes("UTF-8");
//		ChannelBuffer channelBuffer = ChannelBuffers.buffer(message.length);
//		channelBuffer.writeBytes(message);
//		return channelBuffer;
//	}
	
	protected void decode(io.netty.channel.ChannelHandlerContext ctx, ByteBuf msg, java.util.List<Object> out) throws Exception {
		byte[] keyByte = new byte[8];
		msg.readBytes(keyByte);
		SecretKey desKey = new SecretKeySpec(keyByte, "DES");
		String result = CryptSecurity.decryptByDES(desKey,msg.toString(Charset.forName("UTF-8")));
		byte[] message = result.getBytes("UTF-8");
		String locateMessage = new String(message);
		out.add(locateMessage);
	};
}
