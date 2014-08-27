package com.locate.common;

import java.util.HashMap;
import java.util.Map;

import com.locate.rmds.gui.viewer.FieldValue;
import com.locate.rmds.processer.ItemManager;

public class RmdsDataCache {
	public static Map<String, ItemManager> CLIENT_ITEMMANAGER_MAP = new HashMap<String, ItemManager>();//
	/**
	 * 订阅的产品itemName与订阅该产品的消息处理器的映射关系.
	 */
	public static Map<String,ItemManager> RIC_ITEMMANAGER_Map = new HashMap<String,ItemManager>();
	public static Map<String, Map<Short, FieldValue>> filedValueMap = new HashMap<String, Map<Short, FieldValue>>();
}
