package com.locate.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.locate.common.constant.RFANodeconstant;
import com.locate.common.constant.SystemConstant;

public class XmlMessageUtil {
	static final String LOGIN_SUCCESSFUL = "0";
	static final String LOGIN_FAILED = "1";

//	public static Document createAuthenResponse(RFAUserAuthentication userAuthentication,String streamingState,String dataingState,String state) {
//		DocumentFactory factory = DocumentFactory.getInstance();
//		Document doc = factory.createDocument();
//		Element rmds = doc.addElement(RFANodeconstant.RESPONSE_ROOT_NODE);
//
//		Element locateElement = rmds.addElement(RFANodeconstant.LOCATE_NODE);
//		locateElement.addElement(RFANodeconstant.STREAM_STATE_NODE).addText(streamingState);
//		locateElement.addElement(RFANodeconstant.DATA_STATE_NODE).addText(dataingState);
//		locateElement.addElement(RFANodeconstant.ALL_STATE_NODE).addText(state);
//
//		Element response = rmds.addElement(RFANodeconstant.RESPONSE_RESPONSE_NODE);
//		Element login = response.addElement(RFANodeconstant.RESPONSE_LOGIN_NODE);
//		if (userAuthentication == null) {
//			login.addElement(RFANodeconstant.RESPONSE_LOGIN_RESULT_NODE).addText(LOGIN_SUCCESSFUL);
//			login.addElement(RFANodeconstant.RESPONSE_LOGIN_DESC_NODE).addText("You passed authentication");
//		} else {
//			login.addElement(RFANodeconstant.RESPONSE_LOGIN_RESULT_NODE).addText(LOGIN_FAILED);
//			login.addElement(RFANodeconstant.RESPONSE_LOGIN_DESC_NODE).addText(userAuthentication.getException());
//		}
//		return doc;
//	}

	public static Document createErrorDocument(int errorCode, String descriptioin) {
		DocumentFactory factory = DocumentFactory.getInstance();
		Document doc = factory.createDocument();
		Element rmds = doc.addElement(RFANodeconstant.RESPONSE_ROOT_NODE);
		rmds.addElement(RFANodeconstant.LOCATE_NODE);
		Element response = rmds.addElement(RFANodeconstant.RESPONSE_RESPONSE_NODE);
		Element error = response.addElement(RFANodeconstant.RESPONSE_ERROR_NODE);
		error.addElement(RFANodeconstant.RESPONSE_ERROR_CODE_NODE).addText(String.valueOf(errorCode));
		error.addElement(RFANodeconstant.RESPONSE_ERROR_DESC_NODE).addText(String.valueOf(descriptioin));
		return doc;
	}

	public static void addMsgType(Document doc, Byte msgType) {
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);
		if (locateElement.element(RFANodeconstant.MSG_TYPE_NODE) == null) {
			locateElement.addElement(RFANodeconstant.MSG_TYPE_NODE).addText(String.valueOf(msgType));
		}
	}

	public static byte getMsgType(Document doc) {
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);
		String msgType = locateElement.element(RFANodeconstant.MSG_TYPE_NODE).getText();
		return Byte.parseByte(msgType);
	}

	public static void addSequenceNo(Document doc, Integer sequenceNo) {
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);
		locateElement.addElement(RFANodeconstant.SEQUENCE_NO_NODE).addText(String.valueOf(sequenceNo));
	}

	public static int getSequenceNo(Document doc) {
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);
		String sequenceNo = locateElement.element(RFANodeconstant.SEQUENCE_NO_NODE).getText();
		return Integer.parseInt(sequenceNo);
	}

	public static void addErrorCode(Document doc, Integer errorCode) {
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);
		locateElement.addElement(RFANodeconstant.ERROR_CODE_NODE).addText(String.valueOf(errorCode));
	}

	public static int getErrorCode(Document doc) {
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);
		String errorCode = locateElement.element(RFANodeconstant.RESPONSE_ERROR_NODE).getText();
		return Integer.parseInt(errorCode);
	}

	public static void addLocateInfo(Document doc, Byte msgType, Integer sequenceNo, Integer errorCode) {
		addMsgType(doc, msgType);
		addSequenceNo(doc, sequenceNo);
		addErrorCode(doc, errorCode);
	}

	public static Document convertDocument(String doc) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(doc);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}

//	public static Document generateStatusResp(String state,byte streamState, byte dataState, byte msgType) {
//		String streamingState = OMMState.Stream.toString(streamState);
//		String dataingState = OMMState.Data.toString(dataState);
//
//		DocumentFactory factory = DocumentFactory.getInstance();
//		Document responseMsg = factory.createDocument();
//		Element rootElement = responseMsg.addElement(RFANodeconstant.RESPONSE_ROOT_NODE);
//		Element locateElement = rootElement.addElement(RFANodeconstant.LOCATE_NODE);
//		locateElement.addElement(RFANodeconstant.ALL_STATE_NODE).addText(state);
//		locateElement.addElement(RFANodeconstant.STREAM_STATE_NODE).addText(streamingState);
//		locateElement.addElement(RFANodeconstant.DATA_STATE_NODE).addText(dataingState);
//		addLocateInfo(responseMsg, msgType, SystemConstant.sequenceNo.getAndIncrement(), 0);
//		return responseMsg;
//	}

	public static String getAllState(Document doc) {
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);

//		String streamingState = locateElement.element(RFANodeconstant.STREAM_STATE_NODE).getText();
//		String dataingState = locateElement.element(RFANodeconstant.DATA_STATE_NODE).getText();
		String state = locateElement.element(RFANodeconstant.ALL_STATE_NODE).getText();
		return state;
	}
	
	public static String getState(Document doc) {
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);

//		String streamingState = locateElement.element(RFANodeconstant.STREAM_STATE_NODE).getText();
//		String dataingState = locateElement.element(RFANodeconstant.DATA_STATE_NODE).getText();
		String state = locateElement.element(RFANodeconstant.STATE_NODE).getText();
		return state;
	}

	public static Document createHearBeat() {
		DocumentFactory documentFactory = DocumentFactory.getInstance();
		Document reponseDoc = documentFactory.createDocument();
		Element rmds = reponseDoc.addElement(RFANodeconstant.RESPONSE_ROOT_NODE);
		rmds.addElement(RFANodeconstant.LOCATE_NODE);
		Element response = rmds.addElement(RFANodeconstant.RESPONSE_RESPONSE_NODE);
		Element error = response.addElement(RFANodeconstant.RESPONSE_ERROR_NODE);
//		int errorCode = GateWayResponseTypes.CHANNEL_IDLE_TIMEOUT;
//		String descriptioin = LocateResponseEnum.getResultDescription(errorCode);
//		error.addElement(RFANodeconstant.RESPONSE_ERROR_CODE_NODE).addText(String.valueOf(errorCode));
//		error.addElement(RFANodeconstant.RESPONSE_ERROR_DESC_NODE).addText(String.valueOf(descriptioin));
		return reponseDoc;
	}

	public static void addStartHandleTime(Document doc, long startTime) {
		startTime = NetTimeUtil.getCurrentNetTime();
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);
		Element startTimeElement = locateElement.element(RFANodeconstant.START_HANDLE_TIME_NODE);
		if (startTimeElement == null) {
			locateElement.addElement(RFANodeconstant.START_HANDLE_TIME_NODE).addText(String.valueOf(startTime));
		} else {
			startTimeElement.setText(String.valueOf(startTime));
		}
	}

	public static long getStartHandleTime(Document doc) {
		Element rootElement = doc.getRootElement();
		Element locateElement = rootElement.element(RFANodeconstant.LOCATE_NODE);
		String startHandleTime = locateElement.element(RFANodeconstant.START_HANDLE_TIME_NODE).getText();
		return Long.parseLong(startHandleTime);
	}
	
	public static List<String> pickupClientReqItem(Document req){
		Element rmds = req.getRootElement();
		Element requestElement = rmds.element(RFANodeconstant.SELECT_REQUEST_NODE);
		List<Element> itemNodes = requestElement.elements(); 
		List<String> itemNameList = new ArrayList<String>();
		for(Element item: itemNodes){
			itemNameList.add(item.element(RFANodeconstant.SELECT_SINGLE_NAME).getText());
		}
		return itemNameList;
	}
}
