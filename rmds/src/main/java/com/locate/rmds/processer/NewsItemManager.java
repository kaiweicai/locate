package com.locate.rmds.processer;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.locate.bridge.GateWayResponser;
import com.locate.common.constant.LocateMessageTypes;
import com.locate.common.constant.RFANodeconstant;
import com.locate.common.constant.SystemConstant;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.parser.GenericOMMParser;
import com.locate.rmds.util.EventFieldUtils;
import com.reuters.rfa.common.Client;
import com.reuters.rfa.common.Event;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.dictionary.FidDef;
import com.reuters.rfa.omm.OMMEncoder;
import com.reuters.rfa.omm.OMMItemGroup;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMMsg.MsgType;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.omm.OMMTypes;
import com.reuters.rfa.rdm.RDMInstrument;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.session.omm.OMMItemEvent;
import com.reuters.rfa.session.omm.OMMItemIntSpec;
import com.reuters.rmtes.RmtesCharsetProvider;

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

@Component("newsItemManager")@Scope("prototype")
public class NewsItemManager implements Client {
	Handle itemHandle;
	@Resource(name="qSConsumerProxy")
	static QSConsumerProxy _mainApp;
//	public static Map<String, Map<String,IoSession>> newsQueue = new HashMap();
	public static Map<String, List<String>> userNewsCode = new HashMap();
	public static Map<String, Document> newsXmlMap = new HashMap();
	public static Map<String, StringBuffer> newsContentMap = new HashMap();

	RmtesCharsetProvider rmtesProv = new RmtesCharsetProvider();
	Charset rmtesCharset = rmtesProv.charsetForName("RMTES");

	static Logger _logger = LoggerFactory.getLogger(NewsItemManager.class);
	ItemGroupManager _itemGroupManager;
	public static String clientRequestItemName;
	public String clientName;
	public byte responseMessageType;
	public Map<String, Boolean> sentNewsAddress;
	boolean isSubscribeNews = false;
	String pnac;
	static long startTime;
	static long responseCounter;
	// SubAppContext _appContext ;
	static short DSPLY_NAME;
	static short AREA_ID;
	static short PNAC;
	static short BCAST_TEXT;
	static int BCAST_TEXT_LENGTH;
	static short ATTRIBTN;
	static short PROD_CODE;
	static short TOPIC_CODE;
	static short CO_IDS;
	static short LANG_IND;
	static short STORY_TIME;
	static short STORY_DATE;
	static short SF_NAME;
	// static FieldDictionary _dictionary;

	// static Logger _logger;

	private String _className = "ItemManager";

	// constructor
	public NewsItemManager(ItemGroupManager itemGroupManager) {
		_itemGroupManager = itemGroupManager;
	}

	public static synchronized void initializeFids() {

		BCAST_TEXT = EventFieldUtils.getFidDef("BCAST_TEXT");
		if (BCAST_TEXT != 0) {
			// BCAST_TEXT_LENGTH = dictionary.isOMM() ? def.getMaxOMMLength() :
			// def
			// .getMaxMfeedLength();
			BCAST_TEXT_LENGTH = EventFieldUtils.dictionary.getFidDef("BCAST_TEXT").getMaxOMMLengthAsInt();
		}

		DSPLY_NAME = EventFieldUtils.getFidDef("DSPLY_NAME");
		
		AREA_ID = EventFieldUtils.getFidDef("AREA_ID");
		
		PNAC = EventFieldUtils.getFidDef("PNAC");
		
		ATTRIBTN = EventFieldUtils.getFidDef("ATTRIBTN");
		
		PROD_CODE = EventFieldUtils.getFidDef("PROD_CODE");
		
		TOPIC_CODE = EventFieldUtils.getFidDef("TOPIC_CODE");
		
		CO_IDS = EventFieldUtils.getFidDef("CO_IDS");
		
		LANG_IND = EventFieldUtils.getFidDef("LANG_IND");
		
		STORY_TIME = EventFieldUtils.getFidDef("STORY_TIME");
		
		STORY_DATE = EventFieldUtils.getFidDef("STORY_DATE");

		SF_NAME = EventFieldUtils.getFidDef("SF_NAME");
		// FidsInitialized = true;
	}

	// creates streaming request messages for items and register them to RFA
	public void sendRequest(String pItemName) {
		_logger.info("sendRequest: Sending news requests");
		String serviceName = _mainApp.serviceName;
		String[] itemNames = { pItemName };
		short msgModelType = RDMMsgTypes.MARKET_PRICE;

		OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();

		OMMEncoder encoder = _mainApp.getEncoder();
		// Preparing item request message
		OMMPool pool = _mainApp.getPool();
		OMMMsg ommmsg = pool.acquireMsg();

		ommmsg.setMsgType(OMMMsg.MsgType.REQUEST);
		ommmsg.setMsgModelType(msgModelType);
		ommmsg.setIndicationFlags(OMMMsg.Indication.REFRESH);
		ommmsg.setPriority((byte) 1, 1);

		// Setting OMMMsg with negotiated version info from login handle
		if (_mainApp.getLoginHandle() != null) {
			ommmsg.setAssociatedMetaInfo(_mainApp.getLoginHandle());
		}

		// register for each item
		for (int i = 0; i < itemNames.length; i++) {
			String itemName = itemNames[i];
			clientRequestItemName = itemName;
			_logger.info("Subscribing to " + itemName);

			ommmsg.setAttribInfo(serviceName, itemName, RDMInstrument.NameType.RIC);

			// Set the message into interest spec
			ommItemIntSpec.setMsg(ommmsg);
			itemHandle = _mainApp.getOMMConsumer().registerClient(_mainApp.getEventQueue(), ommItemIntSpec, this, null);
			_itemGroupManager.addItem(serviceName, itemName, itemHandle);
		}
		pool.releaseMsg(ommmsg);
	}

	// Unregisters/unsubscribes login handle
	public void closeRegiste() {
		_itemGroupManager._handles.remove(itemHandle);
		_mainApp.getOMMConsumer().unregisterClient(itemHandle);
	}

	// This is a Client method. When an event for this client is dispatched,
	// this method gets called.
	public void processEvent(Event event) {
		isSubscribeNews = false;
		startTime = System.currentTimeMillis();
		sentNewsAddress = new HashMap();
		// Completion event indicates that the stream was closed by RFA
		if (event.getType() == Event.COMPLETION_EVENT) {
			_logger.info(" Receive a COMPLETION_EVENT, " + event.getHandle());
			return;
		}

		// check for an event type; it should be item event.
		_logger.info("processEvent: Received news event #######################");
		if (event.getType() != Event.OMM_ITEM_EVENT) {
			_logger.info("ERROR: Received an unsupported Event type.");
//			_mainApp.cleanup();
			return;
		}

		OMMItemEvent ie = (OMMItemEvent) event;

		OMMMsg respMsg = ie.getMsg();
		
		if (respMsg.getMsgType() == MsgType.STATUS_RESP) {
			_logger.info("ERROR: Received an unsupported Event type.");
			return;
		}
		
		if ((respMsg.getMsgType() == OMMMsg.MsgType.REFRESH_RESP)
				|| (respMsg.getMsgType() == OMMMsg.MsgType.STATUS_RESP && respMsg.has(OMMMsg.HAS_ITEM_GROUP))) {
			OMMItemGroup group = respMsg.getItemGroup();
			Handle itemHandle = event.getHandle();
			_itemGroupManager.applyGroup(itemHandle, group);
		}
		
		processHeadline(ie);

		Document responseMsg = GenericOMMParser.parse(respMsg, clientRequestItemName);

		long endTime = System.currentTimeMillis();
		if (isSubscribeNews && StringUtils.isNotEmpty(pnac)) {
			_logger.info("##############The news has subscribed########################");
			newsXmlMap.put(pnac, responseMsg);
			// Initialize news request manager for news domains
			NewsContentManager newContentManager = SystemConstant.springContext.getBean("newsContentManager",NewsContentManager.class);
			// Send requests
			newContentManager.sendRequest(pnac, pnac, new StringBuffer());
		}
	}

	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟绞裁达拷锟斤拷锟?锟截碉拷锟叫撅拷
	 * @param nevent
	 */
	protected void processHeadline(OMMItemEvent nevent) {
		OMMMsg msg = nevent.getMsg();
		if (msg.getDataType() == OMMTypes.NO_DATA) {
			return;
		}
		// parse mandatory fields
		pnac = EventFieldUtils.getEventField(nevent, PNAC);
		String prodCode = EventFieldUtils.getEventField(nevent, PROD_CODE);

		// parse optional fields
		String lang = EventFieldUtils.getEventField(nevent, LANG_IND);
		String topicCode = EventFieldUtils.getEventField(nevent, TOPIC_CODE);
		String coIds = EventFieldUtils.getEventField(nevent, CO_IDS);
		String areaId = EventFieldUtils.getEventField(nevent, AREA_ID);
		String sfName = EventFieldUtils.getEventField(nevent, SF_NAME);

		if (lang != null) {
			processSubscribe(lang);
		}
		if (prodCode != null) {
			processSubscribe(prodCode);
		}
		if (topicCode != null) {
			processSubscribe(topicCode);
		}
		if (coIds != null) {
			processSubscribe(coIds);
		}
		if (areaId != null) {
			processSubscribe(areaId);
		}
		if (sfName != null) {
			processSubscribe(sfName);
		}

	}

	private void processSubscribe(String keys) {
		String[] keysList = keys.split(" ");
		for (String key : keysList) {
			sendNewsSubscribe(key);
		}
	}

	private void sendNewsSubscribe(String key) {
//		Map<String, IoSession> subItemList = RFASocketServer._clientNewsRequest
//				.get(key);
//		String clientIP;
//		List<String> subscribeCode = null;
//		if (subItemList != null) {
//			Map<String,IoSession> sessionList = newsQueue.get(pnac);
//			if (sessionList == null) {
//				sessionList = new HashMap();
//			}
//			for(IoSession session : subItemList.values()){
//				clientIP = RFACommon.getIP(session);
//				sessionList.put(clientIP, session);
//
//				subscribeCode = userNewsCode.get(pnac+clientIP);
//				if(subscribeCode == null)
//					subscribeCode = new ArrayList();
//				subscribeCode.add(key);
//				userNewsCode.put(pnac+clientIP, subscribeCode);
//			}
//			newsQueue.put(pnac, sessionList);
			isSubscribeNews = true;
//		}
	}

	public static void sendNewsToCustumer(String pnac, StringBuffer newsContent) {
		responseCounter++;
		_logger.info("#############total sent news " + responseCounter + "##############");
		// Map<String,IoSession> sessionList = newsQueue.get(pnac);
		Document news = newsXmlMap.get(pnac);
		if (news == null) {
			_logger.info("#############Not found news's pnac " + pnac + "##############");
			return;
		}
		Element fields = news.getRootElement().element(RFANodeconstant.RESPONSE_RESPONSE_NODE)
				.element(RFANodeconstant.RESPONSE_FIELDS_NODE);
		Element field = fields.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_NODE);
		FidDef def = EventFieldUtils.dictionary.getFidDef("SEG_TEXT");
		String content = newsContent.toString();
		field.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_ID_NODE).addText(String.valueOf(def.getFieldId()));
		field.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_NAME_NODE).addText(def.getName());
		field.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_TYPE_NODE).addText("String");
		field.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_VALUE_NODE).addText(content);
		_logger.info("#############Sent news##############");
		GateWayResponser.sentAllChannelNews(LocateMessageTypes.RESPONSE_NEWS, news);
		newsXmlMap.remove(pnac);
		long endTime = System.currentTimeMillis();
//		_mainApp.updateResponseStat((endTime - startTime), news);
		_logger.info("#############updateResponseStat##############");
	}

//	private boolean isSentForSession(IoSession session) {
//		String clientIP = ((InetSocketAddress) session.getRemoteAddress())
//				.getAddress().getHostAddress();
//		if (sentNewsAddress.get(clientIP) == null) {
//			sentNewsAddress.put(clientIP, true);
//			return false;
//		} else {
//			return true;
//		}
//	}
	

}
