package com.locate.gate.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebAdapterHandler  {
	Logger logger = LoggerFactory.getLogger(getClass());

//	@Override
//	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
//		Document doc = null;
//		doc = (Document) msg;
//		String content = doc.asXML();
//		String result = JsonUtil.getJSONFromXml(content).toString();
//		return new TextWebSocketFrame(result);
//	}
}
