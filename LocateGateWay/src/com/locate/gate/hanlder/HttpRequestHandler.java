package com.locate.gate.hanlder;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.util.CharsetUtil;
import org.springframework.stereotype.Service;

import com.locate.bridge.GateForwardRFA;
import com.locate.common.DataBaseCache;
import com.locate.common.GateWayMessageTypes;
import com.locate.common.RFANodeconstant;
import com.locate.gate.model.ClientInfo;
import com.locate.gate.server.WebSocketServerIndexPage;
import com.locate.rmds.QSConsumerProxy;
import com.sun.istack.internal.logging.Logger;

@Service
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {
	Logger logger = Logger.getLogger(getClass());
	private HttpRequest request;
	private boolean readingChunks;
	@Resource
	private QSConsumerProxy mainApp;
//	@Resource
//	WebItemManager webItemManager;
	@Resource
	private GateForwardRFA gateForwardRFA;
	private static final String WEBSOCKET_PATH = "/websocket";
	private WebSocketServerHandshaker handshaker;
	@Resource
	private WebAdapterHandler webAdapterHandler;
	@Resource
	private WebSocketServerIndexPage webSocketServerIndexPage;
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		Object msg = e.getMessage();
		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}

//		if (!readingChunks) {
//			HttpRequest request = this.request = (HttpRequest) e.getMessage();
//			String uri = request.getUri();
//			logger.info("-----------------------------------------------------------------");
//			logger.info("uri:" + uri);
//			logger.info("-----------------------------------------------------------------");
//			/**
//			 * 100 Continue
//			 * ��������һ�������HTTP�ͻ��˳�����һ��ʵ������岿��Ҫ���͸�����������ϣ���ڷ���֮ǰ�鿴�·������Ƿ��
//			 * �������ʵ�壬�����ڷ���ʵ��֮ǰ�ȷ�����һ��Я��100
//			 * Continue��Expect�����ײ������󡣷��������յ������������Ӧ���� 100 Continue��һ����������������Ӧ��
//			 */
//			if (is100ContinueExpected(request)) {
//				send100Continue(e);
//			}
//			// ����httpͷ��
//			for (Map.Entry<String, String> h : request.headers()) {
//				System.out.println("HEADER: " + h.getKey() + " = " + h.getValue() + "\r\n");
//			}
//			// �����������
//			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
//			Map<String, List<String>> params = queryStringDecoder.getParameters();
//			String ric = "";
//			if (!params.isEmpty()) {
//				for (Entry<String, List<String>> p : params.entrySet()) {
//					String key = p.getKey();
//					List<String> vals = p.getValue();
//					for (String val : vals) {
//						if ("RIC".equalsIgnoreCase(key)) {
//							ric = val;
//							ChannelGroup httpChannelGroup = DataBaseCache.recipientsMap.get(ric);
//							if (httpChannelGroup == null) {
//								httpChannelGroup = new DefaultChannelGroup("httpChannelGroup");
//								DataBaseCache.recipientsMap.put(ric, httpChannelGroup);
//							}
//							httpChannelGroup.add(e.getChannel());
//							webItemManager.sendOneTimeRequest(val, GateWayMessageTypes.RESPONSE_FUTURE, request);
//						}
//					}
//				}
//			}
//			if (request.isChunked()) {
//				readingChunks = true;
//			} else {
//				ChannelBuffer content = request.getContent();
//				if (content.readable()) {
//					logger.info(content.toString(CharsetUtil.UTF_8));
//				}
//				// writeResponse(e, uri);
//			}
//		} else {// Ϊ�ֿ����ʱ
//			HttpChunk chunk = (HttpChunk) e.getMessage();
//			if (chunk.isLast()) {
//				readingChunks = false;
//				// END OF CONTENT\r\n"
//				HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;
//				if (!trailer.trailingHeaders().isEmpty()) {
//					for (Entry<String, String> header : trailer.trailingHeaders()) {
//						logger.info("TRAILING HEADER: " + header.getKey() + " = " + header.getValue() + "\r\n");
//					}
//				}
//				// writeResponse(e, "/");
//			} else {
//				logger.info("CHUNK: " + chunk.getContent().toString(CharsetUtil.UTF_8) + "\r\n");
//			}
//		}
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
		// Allow only GET methods.
		if (req.getMethod() != GET) {
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
			return;
		}

		// Send the demo page and favicon.ico
		if (req.getUri().equals("/")) {
			HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);

			ChannelBuffer content = webSocketServerIndexPage.getContent(getWebSocketLocation(req));

			res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
			setContentLength(res, content.readableBytes());

			res.setContent(content);
			sendHttpResponse(ctx, req, res);
			return;
		} else if (req.getUri().equals("/favicon.ico")) {
			HttpResponse res = new DefaultHttpResponse(HTTP_1_1, NOT_FOUND);
			sendHttpResponse(ctx, req, res);
			return;
		}
			// Handshake
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				this.getWebSocketLocation(req), null, false);
		this.handshaker = wsFactory.newHandshaker(req);
		if (this.handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
		} else {// websocket���ӳɹ�.
			this.handshaker.handshake(ctx.getChannel(), req);
			logger.info("create the websocket shakehand success!");
			ChannelPipeline pipeline = ctx.getPipeline();
			pipeline.addLast("webAdpterHandler", webAdapterHandler);
			DataBaseCache.webSocketGroup.add(ctx.getChannel());
			//��channelId�Ͷ�Ӧ��channel�ŵ�map��,��д�ͻ��˵�ʱ����Ը��ݸ�id�ҵ���Ӧ��channel.
			if(!DataBaseCache.allChannelGroup.contains(ctx.getChannel())){
				DataBaseCache.allChannelGroup.add(ctx.getChannel());
			}
		}
		
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		Channel channel = ctx.getChannel();
		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			ctx.getPipeline().remove(webAdapterHandler);
			this.handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
			ctx.getChannel().close();
			return;
		} else if (frame instanceof PingWebSocketFrame) {
			ctx.getPipeline().remove(webAdapterHandler);
			ctx.getChannel().write(new PongWebSocketFrame(frame.getBinaryData()));
			return;
		} else if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
					.getName()));
		}

		// Send the uppercase string back.
		String ric = ((TextWebSocketFrame) frame).getText();
		logger.info(String.format("Channel %s received %s", ctx.getChannel().getId(), ric));
		
		ChannelGroup httpChannelGroup = DataBaseCache.itemNameChannelMap.get(ric);
		if (httpChannelGroup == null) {
			httpChannelGroup = new DefaultChannelGroup("httpChannelGroup");
			DataBaseCache.itemNameChannelMap.put(ric, httpChannelGroup);
		}
		if(!httpChannelGroup.contains(channel)){
			httpChannelGroup.add(channel);
		}
		
		
		DocumentFactory documentFactory = DocumentFactory.getInstance();
	    Document requestDoc =  documentFactory.createDocument();
	    
		Element rmds = requestDoc.addElement("rmds");
		rmds.addElement(RFANodeconstant.LOCATE_NODE);
		Element login = rmds.addElement("login");
		login.addElement("userName").addText("ztcj");
		login.addElement("password").addText("ztcj2013");
		
		ClientInfo clientInfo = new ClientInfo(requestDoc, "ztcj", channel.getId(), GateWayMessageTypes.LOGIN, "127.0.0.1");
		gateForwardRFA.process(clientInfo);
		
		documentFactory = DocumentFactory.getInstance();
	    requestDoc =  documentFactory.createDocument();
	    rmds = requestDoc.addElement("rmds");
		rmds.addElement(RFANodeconstant.LOCATE_NODE);
		Element request = rmds.addElement("request");
		Element item = request.addElement("item");
		item.addElement("name").addText(ric);
		
		clientInfo = new ClientInfo(requestDoc, "ztcj", channel.getId(), GateWayMessageTypes.FUTURE_REQUEST, "127.0.0.1");
		gateForwardRFA.process(clientInfo);
		
//		DataBaseCache.webSocketGroup.write(new TextWebSocketFrame(ric.toUpperCase()));
//		ctx.getChannel().write(new TextWebSocketFrame(ric.toUpperCase()+"�����ɹ�."));
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
		// Generate an error page if response status code is not OK (200).
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private String getWebSocketLocation(HttpRequest req) {
		return "ws://" + req.headers().get(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
	}

	private void writeResponse(MessageEvent e, String uri) {
		// ����Connection�ײ����ж��Ƿ�Ϊ�־�����
		boolean keepAlive = isKeepAlive(request);

		// Build the response object.
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setStatus(HttpResponseStatus.OK);
		// ����˿���ͨ��location�ײ����ͻ��˵���ĳ����Դ�ĵ�ַ��
		// response.addHeader("Location", uri);
		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
		}
		// �õ��ͻ��˵�cookie��Ϣ�����ٴ�д���ͻ���
		String cookieString = request.getHeader(COOKIE);
		if (cookieString != null) {
			CookieDecoder cookieDecoder = new CookieDecoder();
			Set<Cookie> cookies = cookieDecoder.decode(cookieString);
			if (!cookies.isEmpty()) {
				CookieEncoder cookieEncoder = new CookieEncoder(true);
				for (Cookie cookie : cookies) {
					cookieEncoder.addCookie(cookie);
				}
				response.addHeader(SET_COOKIE, cookieEncoder.encode());
			}
		}
		final String path = "test/com/locate/test/common/message.json";
		File localFile = new File(path);
		// ����ļ����ػ��߲�����
		if (localFile.isHidden() || !localFile.exists()) {
			// �߼�����
			return;
		}
		// �������·��ΪĿ¼
		if (localFile.isDirectory()) {
			// �߼�����
			return;
		}
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(localFile, "r");
			long fileLength = raf.length();
			response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(fileLength));
			Channel ch = e.getChannel();
			ch.write(response);
			// ������Ҫ������ϰ��http�ķ�����head������get�������ƣ����Ƿ���������Ӧ��ֻ�����ײ������᷵��ʵ������岿��
			if (!request.getMethod().equals(HttpMethod.HEAD)) {
				ch.write(new ChunkedFile(raf, 0, fileLength, 8192));// 8kb
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		} finally {
			if (keepAlive) {
				response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
			}
			if (!keepAlive) {
				e.getFuture().addListener(ChannelFutureListener.CLOSE);
			}
		}
	}

	private void send100Continue(MessageEvent e) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
		e.getChannel().write(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		logger.info("channel has been closed.");
		
		Channel channel = ctx.getChannel();
		DataBaseCache.allChannelGroup.remove(channel);
		//�������е�channelgoup,�����и�channel�ľ�remove��.�����channelGroupΪ��,
		for(Entry<String,ChannelGroup> entry:DataBaseCache.webItemChannelMap.entrySet()){
			String itemName = entry.getKey();
			ChannelGroup channelGroup = entry.getValue();
			if(channelGroup.contains(channel)){
				channelGroup.remove(channel);
			}
			if(channelGroup.isEmpty()){
				channelGroup = null;
				gateForwardRFA.closeHandler(itemName);
			}
		}
	}

	static class Config {
		public static String getRealPath(String uri) {
			StringBuilder sb = new StringBuilder("/home/guolei/workspace/Test/web");
			sb.append(uri);
			if (!uri.endsWith("/")) {
				sb.append('/');
			}
			return sb.toString();
		}
	}

	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}
}
