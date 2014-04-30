package com.locate.gate.model;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.locate.common.RFAExceptionTypes;
import com.locate.common.RFAMessageTypes;
import com.locate.common.RFAExceptionTypes.RFAUserAuthentication;
import com.locate.rmds.util.RFANodeconstant;

public class RFAUserResponse extends LocateMessage{
	
	static final String LOGIN_SUCCESSFUL = "0";
	static final String LOGIN_FAILED = "1";
	
	public static Document createAuthenResponse(RFAUserAuthentication userAuthentication){
		DocumentFactory factory = DocumentFactory.getInstance();
		Document doc = factory.createDocument();
		Element rmds = doc.addElement(RFANodeconstant.RESPONSE_ROOT_NODE);
		Element response = rmds.addElement(RFANodeconstant.RESPONSE_RESPONSE_NODE);
		Element login = response.addElement(RFANodeconstant.RESPONSE_LOGIN_NODE);
		if(userAuthentication == null){
			login.addElement(RFANodeconstant.RESPONSE_LOGIN_RESULT_NODE).addText(LOGIN_SUCCESSFUL);
			login.addElement(RFANodeconstant.RESPONSE_LOGIN_DESC_NODE).addText("You passed authentication");
		}else{
			login.addElement(RFANodeconstant.RESPONSE_LOGIN_RESULT_NODE).addText(LOGIN_FAILED);
			login.addElement(RFANodeconstant.RESPONSE_LOGIN_DESC_NODE).addText(userAuthentication.getException());
		}
		return doc;
	}
	
	public static Document createErrorDocument(int errorCode,String descriptioin){
		DocumentFactory factory = DocumentFactory.getInstance();
		Document doc = factory.createDocument();
		Element rmds = doc.addElement(RFANodeconstant.RESPONSE_ROOT_NODE);
		Element response = rmds.addElement(RFANodeconstant.RESPONSE_RESPONSE_NODE);
		Element error = response.addElement(RFANodeconstant.RESPONSE_ERROR_NODE);
		error.addElement(RFANodeconstant.RESPONSE_ERROR_CODE_NODE).addText(String.valueOf(errorCode));
		error.addElement(RFANodeconstant.RESPONSE_ERROR_DESC_NODE).addText(String.valueOf(descriptioin));
		return doc;
	}
}
