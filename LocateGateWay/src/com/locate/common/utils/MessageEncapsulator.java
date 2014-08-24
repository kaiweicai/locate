package com.locate.common.utils;

import com.locate.common.LocateResultCode;
import com.locate.common.model.LocateUnionMessage;

public class MessageEncapsulator {
	public static LocateUnionMessage encapLogionResponseMessage(LocateUnionMessage message,int resultCode,byte msgType){
		message.setResultCode(resultCode);
		String resultDes = LocateResultCode.LocateResponseEnum.getResultDescription(resultCode);
		message.setResultDes(resultDes);
		message.setState(resultDes);
		message.setMsgType(msgType);
		return message;
	}
}
