package com.locate.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientLocateUnionMessage extends LocateUnionMessage {
	private Map<Integer,String[]> tradeRecodeMap;
	
	public ClientLocateUnionMessage(LocateUnionMessage message){
		super();
		this.setItemName(message.getItemName());
		this.setStartTime(message.getStartTime());
		this.setGeneratetime(message.getGeneratetime());
		this.setMsgType(message.getMsgType());
		this.setSeqNumber(message.getSeqNumber());
		this.setLocateSeqNumber(message.getLocateSeqNumber());
		this.setState(message.getState());
		this.setStreamingState(message.getStreamingState());
		this.setResultCode(message.getResultCode());
		this.setResultDes(message.getResultDes());
		this.setHeader(message.getHeader());
		this.setPayLoadSet(message.getPayLoadSet());
		
		tradeRecodeMap = new HashMap<Integer,String[]>();
		List<String[]> payloadList = message.getPayLoadSet();
		for(String[] payLoad:payloadList){
			int id = Integer.parseInt(payLoad[0]);
			tradeRecodeMap.put(id, payLoad);
		}
	}
	
	public Map<Integer, String[]> getTradeRecodeMap() {
		return tradeRecodeMap;
	}
	
	public void setTradeRecodeMap(Map<Integer, String[]> tradeRecodeMap) {
		this.tradeRecodeMap = tradeRecodeMap;
	}
}
