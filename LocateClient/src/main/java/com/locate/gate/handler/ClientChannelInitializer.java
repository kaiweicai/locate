package com.locate.gate.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import com.locate.common.utils.SystemProperties;
import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;
import com.locate.gate.coder.NoEncrytDecoder;
import com.locate.gate.coder.NoEncrytEncoder;
import com.locate.gate.coder.NormalDecoder;
import com.locate.gate.coder.NormalEncoder;

@ChannelHandler.Sharable
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	private static final String USE_ENCRYPT = "useEncrypt";
//	private static final String NORMAL_ENCRYPT = "normalEncrypt";
	private static final String NO_ENCRYPT = "noEncrypt";
	private String encryptUsed = SystemProperties.getProperties(SystemProperties.ENCRYPT_USED);
	private ClientHandler clientHandler;

	public ClientChannelInitializer(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeLine = ch.pipeline();
		pipeLine.addLast("encoder", new LengthFieldPrepender(2));
		if (encryptUsed != null && USE_ENCRYPT.equalsIgnoreCase(encryptUsed))
			pipeLine.addLast("encrytEncoder", new EncrytEncoder());
		else if(encryptUsed != null && NO_ENCRYPT.equalsIgnoreCase(encryptUsed))
			pipeLine.addLast("encrytEncoder", new NoEncrytEncoder());
		else
			pipeLine.addLast("encrytEncoder", new NormalEncoder());
		pipeLine.addLast("fixLengthDecoder", new LengthFieldBasedFrameDecoder(64 * 1024, 0, 2, 0, 2));
		if (encryptUsed != null && USE_ENCRYPT.equalsIgnoreCase(encryptUsed))
			pipeLine.addLast("encrytDecoder", new EncrytDecoder());
		else if(encryptUsed != null && NO_ENCRYPT.equalsIgnoreCase(encryptUsed))
			pipeLine.addLast("encrytDecoder", new NoEncrytDecoder());
		else
			pipeLine.addLast("encrytDecoder", new NormalDecoder());
		pipeLine.addLast("hander", this.clientHandler);
	}

}
