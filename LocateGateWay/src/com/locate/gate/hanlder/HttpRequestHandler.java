package com.locate.gate.hanlder;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
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
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.locate.bridge.GateForwardRFA;
import com.locate.common.DataBaseCache;
import com.locate.common.GateWayMessageTypes;
import com.locate.gate.server.GateWayWebServer;
import com.locate.gate.server.WebSocketServerIndexPage;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.processer.ItemGroupManager;
import com.locate.rmds.processer.ItemManager;
import com.locate.rmds.processer.OneTimeItemManager;
import com.locate.rmds.processer.WebItemManager;
import com.sun.istack.internal.logging.Logger;

@Service
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {
	Logger logger = Logger.getLogger(getClass());
	private HttpRequest request;
	private boolean readingChunks;
	@Resource
	private QSConsumerProxy mainApp;
	@Resource
	WebItemManager webItemManager;
	@Resource
	private GateForwardRFA gateForwardRFA;
	private static final String WEBSOCKET_PATH = "/websocket";
	private WebSocketServerHandshaker handshaker;
	private ChannelGroup webSocketGroup = new DefaultChannelGroup();
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
//			 * 是这样的一种情况：HTTP客户端程序有一个实体的主体部分要发送给服务器，但希望在发送之前查看下服务器是否会
//			 * 接受这个实体，所以在发送实体之前先发送了一个携带100
//			 * Continue的Expect请求首部的请求。服务器在收到这样的请求后，应该用 100 Continue或一条错误码来进行响应。
//			 */
//			if (is100ContinueExpected(request)) {
//				send100Continue(e);
//			}
//			// 解析http头部
//			for (Map.Entry<String, String> h : request.headers()) {
//				System.out.println("HEADER: " + h.getKey() + " = " + h.getValue() + "\r\n");
//			}
//			// 解析请求参数
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
//		} else {// 为分块编码时
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

			res.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
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
		} else {
			this.handshaker.handshake(ctx.getChannel(), req);
			System.out.println(DataBaseCache.recipientsMap.size());
//			DataBaseCache.recipientsMap.put(ctx.getChannel());
			webSocketGroup.add(ctx.getChannel());
			System.out.println(DataBaseCache.recipientsMap.size());
			System.out.println(ctx.getChannel().getId());
		}
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			this.handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
			return;
		} else if (frame instanceof PingWebSocketFrame) {
			ctx.getChannel().write(new PongWebSocketFrame(frame.getBinaryData()));
			return;
		} else if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
					.getName()));
		}

		// Send the uppercase string back.
		String ric = ((TextWebSocketFrame) frame).getText();
		logger.info(String.format("Channel %s received %s", ctx.getChannel().getId(), ric));
		
		ChannelGroup httpChannelGroup = DataBaseCache.recipientsMap.get(ric);
		if (httpChannelGroup == null) {
			httpChannelGroup = new DefaultChannelGroup("httpChannelGroup");
			DataBaseCache.recipientsMap.put(ric, httpChannelGroup);
		}
		httpChannelGroup.add(ctx.getChannel());
		webItemManager.sendRICRequest(ric, GateWayMessageTypes.RESPONSE_FUTURE);
		
		webSocketGroup.write(new TextWebSocketFrame(ric.toUpperCase()));
		ctx.getChannel().write(new TextWebSocketFrame(ric.toUpperCase()));
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
		return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
	}

	private void writeResponse(MessageEvent e, String uri) {
		// 解析Connection首部，判断是否为持久连接
		boolean keepAlive = isKeepAlive(request);

		// Build the response object.
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		response.setStatus(HttpResponseStatus.OK);
		// 服务端可以通过location首部将客户端导向某个资源的地址。
		// response.addHeader("Location", uri);
		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
		}
		// 得到客户端的cookie信息，并再次写到客户端
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
		// 如果文件隐藏或者不存在
		if (localFile.isHidden() || !localFile.exists()) {
			// 逻辑处理
			return;
		}
		// 如果请求路径为目录
		if (localFile.isDirectory()) {
			// 逻辑处理
			return;
		}
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(localFile, "r");
			long fileLength = raf.length();
			response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(fileLength));
			Channel ch = e.getChannel();
			ch.write(response);
			// 这里又要重新温习下http的方法，head方法与get方法类似，但是服务器在响应中只返回首部，不会返回实体的主体部分
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
		webSocketGroup.remove(ctx.getChannel());
		webItemManager.closeRequest();
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
