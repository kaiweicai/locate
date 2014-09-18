package com.locate.common.datacache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

public class GateChannelCache {
	//add by Cloud Wei
	// 管理channelId和channel的映射关系.
	public static ChannelGroup allChannelGroup = new DefaultChannelGroup("allChannels");
	// 存放所有web连接的channel.暂时没有太大作用.
	public static ChannelGroup webSocketGroup = new DefaultChannelGroup();
	// 订阅的itemName与订阅该itemName的所有客户的对应关系.
	public static Map<String, ChannelGroup> itemNameChannelMap = new HashMap<String, ChannelGroup>();
	// 一个商品的所有衍生品存放的map.
	public static Map<String, List<String>> derivedChannelGroupMap = new HashMap<String, List<String>>();
	//增加web用户订阅的itemName与订阅该itemName的web客户的对应关系.
//	public static Map<String ,ChannelGroup> webItemChannelMap = new HashMap<String,ChannelGroup>();
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
