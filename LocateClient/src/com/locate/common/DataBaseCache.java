package com.locate.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
/*
 * 该类主要存放系统中缓存数据.
 */
public class DataBaseCache {
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
//	public static Map<String, ItemManager> _clientRequestItemManager = new HashMap();//

	public static Map<String,Byte> _clientResponseType = new HashMap();
	
	//add by Cloud Wei
	//管理channelId和channel的映射关系.
	public static ChannelGroup allChannelGroup = new DefaultChannelGroup("allChannels"); 
	//订阅的itemName与订阅该itemName的所有客户的对应关系.
	public static Map<String ,ChannelGroup> itemNameChannelMap = new HashMap<String,ChannelGroup>();
	//订阅的产品itemName与订阅该产品的消息处理器的映射关系.
//	public static Map<String,ItemManager> subscribeItemManagerMap = new HashMap<String,ItemManager>();
//	public static Map<String, Map<Short, FieldValue>> filedValueMap = new HashMap<String, Map<Short, FieldValue>>();
}
