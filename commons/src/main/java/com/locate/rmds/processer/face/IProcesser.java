package com.locate.rmds.processer.face;

import java.util.ArrayList;
import java.util.List;

import com.locate.common.model.LocateUnionMessage;


public abstract class IProcesser{
	protected String derivactiveItemName;
	
	public LocateUnionMessage filedFiltrMessage(LocateUnionMessage message,List<Integer> filterIdList){
		if(filterIdList==null){
//			logger.warn("The filter not load. return null value. The message is "+message);
			return null;
		}
		List<String[]> filterPayloadList = new ArrayList<String[]>();
		for (String[] payLoad : message.getPayLoadSet()) {
			// String filedName = payLoad[1];
			int filedId = Integer.parseInt(payLoad[0]);
			for (int id : filterIdList) {
				if (filedId == id) {
					filterPayloadList.add(payLoad);
				}
			}

		}
		message.setPayLoadSet(filterPayloadList);
		return message;
	}
	
	public String getDerivactiveItemName() {
		return derivactiveItemName;
	}

	public void setDerivactiveItemName(String derivactiveItemName) {
		this.derivactiveItemName = derivactiveItemName;
	}

	public abstract void closeRequest();

	public abstract void sendRicRequest(String ric, byte responseFuture);
}