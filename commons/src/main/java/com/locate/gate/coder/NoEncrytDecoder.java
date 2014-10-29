package com.locate.gate.coder;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;

public class NoEncrytDecoder extends MessageToMessageDecoder<ByteBuf> {
	private static final String key0 = "FECOI()*&<MNCXZPKL";
	private static final Charset charset = Charset.forName("UTF-8");
	private static byte[] keyBytes = key0.getBytes(charset);

	protected void decode(io.netty.channel.ChannelHandlerContext ctx, ByteBuf msg, java.util.List<Object> out)
			throws Exception {
		String result = msg.toString(Charset.forName("UTF-8"));
		out.add(result);
	};
}
