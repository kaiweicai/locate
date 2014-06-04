package com.locate.gate;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.locate.LocateGateWayMain;
import com.locate.common.RFANodeconstant;
import com.locate.common.XmlMessageUtil;
import com.locate.common.GateWayExceptionTypes;
import com.locate.common.GateWayExceptionTypes.RFAExceptionEnum;
import com.locate.common.GateWayMessageTypes;
import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;
import com.locate.gate.coder.GateWayDecoder;
import com.locate.gate.coder.GateWayEncoder;
import com.locate.gate.hanlder.GatewayServerHandler;
import com.locate.gate.model.LocateMessage;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.RFAServerManager;
import com.locate.rmds.processer.ItemManager;

public class GateWayServer {
	static Logger logger = Logger.getLogger(GateWayServer.class.getName());
	
	private GatewayServerHandler gateWayServerHandler;

	/**
	 * Create the TCP server
	 */
	public void init() {
		// _logger.info("Server started...");
		logger.info("gate way Server starting...");
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
//				ChannelPipeline pipeline =Channels.pipeline(new GateWayDecoder(),new GateWayEncoder(),gateWayServerHandler);
				ChannelPipeline pipeline = Channels.pipeline();
				//如果服务器端一直都没有向该channel发送信息,需要提醒客户端.
				pipeline.addLast("fixLengthEncoder", new LengthFieldPrepender(2));
				pipeline.addLast("encrytEncoder", new EncrytEncoder());
				pipeline.addLast("fixLengthDecoder", new LengthFieldBasedFrameDecoder(64 * 1024, 0, 2, 0, 2));
				pipeline.addLast("encrytDecoder", new EncrytDecoder());
				pipeline.addLast("hander", gateWayServerHandler);
//				pipeline.addLast("timeout", new IdleStateHandler(new HashedWheelTimer(), 10, 10, 0));
//				pipeline.addLast("hearbeat", new Heartbeat());
				return pipeline;
			}
		};
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(pipelineFactory);
		bootstrap.setOption("tcpNodelay", true);
		bootstrap.setOption("child.keepalive", true);
		bootstrap.setOption("allIdleTime", "5");
		bootstrap.bind(new InetSocketAddress(8888));
		logger.info("gate way Server started success!");
	}

	@Test
	public void testServer(){
		GateWayServer server= new GateWayServer();
		server.init();
	}
	
	public static void main(String[] args){
		GateWayServer server= new GateWayServer();
		server.init();
	}
	
	public GatewayServerHandler getGateWayServerHandler() {
		return gateWayServerHandler;
	}

	public void setGateWayServerHandler(GatewayServerHandler gateWayServerHandler) {
		this.gateWayServerHandler = gateWayServerHandler;
	}
	
	class Heartbeat extends IdleStateAwareChannelHandler {
		int i = 0;

		@Override
		public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
			super.channelIdle(ctx, e);
			if (e.getState() == IdleState.WRITER_IDLE) {
				i++;
			}
			if (i > 3) {
				logger.warn("channel idle timeout, User remote ip is "+e.getChannel().getRemoteAddress());
				Document reponseDoc = XmlMessageUtil.createHearBeat();
				XmlMessageUtil.addLocateInfo(reponseDoc, GateWayMessageTypes.REQUEST_EOCH, RFAServerManager.sequenceNo.getAndIncrement(), 0);
//			    LocateMessage message = new LocateMessage(GateWayMessageTypes.REQUEST_EOCH, reponseDoc, 0);
//			    message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
				byte[] content = reponseDoc.asXML().getBytes("UTF-8");
				ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
				buffer.writeBytes(content);
				e.getChannel().write(buffer);
				i=0;
			}
//			if(i>60){
//				logger.info("channel closeing channel ID ="+e.getChannel().getId());
//				e.getChannel().close();
//				i=0;
//			}
		}
	}
}


