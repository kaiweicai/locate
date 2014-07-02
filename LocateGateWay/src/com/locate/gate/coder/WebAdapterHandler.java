package com.locate.gate.coder;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.springframework.stereotype.Service;

import com.locate.common.JsonUtil;

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
