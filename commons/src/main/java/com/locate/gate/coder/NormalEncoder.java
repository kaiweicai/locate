package com.locate.gate.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.Charset;
import java.util.List;

import com.locate.common.utils.CryptSecurity;

public class NormalEncoder extends MessageToMessageEncoder<String> {
	private static final String key0 = "FECOI()*&<MNCXZPKL";
	private static final Charset charset = Charset.forName("UTF-8");
	private static byte[] keyBytes = key0.getBytes(charset);
	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		byte[] message = msg.getBytes("UTF-8");
		byte[] encrytMessage = encryptEncode(message);
		ByteBuf buffer= PooledByteBufAllocator.DEFAULT.buffer(encrytMessage.length);
		buffer.writeBytes(message);
		out.add(buffer);
	}
	
	public static byte[] encryptEncode(byte[] enc) {
		for (int i = 0, size = enc.length; i < size; i++) {
			for (byte keyBytes0 : keyBytes) {
				enc[i] = (byte) (enc[i] ^ keyBytes0);
			}
		}
		return enc;
	}
}
