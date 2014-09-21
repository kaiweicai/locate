package com.locate.gate.hanlder;

import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.dom4j.io.DocumentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.SystemProperties;

@Service
public class AdapterHandler extends MessageToMessageEncoder<LocateUnionMessage> {
	Logger logger = LoggerFactory.getLogger(getClass());
	private static String defaultEncode = SystemProperties.getProperties(SystemProperties.DEFAULT_ENCODE);
	
	@Override
	protected void encode(io.netty.channel.ChannelHandlerContext ctx, LocateUnionMessage msg, List<Object> out)
			throws Exception {
		String content="";
		switch (defaultEncode) {
		case "JSON":
			JSON jsonObject = JSONObject.fromObject(msg);
			content = jsonObject.toString();
			break;
		case "XML":
			JAXBContext context = JAXBContext.newInstance(LocateUnionMessage.class);
			Marshaller marShaller = context.createMarshaller();
			DocumentResult node = new DocumentResult();
			marShaller.marshal(msg, node);
			content = node.getDocument().asXML();
			break;
		default:

		}
		out.add(content);
	}
	
//	@Override
//	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
//		
//		LocateUnionMessage message = (LocateUnionMessage) msg;
//		byte[] content = null;
//		switch(defaultEncode){
//			case "JSON":
//				JSON jsonObject = JSONObject.fromObject(message);
//				content = jsonObject.toString().getBytes("UTF-8");
//				break;
//			case "XML":
//				JAXBContext context = JAXBContext.newInstance(LocateUnionMessage.class);
//				Marshaller marShaller = context.createMarshaller();
//				DocumentResult node = new DocumentResult();
//				marShaller.marshal(message, node);
//				content = node.getDocument().asXML().getBytes("UTF-8");
//				break;
//			default:
//				
//		}
//		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
//		buffer.writeBytes(content);
//		return buffer;
//	}
}
