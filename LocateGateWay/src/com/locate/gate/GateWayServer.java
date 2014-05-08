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
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.locate.common.GateWayExceptionTypes;
import com.locate.common.GateWayExceptionTypes.RFAExceptionEnum;
import com.locate.common.GateWayMessageTypes;
import com.locate.gate.coder.GateWayDecoder;
import com.locate.gate.coder.GateWayEncoder;
import com.locate.gate.hanlder.GatewayServerHandler;
import com.locate.gate.model.LocateMessage;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.processer.ItemManager;
import com.locate.rmds.util.RFANodeconstant;

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
				ChannelPipeline pipeline =Channels.pipeline(new GateWayDecoder(),new GateWayEncoder(),gateWayServerHandler);
				//如果服务器端一直都没有向该channel发送信息,需要提醒客户端.
				pipeline.addLast("timeout", new IdleStateHandler(new HashedWheelTimer(), 10, 10, 0));
				pipeline.addLast("hearbeat", new Heartbeat());
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
				DocumentFactory documentFactory = DocumentFactory.getInstance();
			    Document reponseDoc =  documentFactory.createDocument();
			    
			    Element rmds = reponseDoc.addElement(RFANodeconstant.RESPONSE_ROOT_NODE);
				Element response = rmds.addElement(RFANodeconstant.RESPONSE_RESPONSE_NODE);
				Element error = response.addElement(RFANodeconstant.RESPONSE_ERROR_NODE);
				int errorCode =GateWayExceptionTypes.CHANNEL_IDLE_TIMEOUT;
				String descriptioin = RFAExceptionEnum.getExceptionDescription(errorCode);
				error.addElement(RFANodeconstant.RESPONSE_ERROR_CODE_NODE).addText(String.valueOf(errorCode));
				error.addElement(RFANodeconstant.RESPONSE_ERROR_DESC_NODE).addText(String.valueOf(descriptioin));
			    
			    LocateMessage message = new LocateMessage(GateWayMessageTypes.REQUEST_EOCH, reponseDoc, 0);
				e.getChannel().write(message);
				i=0;
			}
		}
	}
}


