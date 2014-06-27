package com.locate.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import com.locate.rmds.gui.viewer.FieldValue;
import com.locate.rmds.processer.ItemManager;
import com.locate.rmds.processer.face.IProcesser;
/*
 * ������Ҫ���ϵͳ�л�������.
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
	
	public static Map<String, ItemManager> CLIENT_ITEMMANAGER_MAP = new HashMap<String, ItemManager>();//

	public static Map<String,Byte> _clientResponseType = new HashMap<String,Byte>();
	
	//add by Cloud Wei
	//����channelId��channel��ӳ���ϵ.
	public static ChannelGroup allChannelGroup = new DefaultChannelGroup("allChannels");
	//�������web���ӵ�channel.��ʱû��̫������.
	public static ChannelGroup webSocketGroup = new DefaultChannelGroup();
	//���ĵ�itemName�붩�ĸ�itemName�����пͻ��Ķ�Ӧ��ϵ.
	public static Map<String ,ChannelGroup> itemNameChannelMap = new HashMap<String,ChannelGroup>();
//	//����web�û����ĵ�itemName�붩�ĸ�itemName��web�ͻ��Ķ�Ӧ��ϵ.
//	public static Map<String ,ChannelGroup> webItemChannelMap = new HashMap<String,ChannelGroup>();
	/**
	 * ���ĵĲ�ƷitemName�붩�ĸò�Ʒ����Ϣ��������ӳ���ϵ.
	 */
	public static Map<String,IProcesser> RIC_ITEMMANAGER_Map = new HashMap<String,IProcesser>();
	public static Map<String, Map<Short, FieldValue>> filedValueMap = new HashMap<String, Map<Short, FieldValue>>();
}
