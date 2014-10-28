package com.locate.gate.coder;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;

public class NormalDecoder extends MessageToMessageDecoder<ByteBuf> {
	private static final String key0 = "FECOI()*&<MNCXZPKL";
	private static final Charset charset = Charset.forName("UTF-8");
	private static byte[] keyBytes = key0.getBytes(charset);

	protected void decode(io.netty.channel.ChannelHandlerContext ctx, ByteBuf msg, java.util.List<Object> out)
			throws Exception {

		int length = msg.readableBytes();
		byte[] message = new byte[length];
		msg.getBytes(0, message);
		String result = encryptDecode(message);
		out.add(result);
	};

	public static String encryptDecode(byte[] dec) {
		for (int i = 0, size = dec.length; i < size; i++) {
			for (byte keyBytes0 : keyBytes) {
				dec[i] = (byte) (dec[i] ^ keyBytes0);
			}
		}
		return new String(dec, charset);
	}
}
