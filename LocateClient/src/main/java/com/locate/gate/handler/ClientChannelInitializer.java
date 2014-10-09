package com.locate.gate.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{
	private ClientHandler clientHandler;
	public ClientChannelInitializer(ClientHandler clientHandler){
		this.clientHandler = clientHandler;
	}
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeLine = ch.pipeline();
		pipeLine.addLast("encoder", new LengthFieldPrepender(2));
		pipeLine.addLast("encrytEncoder", new EncrytEncoder());
		pipeLine.addLast("fixLengthDecoder",new LengthFieldBasedFrameDecoder(64 * 1024, 0, 2, 0, 2));
		pipeLine.addLast("encrytDecoder", new EncrytDecoder());
		pipeLine.addLast("hander", this.clientHandler);
	}

}
