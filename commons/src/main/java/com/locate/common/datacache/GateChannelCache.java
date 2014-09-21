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
	public static Map<String, ChannelGroup> itemNameChannelMap;
	// 一个商品的所有衍生品存放的map.
	public static Map<String, List<String>> derivedChannelGroupMap;
	//channelid和channel由系统自己管理.
	public static Map<Integer,Channel> channelMap;
	//增加web用户订阅的itemName与订阅该itemName的web客户的对应关系.
//	public static Map<String ,ChannelGroup> webItemChannelMap = new HashMap<String,ChannelGroup>();
	static{
		EventExecutor executor = GlobalEventExecutor.INSTANCE;
		allChannelGroup =  new DefaultChannelGroup("allChannels",executor);
		webSocketGroup = new DefaultChannelGroup("webSocketChannels", executor);
		itemNameChannelMap = new HashMap<String, ChannelGroup>();
		derivedChannelGroupMap = new HashMap<String, List<String>>();
		channelMap = new HashMap<Integer,Channel>();
	}
	/**
	 * 检查商品和该商品的衍生品是否都已经不再需要.
	 * @param itemName 商品名称
	 * @return 是否都为空
	 */
	public static boolean isEmnpty(String itemName) {
		ChannelGroup itemGroup = itemNameChannelMap.get(itemName);
		boolean itemEnpty = false;
		if (itemGroup == null || itemGroup.isEmpty()) {
			itemEnpty = true;
		}
		boolean derivedItemEnpty = false;
		List<String> derivedChannelGroupList = derivedChannelGroupMap.get(itemName);
		for (String derivedName : derivedChannelGroupList) {
			ChannelGroup derivedChannelGroup = itemNameChannelMap.get(derivedName);
			if (derivedChannelGroup == null || derivedChannelGroup.isEmpty()) {
				derivedItemEnpty = true;
				break;
			}
		}
		boolean result = itemEnpty & derivedItemEnpty;
		return result;
	}
}
