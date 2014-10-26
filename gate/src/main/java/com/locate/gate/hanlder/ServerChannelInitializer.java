package com.locate.gate.hanlder;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import com.locate.common.utils.SystemProperties;
import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;
import com.locate.gate.coder.NormalDecoder;
import com.locate.gate.coder.NormalEncoder;

@ChannelHandler.Sharable
public class ServerChannelInitializer extends ChannelInitializer<NioSocketChannel>{
	private String encryptUsed = SystemProperties.getProperties(SystemProperties.ENCRYPT_USED);
	private GatewayServerHandler gateWayServerHandler;
	private AdapterHandlerEncoder adapterHandler;
	public ServerChannelInitializer(GatewayServerHandler gateWayServerHandler,AdapterHandlerEncoder adapterHandler){
		this.gateWayServerHandler = gateWayServerHandler;
		this.adapterHandler = adapterHandler;
	}
	@Override
	protected void initChannel(NioSocketChannel ch) throws Exception {
		ChannelPipeline pipeLine = ch.pipeline();
		pipeLine.addLast("fixLengthEncoder", new LengthFieldPrepender(2));
		if(encryptUsed!=null&&"true".equalsIgnoreCase(encryptUsed)){
			pipeLine.addLast("encrytEncoder", new EncrytEncoder());
		}else{
			pipeLine.addLast("encrytEncoder", new NormalEncoder());
		}
		pipeLine.addLast("fixLengthDecoder",new LengthFieldBasedFrameDecoder(64 * 1024, 0, 2, 0, 2));
		if(encryptUsed!=null&&"true".equalsIgnoreCase(encryptUsed)){
			pipeLine.addLast("encrytDecoder", new EncrytDecoder());
		}else
			pipeLine.addLast("encrytDecoder", new NormalDecoder());
		pipeLine.addLast("hander", this.gateWayServerHandler);
		pipeLine.addLast("adaptor", this.adapterHandler);
	}
}
