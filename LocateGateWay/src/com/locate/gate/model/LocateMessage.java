package com.locate.gate.model;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.dom4j.Document;

public class LocateMessage {
	Logger logger = Logger.getLogger(LocateMessage.class);
	
	byte msgType;
	int msgLength;
	//如果发生错误errorCode>0;
	int errorCode = 0;
	Document document;
	
	public LocateMessage(){
		
	}
	
	public LocateMessage(byte msgType, Document document, int errorCode) {
		this.msgType = msgType;
		this.errorCode = errorCode;
		try {
			this.msgLength = document.asXML().getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			logger.error("Caculate the document length error!",e);
		}catch(Exception e){
			logger.error("unkonw exception ocurre, Please contact the develepor",e);
		}
		this.document = document;
	}
	
	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}

	public int getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return "LocateMessage [msgType=" + msgType + ", msgLength=" + msgLength + ", errorCode="
				+ errorCode + ", document=" + document + "]";
	}
	
}
