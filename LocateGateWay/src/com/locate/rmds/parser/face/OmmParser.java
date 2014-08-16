package com.locate.rmds.parser.face;

import org.dom4j.Document;

import com.reuters.rfa.omm.OMMMsg;

public interface OmmParser {
	public Document parse(OMMMsg msg,String itemName);
}
