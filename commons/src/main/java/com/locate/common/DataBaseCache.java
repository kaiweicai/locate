package com.locate.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.locate.common.utils.NetTimeUtil;
/*
 * 该类主要存放系统中缓存数据.
 */
public class DataBaseCache {
	static Logger logger = Logger.getLogger(DataBaseCache.class);
	public static final long checkTime = NetTimeUtil.getNetTime() - System.currentTimeMillis();
	public static Map<String, String> _userConnection = new HashMap();
	// Client User Name+itemName -- IoSession
	public static Map<String, Integer> _clientRequestSession = new HashMap();
	// News's itemName -- ( client IP -- IoSession)
	public static Map<String, Map<String, Integer>> _clientNewsRequest = new HashMap();
	// Client User Name -- Client User Name + ItemName
	public static Map<String, List<String>> _clientRequestItemName = new HashMap();
	// Item name-- Client User Name
//	public static Map<String, List<String>> _requestItemNameList = new HashMap();
	// public static Map<String,List<ItemManager>> _clientRequestItemManager =
	// new HashMap();//
	
//	public static Map<String, ItemManager> CLIENT_ITEMMANAGER_MAP = new HashMap<String, ItemManager>();//

	public static Map<String,Byte> _clientResponseType = new HashMap<String,Byte>();
	
//	//增加web用户订阅的itemName与订阅该itemName的web客户的对应关系.
//	public static Map<String ,ChannelGroup> webItemChannelMap = new HashMap<String,ChannelGroup>();
	/**
	 * 订阅的产品itemName与订阅该产品的消息处理器的映射关系.
	 */
//	public static Map<String,ItemManager> RIC_ITEMMANAGER_Map = new HashMap<String,ItemManager>();
//	public static Map<String, Map<Short, FieldValue>> filedValueMap = new HashMap<String, Map<Short, FieldValue>>();
}
