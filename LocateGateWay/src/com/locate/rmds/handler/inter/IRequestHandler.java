package com.locate.rmds.handler.inter;

import com.locate.common.model.ClientRequest;

public interface IRequestHandler {
	public abstract int processRequest(ClientRequest req,String clientName,byte responseMsgType, int channelId );
	
	public abstract int processOneTimesRequest(ClientRequest req, String clientName, byte responseMsgType, int channel);
}