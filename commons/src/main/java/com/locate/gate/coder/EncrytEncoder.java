package com.locate.gate.coder;

import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.locate.common.utils.CryptSecurity;

public class EncrytEncoder extends MessageToMessageEncoder<String> {
//	@Override
//	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
//		ChannelBuffer buffer = (ChannelBuffer)msg;
//		String mes = buffer.toString(Charset.forName("UTF-8"));
//		String result = CryptSecurity.encryptToDES(CryptSecurity.key,mes);
//		byte[] message = result.getBytes("UTF-8");
//		ChannelBuffer channelBuffer = ChannelBuffers.buffer(message.length+8);
//		channelBuffer.writeBytes(CryptSecurity.key.getEncoded());
//		channelBuffer.writeBytes(message);
//		return channelBuffer;
//	}

	@Override
	protected void encode(io.netty.channel.ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		String result = CryptSecurity.encryptToDES(CryptSecurity.key,msg);
		String key = new String(CryptSecurity.key.getEncoded(),"UTF-8");
		result = key+","+result;
		out.add(result);
	}
}
