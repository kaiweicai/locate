package com.locate.rmds.handler.inter;

import org.dom4j.Document;

public interface IRequestHandler {
	public abstract int processRequest(Document req,String clientName,byte responseMsgType, int channelId );
	
	public abstract int processOneTimesRequest(Document req, String clientName, byte responseMsgType, int channel);
}