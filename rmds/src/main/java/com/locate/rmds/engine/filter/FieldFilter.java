package com.locate.rmds.engine.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.locate.common.model.LocateUnionMessage;

public class FieldFilter implements Engine {
	Logger logger = Logger.getLogger(getClass());
	private List<Integer> filterIdList = new ArrayList<Integer>();

	@Override
	public LocateUnionMessage doEngine(LocateUnionMessage message) {
		if(filterIdList==null){
			logger.warn("The filter not load. return null value. The message is "+message);
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

	public List<Integer> getFilterIdList() {
		return filterIdList;
	}

	public void setFilterIdList(List<Integer> filterIdList) {
		this.filterIdList = filterIdList;
	}
}
