package com.locate.rmds.engine.filter;

import java.util.ArrayList;
import java.util.List;

import com.locate.common.model.LocateUnionMessage;

public class FieldFilter {
	public LocateUnionMessage doFilter(LocateUnionMessage message,List<Integer> filterIdList){
		List<String[]> filterPayloadList = new ArrayList<String[]>();
		for(String[] payLoad : message.getPayLoadSet()){
//			String filedName = payLoad[1];
			int filedId = Integer.parseInt(payLoad[0]);
			for(int id:filterIdList){
				if(filedId==id){
					filterPayloadList.add(payLoad);
				}
			}
			
		}
		message.setPayLoadSet(filterPayloadList);
		return message;
	}
}
