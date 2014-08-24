package com.locate.gate.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.junit.Test;
import org.springframework.stereotype.Service;

import com.locate.common.LocateMessageTypes;
import com.locate.common.utils.XmlMessageUtil;
import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;
import com.locate.gate.hanlder.GatewayServerHandler;
import com.locate.rmds.RFAServerManager;
import com.locate.rmds.util.SystemProperties;

@Service
public class GateWayWebServer {
	private Logger logger = Logger.getLogger(GateWayWebServer.class.getName());
	@Resource
	private HttpServerPipelineFactory httpServerPipelineFactory;
	/**
	 * Create the gate way web server
	 */
	@PostConstruct
	public void init() {
		logger.info("gate way web Server starting...");
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(httpServerPipelineFactory);
		int serverPort = Integer.parseInt(SystemProperties.getProperties(SystemProperties.WEB_PORT));
		bootstrap.bind(new InetSocketAddress(serverPort));
		logger.info("gate way web Server started success!");
	}

	@Test
	public void testServer(){
		GateWayWebServer server= new GateWayWebServer();
		server.init();
	}
	
	public static void main(String[] args){
		GateWayWebServer server= new GateWayWebServer();
		server.init();
	}
	
}


