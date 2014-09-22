package com.locate.gate.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.locate.common.exception.LocateException;
import com.locate.common.utils.SystemProperties;
import com.locate.gate.coder.EncrytDecoder;
import com.locate.gate.coder.EncrytEncoder;
import com.locate.gate.hanlder.AdapterHandler;
import com.locate.gate.hanlder.GatewayServerHandler;

@Service
public class GateWayServer {
	static Logger logger = LoggerFactory.getLogger(GateWayServer.class.getName());
	
	@Resource
	private GatewayServerHandler gateWayServerHandler;
	
	@Resource
	private AdapterHandler adapterHandler;
	
	private static int serverPort = Integer.parseInt(SystemProperties.getProperties(SystemProperties.SOCKET_PORT));

	/**
	 * Create the TCP server
	 */
	@PostConstruct
	public void init() {
		// _logger.info("Server started...");
		logger.info("gate way Server starting...");
		ServerBootstrap serverBootStrap = new ServerBootstrap();
		try{
			serverBootStrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
	         .channel( NioServerSocketChannel.class)
	         .option(ChannelOption.SO_BACKLOG, 100)
	         .childOption(ChannelOption.SO_KEEPALIVE, true)
	         .childOption(ChannelOption.TCP_NODELAY, true)
	         .localAddress(serverPort)
	         .childHandler(new ChannelInitializer<NioSocketChannel>() {
	             @Override
						public void initChannel(NioSocketChannel ch) throws Exception {
							ch.pipeline().addLast("fixLengthEncoder", new LengthFieldPrepender(2))
									.addLast("encrytEncoder", new EncrytEncoder())
									.addLast("fixLengthDecoder", new LengthFieldBasedFrameDecoder(64 * 1024, 0, 2, 0, 2))
									.addLast("encrytDecoder", new EncrytDecoder())
									.addLast("hander", gateWayServerHandler)
									.addLast("adaptor", adapterHandler);

						}
	         });
	  
	        // Start the server.
	        ChannelFuture f = serverBootStrap.bind().sync();
	        logger.info("gate way Server started success!");
	        // Wait until the server socket is closed.
	        f.channel().closeFuture().sync();
		}catch(Exception e){
			logger.error("Create the Locate netty server error!",e);
			throw new LocateException("Create the Locate netty server error!",e);
		}
	}
		
//		
//		
//		try{
//			ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
//					Executors.newCachedThreadPool());
//			ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
//				@Override
//				public ChannelPipeline getPipeline() throws Exception {
//	//				ChannelPipeline pipeline =Channels.pipeline(new GateWayDecoder(),new GateWayEncoder(),gateWayServerHandler);
//					ChannelPipeline pipeline = Channels.pipeline();
//					pipeline.addLast("fixLengthEncoder", new LengthFieldPrepender(2));
//					pipeline.addLast("encrytEncoder", new EncrytEncoder());
//					pipeline.addLast("fixLengthDecoder", new LengthFieldBasedFrameDecoder(64 * 1024, 0, 2, 0, 2));
//					pipeline.addLast("encrytDecoder", new EncrytDecoder());
//					pipeline.addLast("hander", gateWayServerHandler);
//					pipeline.addLast("adaptor", adapterHandler);
//					//如果服务器端一直都没有向该channel发送信息,需要提醒客户端.
//	//				pipeline.addLast("timeout", new IdleStateHandler(new HashedWheelTimer(), 10, 10, 0));
//	//				pipeline.addLast("hearbeat", new Heartbeat());
//					return pipeline;
//				}
//			};
//			ServerBootstrap bootstrap = new ServerBootstrap(factory);
//			bootstrap.setPipelineFactory(pipelineFactory);
//			bootstrap.setOption("tcpNodelay", true);
//			bootstrap.setOption("child.keepalive", true);
//			bootstrap.setOption("allIdleTime", "5");
//			bootstrap.bind(new InetSocketAddress(serverPort));
//			logger.info("gate way Server started success!");
//		}catch(Exception e){
//			logger.error("Create the Locate netty server error!",e);
//			throw new LocateException("Create the Locate netty server error!",e);
//		}
//	}

//	@Test
//	public void testServer(){
//		GateWayServer server= new GateWayServer();
//		server.init();
//	}
	
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
	
//	class Heartbeat extends IdleStateAwareChannelHandler {
//		int i = 0;
//
//		@Override
//		public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
//			super.channelIdle(ctx, e);
//			if (e.getState() == IdleState.WRITER_IDLE) {
//				i++;
//			}
//			if (i > 3) {
//				logger.warn("channel idle timeout, User remote ip is "+e.getChannel().getRemoteAddress());
//				Document reponseDoc = XmlMessageUtil.createHearBeat();
//				XmlMessageUtil.addLocateInfo(reponseDoc, LocateMessageTypes.REQUEST_EOCH, SystemConstant.sequenceNo.getAndIncrement(), 0);
////			    LocateMessage message = new LocateMessage(GateWayMessageTypes.REQUEST_EOCH, reponseDoc, 0);
////			    message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
//				byte[] content = reponseDoc.asXML().getBytes("UTF-8");
//				ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
//				buffer.writeBytes(content);
//				e.getChannel().write(buffer);
//				i=0;
//			}
////			if(i>60){
////				logger.info("channel closeing channel ID ="+e.getChannel().getId());
////				e.getChannel().close();
////				i=0;
////			}
//		}
//	}
}


