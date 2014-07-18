package com.locate.rmds.handler;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

import com.locate.common.DataBaseCache;
import com.locate.common.XmlMessageUtil;
import com.locate.rmds.handler.inter.IRequestHandler;
import com.locate.rmds.processer.ItemManager;

public abstract class BaseRequestHandler implements IRequestHandler {
	@Override
	public abstract int processRequest(Document req, String clientName, byte responseMsgType, int channelId);
	
	public List<String> pickupClientReqItem(Document req){
		return XmlMessageUtil.pickupClientReqItem(req);
	}
	
	/**
	 * 这个方法很可能被取消掉.
	 * 因为该产品已经被订阅过了,无需订阅.
	 * 后面有代码专门处理订阅产品和处理器的对应关系.
	 * @param itemName
	 * @param instance
	 */
	@Deprecated
	public void regiestItemRequestManager(String itemName,ItemManager instance){
		DataBaseCache.CLIENT_ITEMMANAGER_MAP.put(itemName,instance);
//		ItemManager itemRequestManager = RFASocketServer._clientRequestItemManager.get(itemName);
//		if(itemRequestManagerList == null){
//			itemRequestManagerList = new ArrayList();
//		}
//		itemRequestManagerList.add(instance);
	}
	
	/**
	 * 所有用户使用的同一个用户名.
	 * 这个方法没有用.很可能被取消掉.
	 * @param clientName
	 * @param itemName
	 */
	@Deprecated
	public void regiestClientRequestItem(String clientName,String itemName){
		List<String> clientRequestItem;
		clientRequestItem = DataBaseCache._clientRequestItemName.get(clientName);
		if(clientRequestItem == null){
			clientRequestItem =  new ArrayList();
		}
		clientRequestItem.add(clientName+itemName);
		DataBaseCache._clientRequestItemName.put(clientName, clientRequestItem);
	}
}
