package com.locate.gate.hanlder;

import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.springframework.stereotype.Service;

import com.locate.common.JsonUtil;
import com.sun.org.apache.bcel.internal.generic.CASTORE;

@Service
public class WebAdapterHandler extends OneToOneEncoder {
	Logger logger = Logger.getLogger(getClass());

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		Document doc = null;
		doc = (Document) msg;
		String content = doc.asXML();
		String result = JsonUtil.getJSONFromXml(content).toString();
		return new TextWebSocketFrame(result);
	}
}
