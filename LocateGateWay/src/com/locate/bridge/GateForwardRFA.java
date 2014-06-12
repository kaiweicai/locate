package com.locate.bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.locate.common.DataBaseCache;
import com.locate.common.GateWayExceptionTypes;
import com.locate.common.GateWayMessageTypes;
import com.locate.common.RFANodeconstant;
import com.locate.common.GateWayExceptionTypes.RFAExceptionEnum;
import com.locate.common.XmlMessageUtil;
import com.locate.gate.hanlder.GatewayServerHandler;
import com.locate.gate.model.ClientInfo;
import com.locate.gate.model.LocateMessage;
import com.locate.gate.model.RFAUserResponse;
import com.locate.gate.server.GateWayServer;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.RFAServerManager;
import com.locate.rmds.client.ClientUserLogin;
import com.locate.rmds.client.RFAUserManagement;
import com.locate.rmds.handler.inter.IRequestHandler;
import com.locate.rmds.processer.ItemManager;
import com.locate.rmds.processer.RFALoginClient;
import com.reuters.rfa.omm.OMMMsg.MsgType;

/**
 * 所有gateway系统向RFA系统发送的的请求必须通过该类代为发送.
 * 起到沟通gateway和RFA沟通的桥梁作用.
 * @author cloud wei
 *
 */
@Service
public class GateForwardRFA {
	
	static Logger _logger = Logger.getLogger(GateForwardRFA.class.getName());
	@Resource
	QSConsumerProxy mainApp;
	@Resource(name="futhureRequestHandler")
	private IRequestHandler requestHandler;
	
	public int process(ClientInfo clientInfo){
		long startTime = System.currentTimeMillis();
		int channelID = clientInfo.getChannelID();
		Document request =clientInfo.getUserRquest();
		String clientName=clientInfo.getUserName();
		String clientIP = clientInfo.getClientIP();
		byte _msgType=clientInfo.getMsgType();
		ClientUserLogin clientUserLogin = new ClientUserLogin(clientIP);
		
		int resultCode =-1;
		byte responseMsgType = -1;
		Document responseData = null;
		
		if(_msgType != GateWayMessageTypes.LOGIN){
	    	String userName = DataBaseCache._userConnection.get(clientIP);
	    	if(userName == null){
	    		resultCode = GateWayExceptionTypes.USER_NOT_LOGIN;
				Document wrongMsg = XmlMessageUtil.createErrorDocument(resultCode,
						GateWayExceptionTypes.RFAExceptionEnum.getExceptionDescription(resultCode));
				GateWayResponser.sentNotiFyResponseMsg(GateWayMessageTypes.RESPONSE_LOGIN, wrongMsg, channelID,resultCode);
				_logger.error("Client didn't login system. sent error message to client");
				return -1;
	    	}
	    }
		
		switch( _msgType){
		    case GateWayMessageTypes.LOGIN : 
		    	responseMsgType = GateWayMessageTypes.RESPONSE_LOGIN;
		    	responseData = clientUserLogin.authUserLogin(request,clientIP);
		    	XmlMessageUtil.addStartHandleTime(responseData, startTime);
		    	GateWayResponser.sentResponseMsg(GateWayMessageTypes.RESPONSE_LOGIN, responseData, channelID);
		    	return 0;
		    case GateWayMessageTypes.UNREGISTER_REQUEST:
		    	responseMsgType = GateWayMessageTypes.RESPONSE_UNREGISTER;
		    	resultCode = requestHandler.processRequest(request,clientName,responseMsgType,channelID);
		    	break;
		    case GateWayMessageTypes.STOCK_REQUEST:
		    	responseMsgType = GateWayMessageTypes.RESPONSE_STOCK;
		    	resultCode = requestHandler.processRequest(request,clientName,responseMsgType,channelID);
		    	break;
		    case GateWayMessageTypes.STOCK_LINK_REQUEST:
		    	resultCode = requestHandler.processOneTimesRequest(request,clientName,GateWayMessageTypes.RESPONSE_STOCK_LINK,channelID);
		    	break;
//
		    case GateWayMessageTypes.CURRENCY_REQUEST:
		    	responseMsgType = GateWayMessageTypes.RESPONSE_CURRENCY;
		    	resultCode=requestHandler.processRequest(request,clientName,responseMsgType,channelID);
		    	break;
//		    case RFAMessageTypes.CURRENCY_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_CURRENCY_LINK);
//		    	break;
//		    	
		    case GateWayMessageTypes.OPTION_REQUEST:
		    	requestHandler.processRequest(request,clientName,GateWayMessageTypes.RESPONSE_OPTION,channelID);
		    	break;
//		    case RFAMessageTypes.OPTION_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_OPTION_LINK);
//		    	break;
		    case GateWayMessageTypes.FUTURE_REQUEST:
		    	responseMsgType = GateWayMessageTypes.RESPONSE_FUTURE;
		    	resultCode = requestHandler.processRequest(request,clientName,responseMsgType,channelID);
		    	break;
//		    case RFAMessageTypes.FUTURE_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_FUTURE_LINK);
//		    	break;
		    case GateWayMessageTypes.INDEX_REQUEST:
		    	resultCode = requestHandler.processRequest(request,clientName,GateWayMessageTypes.RESPONSE_INDEX,channelID);
		    	break;
//		    case RFAMessageTypes.INDEX_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_INDEX_LINK);
//		    	break;
//		    case RFAMessageTypes.NEWS_REQUEST:
//		    	processNewsRequest(request,clientName);
//		    	break;
//
//		    case RFAMessageTypes.NEWS_COMPOSE_REQUEST:
//		    	processNewsComponseRequest(request,clientName,RFAMessageTypes.RESPONSE_NEWS_COMPOSE);
//		    	break;
//		    case RFAMessageTypes.ONE_TIMES_REQUEST:
////		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_ONE_TIMES);
//		    	processRequest(request,clientName,RFAMessageTypes.RESPONSE_ONE_TIMES);
//		    	break;
	    }
		//resultCode大于零,表示处理存在错误需要向客户端发送错误信息.
		if(resultCode>0){
			responseData = XmlMessageUtil.createErrorDocument(resultCode,
					RFAExceptionEnum.getExceptionDescription(resultCode));
			GateWayResponser.sentNotiFyResponseMsg(responseMsgType, responseData, channelID , resultCode);
		}
		
		return resultCode;
	}
	
//	private void processNewsComponseRequest(Document req,String clientName,byte responseMsgType){
//		List<String> itemNames = pickupClientReqItem(req);
//		if(!checkRequestItem(responseMsgType,clientName,itemNames))
//			return;
//		_logger.info("Begin register client request "+clientName);
//		for(String itemName : itemNames){
//			_logger.info("Register client request item "+itemName);
//			RFASocketServer._clientRequestSession.put(clientName+itemName, channel);
//			if(regiestItemNameForClient(itemName,clientName)){
//				NewsComposeItemManager clientInstance = _mainApp.newsComposeRequests(clientName,itemName,responseMsgType);
//				regiestItemRequestManager(itemName,null);
//				regiestNewsRequestManager(itemName,clientInstance);
//			}
//			regiestClientRequestItem(clientName,itemName);
//		}
//		_logger.info("End register client request "+clientName);
//		
//	}
	
//	private void processNewsRequest(Document req,String clientName){
//		List<String> itemNames = pickupClientReqItem(req);
//		if(! checkRequestNews(RFAMessageTypes.NEWS_REQUEST,clientName,itemNames)){
//			return ;
//		}
//		Map<String,IoSession> subItemList = null;
//		_logger.info("Begin register client request "+clientName);
//		for(String itemName : itemNames){
//			_logger.info("Register client request item "+itemName);
//			subItemList = RFASocketServer._clientNewsRequest.get(itemName);
//			if(subItemList == null){
//				subItemList = new HashMap();
//			}
//			subItemList.put(clientIP,channel);
//			RFASocketServer._clientNewsRequest.put(itemName, subItemList);
//			regiestClientRequestItem(clientName,itemName);
//		}
//		_logger.info("End register client request "+clientName);
//		
//	}
	
	
	
	
//	private boolean regiestItemNameForClient(String itemName,String clientName){
//		List<String> clientNameList = GateWayServer._requestItemNameList.get(itemName);
//		if(clientNameList != null){
//			if(! clientNameList.contains(clientName)){
//				clientNameList.add(clientName);
//			}else{
//				return false;
//			}
//		}else{
//			clientNameList = new ArrayList();
//			clientNameList.add(clientName);
//		}
//		GateWayServer._requestItemNameList.put(itemName,clientNameList);
//		return true;
//	}
	
	
//	private void regiestItemRequestManager(String itemName,ItemManager instance){
//		List<ItemManager> itemRequestManagerList;
//		itemRequestManagerList = RFASocketServer._clientRequestItemManager.get(clientIP);
//		if(itemRequestManagerList == null){
//			itemRequestManagerList = new ArrayList();
//		}
//		itemRequestManagerList.add(instance);
//	}
	
//	private void regiestNewsRequestManager(String itemName,NewsComposeItemManager instance){
//		RFASocketServer._clientRequestNewsItemManager.put(itemName,instance);
//	}
	
	private boolean checkRequestItem(byte msgType,String userName,List<String> itemNames){
		String businessName = GateWayMessageTypes.RFAMessageName.getRFAMessageName(msgType).toUpperCase();
		if(!RFAUserManagement.checkMaxBusinessValiable(userName,businessName,itemNames)){
//			RFAUserResponse.sentWrongMsgResponse(RFAExceptionTypes.USER_BUSINESS_NUMBER_OUT, channel);
	        _logger.error("Client request's business number over.");
	        return false;
		}
		return true;
	}

	public void closeHandler(String itemName) {
		ItemManager itemHandler = DataBaseCache.subscribeItemManagerMap.get(itemName);
		//取消订阅该产品
		if(itemHandler!=null){
			itemHandler.closeRequest();
		}
		DataBaseCache._clientRequestItemManager.remove(itemName);
	}
	
//	private boolean checkRequestNews(byte msgType,String userName,List<String> newsKey){
//		String businessName = RFAMessageTypes.RFAMessageName.getRFAMessageName(msgType).toUpperCase();
//		if(!RFAUserManagement.checkNewsCodeValiable(userName,businessName,newsKey)){
//			RFAUserResponse.sentWrongMsgResponse(RFAExceptionTypes.USER_BUSINESS_NO_SUBSCRIBE, channel);
//	        _logger.error("Client request's business no subscription over.");
//	        return false;
//		}
//		return true;
//	}
	
}
