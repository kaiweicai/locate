package com.locate.gate.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.constant.LocateMessageTypes;
import com.locate.common.exception.LocateException;
import com.locate.common.model.ClientRequest;
import com.locate.face.IBussiness;
import com.locate.face.IClientConnector;
import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;

public class ClientConnector implements IClientConnector {
	Logger logger = LoggerFactory.getLogger(ClientConnector.class);
	private Channel clientchannel;
	private Bootstrap bootstrap;
	private boolean conLocate;
	private ClientHandler clientHandler;
	private IBussiness bussinessHandler;

	public ClientConnector(IBussiness bussinessHandler) {
		this.bussinessHandler = bussinessHandler;
		this.clientHandler = new ClientHandler(this.bussinessHandler);
		initNettyClient();
	}
	
	private void initNettyClient() {
		logger.info("gate way Server starting...");
		// 创建客户端channel的辅助类,发起connection请求
		
		bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup())
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<NioSocketChannel>() {
		             @Override
							public void initChannel(NioSocketChannel ch) throws Exception {
								ch.pipeline().addLast("encoder", new LengthFieldPrepender(2))
										.addLast("encrytEncoder", new EncrytEncoder())
										.addLast("fixLengthDecoder", new LengthFieldBasedFrameDecoder(64 * 1024, 0, 2, 0, 2))
										.addLast("encrytDecoder", new EncrytDecoder())
										.addLast("hander", clientHandler);

							}
		         });
		
		
//		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
//			@Override
//			public ChannelPipeline getPipeline() throws Exception {
//				ChannelPipeline pipeline = Channels.pipeline();
//				pipeline.addLast("encoder", new LengthFieldPrepender(2));
//				pipeline.addLast("encrytEncoder", new EncrytEncoder());
//				pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(64*1024,0,2,0,2));
//				pipeline.addLast("encrytDecoder", new EncrytDecoder());
////				pipeline.addLast("encoder", new GateWayEncoder());
////				pipeline.addLast("decoder", new GateWayDecoder());
//				pipeline.addLast("hanlder", clientHandler);
//				// pipeline.addLast("timeout", new IdleStateHandler(new
//				// HashedWheelTimer(), 0, 0, 10));
//				// pipeline.addLast("heartBeat", new ClientIdleHandler());
//				return pipeline;
//			}
//		});

	}
	
	/* (non-Javadoc)
	 * @see com.locate.gate.handler.ClientConnectedInterface#conneteLocateGateWay(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void conneteLocateGateWay(String serverAddress,int port,String userName,String password) {
//		bootstrap.setOption("tcpNodelay", true);
//		bootstrap.setOption("child.keepalive", true);
		logger.info("start to conneted to server");
		try{
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(serverAddress,port));
			clientchannel = future.channel();
//			future.awaitUninterruptibly();
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
	
//	class ClientIdleHandler extends IdleStateAwareChannelHandler implements ChannelHandler {
//		@Override
//		public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
//			if(e.getState()==IdleState.ALL_IDLE){
//				logger.debug("链路空闲,发送心跳 S:{" + e.getChannel().getRemoteAddress() + "} - C:{"
//						+ e.getChannel().getLocalAddress() + "} idleState:{" + e.getState() + "}");
//			}
//			super.channelIdle(ctx, e);
//		}
//	}
	
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
//		try {
//			content = jsonObject.toString().getBytes("UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			logger.error("Not surport encoding",e);
//		}
//		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
//		buffer.writeBytes(jsonObject.toString());
		clientchannel.writeAndFlush(jsonObject.toString());
		logger.info("client downStream message is :"+jsonObject.toString());
	}
	
}