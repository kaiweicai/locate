package com.locate.gate.handler;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

import com.locate.common.LocateMessageTypes;
import com.locate.common.LocateException;
import com.locate.common.model.ClientRequest;
import com.locate.face.IBussiness;
import com.locate.face.IClientConnector;
import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;

public class ClientConnector implements IClientConnector {
	Logger logger = Logger.getLogger(ClientConnector.class);
	private Channel clientchannel;
	private ClientBootstrap bootstrap;
	private boolean conLocate;
	private SimpleChannelHandler clientHandler;
	private IBussiness bussinessHandler;

	public ClientConnector(IBussiness bussinessHandler) {
		this.bussinessHandler = bussinessHandler;
		this.clientHandler = new ClientHandler(this.bussinessHandler);
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
			clientchannel = future.getChannel();
			future.awaitUninterruptibly();
		}catch(Exception e){
			logger.error("NIO error "+e.getCause());
		}
		this.conLocate = true;
		logger.info("conneted to server "+serverAddress+" port:" + port);
		
	    
		ClientRequest clientRequest = createLoginRequest(userName,password);
    	sentMessageToServer(clientRequest);
	}
	
	/* (non-Javadoc)
	 * @see com.locate.gate.handler.ClientConnectedInterface#openRICMarket(java.lang.String)
	 */
	@Override
	public void openRICMarket(String ric){
		if(StringUtils.isBlank(ric)){
			logger.error("The RIC vlaue can not be blank!!");
			throw new LocateException("The RIC vlaue can not be blank!!");
		}
    	ClientRequest request = createFutureRequest(ric);
    	sentMessageToServer(request);
	}
	
	private ClientRequest createLoginRequest(String userName,String password){
		ClientRequest request = new ClientRequest();
		request.setUserName(userName);
		request.setPassword(password);
		request.setMsgType(LocateMessageTypes.LOGIN);
		return request;
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
	
	private ClientRequest createFutureRequest(String ric){
		ClientRequest request = new ClientRequest();
		request.setMsgType(LocateMessageTypes.FUTURE_REQUEST);
		request.setRIC(ric);
		return request;
	}
	
//	private void createEcho(){
//		DocumentFactory documentFactory = DocumentFactory.getInstance();
//	    Document requestDoc =  documentFactory.createDocument();
//	    String ric = ricTextField.getText();
//    	createFutureRequest(requestDoc,ric);
//    	sentMessageToServer(GateWayMessageTypes.REQUEST_EOCH,requestDoc);
//	}
	
	private void sentMessageToServer( ClientRequest request){
		if(clientchannel==null){
			logger.error("The connection of server not establish. Please connect first.");
			throw new LocateException("The connection of server not establish. Please connect first.");
		}
		byte[] content = null;
		JSONObject jsonObject = JSONObject.fromObject(request);
		try {
			content = jsonObject.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Not surport encoding",e);
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		clientchannel.write(buffer);
		logger.info("client downStream message is :"+jsonObject.toString());
	}
	
}