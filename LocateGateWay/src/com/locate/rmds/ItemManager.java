package com.locate.rmds;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;


import com.locate.bridge.GateWayResponser;
import com.locate.common.GateWayMessageTypes;
import com.locate.gate.GateWayServer;
import com.locate.gate.hanlder.GatewayServerHandler;
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

public class ItemManager implements Client
{
	Handle  itemHandle;
	QSConsumerProxy _mainApp;
    static Logger _logger = Logger.getLogger(ItemManager.class.getName());
    ItemGroupManager _itemGroupManager;
    public String clientRequestItemName;
//    public String clientName;
    public byte responseMessageType;

    private	String	_className = "ItemManager";

	private Integer channelID = 0;
    // constructor
//    public ItemManager(QSConsumerProxy mainApp, ItemGroupManager itemGroupManager,String clientName)
//    {
//    	this._mainApp = mainApp;
//        this._itemGroupManager = itemGroupManager;
//        this.clientName = clientName;
//    }
    // constructor
    public ItemManager(QSConsumerProxy mainApp, ItemGroupManager itemGroupManager)
    {
    	this._mainApp = mainApp;
        this._itemGroupManager = itemGroupManager;
    }
    // creates streaming request messages for items and register them to RFA
    public void sendRequest(String pItemName,byte responseMsgType, int channelID)
    {
    	this.channelID = channelID;
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
			this.clientRequestItemName = itemName;
			_logger.info(_className + ": Subscribing to " + itemName);

			ommmsg.setAttribInfo(serviceName, itemName, RDMInstrument.NameType.RIC);

			// Set the message into interest spec
			ommItemIntSpec.setMsg(ommmsg);
			/**
			 * 重要逻辑:
			 * 向datasource注册感兴趣的Item,EventQueue使用QSConsumerProxy的Queue,监听器就是itemManager自己.
			 * dataSource产生感兴趣的时间后通过eventQueue向外发布.ItemManager能够收到这个事件.
			 * ItemManager由于持有channelId,可以向gaichannel写返回的数据,
			 * 该channel对应订阅了该item的client.
			 */
			itemHandle = _mainApp.getOMMConsumer().registerClient(_mainApp.getEventQueue(), ommItemIntSpec, this, null);
			_itemGroupManager.addItem(serviceName, itemName, itemHandle);
		}
        pool.releaseMsg(ommmsg);
    }

    // Unregisters/unsubscribes login handle
    public void closeRequest()
    {
    	 _itemGroupManager._handles.remove(itemHandle);
         _mainApp.getOMMConsumer().unregisterClient(itemHandle);
    }

    // This is a Client method. When an event for this client is dispatched,
    // this method gets called.
    public void processEvent(Event event)
    {
    	long startTime = System.currentTimeMillis();
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
        // Status response can contain group id
		if ((respMsg.getMsgType() == OMMMsg.MsgType.REFRESH_RESP)
				|| (respMsg.getMsgType() == OMMMsg.MsgType.STATUS_RESP && respMsg.has(OMMMsg.HAS_ITEM_GROUP))) {
			OMMItemGroup group = respMsg.getItemGroup();
			Handle itemHandle = event.getHandle();
			_itemGroupManager.applyGroup(itemHandle, group);
		}
        
		Document responseMsg = GenericOMMParser.parse(respMsg, clientRequestItemName);
		GateWayResponser.sentResponseMsg(respMsg.getMsgType(), responseMsg, channelID);
        if(responseMsg != null){
//        	List<String> clientNameList =  GateWayServer._requestItemNameList.get(clientRequestItemName);
//        	if(clientNameList != null){
//	        	for(String clientName : clientNameList){
//		        	session = RFASocketServer._clientRequestSession.get(clientName+clientRequestItemName);
        	
//	        	}
//        	}
        	long endTime = System.currentTimeMillis();
//            _mainApp.updateResponseStat((endTime-startTime),responseMsg);
        }
        
    }
}
