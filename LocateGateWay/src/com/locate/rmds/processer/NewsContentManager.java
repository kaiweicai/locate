package com.locate.rmds.processer;

import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.util.EventFieldUtils;
import com.reuters.rfa.common.Client;
import com.reuters.rfa.common.Event;
import com.reuters.rfa.common.Handle;
//import com.reuters.rfa.example.framework.sub.NormalizedEvent;
//import com.reuters.rfa.example.framework.sub.SubAppContext;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.omm.OMMTypes;
import com.reuters.rfa.rdm.RDMInstrument;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.session.omm.OMMItemEvent;
import com.reuters.rfa.session.omm.OMMItemIntSpec;

/**
 * <p>
 * This class provides several operations that application must perform for
 * consuming and managing the segment chain data such as news story.
 * </p>
 * 
 * This class is responsible for the following actions:
 * <ul>
 * <li>Request the first segment and find the next link from NEXT_LR field.
 * <li>Request the next link until no next link.
 * <li>Notify a {@link SegmentChainClient client} when receiving each segment
 * data, completing all segments and founding error.
 * </ul>
 * 
 * @see SegmentChainClient
 */
public class NewsContentManager  implements Client {
	static Logger _logger = Logger
			.getLogger(NewsContentManager.class.getName());
//	ItemGroupManager _itemGroupManager;
	Handle itemHandle ;
	static boolean FidsInitialized = false;
	static boolean initFidsFail = false;
	static short SegText;
	static int SegTextLength;
	static short NextLink;
	static short NextTake;
	static short PreviousLink;
	static short PreviousTake;
	static short NumberOfTakes;
	static short CurrentTake;
	static short TabText;
	QSConsumerProxy mainApp;

	static final int REFRESH = 1;
	static final int UPDATE = 2;
	static final int STATUS = 3;

	// protected SubAppContext _subContext;
	protected Handle _handle;
	StringBuffer _currentSegText;
	boolean _initialized;
	boolean _error;
	boolean _complete;
	int _limit;
	int _count;
	String _errorText;
	String _symbol;
	String _nextLink;
	String _nextTake;
	String _previousLink;
	String _previousTake;
	String _tabText;
	String _newsPnac;

	public NewsContentManager(QSConsumerProxy mainApp) {
		// this._itemGroupManager = itemGroupManager;
		this.mainApp = mainApp;
		initializeFids();
		_limit = -1;
		_count = 0;
		_complete = false;
		_error = false;
		_initialized = false;
		// _currentSegText = new StringBuffer();

		if (!FidsInitialized) {
			initFidsFail = true;
			_error = true;
			_complete = true;
			_errorText = "Initialize field id from dictionary fail";
			_logger.error("Initialize field id from dictionary fail");
			return;
		}

	}

	public void cleanup() {
		dropRecord();
	}

	public void setLimit(int limit) {
		_limit = limit;
	}

	public boolean complete() {
		return _complete;
	}

	public boolean error() {
		return _error;
	}

	public int count() {
		return _count;
	}

	public int limit() {
		return _limit;
	}

	public String errorText() {
		return _errorText;
	}

	public String symbol() {
		return _symbol;
	}

	public String tabText() {
		return _tabText;
	}

	public String nextTake() {
		return _nextTake;
	}

	public String previousTake() {
		return _previousTake;
	}

	public String currentSegText() {
		return _currentSegText.toString();
	}

	public void sendRequest(String pnac,String newsPnac,StringBuffer sb) {
		this._currentSegText = sb;
		this._newsPnac = newsPnac;
		// this.responseMessageType = responseMsgType;
		_logger.info(NewsContentManager.class.getName()
				+ ".sendRequest: Feed news content item requests for " + pnac);
		String serviceName = mainApp._serviceName;
//		String[] itemNames = { newsId };
		short msgModelType = RDMMsgTypes.MARKET_PRICE;

		OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();

		// Preparing item request message
		OMMPool pool = mainApp.getPool();
		OMMMsg ommmsg = pool.acquireMsg();

		ommmsg.setMsgType(OMMMsg.MsgType.REQUEST);
		ommmsg.setMsgModelType(msgModelType);
		ommmsg.setIndicationFlags(OMMMsg.Indication.REFRESH);
		ommmsg.setPriority((byte) 1, 1);

		// Setting OMMMsg with negotiated version info from login handle
		if (mainApp.getLoginHandle() != null) {
			ommmsg.setAssociatedMetaInfo(mainApp.getLoginHandle());
		}

		// register for  newsId

		ommmsg.setAttribInfo(serviceName, pnac,
				RDMInstrument.NameType.RIC);

		// Set the message into interest spec
		ommItemIntSpec.setMsg(ommmsg);
		itemHandle = mainApp.getOMMConsumer().registerClient(
				mainApp.getEventQueue(), ommItemIntSpec, this, null);
//		_itemGroupManager.addItem(serviceName, pnac, itemHandle);
		pool.releaseMsg(ommmsg);
		_initialized = true;
	}

	protected void dropRecord() {
//		 _itemGroupManager._handles.remove(itemHandle);
         mainApp.getOMMConsumer().unregisterClient(itemHandle);
	}

	protected void indicateComplete() {
		_complete = true;
		dropRecord();
	}

	static synchronized void initializeFids() {
		if (FidsInitialized || initFidsFail) {
			return;
		}
		
		SegText = EventFieldUtils.getFidDef("SEG_TEXT");
		
		SegTextLength = EventFieldUtils.dictionary.getFidDef("SEG_TEXT").getMaxOMMLengthAsInt(); 
//		SegTextLength = dictionary.isOMM() ? def.getMaxOMMLength() : def
//				.getMaxMfeedLength();

		NextLink = EventFieldUtils.getFidDef("NEXT_LR");
		
		NextTake = EventFieldUtils.getFidDef("SEG_FORW");
		
		PreviousLink = EventFieldUtils.getFidDef("PREV_LR");
		
		PreviousTake = EventFieldUtils.getFidDef("SEG_BACK");
		
		NumberOfTakes = EventFieldUtils.getFidDef("NO_TAKES");
		
		CurrentTake = EventFieldUtils.getFidDef("CUR_TAKE");
		
		TabText = EventFieldUtils.getFidDef("TABTEXT");

		FidsInitialized = true;
	}

	public void processEvent(Event event) {
		_logger.info("#########Received response message for news#############");
		_currentSegText = new StringBuffer();
		if (event.getType() != Event.OMM_ITEM_EVENT){
			_logger.info("Message is not OMM_ITEM_EVENT, It's "+event.getType());
			return ;
		}
		OMMItemEvent oevent = (OMMItemEvent) event;
		OMMMsg respMsg = oevent.getMsg();
		if (respMsg.getDataType() == OMMTypes.NO_DATA) {
			return;
		}
		String statusValue = "";
		if (respMsg.getMsgType() == STATUS) {
			if (respMsg.isFinal()) {

				if (respMsg.has(OMMMsg.HAS_STATE))
					statusValue = respMsg.getState().getText();
				indicateError(oevent, statusValue);
				return;
			}
		} 
//		else {
			byte[] bytes = new byte[SegTextLength];
			String segText = "";
			int n = EventFieldUtils.getFieldBytes(oevent, SegText, bytes, 0);
			if (n == 0) {
//				_currentSegText = new StringBuffer();
				indicateError(oevent, EventFieldUtils.getItemName(oevent)
						+ " is missing SEG_TEXT field");
				return;
			}
			try {
				segText = new String(bytes, Charset.forName("RMTES"));
				_logger.info("***********************************");
				_logger.info(segText);
				_logger.info("***********************************");
			} catch (Exception e) {
				segText = new String(bytes);
			}

			_currentSegText.append(segText);
//		}

		_previousTake = EventFieldUtils.getFieldString(oevent, PreviousTake);
		_previousLink = EventFieldUtils.getFieldString(oevent, PreviousLink);

		_count++;
		String tabText = EventFieldUtils.getFieldString(oevent, TabText);
		if (tabText != null)
			_tabText = tabText;

		_nextLink = EventFieldUtils.getFieldString(oevent, NextLink);
		_nextLink = (_nextLink == null) ? "" : _nextLink.trim();
		_nextTake = EventFieldUtils.getFieldString(oevent, NextTake);

		if ((_limit > 0) && (_count >= _limit)) {
			indicateComplete();
			return;
		}

		if (!StringUtils.isBlank(_nextLink)) {
			_logger.info("Go ahead get next related news content for "+_nextLink);
			dropRecord();
			this.sendRequest(_nextLink,_newsPnac,_currentSegText);
			return;
		}

		_logger.info("------------1-------------------");
    	_logger.info(_currentSegText.toString());
    	_logger.info("-------------------------------");
		String content = _currentSegText.toString();

//		content = HtmlUtils.htmlEscape(content);
//		content = HtmlUtils.htmlUnescape(content);
    	NewsItemManager.sendNewsToCustumer(_newsPnac, _currentSegText);
		indicateComplete();
	}

	private void indicateError(OMMItemEvent nevent, String text) {
		_errorText = text;
		_error = true;
		_complete = true;
		dropRecord();
	}
}
