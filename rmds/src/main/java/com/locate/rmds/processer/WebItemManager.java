package com.locate.rmds.processer;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.locate.common.constant.SystemConstant;
import com.locate.common.utils.XmlMessageUtil;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.parser.GenericOMMParser;
import com.reuters.rfa.common.Client;
import com.reuters.rfa.common.Event;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.omm.OMMItemGroup;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.rdm.RDMInstrument;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.session.omm.OMMItemEvent;
import com.reuters.rfa.session.omm.OMMItemIntSpec;

// This class is a Client implementation that is utilized to handle item requests
// and responses between application and RFA.
// An instance of this class is created by QSConsumerDemo.
// This class performs the following functions:
// - Creates and encodes item request messages and registers the client (itself) 
// - with RFA (method sendRequest()). The registration will cause RFA
//  to send item open request. RFA will return back a handle instance.
// This application will request two items - TRI.N and MSFT.O.
// - Unregisters the client in RFA (method closeRequest()).
// - Processes events for this client (method processEvent()). processEvent() method
// must be implemented by a class that implements Client interface.
//
// The class keeps the following members:
// ArrayList<Handle> _itemHandles - handles returned by RFA on registering the items
//							application uses this handles to identify the items
// QSConsumerDemo _mainApp - main application class
/**
 * 该类有多个实例.一个订阅的产品对应一个itemManager.
 * 
 * @author Cloud.Wei
 * 
 */
@Deprecated
@Service
public class WebItemManager implements Client {
	Handle itemHandle;
	@Resource
	QSConsumerProxy mainApp;
	static Logger logger = LoggerFactory.getLogger(WebItemManager.class.getName());
	public String clientRequestItemName;
	// public String clientName;
	public byte responseMessageType;

	private String _className = "ItemManager";

	private Integer channelID = 0;

	static int _timeline;
//	@Deprecated
//	private HttpRequest request;
	
	// creates streaming request messages for items and register them to RFA
//	public void sendOneTimeRequest(String pItemName, byte responseMsgType,HttpRequest request) {
//		this.request = request;
//		this.responseMessageType = responseMsgType;
//		logger.info(_className + ".webRequest: Sending item(" + pItemName + ") requests to server ");
//		String serviceName = mainApp.serviceName;
//		this.clientRequestItemName = pItemName;
//		String[] itemNames = { pItemName };
//		short msgModelType = RDMMsgTypes.MARKET_PRICE;
//
//		OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();
//
//		// Preparing item request message
//		OMMPool pool = mainApp.getPool();
//		OMMMsg ommmsg = pool.acquireMsg();
//		// NONSTREAMING_REQ为只取一个snapshort.
//		ommmsg.setMsgType(OMMMsg.MsgType.NONSTREAMING_REQ);
//		ommmsg.setMsgModelType(msgModelType);
//		// ommmsg.setIndicationFlags(OMMMsg.Indication.REFRESH);
//		ommmsg.setPriority((byte) 1, 1);
//
//		// Setting OMMMsg with negotiated version info from login handle
//		if (mainApp.getLoginHandle() != null) {
//			ommmsg.setAssociatedMetaInfo(mainApp.getLoginHandle());
//		}
//
//		// register for each item
//		for (int i = 0; i < itemNames.length; i++) {
//			String itemName = itemNames[i];
//			// this.clientRequestItemName = itemName;
//			logger.info(_className + ": Subscribing one time to " + itemName);
//
//			ommmsg.setAttribInfo(serviceName, itemName, RDMInstrument.NameType.RIC);
//
//			// Set the message into interest spec
//			ommItemIntSpec.setMsg(ommmsg);
//			itemHandle = mainApp.getOMMConsumer().registerClient(mainApp.getEventQueue(), ommItemIntSpec, this, null);
//		}
//		pool.releaseMsg(ommmsg);
//	}
	
	// creates streaming request messages for items and register them to RFA
		public void sendRICRequest(String pItemName, byte responseMsgType) {
			this.responseMessageType = responseMsgType;
			logger.info(_className + ".webRequest: Sending item(" + pItemName + ") requests to server ");
			String serviceName = mainApp.serviceName;
			this.clientRequestItemName = pItemName;
			String[] itemNames = { pItemName };
			short msgModelType = RDMMsgTypes.MARKET_PRICE;

			OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();

			// Preparing item request message
			OMMPool pool = mainApp.getPool();
			OMMMsg ommmsg = pool.acquireMsg();
			// NONSTREAMING_REQ为只取一个snapshort.
			ommmsg.setMsgType(OMMMsg.MsgType.REQUEST);
			ommmsg.setMsgModelType(msgModelType);
			// ommmsg.setIndicationFlags(OMMMsg.Indication.REFRESH);
			ommmsg.setPriority((byte) 1, 1);

			// Setting OMMMsg with negotiated version info from login handle
			if (mainApp.getLoginHandle() != null) {
				ommmsg.setAssociatedMetaInfo(mainApp.getLoginHandle());
			}

			// register for each item
			for (int i = 0; i < itemNames.length; i++) {
				String itemName = itemNames[i];
				// this.clientRequestItemName = itemName;
				logger.info(_className + ": Subscribing one time to " + itemName);

				ommmsg.setAttribInfo(serviceName, itemName, RDMInstrument.NameType.RIC);

				// Set the message into interest spec
				ommItemIntSpec.setMsg(ommmsg);
				itemHandle = mainApp.getOMMConsumer().registerClient(mainApp.getEventQueue(), ommItemIntSpec, this, null);
			}
			pool.releaseMsg(ommmsg);
		}
	

	// This is a Client method. When an event for this client is dispatched,
	// this method gets called.
	public void processEvent(Event event) {
		long startTime = System.currentTimeMillis();
		switch (event.getType()) {
		case Event.OMM_SOLICITED_ITEM_EVENT:
			break;
		}

		// Completion event indicates that the stream was closed by RFA
		if (event.getType() == Event.COMPLETION_EVENT) {
			logger.info(_className + ": Receive a COMPLETION_EVENT, " + event.getHandle());
//			DataBaseCache.recipientsMap.get(clientRequestItemName).close();
			return;
		}

		// check for an event type; it should be item event.
		logger.info(_className + ".processEvent: Received Item(" + clientRequestItemName + ") Event from server ");
		if (event.getType() != Event.OMM_ITEM_EVENT) {
			logger.error("ERROR: " + _className + " Received an unsupported Event type.");
			return;
		}

		OMMItemEvent ommItemEvent = (OMMItemEvent) event;
		OMMMsg respMsg = ommItemEvent.getMsg();
		Document responseMsg = GenericOMMParser.parse(respMsg, clientRequestItemName);
		// 将信息开始处理时间加入到消息中
		XmlMessageUtil.addStartHandleTime(responseMsg, startTime);
		// 如果是状态消息.处理后直接发送给订阅了该产品的所有客户.
		if (respMsg.getMsgType() == OMMMsg.MsgType.STATUS_RESP && (respMsg.has(OMMMsg.HAS_STATE))) {
			byte streamState = respMsg.getState().getStreamState();
			byte dataState = respMsg.getState().getDataState();
			byte msgType = respMsg.getMsgType();
			String state = respMsg.getState().toString();
			responseMsg = null;
			XmlMessageUtil.addLocateInfo(responseMsg, msgType, SystemConstant.sequenceNo.getAndIncrement(), 0);
//			HttpWayResponser.sentMrketPriceToSubsribeChannel(responseMsg, clientRequestItemName);
			logger.warn("RFA server has new state. streamState:" + streamState + " datasstate " + dataState);
			return;
		}

		// Status response can contain group id
		if ((respMsg.getMsgType() == OMMMsg.MsgType.REFRESH_RESP)
				|| (respMsg.getMsgType() == OMMMsg.MsgType.STATUS_RESP && respMsg.has(OMMMsg.HAS_ITEM_GROUP))) {
			OMMItemGroup group = respMsg.getItemGroup();
			Handle itemHandle = event.getHandle();
//			_itemGroupManager.applyGroup(itemHandle, group);
		}

//		HttpWayResponser.writeWebSocket(respMsg.getMsgType(), responseMsg, clientRequestItemName, request);
		if (responseMsg != null) {
			long endTime = System.currentTimeMillis();
			logger.info("publish Item " + clientRequestItemName + " use time " + (endTime - startTime)
					+ " microseconds");
		}
	}
	
	// Unregisters/unsubscribes login handle
	public void closeRequest() {
		mainApp.getOMMConsumer().unregisterClient(itemHandle);
		// DataBaseCache.subscribeItemManagerMap.remove(clientRequestItemName);
	}
}
