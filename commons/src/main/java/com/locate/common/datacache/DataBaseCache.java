package com.locate.common.datacache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.utils.NetTimeUtil;
/*
 * 该类主要存放系统中缓存数据.
 */
public class DataBaseCache {
	static Logger logger = LoggerFactory.getLogger(DataBaseCache.class);
	public static final long NET_SUB_LOCAL_TIME = NetTimeUtil.getNetTime() - System.currentTimeMillis();
	public static Map<String, String> _userConnection = new HashMap<String, String>();
	// Client User Name+itemName -- IoSession
	public static Map<String, Integer> _clientRequestSession = new HashMap<String, Integer>();
	// News's itemName -- ( client IP -- IoSession)
	public static Map<String, Map<String, Integer>> _clientNewsRequest = new HashMap<String, Map<String, Integer>>();
	// Client User Name -- Client User Name + ItemName
	public static Map<String, List<String>> _clientRequestItemName = new HashMap<String, List<String>>();
	public static Map<String,Byte> _clientResponseType = new HashMap<String,Byte>();
	
}
