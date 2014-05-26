package com.locate.common;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.locate.rmds.util.RFANodeconstant;

public class Dom4jUtil {
	private static DocumentFactory factory = new DocumentFactory();

	public static void addMsgType(Document doc, Byte msgType) {
		Element rootElement = doc.getRootElement();
		rootElement.addElement(RFANodeconstant.MSG_TYPE_NODE).addText(String.valueOf(msgType));
	}

	public static byte getMsgType(Document doc) {
		Element rootElement = doc.getRootElement();
		String msgType = rootElement.element(RFANodeconstant.MSG_TYPE_NODE).getText();
		return Byte.parseByte(msgType);
	}

	public static void addSequenceNo(Document doc, Integer sequenceNo) {
		Element rootElement = doc.getRootElement();
		rootElement.addElement(RFANodeconstant.SEQUENCE_NO_NODE).addText(String.valueOf(sequenceNo));
	}

	public static int getSequenceNo(Document doc) {
		Element rootElement = doc.getRootElement();
		String sequenceNo = rootElement.element(RFANodeconstant.SEQUENCE_NO_NODE).getText();
		return Integer.parseInt(sequenceNo);
	}

	public static void addErrorCode(Document doc, Integer errorCode) {
		Element rootElement = doc.getRootElement();
		rootElement.addElement(RFANodeconstant.ERROR_CODE_NODE).addText(String.valueOf(errorCode));
	}

	public static int getErrorCode(Document doc) {
		Element rootElement = doc.getRootElement();
		String errorCode = rootElement.element(RFANodeconstant.RESPONSE_ERROR_NODE).getText();
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
}
