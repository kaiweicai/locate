package com.locate.gate.hanlder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.dom4j.io.DocumentResult;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.springframework.stereotype.Service;

import com.locate.common.model.LocateUnionMessage;
import com.locate.rmds.util.SystemProperties;

@Service
public class AdapterHandler extends OneToOneEncoder {
	Logger logger = Logger.getLogger(getClass());
	private static String defaultEncode = SystemProperties.getProperties(SystemProperties.DEFAULT_ENCODE);
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		
		LocateUnionMessage message = (LocateUnionMessage) msg;
		byte[] content = null;
		switch(defaultEncode){
			case "JSON":
				JSON jsonObject = JSONObject.fromObject(message);
				content = jsonObject.toString().getBytes("UTF-8");
				break;
			case "XML":
				JAXBContext context = JAXBContext.newInstance(LocateUnionMessage.class);
				Marshaller marShaller = context.createMarshaller();
				DocumentResult node = new DocumentResult();
				marShaller.marshal(message, node);
				content = node.getDocument().asXML().getBytes("UTF-8");
				break;
			default:
				
		}
		ChannelBuffer buffer = ChannelBuffers.buffer(content.length);
		buffer.writeBytes(content);
		return buffer;
	}
}
