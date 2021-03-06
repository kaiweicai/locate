package com.locate.rmds.processer;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.locate.bridge.GateWayResponser;
import com.locate.common.DataBaseCache;
import com.locate.common.XmlMessageUtil;
import com.locate.gate.server.GateWayServer;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.RFAServerManager;
import com.locate.rmds.processer.face.IProcesser;
import com.locate.rmds.statistic.CycleStatistics;
import com.locate.rmds.statistic.LogTool;
import com.locate.rmds.statistic.OutputFormatter;
import com.locate.rmds.statistic.ResourceStatistics;
import com.locate.rmds.util.GenericOMMParser;
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
import com.reuters.rfa.session.omm.OMMSolicitedItemEvent;

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
 * @author Cloud.Wei
 *
 */
@Service("itemManager")@Scope("prototype")
public class ItemManager implements Client,IProcesser
{
	Handle  itemHandle;
	@Resource
	QSConsumerProxy _mainApp;
    static Logger _logger = Logger.getLogger(ItemManager.class.getName());
    @Resource
    ItemGroupManager _itemGroupManager;
    public String clientRequestItemName;
//    public String clientName;
    public byte responseMessageType;

    private	String	_className = "ItemManager";

	private Integer channelID = 0;

	private Document initialDocument;
	
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
    public void sendRicRequest(String pItemName,byte responseMsgType)
    {
    	this.responseMessageType = responseMsgType;
    	_logger.info(_className+".sendRequest: Sending item("+pItemName+") requests to server ");
        String serviceName = _mainApp._serviceName;
        this.clientRequestItemName = pItemName;
        String[] itemNames = {pItemName};
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
			_logger.info(_className + ": Subscribing to " + itemName);

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
		DataBaseCache.RIC_ITEMMANAGER_Map.remove(clientRequestItemName);
		DataBaseCache.CLIENT_ITEMMANAGER_MAP.remove(clientRequestItemName);
	}

    // This is a Client method. When an event for this client is dispatched,
    // this method gets called.
    public void processEvent(Event event)
    {
    	long startTime = System.currentTimeMillis();
    	switch (event.getType())
        {
            case Event.OMM_SOLICITED_ITEM_EVENT:
                processOMMSolicitedItemEvent((OMMSolicitedItemEvent)event);
                break;
        }
    	
    	// Completion event indicates that the stream was closed by RFA
    	if (event.getType() == Event.COMPLETION_EVENT) 
    	{
    		_logger.info(_className+": Receive a COMPLETION_EVENT, "+ event.getHandle());
    		return;
    	}

    	// check for an event type; it should be item event.
        _logger.info(_className+".processEvent: Received Item("+clientRequestItemName+") Event from server ");
        if (event.getType() != Event.OMM_ITEM_EVENT) 
        {
        	//这里程序太危险了,因为RFA给的消息有误就要退出程序.恐怖的逻辑啊.还是去掉cleanup好了.
            _logger.error("ERROR: "+_className+" Received an unsupported Event type.");
//            _mainApp.cleanup();
            return;
        }

        OMMItemEvent ommItemEvent = (OMMItemEvent) event;
        OMMMsg respMsg = ommItemEvent.getMsg();
        Document responseMsg = GenericOMMParser.parse(respMsg, clientRequestItemName);
        //将信息开始处理时间加入到消息中
		XmlMessageUtil.addStartHandleTime(responseMsg, startTime);
        //如果是状态消息.处理后直接发送给客户端.
        if(respMsg.getMsgType()==OMMMsg.MsgType.STATUS_RESP && (respMsg.has(OMMMsg.HAS_STATE))){
        	byte streamState= respMsg.getState().getStreamState();
        	byte dataState = respMsg.getState().getDataState();
			byte msgType = respMsg.getMsgType();
			String state = respMsg.getState().toString();
			responseMsg = XmlMessageUtil.generateStatusResp(state,streamState,dataState,msgType);
			XmlMessageUtil.addLocateInfo(responseMsg, msgType, RFAServerManager.sequenceNo.getAndIncrement(), 0);
			GateWayResponser.sentMrketPriceToSubsribeChannel(responseMsg, clientRequestItemName);
			_logger.warn("RFA server has new state. streamState:"+streamState+" datasstate "+dataState);
			return;
        }
        if (respMsg.getMsgType() == OMMMsg.MsgType.REFRESH_RESP){
        	initialDocument = responseMsg;
        }
        
        // Status response can contain group id
		if ((respMsg.getMsgType() == OMMMsg.MsgType.REFRESH_RESP)
				|| (respMsg.getMsgType() == OMMMsg.MsgType.STATUS_RESP && respMsg.has(OMMMsg.HAS_ITEM_GROUP))) {
			OMMItemGroup group = respMsg.getItemGroup();
			Handle itemHandle = event.getHandle();
			_itemGroupManager.applyGroup(itemHandle, group);
		}
        
		XmlMessageUtil.addLocateInfo(responseMsg, respMsg.getMsgType(), RFAServerManager.sequenceNo.getAndIncrement(), 0);
		GateWayResponser.sentMrketPriceToSubsribeChannel(responseMsg, clientRequestItemName);
        if(responseMsg != null){
//        	List<String> clientNameList =  GateWayServer._requestItemNameList.get(clientRequestItemName);
//        	if(clientNameList != null){
//	        	for(String clientName : clientNameList){
//		        	session = RFASocketServer._clientRequestSession.get(clientName+clientRequestItemName);
        	
//	        	}
//        	}
        	long endTime = System.currentTimeMillis();
        	_logger.info("publish Item "+clientRequestItemName+" use time "+(endTime-startTime)+" microseconds");
//            _mainApp.updateResponseStat((endTime-startTime),responseMsg);
        }
        
    }
    
    public void sendInitialDocument(int channelId) {
    	if(initialDocument!=null){
    		long startTime = System.currentTimeMillis();
    		XmlMessageUtil.addStartHandleTime(initialDocument, startTime);
    		GateWayResponser.sendSnapShotToChannel(OMMMsg.MsgType.REFRESH_RESP, initialDocument, clientRequestItemName,channelId);
    	}
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
    
    /**
     * Gathers, calculate, displays and writes the metrics. Called by
     * application thread periodically.
     */
    public static void runMetricsForCurrentInterval()
    {
        // calculate cycle metrics
        _request_cycleStats.calculateDifference(_request_count);
        _refresh_cycleStats.calculateDifference(_refresh_count);
        _update_cycleStats.calculatePeriodicRate(_update_count,_update_begin_milliTime);

        _post_received_cycleStats.calculateDifference(_post_received_count);
        _post_resent_cycleStats.calculateDifference(_post_resent_count);

        _resourceStats.updateResourceStatistics();

        String s = null;

        // display statistics to console
//        if (_appInput.bDisplayStats == true){
            _close_cycleStats.calculateDifference(_close_count);

            // update console metrics for this cycle
            s = String.format("%03d: UpdRate: %,6d, CPU: %s, Mem: %6.2fMB%n", _timeline,
                              _update_cycleStats.periodicRate,
                              _resourceStats.getCurrentCPULoadAsPercentString(),
                              _resourceStats.currentMemoryUsage);

            _consoleLogger.print(s);

            // request console metrics for this cycle
            if (_request_cycleStats.periodicCount > 0)
            {
                _consoleLogger.print(String.format(
                        "- Received %,6d item requests (total: %,6d), sent %,6d images (total: %,6d)%n",
                        _request_cycleStats.periodicCount,
                        _request_count,
                        _refresh_cycleStats.periodicCount,
                        _refresh_count));
            }

            // close console metrics for this cycle
            if (_close_cycleStats.periodicCount > 0)
            {
                _consoleLogger.print(String.format("- Received %,6d closes%n", _close_cycleStats.periodicCount));
            }

            // post console metrics for this cycle
            if (_post_received_cycleStats.periodicCount > 0)
            {
                _consoleLogger.print(String.format("- Posting: received %,6d, sent %,6d%n",
                      _post_received_cycleStats.periodicCount,
                      _post_resent_cycleStats.periodicCount));
            }

        // write periodic statistics to output stats file ( provStat.out )

        // "Update rate: %8d, CPU: %6.2f, Mem: %6.2fMB%n",
        s = String.format("%s, %d, %d, %d, %d, %s, %.2f%n",
                          _outputFormatter.getDateTimeAsString(),
                          _request_cycleStats.periodicCount, 
                          _refresh_cycleStats.periodicCount,
                          _update_cycleStats.periodicCount, 
                          _post_resent_cycleStats.periodicCount,
                          _resourceStats.getCurrentCPULoadAsString(),
                          _resourceStats.currentMemoryUsage);

        _statsStringBuffer.append(s);

        // flush to file for every 1k length;
        // this avoids file access for every line
        if (_statsStringBuffer.length() >= 1024)
        {
            _statsFileLogger.print(_statsStringBuffer.toString());
            _statsStringBuffer.setLength(0);
        }

        // increment the time line
//        _timeline += _appInput.statsMonitorIntervalInSecs;
    }


	public Handle getItemHandle() {
		return itemHandle;
	}


	public void setItemHandle(Handle itemHandle) {
		this.itemHandle = itemHandle;
	}
	
}
