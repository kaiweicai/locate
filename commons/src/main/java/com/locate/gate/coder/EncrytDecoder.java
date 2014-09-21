package com.locate.gate.coder;

import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.locate.common.utils.CryptSecurity;

public class EncrytDecoder extends MessageToMessageDecoder<String> {

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
	@Override
	protected void decode(io.netty.channel.ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		String[] message = msg.split(",");
		byte[] keyByte = message[0].getBytes("UTF-8");
		SecretKey desKey = new SecretKeySpec(keyByte, "DES");
		String result = CryptSecurity.decryptByDES(desKey,message[1]);
		out.add(result);
	}
}
