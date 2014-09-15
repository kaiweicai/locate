package com.locate.gate.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.dom4j.Document;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.locate.common.constant.LocateMessageTypes;
import com.locate.common.constant.SystemConstant;
import com.locate.common.exception.LocateException;
import com.locate.common.utils.SystemProperties;
import com.locate.common.utils.XmlMessageUtil;
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

	/**
	 * Create the TCP server
	 */
	@PostConstruct
	public void init() {
		// _logger.info("Server started...");
		logger.info("gate way Server starting...");
		try{
			ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool());
			ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
				@Override
				public ChannelPipeline getPipeline() throws Exception {
	//				ChannelPipeline pipeline =Channels.pipeline(new GateWayDecoder(),new GateWayEncoder(),gateWayServerHandler);
					ChannelPipeline pipeline = Channels.pipeline();
					pipeline.addLast("fixLengthEncoder", new LengthFieldPrepender(2));
					pipeline.addLast("encrytEncoder", new EncrytEncoder());
					pipeline.addLast("fixLengthDecoder", new LengthFieldBasedFrameDecoder(64 * 1024, 0, 2, 0, 2));
					pipeline.addLast("encrytDecoder", new EncrytDecoder());
					pipeline.addLast("hander", gateWayServerHandler);
					pipeline.addLast("adaptor", adapterHandler);
					//如果服务器端一直都没有向该channel发送信息,需要提醒客户端.
	//				pipeline.addLast("timeout", new IdleStateHandler(new HashedWheelTimer(), 10, 10, 0));
	//				pipeline.addLast("hearbeat", new Heartbeat());
					return pipeline;
				}
			};
			ServerBootstrap bootstrap = new ServerBootstrap(factory);
			bootstrap.setPipelineFactory(pipelineFactory);
			bootstrap.setOption("tcpNodelay", true);
			bootstrap.setOption("child.keepalive", true);
			bootstrap.setOption("allIdleTime", "5");
			int serverPort = Integer.parseInt(SystemProperties.getProperties(SystemProperties.SOCKET_PORT));
			bootstrap.bind(new InetSocketAddress(serverPort));
			logger.info("gate way Server started success!");
		}catch(Exception e){
			logger.error("Create the Locate netty server error!",e);
			throw new LocateException("Create the Locate netty server error!",e);
		}
	}

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
				Document reponseDoc = XmlMessageUtil.createHearBeat();
				XmlMessageUtil.addLocateInfo(reponseDoc, LocateMessageTypes.REQUEST_EOCH, SystemConstant.sequenceNo.getAndIncrement(), 0);
//			    LocateMessage message = new LocateMessage(GateWayMessageTypes.REQUEST_EOCH, reponseDoc, 0);
//			    message.setSequenceNo(RFAServerManager.sequenceNo.getAndIncrement());
				byte[] content = reponseDoc.asXML().getBytes("UTF-8");
				ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
				buffer.writeBytes(content);
				e.getChannel().write(buffer);
				i=0;
			}
//			if(i>60){
//				logger.info("channel closeing channel ID ="+e.getChannel().getId());
//				e.getChannel().close();
//				i=0;
//			}
		}
	}
}


