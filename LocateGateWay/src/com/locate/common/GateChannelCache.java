package com.locate.common;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

public class GateChannelCache {
	//add by Cloud Wei
		//管理channelId和channel的映射关系.
		public static ChannelGroup allChannelGroup = new DefaultChannelGroup("allChannels");
		//存放所有web连接的channel.暂时没有太大作用.
		public static ChannelGroup webSocketGroup = new DefaultChannelGroup();
		//订阅的itemName与订阅该itemName的所有客户的对应关系.
		public static Map<String ,ChannelGroup> itemNameChannelMap = new HashMap<String,ChannelGroup>();
//		//增加web用户订阅的itemName与订阅该itemName的web客户的对应关系.
//		public static Map<String ,ChannelGroup> webItemChannelMap = new HashMap<String,ChannelGroup>();
}
