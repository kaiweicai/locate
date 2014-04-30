package com.locate.gate.model;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.dom4j.Document;

public class LocateMessage {
	Logger logger = Logger.getLogger(LocateMessage.class);
	
	public LocateMessage(){
		
	}
	public LocateMessage(byte msgType, Document document) {
		this.msgType = msgType;
		try {
			this.msgLength = document.asXML().getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			logger.error("Caculate the document length error!",e);
		}catch(Exception e){
			logger.error("unkonw exception ocurre, Please contact the develepor",e);
		}
		this.document = document;
	}
	
	byte msgType;
	int msgLength;
	Document document;

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
	
	@Override
	public String toString() {
		return "LocateMessage [msgType=" + msgType + ", msgLength=" + msgLength + ", document=" + document.asXML() + "]";
	}
}
