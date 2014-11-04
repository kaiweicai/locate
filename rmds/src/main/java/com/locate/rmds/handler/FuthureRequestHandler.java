package com.locate.rmds.handler;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.locate.bridge.GateWayResponser;
import com.locate.common.constant.LocateResultCode;
import com.locate.common.datacache.DataBaseCache;
import com.locate.common.model.ClientRequest;
import com.locate.rmds.IConsumerProxy;
import com.locate.rmds.RFAServerManager;
import com.locate.rmds.processer.ItemManager;
import com.locate.rmds.processer.RFALoginClient;

/**
 * 期货类产品请求类.
 * 现在所有产品基本都是用该类进行请求.
 * 如果以后有新闻类产品,可能会使用newsRequest记性请求.
 * @author CloudWei kaiweicai@163.com
 * create time 2014年10月21日
 * @copyRight by Author
 */
@Service
public class FuthureRequestHandler extends BaseRequestHandler {
	private static Logger logger = LoggerFactory.getLogger(FuthureRequestHandler.class.getName());
	@Resource(name="qSConsumerProxy")
	private IConsumerProxy mainApp;
	@Override
	public int processRequest(ClientRequest req,String clientName,byte responseMsgType, int channelId ){
		int resultCode = LocateResultCode.SUCCESS_RESULT;
		String[] itemNames = req.getItemCode().split(",");
		//这里判断可以移到QSComsumerProxey中,让这个requester专注于处理期货.
		if(!RFAServerManager.isConnectedDataSource()){
			logger.warn("The RFA Datasource not connected.Can not register the intresting Product!");
			resultCode = LocateResultCode.SERVER_NOT_READY;
		}
		logger.info("Begin register client request "+clientName);
		for(String itemName : itemNames){
			DataBaseCache._clientResponseType.put(itemName, responseMsgType);
			logger.info("Register client request item "+itemName);
			mainApp.itemRequests(itemName, responseMsgType,channelId);
//			regiestItemRequestManager(itemName, clientInstance);
//			regiestClientRequestItem(clientName,itemName);
		}
		//用户开始订阅产品,发送回用户一个RFA的服务器状态信息.
		GateWayResponser.sentResponseMsg(RFALoginClient.rfaStateMessage,channelId);
		logger.info("End register client request "+clientName);
		return resultCode;
	}
	
	/**
	 * send snapshot request
	 * @param req
	 * @param clientName
	 * @param responseMsgType
	 * @param channel
	 */
	@Deprecated
	public int processOneTimesRequest(ClientRequest req, String clientName, byte responseMsgType, int channel) {
		String[] itemNames = req.getItemCode().split(",");
		// if(!checkRequestItem(responseMsgType,clientName,itemNames))
		// return;
		logger.info("Begin register one time client request " + clientName);
		for (String itemName : itemNames) {
			DataBaseCache._clientResponseType.put(itemName, responseMsgType);
			logger.info("Register client request item " + itemName);
			DataBaseCache._clientRequestSession.put(clientName + itemName, channel);
			mainApp.itemRequests(itemName, responseMsgType, channel);
//			regiestItemRequestManager(itemName, clientInstance);
//			regiestClientRequestItem(clientName, itemName);
		}
		logger.info("End register client request " + clientName);
		return 0;
	}
}
