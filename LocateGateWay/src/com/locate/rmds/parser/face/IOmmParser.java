package com.locate.rmds.parser.face;

import java.util.HashMap;
import java.util.Map;

import com.locate.gate.model.LocateUnionMessage;
import com.locate.rmds.gui.viewer.FieldValue;
import com.reuters.rfa.omm.OMMMsg;

public interface IOmmParser {
	public static Map<String, Map<Short, FieldValue>> ITEM_FIELD_MAP = new HashMap<String, Map<Short, FieldValue>>();

	public LocateUnionMessage parse(OMMMsg msg, String itemName);

	public void handelLocateState(OMMMsg respMsg, LocateUnionMessage lcateMessage);

}
