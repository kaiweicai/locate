package com.locate.gate.server;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GateWayWebServer {
	private Logger logger = LoggerFactory.getLogger(GateWayWebServer.class.getName());
	@Resource
	private HttpServerPipelineFactory httpServerPipelineFactory;
	/**
	 * Create the gate way web server
	 */
//	@PostConstruct
//	public void init() {
//		logger.info("gate way web Server starting...");
//		try {
//			ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
//					Executors.newCachedThreadPool());
//			ServerBootstrap bootstrap = new ServerBootstrap(factory);
//			bootstrap.setPipelineFactory(httpServerPipelineFactory);
//			int serverPort = Integer.parseInt(SystemProperties.getProperties(SystemProperties.WEB_PORT));
//			bootstrap.bind(new InetSocketAddress(serverPort));
//			logger.info("gate way web Server started success!");
//		} catch (Exception e) {
//			errorLogHandler.error("Create the Locate netty web server error!", e);
//			throw new LocateException("Create the Locate netty server error!", e);
//		}
//	}

//	@Test
//	public void testServer(){
//		GateWayWebServer server= new GateWayWebServer();
//		server.init();
//	}
	
//	public static void main(String[] args){
//		GateWayWebServer server= new GateWayWebServer();
//		server.init();
//	}
	
}


