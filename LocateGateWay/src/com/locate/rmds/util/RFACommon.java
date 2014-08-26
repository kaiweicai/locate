package com.locate.rmds.util;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
//import org.apache.mina.core.session.IoSession;

import com.locate.common.RFANodeconstant;

@Deprecated
public class RFACommon {

//	public static String getIP(MessageEvent e) {
//		String clientIP = ((InetSocketAddress) e.getRemoteAddress()).getAddress().getHostAddress();
//		return clientIP;
//	}

	public static void addNewsCode(List<String> subscribeCodes, Document news) {
		Element response = news.getRootElement().element(RFANodeconstant.RESPONSE_RESPONSE_NODE);
		Element item = response.element(RFANodeconstant.SELECT_SINGLE_ITEM);
		response.remove(item);
		Element itemElement = null;
		for (String itemName : subscribeCodes) {
			itemElement = response.addElement(RFANodeconstant.SELECT_SINGLE_ITEM);
			itemElement.addElement(RFANodeconstant.SELECT_SINGLE_NAME).addText(itemName);
		}

	}
}
