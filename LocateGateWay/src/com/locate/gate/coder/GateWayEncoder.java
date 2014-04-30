package com.locate.gate.coder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.locate.gate.model.LocateMessage;

public class GateWayEncoder extends SimpleChannelHandler {

	Logger logger = Logger.getLogger(GateWayEncoder.class);

	private final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		buffer.clear();
		LocateMessage messgae = (LocateMessage) e.getMessage();
		buffer.writeByte(messgae.getMsgType());
		String xml = messgae.getDocument().asXML();
		byte[] content = xml.getBytes("UTF-8");
		int dataLength = content.length;
		buffer.writeInt(dataLength);
		buffer.writeBytes(content);
		Channels.write(ctx, e.getFuture(), buffer);
//		buffer.clear();
		logger.info("Send message to client:\n" + messgae);
		// byte[] len ;
		// if("true".equalsIgnoreCase(SystemProperties.getProperties(SystemProperties.USE_SOCKET_MINA))){
		// dataLength = SizeCalculator.calcSize(response)+5;
		// len = RFATypeConvert.intToByteArray1(dataLength);
		// buffer.put(len);
		// buffer.putObject(response);
		//
		// _logger.info("Send message to client:\n"+xml);
		// }else{
		// byte[] xmlbyte = xml.getBytes(CHARSET);
		// dataLength = xmlbyte.length+5;
		// len = RFATypeConvert.intToByteArray1(dataLength);
		// buffer.put(len);
		// buffer.put(xmlbyte);
		// }
		//
		// buffer.flip();
		// _logger.info("Send message to client:\n"+buffer);
		// try{
		// _session.write(buffer);
		// }catch(Exception e){
		// _logger.error("Message sent failed.");
		// _logger.error(e.getMessage(),e);
		// }

	}
}
