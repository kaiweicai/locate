package com.locate.gate.coder;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;

public class NormalDecoder extends MessageToMessageDecoder<ByteBuf> {

	protected void decode(io.netty.channel.ChannelHandlerContext ctx, ByteBuf msg, java.util.List<Object> out) throws Exception {
		String result = msg.toString(Charset.forName("UTF-8"));
		out.add(result);
	};
}
