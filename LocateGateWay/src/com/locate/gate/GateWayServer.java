package com.locate.gate;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.locate.gate.coder.GateWayDecoder;
import com.locate.gate.coder.GateWayEncoder;
import com.locate.gate.hanlder.GatewayServerHandler;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.processer.ItemManager;

public class GateWayServer {
	static Logger logger = Logger.getLogger(GateWayServer.class.getName());

	public static Map<String, String> _userConnection = new HashMap();
	// Client User Name+itemName -- IoSession
	public static Map<String, Integer> _clientRequestSession = new HashMap();
	// News's itemName -- ( client IP -- IoSession)
	public static Map<String, Map<String, Integer>> _clientNewsRequest = new HashMap();
	// Client User Name -- Client User Name + ItemName
	public static Map<String, List<String>> _clientRequestItemName = new HashMap();
	// Item name-- Client User Name
	public static Map<String, List<String>> _requestItemNameList = new HashMap();
	// public static Map<String,List<ItemManager>> _clientRequestItemManager =
	// new HashMap();//
	public static Map<String, ItemManager> _clientRequestItemManager = new HashMap();//

	public static Map<String,Byte> _clientResponseType = new HashMap();
	
	//add by Cloud Wei
	//管理channelId和channel的映射关系.
	public static ChannelGroup allChannelGroup = new DefaultChannelGroup("allChannels"); 
	//订阅的itemName与订阅该itemName的所有客户的对应关系.
	public static Map<String ,ChannelGroup> itemNameChannelMap = new HashMap<String,ChannelGroup>();
	//订阅的产品itemName与订阅该产品的消息处理器的映射关系.
	public static Map<String,ItemManager> subscribeItemManagerMap = new HashMap<String,ItemManager>();
	
	
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
				
				return Channels.pipeline(new GateWayDecoder(),new GateWayEncoder(),gateWayServerHandler);
			}
		};
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(pipelineFactory);
		bootstrap.setOption("tcpNodelay", true);
		bootstrap.setOption("child.keepalive", true);
		bootstrap.bind(new InetSocketAddress(8888));
		logger.info("gate way Server started success!");
	}

	@Test
	public void testServer(){
		GateWayServer server= new GateWayServer();
		server.init();
	}
	
	public static void main(String[] args){
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		final GatewayServerHandler gateWayServerHandler = new GatewayServerHandler();
//		gateWayServerHandler.set_mainApp(app);
		ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				
				return Channels.pipeline(gateWayServerHandler,new GateWayEncoder());
			}
		};
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(pipelineFactory);
		bootstrap.setOption("child.tcpNodelay", true);
		bootstrap.setOption("child.keepalive", true);
		bootstrap.bind(new InetSocketAddress(8888));
	}
	
	public GatewayServerHandler getGateWayServerHandler() {
		return gateWayServerHandler;
	}

	public void setGateWayServerHandler(GatewayServerHandler gateWayServerHandler) {
		this.gateWayServerHandler = gateWayServerHandler;
	}
}
