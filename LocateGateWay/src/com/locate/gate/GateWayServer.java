package com.locate.gate;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.locate.rmds.QSConsumerProxy;

public class GateWayServer {
	static Logger logger = Logger.getLogger(GateWayServer.class.getName());
	@Autowired
	private QSConsumerProxy app;
	public GateWayServer() {
		init();
	}

	/**
	 * Create the TCP server
	 */
	public void init() {
		// _mainApp = app;
		// _logger.info("Initial RMDS socket server");
		// NioSocketAcceptor acceptor = new NioSocketAcceptor();
		// acceptor.getFilterChain().addLast("protocal", new
		// ProtocolCodecFilter(new RFACodecFactory()));
		// acceptor.getSessionConfig().setTcpNoDelay(true);
		// acceptor.setReuseAddress(true);
		//
		// acceptor.getSessionConfig().setReuseAddress(true);
		// acceptor.setHandler(this);
		//
		// // The logger, if needed. Commented atm
		// // DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		// // chain.addLast("logger", new LoggingFilter());
		//
		// SocketSessionConfig scfg = acceptor.getSessionConfig();
		// int port =
		// NumberUtils.stringToInt(SystemProperties.getProperties(SystemProperties.SOCKET_PORT));
		// try {
		// acceptor.bind(new InetSocketAddress(port));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// _logger.error(e.getMessage(), e);
		// System.exit(0);
		// }
		//
		// _logger.info("Server started...");
		logger.info("gate way Server started...");
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		final GatewayServerHandler gateWayServerHandler = new GatewayServerHandler();
		gateWayServerHandler.set_mainApp(app);
		ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				
				return Channels.pipeline(gateWayServerHandler,new LocateEncoder());
			}
		};
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(pipelineFactory);
		bootstrap.setOption("child.tcpNodelay", true);
		bootstrap.setOption("child.keepalive", true);
		bootstrap.bind(new InetSocketAddress(8888));
		logger.info("gate way Server ended");
	}
}
