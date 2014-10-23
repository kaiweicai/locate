package com.locate.rmds.processer;


import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.NetTimeUtil;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.engine.CurrencyEngine;
import com.locate.rmds.engine.EngineLine;
import com.locate.rmds.engine.filter.EngineManager;
import com.locate.rmds.parser.face.IOmmParser;
import com.locate.rmds.statistic.CycleStatistics;
import com.locate.rmds.statistic.LogTool;
import com.locate.rmds.statistic.OutputFormatter;
import com.locate.rmds.statistic.ResourceStatistics;
import com.reuters.rfa.common.Client;
import com.reuters.rfa.common.Event;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.rdm.RDMInstrument;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.session.omm.OMMItemEvent;
import com.reuters.rfa.session.omm.OMMItemIntSpec;
import com.reuters.rfa.session.omm.OMMSolicitedItemEvent;

/**
 * 该类有多个实例.一个订阅的产品对应一个itemManager.
 * @author Cloud.Wei
 *
 */
@Service("chyCurrencyManager")@Scope("prototype")
public class ChyCurrencyManager implements Client{
	private static final String CNY_CURRENCY_SOURCE_CODE = "CNY=CFXS";
	
	Handle  itemHandle;
	@Resource
	QSConsumerProxy _mainApp;
    static Logger _logger = LoggerFactory.getLogger(ChyCurrencyManager.class);
    @Resource
    ItemGroupManager _itemGroupManager;
    @Resource(name="locateOMMParser")
    IOmmParser ommParser;
	//    public String clientName;
    public byte responseMessageType;

	// requests
	static private int _request_count;
    static private CycleStatistics _request_cycleStats;
    
    //refreshes
    static private int _refresh_count;
    static private CycleStatistics _refresh_cycleStats;
    
	// updates
    static private int _update_count;
    static private CycleStatistics _update_cycleStats;
    static long _update_begin_milliTime;
	
	// posts
    static private int _post_received_count;
    static private int _post_resent_count;
    static private CycleStatistics _post_received_cycleStats;
    static private CycleStatistics _post_resent_cycleStats;
    
 // resource usage ( CPU and Memory )
    static ResourceStatistics _resourceStats;
    
 // close
    static private int _close_count;
    static private CycleStatistics _close_cycleStats;
	
    // display
    static LogTool _consoleLogger;
    static LogTool _statsFileLogger;
    static OutputFormatter _outputFormatter;
    static StringBuilder _statsStringBuffer;
    static int _timeline;
    
	static {
		_statsStringBuffer = new StringBuilder(1024);
		// output formatter
        _outputFormatter = new OutputFormatter();
        _outputFormatter.initializeDateFormat("yyyy-MM-dd HH:mm:ss", "UTC");
		String _myName = "AdminClient";
		_consoleLogger = new LogTool();
		_consoleLogger.log2Screen();
		_consoleLogger.setName(_myName);
		// stats output file and statistics file logger
		_statsFileLogger = new LogTool();
		_consoleLogger.tagErrPrintln(_statsFileLogger.getStatusText());

		_statsFileLogger.println("UTC, " + "Requests Received, " + "Images Sent, " + "Updates sent, "
				+ "Posts Reflected, " + "CPU usage (%), " + "Memory usage (MB)");

		// metrics
		_resourceStats = new ResourceStatistics();

		_request_cycleStats = new CycleStatistics("request");
		_refresh_cycleStats = new CycleStatistics("refresh");
		_update_cycleStats = new CycleStatistics("updates");
		_close_cycleStats = new CycleStatistics("close");
		_post_received_cycleStats = new CycleStatistics("postReceived");
		_post_resent_cycleStats = new CycleStatistics("postResent");
	}
    
    // constructor
//    public ItemManager(QSConsumerProxy mainApp, ItemGroupManager itemGroupManager,String clientName)
//    {
//    	this._mainApp = mainApp;
//        this._itemGroupManager = itemGroupManager;
//        this.clientName = clientName;
//    }
    // creates streaming request messages for items and register them to RFA
    public void sendRicRequest()
    {
    	_logger.info("sendRequest: Sending item("+CNY_CURRENCY_SOURCE_CODE+") requests to server ");
        String serviceName = _mainApp.serviceName;
        String[] itemNames = {CNY_CURRENCY_SOURCE_CODE};
        short msgModelType = RDMMsgTypes.MARKET_PRICE;

        OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();

        //Preparing item request message
        OMMPool pool = _mainApp.getPool();
        OMMMsg ommmsg = pool.acquireMsg();

        ommmsg.setMsgType(OMMMsg.MsgType.REQUEST);
        ommmsg.setMsgModelType(msgModelType);
        ommmsg.setIndicationFlags(OMMMsg.Indication.REFRESH);
        ommmsg.setPriority((byte) 1, 1);
        
        // Setting OMMMsg with negotiated version info from login handle        
        if( _mainApp.getLoginHandle() != null )
        {
        	ommmsg.setAssociatedMetaInfo(_mainApp.getLoginHandle());
        }

        // register for each item
		for (int i = 0; i < itemNames.length; i++) {
			String itemName = itemNames[i];
//			this.clientRequestItemName = itemName;
			_logger.info("Subscribing to " + itemName);

			ommmsg.setAttribInfo(serviceName, itemName, RDMInstrument.NameType.RIC);

			// Set the message into interest spec
			ommItemIntSpec.setMsg(ommmsg);
			/**
			 * 重要逻辑:
			 * 向data source注册感兴趣的Item,EventQueue使用QSConsumerProxy的Queue,监听器就是itemManager自己.
			 * dataSource产生感兴趣的时间后通过eventQueue向外发布.ItemManager能够收到这个事件.
			 * itemmanager 想所有订阅了该产品的channelGroup回写收到的消息.
			 */
			itemHandle = _mainApp.getOMMConsumer().registerClient(_mainApp.getEventQueue(), ommItemIntSpec, this, null);
			_itemGroupManager.addItem(serviceName, itemName, itemHandle);
		}
        pool.releaseMsg(ommmsg);
    }
    

    /**
     * 
     */
	public void closeRequest() {
		_itemGroupManager._handles.remove(itemHandle);
		_mainApp.getOMMConsumer().unregisterClient(itemHandle);
//		RmdsDataCache.RIC_ITEMMANAGER_Map.remove(this.clientRequestItemName);
//		RmdsDataCache.RIC_ITEMMANAGER_Map.remove(this.derivactiveItemName);
//		RmdsDataCache.CLIENT_ITEMMANAGER_MAP.remove(this.clientRequestItemName);
//		RmdsDataCache.CLIENT_ITEMMANAGER_MAP.remove(this.derivactiveItemName);
	}

    // This is a Client method. When an event for this client is dispatched,
    // this method gets called.
    public void processEvent(Event event)
    {
    	long startTime = System.currentTimeMillis();
    	// Completion event indicates that the stream was closed by RFA
    	if (event.getType() == Event.COMPLETION_EVENT) 
    	{
    		_logger.info("Receive a COMPLETION_EVENT, "+ event.getHandle());
    		//@TODO 判断是否通知所有现存客户端某个产品已经停止发布.
    		return;
    	}

        if (event.getType() != Event.OMM_ITEM_EVENT) 
        {
        	//这里程序太危险了,因为RFA给的消息有误就要退出程序.恐怖的逻辑啊.还是去掉cleanup好了.
            _logger.error("Received an unsupported Event type.");
//            _mainApp.cleanup();
            return;
        }

        OMMItemEvent ommItemEvent = (OMMItemEvent) event;
        OMMMsg respMsg = ommItemEvent.getMsg();
        //可以考虑将以下的动作放入到
        final LocateUnionMessage locateMessage = ommParser.parse(respMsg, CNY_CURRENCY_SOURCE_CODE);
        locateMessage.setStartTime(startTime);
        //将信息开始处理时间加入到消息中
//		XmlMessageUtil.addStartHandleTime(responseMsg, startTime);
        //如果是状态消息.处理后直接发送给客户端.
        if(respMsg.getMsgType()==OMMMsg.MsgType.STATUS_RESP && (respMsg.has(OMMMsg.HAS_STATE))){
        	ommParser.handelLocateState(respMsg, locateMessage);
			_logger.warn("RFA server has new state. streamState:"+locateMessage.getStreamingState()+" datasstate "+locateMessage.getDataingState());
			return;
        }
        
        List<String[]> payLoadList=locateMessage.getPayLoadSet();
        for(String[] payLoad:payLoadList){
        	if("6".equalsIgnoreCase(payLoad[0])){
        		if(payLoad.length>3){
        			CurrencyEngine.currency=payLoad[3];
        		}
        	}
        }
        
		EngineLine engineLine = EngineManager.engineLineCache.get("DE_XAG=_CNY");
		if (engineLine != null) {
			Future<LocateUnionMessage> engineFuture = engineLine.applyStrategy();
			if (engineFuture != null) {
				try {
					engineFuture.get();
				} catch (InterruptedException | ExecutionException e) {
					_logger.warn("get the engine result error!", e);
				}
			}
		}
		long endTime = NetTimeUtil.getCurrentNetTime();
		_logger.info("publish Item " + CNY_CURRENCY_SOURCE_CODE + " use time " + (endTime - startTime) + " microseconds");
    }
    
    /**
     * Handle solicited item events.
     * 
     * @param event the event to be processed.
     */
	protected void processOMMSolicitedItemEvent(OMMSolicitedItemEvent event) {
		OMMMsg msg = event.getMsg();

		if (msg.getMsgModelType() > RDMMsgTypes.DICTIONARY) {
			handleItemEvent(event);
		}
	}
	/**
     * Process incoming item events and call the appropriate method to process
     * the event. Called from the AdminClient.
     * 
     * @param event the event to process/handle.
     */
    @SuppressWarnings("deprecation")
    void handleItemEvent(OMMSolicitedItemEvent event)
    {
        switch (event.getMsg().getMsgType())
        {
            case OMMMsg.MsgType.REQUEST:
            {
            	++_request_count;
//                processItemRequest(event);
                return;
            }

//            case OMMMsg.MsgType.POST:
//            {
//                processPostMessage(event);
//                return;
//            }
//
//            case OMMMsg.MsgType.CLOSE_REQ:
//            {
//                processCloseItem(event);
//                return;
//            }

            case OMMMsg.MsgType.STREAMING_REQ:
            case OMMMsg.MsgType.NONSTREAMING_REQ:
            case OMMMsg.MsgType.PRIORITY_REQ:
            {
                OMMMsg ommMsg = event.getMsg();

                _consoleLogger.println("Received deprecated message type of "
                        + OMMMsg.MsgType.toString(ommMsg.getMsgType()) + ", not supported. ");
                return;
            }

            default:
                break;
        }
    }
    
	public Handle getItemHandle() {
		return itemHandle;
	}


	public void setItemHandle(Handle itemHandle) {
		this.itemHandle = itemHandle;
	}
}
