package com.locate.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
/*
 * ������Ҫ���ϵͳ�л�������.
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
	//����channelId��channel��ӳ���ϵ.
	public static ChannelGroup allChannelGroup = new DefaultChannelGroup("allChannels"); 
	//���ĵ�itemName�붩�ĸ�itemName�����пͻ��Ķ�Ӧ��ϵ.
	public static Map<String ,ChannelGroup> itemNameChannelMap = new HashMap<String,ChannelGroup>();
	//���ĵĲ�ƷitemName�붩�ĸò�Ʒ����Ϣ��������ӳ���ϵ.
//	public static Map<String,ItemManager> subscribeItemManagerMap = new HashMap<String,ItemManager>();
//	public static Map<String, Map<Short, FieldValue>> filedValueMap = new HashMap<String, Map<Short, FieldValue>>();
}
