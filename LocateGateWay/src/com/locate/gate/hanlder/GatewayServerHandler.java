package com.locate.gate.hanlder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.locate.common.RFAExceptionTypes;
import com.locate.common.RFAMessageTypes;
import com.locate.gate.GateWayServer;
import com.locate.gate.hanlder.GatewayServerHandler;
import com.locate.gate.model.LocateMessage;
import com.locate.gate.model.RFAUserResponse;
import com.locate.rmds.ItemManager;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.RFApplication;
import com.locate.rmds.client.ClientHandle;
import com.locate.rmds.util.RFACommon;
import com.locate.rmds.util.SystemProperties;

public class GatewayServerHandler extends SimpleChannelHandler {
	static Logger _logger = Logger.getLogger(GatewayServerHandler.class.getName());

	/** The number of message to receive */
	public static final int MAX_RECEIVED = 100000;

	/** The starting point, set when we receive the first message */
	private static long t0;

	/** A counter incremented for every recieved message */
	private AtomicInteger numberOfReceived = new AtomicInteger(0);
	// Client IP -- Client User Name


	QSConsumerProxy _mainApp;

	public QSConsumerProxy get_mainApp() {
		return _mainApp;
	}


	public void set_mainApp(QSConsumerProxy _mainApp) {
		this._mainApp = _mainApp;
	}


	public  void requestAgain(){
		//Re-request item
		for(String clientName : GateWayServer._clientRequestItemName.keySet()){
			List<String> itemNames = GateWayServer._clientRequestItemName.get(clientName);
			for(String itemName : itemNames){
				System.out.println("clientName "+clientName);
				itemName = itemName.replaceAll(clientName, "");
				_logger.info("Register client request item "+itemName);
				System.out.println("Register client request item "+itemName);
				System.out.println("_clientResponseType size "+GateWayServer._clientResponseType.size());
				byte responseMsgType  =  GateWayServer._clientResponseType.get(itemName);
				if( GateWayServer._requestItemNameList.get(itemName) != null){
					ItemManager clientInstance = _mainApp.itemRequests(itemName,responseMsgType, 0);
					GateWayServer._clientRequestItemManager.put(itemName,clientInstance);
				}
			}
		}
		_logger.info("End re-register all of client request ");
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		_logger.error("Unexpect Exception from downstream! please contact the developer!"+e.getCause());
//		e.getChannel().close();
	}

	@Deprecated
	private void updateServerStatInfo() {
		// need to modify the monitor socke connected.
		RFApplication.currentUserNumber.setText(String.valueOf(GateWayServer._userConnection.size()));
		int currentRequestItemNum = 0;
		for (List<String> list : GateWayServer._clientRequestItemName.values()) {
			currentRequestItemNum += list.size();
		}
		RFApplication.currentRequestNumber.setText(String.valueOf(currentRequestItemNum));
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		// super.messageReceived(ctx, e);
		t0 = System.currentTimeMillis();
		int numberOfMessage = numberOfReceived.incrementAndGet();
		try {
			LocateMessage data = (LocateMessage) e.getMessage();
			byte msgType = data.getMsgType();
			// Pick up message type from client's data
			Document userRequest = data.getDocument();
			// Judge client whether logon
			String userName = null;

			String clientIP = ((InetSocketAddress) e.getRemoteAddress()).getAddress().getHostAddress();
			_logger.info("Server received " + clientIP + " messages, Request message type:" + msgType);
			_logger.info("Client request :" + userRequest.asXML());
			
			Channel channel = e.getChannel();
			//这段逻辑不应该在这里判断,用户是否登录系统应该放到clientHandle里面去判断.
			if(msgType != RFAMessageTypes.LOGIN){
		    	userName = GateWayServer._userConnection.get(clientIP);
		    	if(userName == null){
		    		int errorCode = RFAExceptionTypes.USER_NOT_LOGIN;
					Document wrongMsg = RFAUserResponse.createErrorDocument(errorCode,
							RFAExceptionTypes.RFAExceptionEnum.getExceptionDescription(errorCode));
					LocateMessage message = new LocateMessage(RFAMessageTypes.ERROR,wrongMsg);
					e.getChannel().write(message);
					_logger.error("Client didn't login system. sent error message to client");
					return;
		    	}
		    }
			//将channelId和对应的channel放到map中,会写客户端的时候可以根据该id找到对应的channel.
			GateWayServer.channelMap.put(channel.getId(), channel);
		    ClientHandle clientHandle = new ClientHandle(_mainApp,clientIP,msgType);
		    //RFAClientHandler process message and send the request to RFA.
	    	clientHandle.process(userRequest,userName,channel.getId());
		} catch (Throwable throwable) {
			_logger.error("Unexpected error ocurres", throwable);
		}
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		GateWayServer.channelMap.remove(e.getChannel().getId());
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
