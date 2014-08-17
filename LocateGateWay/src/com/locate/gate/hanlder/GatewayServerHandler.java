package com.locate.gate.hanlder;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.springframework.stereotype.Service;

import com.locate.bridge.GateForwardRFA;
import com.locate.common.DataBaseCache;
import com.locate.common.GateWayMessageTypes;
import com.locate.common.XmlMessageUtil;
import com.locate.gate.model.ClientInfo;
import com.locate.rmds.handler.inter.IRequestHandler;

@Service
public class GatewayServerHandler extends SimpleChannelHandler {
	static Logger _logger = Logger.getLogger(GatewayServerHandler.class.getName());
	
	/** The number of message to receive */
	public static final int MAX_RECEIVED = 100000;

	/** The starting point, set when we receive the first message */
	private static long t0;

	/** A counter incremented for every recieved message */
	private AtomicInteger numberOfReceived = new AtomicInteger(0);
	// Client IP -- Client User Name
	
//	private ClientHandle clientHandle = (ClientHandle)LocateGateWayMain.springContext.getBean("clientHandler"); 
	@Resource
	private GateForwardRFA gateForwardRFA;
	
	private  void requestAgain(){
		//Re-request item
		for(String clientName : DataBaseCache._clientRequestItemName.keySet()){
			List<String> itemNames = DataBaseCache._clientRequestItemName.get(clientName);
			for(String itemName : itemNames){
				System.out.println("clientName "+clientName);
				itemName = itemName.replaceAll(clientName, "");
				_logger.info("Register client request item "+itemName);
				System.out.println("Register client request item "+itemName);
				System.out.println("_clientResponseType size "+DataBaseCache._clientResponseType.size());
				byte responseMsgType  =  DataBaseCache._clientResponseType.get(itemName);
//				if( GateWayServer._requestItemNameList.get(itemName) != null){
//					ClientHandle clientHandle = (ClientHandle)LocateGateWayMain.springContext.getBean("clientHandler"); 
//					ItemManager clientInstance = mainAppProxy.itemRequests(itemName,responseMsgType, 0);
//					GateWayServer._clientRequestItemManager.put(itemName,clientInstance);
//				}
			}
		}
		_logger.info("End re-register all of client request ");
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		_logger.error("Unexpect Exception from downstream! please contact the developer!",e.getCause());
//		e.getChannel().close();
	}

//	@Deprecated
//	private void updateServerStatInfo() {
//		// need to modify the monitor socke connected.
//		RFApplication.currentUserNumber.setText(String.valueOf(DataBaseMap._userConnection.size()));
//		int currentRequestItemNum = 0;
//		for (List<String> list : DataBaseMap._clientRequestItemName.values()) {
//			currentRequestItemNum += list.size();
//		}
//		RFApplication.currentRequestNumber.setText(String.valueOf(currentRequestItemNum));
//	}

//	@Override
//	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//		// super.messageReceived(ctx, e);
//		t0 = System.currentTimeMillis();
//		int numberOfMessage = numberOfReceived.incrementAndGet();
//		try {
//			LocateMessage data = (LocateMessage) e.getMessage();
//			byte msgType = data.getMsgType();
//			// Pick up message type from client's data
//			Document userRequest = data.getDocument();
//			// Judge client whether logon
//			String userName = null;
//			
//			String clientIP = ((InetSocketAddress) e.getRemoteAddress()).getAddress().getHostAddress();
//			_logger.info("Server received " + clientIP + " messages, Request message type:" + msgType);
//			_logger.info("Client request :" + userRequest.asXML());
//			
//			Channel channel = e.getChannel();
//			
//			//将channelId和对应的channel放到map中,会写客户端的时候可以根据该id找到对应的channel.
//			if(!GateWayServer.allChannelGroup.contains(channel)){
//				GateWayServer.allChannelGroup.add(channel);
//			}
//			userName = GateWayServer._userConnection.get(clientIP);
//			ClientInfo clientInfo = new ClientInfo(userRequest, userName, channel.getId(), msgType, clientIP);
////		    ClientHandle clientHandle = (ClientHandle)LocateGateWayMain.springContext.getBean("clientHandler"); 
//			if(msgType != GateWayMessageTypes.LOGIN){
//				for (String subcribeItemName : clientHandle.pickupClientReqItem(userRequest)) {
//					Map<String, ChannelGroup> subscribeChannelMap = GateWayServer.itemNameChannelMap;
//					ChannelGroup subChannelGroup = subscribeChannelMap.get(subcribeItemName);
//					if (subChannelGroup == null) {
//						subChannelGroup = new DefaultChannelGroup();
//						subscribeChannelMap.put(subcribeItemName,subChannelGroup);
//					}
//					if (!subChannelGroup.contains(channel)) {
//						subChannelGroup.add(channel);
//					}
//				}
//			}
//			//RFAClientHandler process message and send the request to RFA.
//			clientHandle.process(clientInfo);
//		} catch (Throwable throwable) {
//			_logger.error("Unexpected error ocurres", throwable);
//		}
//	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		// super.messageReceived(ctx, e);
		t0 = System.currentTimeMillis();
		int numberOfMessage = numberOfReceived.incrementAndGet();
		try {
			ChannelBuffer channelBuffer = (ChannelBuffer) e.getMessage();
			String msg = channelBuffer.toString(Charset.forName("UTF-8"));
			Document userRequest = XmlMessageUtil.convertDocument(msg);
			_logger.info("original message -------"+msg);
			
			byte msgType = XmlMessageUtil.getMsgType(userRequest);
			// Judge client whether logon
			String userName = null;

			String clientIP = ((InetSocketAddress) e.getRemoteAddress()).getAddress().getHostAddress();
			_logger.info("Server received " + clientIP + " messages, Request message type:" + msgType);
			_logger.info("Client request :" + userRequest.asXML());
			
			Channel channel = e.getChannel();
			
			//将channelId和对应的channel放到map中,会写客户端的时候可以根据该id找到对应的channel.
			if(!DataBaseCache.allChannelGroup.contains(channel)){
				DataBaseCache.allChannelGroup.add(channel);
			}
			userName = DataBaseCache._userConnection.get(clientIP);
			ClientInfo clientInfo = new ClientInfo(userRequest, userName, channel.getId(), msgType, clientIP);
//		    ClientHandle clientHandle = (ClientHandle)LocateGateWayMain.springContext.getBean("clientHandler"); 
		    if(msgType != GateWayMessageTypes.LOGIN){
				for (String subcribeItemName : XmlMessageUtil.pickupClientReqItem(userRequest)) {
					Map<String, ChannelGroup> subscribeChannelMap = DataBaseCache.itemNameChannelMap;
					ChannelGroup subChannelGroup = subscribeChannelMap.get(subcribeItemName);
					if (subChannelGroup == null) {
						subChannelGroup = new DefaultChannelGroup();
						subscribeChannelMap.put(subcribeItemName,subChannelGroup);
					}
					if (!subChannelGroup.contains(channel)) {
						subChannelGroup.add(channel);
					}
				}
		    }
		    //RFAClientHandler process message and send the request to RFA.
	    	gateForwardRFA.process(clientInfo);
		} catch (Throwable throwable) {
			_logger.error("Unexpected error ocurres", throwable);
		}
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		_logger.info("channel has been closed.");
		
		Channel channel = ctx.getChannel();
		DataBaseCache.allChannelGroup.remove(channel);
		List<String> unregisterList = new ArrayList<String>();
		//遍历所有的channelgoup,发现有该channel的就remove掉.如果该channelGroup为空,
		for(Entry<String,ChannelGroup> entry:DataBaseCache.itemNameChannelMap.entrySet()){
			String itemName = entry.getKey();
			ChannelGroup channelGroup = entry.getValue();
			if(channelGroup.contains(channel)){
				channelGroup.remove(channel);
			}
			if(channelGroup.isEmpty()){//没有用户订阅了,退订该item
				unregisterList.add(itemName);
				gateForwardRFA.closeHandler(itemName);
			}
		}
		//清空掉该itemname和ChannelGroup的对应关系.
		for (String itemName : unregisterList) {
			ChannelGroup itemChannelGroup = DataBaseCache.itemNameChannelMap.get(itemName);
			if (itemChannelGroup.isEmpty()) {
				DataBaseCache.itemNameChannelMap.remove(itemName);
			}
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
//	@Override
//	public void sessionClosed(IoSession session) throws Exception {
//		String clientIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
//		_logger.info("Begin to sign off client " + clientIP);
//		String userName = _userConnection.get(clientIP);
//		List<String> clientRequestItem = _clientRequestItemName.get(userName);
//		Map<String, IoSession> clientSessionMap;
//		// Sign off client request item
//		for (String itemName : clientRequestItem) {
//			_logger.info("Removing request item " + itemName);
//			if (_clientRequestSession.get(itemName) != null) {
//				_clientRequestSession.remove(itemName);
//			}
//			itemName = itemName.replaceAll(userName, "");
//			clientSessionMap = _clientNewsRequest.get(itemName);
//			if (clientSessionMap != null) {
//				if (clientSessionMap.get(clientIP) != null) {
//					clientSessionMap.remove(clientIP);
//				}
//				if (clientSessionMap.size() < 1) {
//					_clientNewsRequest.remove(itemName);
//				}
//			}
//			_clientNewsRequest.put(itemName, clientSessionMap);
//			List<String> itemForClientName = _requestItemNameList.get(itemName);
//			if (itemForClientName != null) {
//				if (itemForClientName.size() == 1) {
//					ItemManager instance = _clientRequestItemManager.get(itemName);
//					instance.closeRequest();
//					_clientRequestItemManager.remove(itemName);
//					_requestItemNameList.remove(itemName);
//				}
//			}
//		}
//		_clientRequestItemName.remove(userName);
//		_userConnection.remove(clientIP);
//		RFAUserManagement.RFAUserBusiness.remove(userName);
//		updateServerStatInfo();
//		_logger.info("Client " + clientIP + " session closed...");
//	}

//	/**
//	 * Create the TCP server
//	 */
//	public GatewayServerHandler(QSConsumerProxy app) {
//		_mainApp = app;
//		_logger.info("Initial RMDS socket server");
//		NioSocketAcceptor acceptor = new NioSocketAcceptor();
//		acceptor.getFilterChain().addLast("protocal", new ProtocolCodecFilter(new RFACodecFactory()));
//		acceptor.getSessionConfig().setTcpNoDelay(true);
//		acceptor.setReuseAddress(true);
//
//		acceptor.getSessionConfig().setReuseAddress(true);
//		acceptor.setHandler(this);
//
//		// The logger, if needed. Commented atm
//		// DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
//		// chain.addLast("logger", new LoggingFilter());
//
//		SocketSessionConfig scfg = acceptor.getSessionConfig();
//		int port = NumberUtils.stringToInt(SystemProperties.getProperties(SystemProperties.SOCKET_PORT));
//		try {
//			acceptor.bind(new InetSocketAddress(port));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			_logger.error(e.getMessage(), e);
//			System.exit(0);
//		}
//
//		_logger.info("Server started...");
//	}

}
