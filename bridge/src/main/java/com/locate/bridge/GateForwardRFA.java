package com.locate.bridge;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.locate.common.DataBaseCache;
import com.locate.common.LocateMessageTypes;
import com.locate.common.LocateResultCode;
import com.locate.common.LocateResultCode.LocateResponseEnum;
import com.locate.common.RmdsDataCache;
import com.locate.common.model.ClientRequest;
import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.MessageEncapsulator;
import com.locate.rmds.client.ClientUserValidator;
import com.locate.rmds.handler.inter.IRequestHandler;
import com.locate.rmds.processer.face.IProcesser;

/**
 * 所有gateway系统向RFA系统发送的的请求必须通过该类代为发送.
 * 起到沟通gateway和RFA沟通的桥梁作用.
 * @author cloud wei
 *
 */
@Service
public class GateForwardRFA {
	
	static Logger _logger = Logger.getLogger(GateForwardRFA.class.getName());
	@Resource(name="futhureRequestHandler")
	private IRequestHandler requestHandler;
	@Resource
	private IProcesser itemManager;
	
	public int process(ClientRequest clientInfo){
		long startTime = System.currentTimeMillis();
		int channelID = clientInfo.getChannelID();
		ClientRequest request =clientInfo.getClientRequest();
		String clientName = clientInfo.getUserName();
		String clientIP = clientInfo.getClientIP();
		byte _msgType=clientInfo.getMsgType();
		ClientUserValidator clientUserLogin = new ClientUserValidator(clientIP);
		
		int resultCode =-1;
		byte responseMsgType = -1;
		
		if(_msgType != LocateMessageTypes.LOGIN){
	    	String userName = DataBaseCache._userConnection.get(clientIP);
	    	if(userName == null){
	    		resultCode = LocateResultCode.USER_NOT_LOGIN;
	    		LocateUnionMessage message = new LocateUnionMessage();
	    		byte msgType = LocateMessageTypes.STATUS_RESP;
	    		MessageEncapsulator.encapLogionResponseMessage(message,resultCode, msgType);
				GateWayResponser.sentResponseMsg(message, channelID);
				_logger.error("Client didn't login system. sent error message to client");
				return resultCode;
	    	}
	    }
		
		switch( _msgType){
		    case LocateMessageTypes.LOGIN : 
		    	resultCode = clientUserLogin.authUserLogin(request,clientIP);
		    	LocateUnionMessage message = new LocateUnionMessage();
				byte msgType = LocateMessageTypes.STATUS_RESP;
				MessageEncapsulator.encapLogionResponseMessage(message,resultCode, msgType);
		    	GateWayResponser.sentResponseMsg( message, channelID);
		    	return resultCode;
		    case LocateMessageTypes.UNREGISTER_REQUEST:
		    	responseMsgType = LocateMessageTypes.RESPONSE_UNREGISTER;
		    	resultCode = requestHandler.processRequest(request,clientName,responseMsgType,channelID);
		    	break;
		    case LocateMessageTypes.STOCK_REQUEST:
		    	responseMsgType = LocateMessageTypes.RESPONSE_STOCK;
		    	resultCode = requestHandler.processRequest(request,clientName,responseMsgType,channelID);
		    	break;
		    case LocateMessageTypes.STOCK_LINK_REQUEST:
		    	resultCode = requestHandler.processOneTimesRequest(request,clientName,LocateMessageTypes.RESPONSE_STOCK_LINK,channelID);
		    	break;
//
		    case LocateMessageTypes.CURRENCY_REQUEST:
		    	responseMsgType = LocateMessageTypes.RESPONSE_CURRENCY;
		    	resultCode=requestHandler.processRequest(request,clientName,responseMsgType,channelID);
		    	break;
//		    case RFAMessageTypes.CURRENCY_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_CURRENCY_LINK);
//		    	break;
//		    	
		    case LocateMessageTypes.OPTION_REQUEST:
		    	responseMsgType = LocateMessageTypes.RESPONSE_OPTION;
		    	resultCode=requestHandler.processRequest(request,clientName,responseMsgType,channelID);
		    	break;
//		    case RFAMessageTypes.OPTION_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_OPTION_LINK);
//		    	break;
		    case LocateMessageTypes.FUTURE_REQUEST:
		    	responseMsgType = LocateMessageTypes.RESPONSE_FUTURE;
		    	resultCode = requestHandler.processRequest(request,clientName,responseMsgType,channelID);
		    	break;
//		    case RFAMessageTypes.FUTURE_LINK_REQUEST:
//		    	processOneTimesRequest(request,clientName,RFAMessageTypes.RESPONSE_FUTURE_LINK);
//		    	break;
		    case LocateMessageTypes.INDEX_REQUEST:
		    	resultCode = requestHandler.processRequest(request,clientName,LocateMessageTypes.RESPONSE_INDEX,channelID);
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
		//处理存在错误需要向客户端发送错误信息.
//		if(resultCode != LocateResultCode.SUCCESS_RESULT){
			LocateUnionMessage message = new LocateUnionMessage();
			message.setResultCode(resultCode);
			message.setResultDes(LocateResponseEnum.getResultDescription(resultCode));
			message.setMsgType(LocateMessageTypes.SERVER_STATE);
			GateWayResponser.sentResponseMsg(message, channelID);
//		}
//		else{
//			LocateUnionMessage message = new LocateUnionMessage();
//			message.setResultCode(resultCode);
//			String state = LocateResponseEnum.getResultDescription(resultCode);
//			message.setResultDes(state);
//			message.setMsgType(LocateMessageTypes.STATUS_RESP);
//			GateWayResponser.sentResponseMsg(message, channelID);
//		}
		
		return resultCode;
	}
	
//	public void sendRicRequest(String pItemName,byte responseMsgType){
//		itemManager.sendRicRequest(pItemName, responseMsgType);
//	}
	
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
	
//	private boolean checkRequestItem(byte msgType,String userName,List<String> itemNames){
//		String businessName = GateWayMessageTypes.RFAMessageName.getRFAMessageName(msgType).toUpperCase();
//		if(!RFAUserManagement.checkMaxBusinessValiable(userName,businessName,itemNames)){
//			RFAUserResponse.sentWrongMsgResponse(RFAExceptionTypes.USER_BUSINESS_NUMBER_OUT, channel);
//	        _logger.error("Client request's business number over.");
//	        return false;
//		}
//		return true;
//	}

	/**
	 * 取消产品的订阅,并释放该产品所有资源
	 * @param itemName
	 */
	public void closeHandler(String itemName) {
		IProcesser itemHandler = RmdsDataCache.RIC_ITEMMANAGER_Map.get(itemName);
		//取消订阅该产品
		if(itemHandler!=null){
			itemHandler.closeRequest();
		}
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
