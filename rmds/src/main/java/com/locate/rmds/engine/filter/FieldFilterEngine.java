package com.locate.rmds.engine.filter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.model.LocateUnionMessage;
import com.locate.rmds.engine.Engine;

public class FieldFilterEngine implements Engine {
	Logger logger = LoggerFactory.getLogger(getClass());
	private List<Integer> filterIdList = new ArrayList<Integer>();

	@Override
	public LocateUnionMessage doEngine(LocateUnionMessage message) {
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

	public List<Integer> getFilterIdList() {
		return filterIdList;
	}

	public void setFilterIdList(List<Integer> filterIdList) {
		this.filterIdList = filterIdList;
	}
}
