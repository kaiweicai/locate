package com.locate.rmds.engine;

import java.util.List;

import com.locate.common.model.LocateUnionMessage;

public class CurrencyEngine implements Engine {
	public static float currency;
	@Override
	public LocateUnionMessage doEngine(LocateUnionMessage message) {
		List<String[]> payLoadList = message.getPayLoadSet();
		for(String[] payLoad:payLoadList){
			String id = payLoad[0];
			if(id.equals("6")||id.equals("25")){
				float exchangValue=Float.parseFloat(payLoad[3])*currency;
				payLoad[3]=""+exchangValue;
			}
		}
		return message;
	}

}
