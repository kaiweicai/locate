package com.locate.gate.handler;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

import com.locate.common.GateWayMessageTypes;
import com.locate.common.RFANodeconstant;
import com.locate.common.XmlMessageUtil;
import com.locate.face.IBussiness;
import com.locate.face.IClientConnected;
import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;
import com.locate.rmds.RFAServerManager;

public class ClientConnector implements IClientConnected {
	Logger logger = Logger.getLogger(ClientConnector.class);
	private Channel Clientchannel;
	private ClientBootstrap bootstrap;
	private boolean conLocate;
	private SimpleChannelHandler clientHandler;
	private IBussiness bussinessHandler;

	public ClientConnector(IBussiness bussinessHandler) {
		this.bussinessHandler = bussinessHandler;
		this.clientHandler = new ClientHandler(bussinessHandler);
		initNettyClient();
	}
	
	private void initNettyClient() {
		// 创建客户端channel的辅助类,发起connection请求
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("encoder", new LengthFieldPrepender(2));
				pipeline.addLast("encrytEncoder", new EncrytEncoder());
				pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(64*1024,0,2,0,2));
				pipeline.addLast("encrytDecoder", new EncrytDecoder());
//				pipeline.addLast("encoder", new GateWayEncoder());
//				pipeline.addLast("decoder", new GateWayDecoder());
				pipeline.addLast("hanlder", clientHandler);
				// pipeline.addLast("timeout", new IdleStateHandler(new
				// HashedWheelTimer(), 0, 0, 10));
				// pipeline.addLast("heartBeat", new ClientIdleHandler());
				return pipeline;
			}
		});

	}
	
	/* (non-Javadoc)
	 * @see com.locate.gate.handler.ClientConnectedInterface#conneteLocateGateWay(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void conneteLocateGateWay(String serverAddress,int port,String userName,String password) {
		bootstrap.setOption("tcpNodelay", true);
		bootstrap.setOption("child.keepalive", true);
		logger.info("start to conneted to server");
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(serverAddress,port));
		try{
			Clientchannel = future.getChannel();
			future.awaitUninterruptibly();
		}catch(Exception e){
			logger.error("NIO error "+e.getCause());
		}
		this.conLocate = true;
		logger.info("conneted to server "+serverAddress+" port:" + port);
		
		DocumentFactory documentFactory = DocumentFactory.getInstance();
	    Document requestDoc =  documentFactory.createDocument();
	    
	    createLoginRequest(requestDoc,userName,password);
    	sentMessageToServer(GateWayMessageTypes.LOGIN, requestDoc);
	}
	
	/* (non-Javadoc)
	 * @see com.locate.gate.handler.ClientConnectedInterface#openRICMarket(java.lang.String)
	 */
	@Override
	public void openRICMarket(String ric){
		DocumentFactory documentFactory = DocumentFactory.getInstance();
	    Document requestDoc =  documentFactory.createDocument();
    	createFutureRequest(requestDoc,ric);
    	sentMessageToServer(GateWayMessageTypes.FUTURE_REQUEST,requestDoc);
	}
	
	private void createLoginRequest(Document doc,String userName,String password){
		Element rmds = doc.addElement("rmds");
		rmds.addElement(RFANodeconstant.LOCATE_NODE);
		Element login = rmds.addElement("login");
		login.addElement("userName").addText(userName);
		login.addElement("password").addText(password);
	}
	
	class ClientIdleHandler extends IdleStateAwareChannelHandler implements ChannelHandler {
		@Override
		public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
			if(e.getState()==IdleState.ALL_IDLE){
				logger.debug("链路空闲,发送心跳 S:{" + e.getChannel().getRemoteAddress() + "} - C:{"
						+ e.getChannel().getLocalAddress() + "} idleState:{" + e.getState() + "}");
			}
			super.channelIdle(ctx, e);
		}
	}
	
	private void createFutureRequest(Document doc,String ric){
		Element rmds = doc.addElement("rmds");
		rmds.addElement(RFANodeconstant.LOCATE_NODE);
		Element request = rmds.addElement("request");
		Element item = request.addElement("item");
		item.addElement("name").addText(ric);
	}
	
//	private void createEcho(){
//		DocumentFactory documentFactory = DocumentFactory.getInstance();
//	    Document requestDoc =  documentFactory.createDocument();
//	    String ric = ricTextField.getText();
//    	createFutureRequest(requestDoc,ric);
//    	sentMessageToServer(GateWayMessageTypes.REQUEST_EOCH,requestDoc);
//	}
	
	private void sentMessageToServer(byte msgType,Document doc){
		XmlMessageUtil.addLocateInfo(doc, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
		byte[] content = null;
		try {
			content = doc.asXML().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Not surport encoding",e);
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		ChannelFuture future = Clientchannel.write(buffer);
		logger.info("client downStream message is :"+doc.asXML());
	}
	
}
