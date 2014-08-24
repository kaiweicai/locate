package com.locate.common.utils;

import com.locate.common.model.LocateUnionMessage;

public class MessageEncapsulator {
	public static LocateUnionMessage encapLogionResponseMessage(LocateUnionMessage message,int resultCode,String resultDes){
		message.setResultCode(resultCode);
		message.setResultDes(resultDes);
		message.setState(resultDes);
		message.setStreamingState(resultDes);
		return message;
	}
}
