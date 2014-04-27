package com.locate.rmds.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.locate.gate.GateWayServer;
import com.locate.gate.GatewayServerHandler;
import com.locate.rmds.ItemManager;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.util.RFAExceptionTypes;
import com.locate.rmds.util.RFAMessageTypes;
import com.locate.rmds.util.RFANodeconstant;

/**
 * 该类主要被netty框架开发的gateway调用
 * 客户发送过来的请求通过该类处理后转发给RFA.
 * @author cloud wei
 *
 */
public class ClientHandle {
	
	static Logger _logger = Logger.getLogger(ClientHandle.class.getName());
	private byte _msgType;
	QSConsumerProxy _mainApp;
	String clientIP ;
	private ClientUserLogin clientUserLogin = null;
	
//	private Map<String,String> userConnection = new HashMap();
	
	public ClientHandle(QSConsumerProxy app,String clientIP,byte msgType){
		this._mainApp = app;
		this.clientIP = clientIP;
		this._msgType = msgType;
		clientUserLogin= new ClientUserLogin(clientIP);
	}
	
	public int process(Document request,String clientName,int channelID){
		int error =-1;
		byte responseMsgType = -1;
		Document responseData = null;
//		clientIP = ((InetSocketAddress)channel.getRemoteAddress()).getAddress().getHostAddress();
		switch( _msgType){
		    case RFAMessageTypes.LOGIN : 
		    	responseMsgType = RFAMessageTypes.RESPONSE_LOGIN;
		    	responseData = clientUserLogin.authUserLogin(request,clientIP);
//		    	RFAResponse.sentResponseMsg(responseMsgType, responseData, channel);
		    	break;
		    case RFAMessageTypes.STOCK_REQUEST:
		    	processRequest(request,clientName,RFAMessageTypes.RESPONSE_STOCK,channelID);
		    	break;
//		    case RFAMessageTypes.STOCK_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_STOCK_LINK);
//		    	break;
//
//		    case RFAMessageTypes.CURRENCY_REQUEST:
//		    	processRequest(request,clientName,RFAMessageTypes.RESPONSE_CURRENCY);
//		    	break;
//		    case RFAMessageTypes.CURRENCY_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_CURRENCY_LINK);
//		    	break;
//		    	
//		    case RFAMessageTypes.OPTION_REQUEST:
//		    	processRequest(request,clientName,RFAMessageTypes.RESPONSE_OPTION);
//		    	break;
//		    case RFAMessageTypes.OPTION_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_OPTION_LINK);
//		    	break;
//		    case RFAMessageTypes.FUTURE_REQUEST:
//		    	processRequest(request,clientName,RFAMessageTypes.RESPONSE_FUTURE);
//		    	break;
//		    case RFAMessageTypes.FUTURE_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_FUTURE_LINK);
//		    	break;
//		    case RFAMessageTypes.INDEX_REQUEST:
//		    	processRequest(request,clientName,RFAMessageTypes.RESPONSE_INDEX);
//		    	break;
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
		return error;
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
	
//	private void processOneTimesRequest(Document req,String clientName,byte responseMsgType){
//		List<String> itemNames = pickupClientReqItem(req);
//		if(!checkRequestItem(responseMsgType,clientName,itemNames))
//			return;
//		_logger.info("Begin register client request "+clientName);
//		for(String itemName : itemNames){
//			RFASocketServer._clientResponseType.put(itemName, responseMsgType);
//			_logger.info("Register client request item "+itemName);
//			RFASocketServer._clientRequestSession.put(clientName+itemName, channel);
//			if(regiestItemNameForClient(itemName,clientName)){
//				ItemManager clientInstance = _mainApp.itemRequests(itemName,responseMsgType);
//				regiestItemRequestManager(itemName,clientInstance);
//			}
//			regiestClientRequestItem(clientName,itemName);
//		}
//		_logger.info("End register client request "+clientName);
//		
//		
////		List<String> itemNames = pickupClientReqItem(req);
////		_logger.info("Begin register client request "+clientName);
////		for(String itemName : itemNames){
////			_logger.info("Register client request item "+itemName);
////			RFASocketServer._clientRequestSession.put(clientName+itemName, _session);
////			_mainApp.oneTimesItemRequests(itemName,clientName,responseMsgType);
////			regiestClientRequestItem(clientName,itemName);
////		}
////		_logger.info("End register client request "+clientName);
//	}
	
	private int processRequest(Document req,String clientName,byte responseMsgType,int channelId){
		int errorCode = -1;
		List<String> itemNames = pickupClientReqItem(req);
		if(!checkRequestItem(responseMsgType,clientName,itemNames))
			return errorCode=RFAExceptionTypes.USER_BUSINESS_NUMBER_OUT;
		_logger.info("Begin register client request "+clientName);
		for(String itemName : itemNames){
			GateWayServer._clientResponseType.put(itemName, responseMsgType);
			_logger.info("Register client request item "+itemName);
			GateWayServer._clientRequestSession.put(clientName+itemName, channelId);
			if(regiestItemNameForClient(itemName,clientName)){
				ItemManager clientInstance = _mainApp.itemRequests(itemName,responseMsgType, channelId);
				regiestItemRequestManager(itemName,clientInstance);
			}
			regiestClientRequestItem(clientName,itemName);
		}
		_logger.info("End register client request "+clientName);
		return errorCode;
	}
	
	private boolean regiestItemNameForClient(String itemName,String clientName){
		List<String> clientNameList = GateWayServer._requestItemNameList.get(itemName);
		if(clientNameList != null){
			if(! clientNameList.contains(clientName)){
				clientNameList.add(clientName);
			}else{
				return false;
			}
		}else{
			clientNameList = new ArrayList();
			clientNameList.add(clientName);
		}
		GateWayServer._requestItemNameList.put(itemName,clientNameList);
		return true;
	}
	
	private void regiestClientRequestItem(String clientName,String itemName){
		List<String> clientRequestItem;
		clientRequestItem = GateWayServer._clientRequestItemName.get(clientName);
		if(clientRequestItem == null){
			clientRequestItem =  new ArrayList();
		}
		clientRequestItem.add(clientName+itemName);
		GateWayServer._clientRequestItemName.put(clientName, clientRequestItem);
	}
	
//	private void regiestItemRequestManager(String itemName,ItemManager instance){
//		List<ItemManager> itemRequestManagerList;
//		itemRequestManagerList = RFASocketServer._clientRequestItemManager.get(clientIP);
//		if(itemRequestManagerList == null){
//			itemRequestManagerList = new ArrayList();
//		}
//		itemRequestManagerList.add(instance);
//	}
	
	private void regiestItemRequestManager(String itemName,ItemManager instance){
		GateWayServer._clientRequestItemManager.put(itemName,instance);
//		ItemManager itemRequestManager = RFASocketServer._clientRequestItemManager.get(itemName);
//		if(itemRequestManagerList == null){
//			itemRequestManagerList = new ArrayList();
//		}
//		itemRequestManagerList.add(instance);
	}
	
//	private void regiestNewsRequestManager(String itemName,NewsComposeItemManager instance){
//		RFASocketServer._clientRequestNewsItemManager.put(itemName,instance);
//	}
	
	private List<String> pickupClientReqItem(Document req){
		Element rmds = req.getRootElement();
		Element requestElement = rmds.element(RFANodeconstant.SELECT_REQUEST_NODE);
		List<Element> itemNodes = requestElement.elements(); 
		List<String> itemNameList = new ArrayList<String>();
		for(Element item: itemNodes){
			itemNameList.add(item.element(RFANodeconstant.SELECT_SINGLE_NAME).getText());
		}
		return itemNameList;
	}
	
	private boolean checkRequestItem(byte msgType,String userName,List<String> itemNames){
		String businessName = RFAMessageTypes.RFAMessageName.getRFAMessageName(msgType).toUpperCase();
		if(!RFAUserManagement.checkMaxBusinessValiable(userName,businessName,itemNames)){
//			RFAUserResponse.sentWrongMsgResponse(RFAExceptionTypes.USER_BUSINESS_NUMBER_OUT, channel);
	        _logger.error("Client request's business number over.");
	        return false;
		}
		return true;
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
