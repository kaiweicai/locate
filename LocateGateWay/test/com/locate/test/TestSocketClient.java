package com.locate.test;


import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.sql.Time;
import java.util.concurrent.Executors;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.junit.Test;
import org.springframework.web.util.HtmlUtils;


import com.locate.gate.coder.GateWayDecoder;
import com.locate.gate.coder.GateWayEncoder;
import com.locate.gate.model.LocateMessage;
import com.locate.test.common.RFAMessageTypes;
import com.locate.test.common.RFAMessageTypes.RFAMessageName;


public class TestSocketClient{
	private Channel channel;
	class clientHandler extends SimpleChannelHandler{
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			System.out.println(e.getCause());
		}
		
		public Document parseFile(){
			SAXReader reader=new SAXReader();
			String userFile = "D:/rfa/testData.xml";
			Document userData = null;
			try {
				userData = reader.read(userFile);
			} catch (DocumentException e) {
				System.out.println("Inital RFA user data error.");
			}
			return userData;
		}
		
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			super.messageReceived(ctx, e);
			LocateMessage message =  (LocateMessage) e.getMessage();
			byte msgType = message.getMsgType();
		    int length = message.getMsgLength(); 
		    System.out.println("receive messages length:"+length);
			System.out.println("Received message type:"+RFAMessageName.getRFAMessageName(msgType));
		    
		    Document document = message.getDocument();
		    if(document == null){
		    	System.out.println("Received server's  message is null ");
		    	return ;
		    }
		    String content = HtmlUtils.htmlUnescape(document.asXML());
//		    String content = response.asXML();
			System.out.println("Received server's  message : " + content);
			
			t1 = System.currentTimeMillis();
		    
		    System.out.println("Sent messages delay : " + (t1 - t0));
		    System.out.println();
		    
		}
		
	}
	
	private static int PORT = 9999;
	
	long t0,t1;

	private void createOneTimesRequest(Document doc){
		Element rmds = doc.addElement("rmds");
		Element request = rmds.addElement("request");
		Element item = request.addElement("item");
		item.addElement("name").addText("JPSTKO=ECI");//LIBOR;ECONCN
//		Element item = request.addElement("item");
//		item.addElement("name").addText("XAG=");
	}
	
	
	private void createLoginRequest(Document doc){
		Element rmds = doc.addElement("rmds");
		Element login = rmds.addElement("login");
		login.addElement("userName").addText("ztcj");
		login.addElement("password").addText("ztcj2013");
	}
	
	private void createWrongLoginRequest(Document doc){
		Element rmds = doc.addElement("rmds");
		Element login = rmds.addElement("login");
		login.addElement("userName").addText("cloudwei");
		login.addElement("password").addText("ztcj2013");
	}
	
	private void createWrongPasswordRequest(Document doc){
		Element rmds = doc.addElement("rmds");
		Element login = rmds.addElement("login");
		login.addElement("userName").addText("ztcj");
		login.addElement("password").addText("123456");
	}

	
	private void sentMessageToServer(byte msgType,Document doc){
		LocateMessage message = new LocateMessage(msgType, doc);
		ChannelFuture future = channel.write(message);
		future.awaitUninterruptibly();
	}
	
	private void createCurrencyRequest(Document doc){
		Element rmds = doc.addElement("rmds");
		Element request = rmds.addElement("request");
		Element item = request.addElement("item");
		item.addElement("name").addText("=USD");
//		item = request.addElement("item");
//		item.addElement("name").addText("EUA=");
	}
	
	private void createFutureRequest(Document doc){
		Element rmds = doc.addElement("rmds");
		Element request = rmds.addElement("request");
		Element item = request.addElement("item");
		item.addElement("name").addText("XAU=");
		item = request.addElement("item");
		item.addElement("name").addText("XAG=");
	}
	
	private void createNewsRequest(Document doc){
		Element rmds = doc.addElement("rmds");
		Element request = rmds.addElement("request");
		Element item = request.addElement("item");
		item.addElement("name").addText("ASIA");
//		Element item = request.addElement("item");
//		item.addElement("name").addText("XAG=");
	}
	
	public void createClientConnetion() {
		// 创建客户端channel的辅助类,发起connection请求
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("encoder", new GateWayEncoder());
				pipeline.addLast("decoder", new GateWayDecoder());
				pipeline.addLast("hanlder", new clientHandler());
				return pipeline;
			}
		});
		
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(8888));
		channel = future.getChannel();
		future.awaitUninterruptibly();
//		bootstrap.releaseExternalResources();
		
	}
	
	public void testSendRequest(byte msgType){
		DocumentFactory documentFactory = DocumentFactory.getInstance();
	    Document requestDoc =  documentFactory.createDocument();
		switch( msgType){
		    case RFAMessageTypes.ERROR : 
				System.out.println("收到包含错误信息的消息");
		    	break;
		    case RFAMessageTypes.LOGINWRONGUSER :
		    	createWrongLoginRequest(requestDoc);
			    sentMessageToServer(RFAMessageTypes.LOGIN, requestDoc);
		    	break;
		    case RFAMessageTypes.LOGINWRONGPASSWORD :
		    	createWrongPasswordRequest(requestDoc);
			    sentMessageToServer(RFAMessageTypes.LOGIN, requestDoc);
		    	break;
		    case RFAMessageTypes.LOGIN:
		    	createLoginRequest(requestDoc);
		    	sentMessageToServer(RFAMessageTypes.LOGIN, requestDoc);
		    	break;
		    case RFAMessageTypes.CURRENCY_REQUEST : 
		    	createCurrencyRequest(requestDoc);
		    	sentMessageToServer(RFAMessageTypes.CURRENCY_REQUEST,requestDoc);
		    	break;
		    case RFAMessageTypes.FUTURE_REQUEST : 
		    	createFutureRequest(requestDoc);
		    	sentMessageToServer(RFAMessageTypes.FUTURE_REQUEST,requestDoc);
		    break;
		    case RFAMessageTypes.ONE_TIMES_REQUEST : 
		    	createOneTimesRequest(requestDoc);
		    	sentMessageToServer(RFAMessageTypes.ONE_TIMES_REQUEST,requestDoc);
//		    	requestDoc =  factory.createDocument();
//		    	createNewsRequest(requestDoc);
//		    	requestDoc = parseFile();
//		    	sentMessageToServer(RFAMessageTypes.NEWS_REQUEST,requestDoc,channel);
		    	
		    	//Parse messageContext to xml document and pick up data to handle it
		    	break;
		    case RFAMessageTypes.RESPONSE_STOCK:
		    	//Parse messageContext to xml document and pick up data to handle it
		    	break;
		    case RFAMessageTypes.RESPONSE_STOCK_LINK:
		    	//Parse messageContext to xml document and pick up data to handle it
	    }
	}
	
	
	
	@Test
	public void testClient(){
		t0=System.currentTimeMillis();
		createClientConnetion();
//		testSendRequest(RFAMessageTypes.LOGINWRONGUSER);
//		testSendRequest(RFAMessageTypes.LOGINWRONGPASSWORD);
//		testSendRequest(RFAMessageTypes.FUTURE_REQUEST);
		testSendRequest(RFAMessageTypes.LOGIN);
		testSendRequest(RFAMessageTypes.FUTURE_REQUEST);
		
		
		channel.getCloseFuture().awaitUninterruptibly();
	}
	
//	/**
//	 * Create the UdpClient's instance
//	 */
//	public TestSocketClient() {
//	    connector = new NioSocketConnector();
//	    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new RFAClientCodecFactory()));
//	    connector.setHandler(this);
//	    SocketSessionConfig dcfg = (SocketSessionConfig) connector.getSessionConfig();
//	    //When have message need sent to server, True means immediately send.
//	    dcfg.setTcpNoDelay(true);
//	    
//	    ConnectFuture connFuture = connector.connect(new InetSocketAddress("localhost", PORT));
//
//	    connFuture.awaitUninterruptibly();
//
//	    session = connFuture.getSession();
//	}
//	
//	
//	/**
//	 * The main method : instanciates a client, and send N messages. We sleep 
//	 * between each K messages sent, to avoid the server saturation.
//	 * @param args
//	 * @throws Exception
//	 */
//	public static void main(String[] args) throws Exception {
//		TestSocketClient client = new TestSocketClient();
//
//		client.t0 = System.currentTimeMillis();
//
//        IoBuffer buffer = IoBuffer.allocate(2048);
//        buffer.clear();
//        buffer.setAutoShrink(true);
//        buffer.setAutoExpand(true);
//        
//        DocumentFactory factory = DocumentFactory.getInstance();
//        Document req =  factory.createDocument();
//        //Login option to RMDS 
//        //You can create a XML document by dom4j or others
//        buffer.put(RFAMessageTypes.LOGIN);
//        client.createLoginRequest(req);
//        
//        int length = req.asXML().length()*2+5;        
//        buffer.put(RFATypeConvert.intToByteArray1(length));
//        buffer.putObject(req);
//		System.out.println("Sent messages :"+req.asXML());
//			
//        buffer.flip();
//        session.write(buffer);
//        client.t1 = System.currentTimeMillis();	    
//	    System.out.println("Sent messages delay : " + (client.t1 - client.t0));	    
////	    Thread.sleep(100000);
////	    client.connector.dispose(true);
//	    
//	}

}
