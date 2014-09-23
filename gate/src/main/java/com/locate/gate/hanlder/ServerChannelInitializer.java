package com.locate.gate.hanlder;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;

@ChannelHandler.Sharable
public class ServerChannelInitializer extends ChannelInitializer<NioSocketChannel>{
	private GatewayServerHandler gateWayServerHandler;
	private AdapterHandler adapterHandler;
	public ServerChannelInitializer(GatewayServerHandler gateWayServerHandler,AdapterHandler adapterHandler){
		this.gateWayServerHandler = gateWayServerHandler;
		this.adapterHandler = adapterHandler;
	}
	@Override
	protected void initChannel(NioSocketChannel ch) throws Exception {
		ChannelPipeline pipeLine = ch.pipeline();
		pipeLine.addLast("fixLengthEncoder", new LengthFieldPrepender(2));
		pipeLine.addLast("encrytEncoder", new EncrytEncoder());
		pipeLine.addLast("fixLengthDecoder",new LengthFieldBasedFrameDecoder(64 * 1024, 0, 2, 0, 2));
		pipeLine.addLast("encrytDecoder", new EncrytDecoder());
		pipeLine.addLast("hander", this.gateWayServerHandler);
		pipeLine.addLast("adaptor", this.adapterHandler);
	}
}
