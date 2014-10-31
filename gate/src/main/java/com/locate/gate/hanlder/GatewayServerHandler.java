package com.locate.gate.hanlder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.locate.bridge.GateForwardRFA;
import com.locate.common.constant.LocateMessageTypes;
import com.locate.common.datacache.DataBaseCache;
import com.locate.common.datacache.GateChannelCache;
import com.locate.common.logging.err.ErrorLogHandler;
import com.locate.common.model.ClientRequest;
import com.locate.common.model.InstrumentCodeData;
import com.locate.common.utils.DerivedUtils;

@Service
public class GatewayServerHandler extends StringDecoder {
	private Logger logger = LoggerFactory.getLogger(GatewayServerHandler.class.getName());
	private ErrorLogHandler errorLogHandler = ErrorLogHandler.getLogger(getClass());
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
				logger.info("Register client request item "+itemName);
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
		logger.info("End re-register all of client request ");
	}
	
	@Override
	public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		super.exceptionCaught(ctx, cause);
		if(cause instanceof IOException){
			logger.warn("A client force close a connection!",cause);
			return;
		}else{
			errorLogHandler.error("Netty catch a exception !",cause);
		}
	}
	
//	@Override
//	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
//		if(e instanceof IOException){
//			logger.warn(((IOException) e).getMessage(),e);
//			return;
//		}else{
//			errorLogHandler.error("Unexpect Exception from downstream! please contact the developer!",e);
//		}
//	}

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
//			errorLogHandler.error("Unexpected error ocurres", throwable);
//		}
//	}
	
	public void channelRead(ChannelHandlerContext ctx, Object mesg) throws Exception {
		t0 = System.currentTimeMillis();
		int numberOfMessage = numberOfReceived.incrementAndGet();
		try {
			String message = (String)mesg;
			JSONObject jsonObject = JSONObject.fromObject(message);
			ClientRequest request = (ClientRequest) JSONObject.toBean(jsonObject, ClientRequest.class);
			logger.info("original message -------" + request);
			Channel channel = ctx.channel();
			byte msgType = request.getMsgType();
			// Judge client whether logon
			String userName = request.getUserName();

			String clientIP = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
			logger.info("Server received " + clientIP + " messages, Request message type:"
					+ LocateMessageTypes.toString(msgType));
			
			// 将channelId和对应的channel放到map中,会写客户端的时候可以根据该id找到对应的channel.
			if (!GateChannelCache.allChannelGroup.contains(channel)) {
				GateChannelCache.allChannelGroup.add(channel);
			}
			// store the channel of customer in a map according by the RIC
			if (msgType != LocateMessageTypes.LOGIN) {
				StringBuilder sourceRic = new StringBuilder();
				for (String subcribeItemName : request.getItemCode().split(",")) {
					Map<String, ChannelGroup> subscribeChannelMap = GateChannelCache.itemNameChannelGroupMap;
					//将客户端的instrumentCode转成Locate系统使用的sourceCode.
					subcribeItemName = InstrumentCodeData.exchangeInstrumentCodeToSourceCode(subcribeItemName);
					sourceRic.append(subcribeItemName+",");
					boolean isDerived = DerivedUtils.isDerived(subcribeItemName);
					//订阅了该产品,则将该产品的channel放入到该产品对应的channelGroup中.
					ChannelGroup subChannelGroup = subscribeChannelMap.get(subcribeItemName);
					if (subChannelGroup == null) {
						subChannelGroup = new DefaultChannelGroup(subcribeItemName,GlobalEventExecutor.INSTANCE);
						subscribeChannelMap.put(subcribeItemName, subChannelGroup);
					}
					if (!subChannelGroup.contains(channel)) {
						subChannelGroup.add(channel);
					}
					//如果该产品是一种衍生品的化,将衍生品和它的源单品对应起来.
					if (isDerived) {
						
						String itemName = DerivedUtils.restoreRic(subcribeItemName);
						List<String> derivedChannelList = GateChannelCache.item2derivedMap.get(itemName);
						if (derivedChannelList == null) {
							derivedChannelList = new ArrayList<String>();
							derivedChannelList.add(subcribeItemName);
							GateChannelCache.item2derivedReadWriteLock.writeLock().lock();
							GateChannelCache.item2derivedMap.put(itemName, derivedChannelList);
							GateChannelCache.item2derivedReadWriteLock.writeLock().unlock();
						} else if (!derivedChannelList.contains(subcribeItemName)) {
							derivedChannelList.add(subcribeItemName);
						}
					}
				}
				request.setItemCode(sourceRic.toString());
			}
			if (StringUtils.isBlank(userName)) {
				userName = DataBaseCache._userConnection.get(clientIP);
			}
			
			int channelId = channel.hashCode();
			if (msgType == LocateMessageTypes.LOGIN) {
				if(GateChannelCache.id2ChannelMap.get(channelId)==null){
					GateChannelCache.id2ChannelMap.put(channelId, channel);
				}else{
					Channel channle = GateChannelCache.id2ChannelMap.get(channelId);
					errorLogHandler.error("channel has been used by other user! Used Channel is "+channle);
				}
			}
			request.encapNetInfo(userName, channelId, clientIP);
			// RFAClientHandler process message and send the request to RFA.
			gateForwardRFA.process(request);
		} catch (Throwable throwable) {
			errorLogHandler.error("Unexpected error ocurres", throwable);
		}
	};
	
//	@Override
//	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//		// super.messageReceived(ctx, e);
//		t0 = System.currentTimeMillis();
//		int numberOfMessage = numberOfReceived.incrementAndGet();
//		try {
//			ChannelBuffer channelBuffer = (ChannelBuffer) e.getMessage();
//			String msg = channelBuffer.toString(Charset.forName("UTF-8"));
//			JSONObject jsonObject = JSONObject.fromObject(msg);
//			ClientRequest request = (ClientRequest)JSONObject.toBean(jsonObject,ClientRequest.class);
//			logger.info("original message -------"+request);
//			
//			byte msgType = request.getMsgType();
//			// Judge client whether logon
//			String userName = request.getUserName();
//			
//			String clientIP = ((InetSocketAddress) e.getRemoteAddress()).getAddress().getHostAddress();
//			logger.info("Server received " + clientIP + " messages, Request message type:" + LocateMessageTypes.toString(msgType));
//			
//			Channel channel = e.getChannel();
//			
//			//将channelId和对应的channel放到map中,会写客户端的时候可以根据该id找到对应的channel.
//			if(!GateChannelCache.allChannelGroup.contains(channel)){
//				GateChannelCache.allChannelGroup.add(channel);
//			}
//			//store the channel of customer in a map according by the RIC 
//		    if(msgType != LocateMessageTypes.LOGIN){
//				for (String subcribeItemName : request.getRIC().split(",")) {
//					Map<String, ChannelGroup> subscribeChannelMap = GateChannelCache.itemNameChannelMap;
//					boolean isDerived = DerivedUtils.isDerived(subcribeItemName);
//					String itemName = DerivedUtils.restoreRic(subcribeItemName);
//					ChannelGroup subChannelGroup = subscribeChannelMap.get(subcribeItemName);
//					if (subChannelGroup == null) {
//						subChannelGroup = new DefaultChannelGroup();
//						subscribeChannelMap.put(subcribeItemName,subChannelGroup);
//					}
//					if (!subChannelGroup.contains(channel)) {
//						subChannelGroup.add(channel);
//					}
//					if(isDerived){
//						List<String> derivedChannelList = GateChannelCache.derivedChannelGroupMap.get(itemName);
//						if(derivedChannelList==null){
//							derivedChannelList = new ArrayList<String>();
//							derivedChannelList.add(subcribeItemName);
//							GateChannelCache.derivedChannelGroupMap.put(itemName, derivedChannelList);
//						}else if(!derivedChannelList.contains(subcribeItemName)){
//							derivedChannelList.add(subcribeItemName);
//						}
//					}
//				}
//		    }
//		    if(StringUtils.isBlank(userName)){
//				userName = DataBaseCache._userConnection.get(clientIP);
//			}
//			ClientRequest clientInfo = new ClientRequest(request,userName, channel.getId(), clientIP);
//		    //RFAClientHandler process message and send the request to RFA.
//	    	gateForwardRFA.process(clientInfo);
//		} catch (Throwable throwable) {
//			errorLogHandler.error("Unexpected error ocurres", throwable);
//		}
//	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		Channel channel = ctx.channel();
		logger.info("channel has been closed. channel is "+channel);
		GateChannelCache.allChannelGroup.remove(channel);
		Set<String> unregisterSet = new HashSet<String>();
		List<String> passedDevriedList = new ArrayList<String>();
		//加入读取锁
		GateChannelCache.item2ChannelGroupReadWriteLock.readLock().lock();
		//遍历所有的channelgoup,发现有该channel的就remove掉.
		for(Entry<String,ChannelGroup> entry:GateChannelCache.itemNameChannelGroupMap.entrySet()){
			String itemName = entry.getKey();
			String derivedName = "";
			boolean derived = DerivedUtils.isDerived(itemName);
			ChannelGroup channelGroup = entry.getValue();
			if(channelGroup.contains(channel)){
				channelGroup.remove(channel);
			}
			if(derived){
				derivedName = itemName;
				itemName = DerivedUtils.restoreRic(itemName);
			}
			//没有用户对该产品以及衍生品感兴趣.清除所有该项目的缓存.
			if (GateChannelCache.isEnmptyAll(itemName)) {
				unregisterSet.add(itemName);
				GateChannelCache.item2derivedReadWriteLock.readLock().lock();
				List<String> derivedList = GateChannelCache.item2derivedMap.get(itemName);
				GateChannelCache.item2derivedReadWriteLock.readLock().unlock();
				if (derivedList != null) {
					unregisterSet.addAll(derivedList);
				}
				gateForwardRFA.closeHandler(itemName);
			}
			if(StringUtils.isNotBlank(derivedName))
				if(GateChannelCache.isEnmptyDerived(derivedName)){
					passedDevriedList.add(derivedName);
				}
		}
		GateChannelCache.item2ChannelGroupReadWriteLock.readLock().unlock();
		//清空掉该itemname和ChannelGroup的对应关系.注意ITEMName和devriedPiepline的对应关系可以不用清除.
		//保存在内存中.
		for (String itemName : unregisterSet) {
			ChannelGroup itemChannelGroup = GateChannelCache.itemNameChannelGroupMap.get(itemName);
			if (itemChannelGroup==null || itemChannelGroup.isEmpty()) {
				//写锁锁住
				GateChannelCache.item2ChannelGroupReadWriteLock.writeLock().lock();
				GateChannelCache.itemNameChannelGroupMap.remove(itemName);
				//写锁解开
				GateChannelCache.item2ChannelGroupReadWriteLock.writeLock().unlock();
				logger.debug("debug infomation: removed "+itemName+" from itemNameChannelGroupMap!");
				itemChannelGroup = null;
			}else{
				errorLogHandler.error("The channelGroup should be null or empty, but the it is not. The itemName is "
						+ itemName);
			}
		}
		//衍生品没有用户订阅.删除该衍生品的对应关系.但是原产品不变.
		for(String derived:passedDevriedList){
			ChannelGroup itemChannelGroup = GateChannelCache.itemNameChannelGroupMap.get(derived);
			if (itemChannelGroup==null || itemChannelGroup.isEmpty()) {
				GateChannelCache.item2ChannelGroupReadWriteLock.writeLock().lock();
				GateChannelCache.itemNameChannelGroupMap.remove(derived);
				GateChannelCache.item2ChannelGroupReadWriteLock.writeLock().unlock();
				itemChannelGroup = null;
			}
			GateChannelCache.item2derivedReadWriteLock.writeLock().lock();
			for(List<String> devridedList:GateChannelCache.item2derivedMap.values()){
				if(devridedList!=null&&devridedList.contains(derived)){
					devridedList.remove(derived);
				}
			}
			GateChannelCache.item2derivedReadWriteLock.writeLock().unlock();
			gateForwardRFA.closeDerivedRequest(DerivedUtils.restoreRic(derived),derived);
		}
	}
	
//	@Override
//	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//		logger.info("channel has been closed.");
//		
//		Channel channel = ctx.getChannel();
//		GateChannelCache.allChannelGroup.remove(channel);
//		List<String> unregisterList = new ArrayList<String>();
//		//遍历所有的channelgoup,发现有该channel的就remove掉.如果该channelGroup为空,
//		for(Entry<String,ChannelGroup> entry:GateChannelCache.itemNameChannelMap.entrySet()){
//			String itemName = entry.getKey();
//			boolean derived = DerivedUtils.isDerived(itemName);
//			if(derived){
//				itemName = DerivedUtils.restoreRic(itemName);
//			}
//			ChannelGroup channelGroup = entry.getValue();
//			if(channelGroup.contains(channel)){
//				channelGroup.remove(channel);
//			}
//			if(GateChannelCache.isEmnpty(itemName)){//没有用户订阅了,退订该item
//				unregisterList.add(itemName);
//				gateForwardRFA.closeHandler(itemName);
//			}
//		}
//		//清空掉该itemname和ChannelGroup的对应关系.
//		for (String itemName : unregisterList) {
//			ChannelGroup itemChannelGroup = GateChannelCache.itemNameChannelMap.get(itemName);
//			if (itemChannelGroup.isEmpty()) {
//				GateChannelCache.itemNameChannelMap.remove(itemName);
//			}
//		}
//	}
	
	
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
//			errorLogHandler.error(e.getMessage(), e);
//			System.exit(0);
//		}
//
//		_logger.info("Server started...");
//	}

}
