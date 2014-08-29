package com.locate.common;

import java.util.HashMap;
import java.util.Map;

import com.locate.rmds.processer.face.IProcesser;

public class RmdsDataCache {
	public static Map<String, IProcesser> CLIENT_ITEMMANAGER_MAP = new HashMap<String, IProcesser>();//
	/**
	 * 订阅的产品itemName与订阅该产品的消息处理器的映射关系.
	 */
	public static Map<String,IProcesser> RIC_ITEMMANAGER_Map = new HashMap<String,IProcesser>();
}
