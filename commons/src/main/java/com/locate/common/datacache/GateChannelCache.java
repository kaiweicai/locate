package com.locate.common.datacache;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GateChannelCache {
	//add by Cloud Wei
	// 管理channelId和channel的映射关系.
	public static ChannelGroup allChannelGroup; 
	// 存放所有web连接的channel.暂时没有太大作用.
	public static ChannelGroup webSocketGroup;
	// 订阅的itemName与订阅该itemName的所有客户的对应关系.
	public static Map<String, ChannelGroup> itemNameChannelGroupMap;
	// 一个商品的所有衍生品存放的map.
	public static Map<String, List<String>> item2derivedMap;
	//channelid和channel由系统自己管理.
	public static Map<Integer,Channel> id2ChannelMap;
	//增加web用户订阅的itemName与订阅该itemName的web客户的对应关系.
//	public static Map<String ,ChannelGroup> webItemChannelMap = new HashMap<String,ChannelGroup>();
	static{
		EventExecutor executor = GlobalEventExecutor.INSTANCE;
		allChannelGroup =  new DefaultChannelGroup("allChannels",executor);
		webSocketGroup = new DefaultChannelGroup("webSocketChannels", executor);
		itemNameChannelGroupMap = new HashMap<String, ChannelGroup>();
		item2derivedMap = new HashMap<String, List<String>>();
		id2ChannelMap = new HashMap<Integer,Channel>();
	}
	/**
	 * 检查商品和该商品的衍生品是否都已经不再需要.既要检查原生的该商品,也要检查衍生的商品.
	 * @param itemName 原生商品名称
	 * @return 是否都为空
	 */
	public static boolean isEmnpty(String itemName) {
		ChannelGroup itemGroup = itemNameChannelGroupMap.get(itemName);
		boolean itemEnpty = false;
		if (itemGroup == null || itemGroup.isEmpty()) {
			itemEnpty = true;
		}
		boolean derivedItemEnpty = true;
		List<String> derivedItemList = item2derivedMap.get(itemName);
		if(derivedItemList!=null){
			for (String derivedName : derivedItemList) {
				ChannelGroup derivedChannelGroup = itemNameChannelGroupMap.get(derivedName);
				if (derivedChannelGroup != null && !derivedChannelGroup.isEmpty()) {
					derivedItemEnpty = false;
					break;
				}
			}
		}
		boolean result = itemEnpty & derivedItemEnpty;
		return result;
	}
}
