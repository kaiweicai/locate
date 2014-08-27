package com.locate.rmds.processer;

import org.apache.log4j.Logger;

import com.locate.bridge.GateWayResponser;
import com.locate.common.model.LocateUnionMessage;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.parser.LocateOMMParser;
import com.locate.rmds.statistic.LogTool;
import com.locate.rmds.statistic.OutputFormatter;
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
public class OneTimeItemManager implements Client
{
	Handle  itemHandle;
	QSConsumerProxy _mainApp;
    static Logger _logger = Logger.getLogger(OneTimeItemManager.class.getName());
    ItemGroupManager _itemGroupManager;
    public String clientRequestItemName;
//    public String clientName;
    public byte responseMessageType;

    private	String	_className = "ItemManager";

	private Integer channelID = 0;
	
    // display
    static LogTool _consoleLogger;
    static LogTool _statsFileLogger;
    static OutputFormatter _outputFormatter;
    static StringBuilder _statsStringBuffer;
    static int _timeline;
    private LocateOMMParser locateGenericOMMParser = new LocateOMMParser();
    // constructor
    public OneTimeItemManager(QSConsumerProxy mainApp, ItemGroupManager itemGroupManager,int channelID)
    {
    	this._mainApp = mainApp;
        this._itemGroupManager = itemGroupManager;
        this.channelID = channelID;
    }
    
 // creates streaming request messages for items and register them to RFA
    public void sendOneTimeRequest(String pItemName,byte responseMsgType)
    {
    	this.responseMessageType = responseMsgType;
    	_logger.info(_className+".sendOneTimeRequest: Sending item("+pItemName+") requests to server ");
        String serviceName = _mainApp.serviceName;
        this.clientRequestItemName = pItemName;
        String[] itemNames = {pItemName};
        short msgModelType = RDMMsgTypes.MARKET_PRICE;

        OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();

        //Preparing item request message
        OMMPool pool = _mainApp.getPool();
        OMMMsg ommmsg = pool.acquireMsg();
        //NONSTREAMING_REQ为只取一个snapshort.
        ommmsg.setMsgType(OMMMsg.MsgType.NONSTREAMING_REQ);
        ommmsg.setMsgModelType(msgModelType);
//        ommmsg.setIndicationFlags(OMMMsg.Indication.REFRESH);
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
			_logger.info(_className + ": Subscribing one time to " + itemName);

			ommmsg.setAttribInfo(serviceName, itemName, RDMInstrument.NameType.RIC);

			// Set the message into interest spec
			ommItemIntSpec.setMsg(ommmsg);
			itemHandle = _mainApp.getOMMConsumer().registerClient(_mainApp.getEventQueue(), ommItemIntSpec, this, null);
			_itemGroupManager.addItem(serviceName, itemName, itemHandle);
		}
        pool.releaseMsg(ommmsg);
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
        LocateUnionMessage locateMessage = locateGenericOMMParser.parse(respMsg, clientRequestItemName);
        
        // Status response can contain group id
		if ((respMsg.getMsgType() == OMMMsg.MsgType.REFRESH_RESP)
				|| (respMsg.getMsgType() == OMMMsg.MsgType.STATUS_RESP && respMsg.has(OMMMsg.HAS_ITEM_GROUP))) {
			OMMItemGroup group = respMsg.getItemGroup();
			Handle itemHandle = event.getHandle();
			_itemGroupManager.applyGroup(itemHandle, group);
		}
        
		GateWayResponser.sendSnapShotToChannel(locateMessage, channelID);
        if(locateMessage != null){
        	long endTime = System.currentTimeMillis();
        	_logger.info("publish Item "+clientRequestItemName+" use time "+(endTime-startTime)+" microseconds");
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
                return;
            }


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
}
