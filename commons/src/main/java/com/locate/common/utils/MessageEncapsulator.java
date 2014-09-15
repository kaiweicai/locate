package com.locate.common.utils;

import com.locate.common.constant.LocateResultCode;
import com.locate.common.model.LocateUnionMessage;

public class MessageEncapsulator {
	public static LocateUnionMessage encapLogionResponseMessage(LocateUnionMessage message,int resultCode,byte msgType){
		message.setResultCode(resultCode);
		String resultDes = LocateResultCode.getResultDescription(resultCode);
		message.setResultDes(resultDes);
		message.setMsgType(msgType);
		return message;
	}
	
	public static LocateUnionMessage encapStateResponseMessage(String streamingState,String dataingState,String state,byte msgType,long startTime){
		LocateUnionMessage message = new LocateUnionMessage();
		message.setStreamingState(streamingState);
		message.setDataingState(dataingState);
		message.setState(state);
		message.setMsgType(msgType);
		message.setStartTime(startTime);
		return message;
	}
}
