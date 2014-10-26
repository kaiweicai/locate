package com.locate.gate.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.locate.common.utils.CryptSecurity;

public class NormalEncoder extends MessageToMessageEncoder<String> {
	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		byte[] message = msg.getBytes("UTF-8");
		ByteBuf buffer= PooledByteBufAllocator.DEFAULT.buffer(message.length);
		buffer.writeBytes(message);
		out.add(buffer);
	}
}
