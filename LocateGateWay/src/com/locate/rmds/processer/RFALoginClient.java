package com.locate.rmds.processer;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.springframework.stereotype.Component;

import com.locate.bridge.GateWayResponser;
import com.locate.common.XmlMessageUtil;
import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.dict.RDMServiceInfo;
import com.locate.rmds.dict.ServiceInfo;
import com.locate.rmds.processer.face.INotifier;
import com.locate.rmds.util.GenericRFALoginParser;
import com.reuters.rfa.common.Client;
import com.reuters.rfa.common.Event;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.dictionary.FieldDictionary;
import com.reuters.rfa.omm.OMMElementEntry;
import com.reuters.rfa.omm.OMMElementList;
import com.reuters.rfa.omm.OMMEncoder;
import com.reuters.rfa.omm.OMMFilterList;
import com.reuters.rfa.omm.OMMMap;
import com.reuters.rfa.omm.OMMMapEntry;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMNumeric;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.omm.OMMSeries;
import com.reuters.rfa.omm.OMMState;
import com.reuters.rfa.omm.OMMTypes;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.rdm.RDMService;
import com.reuters.rfa.rdm.RDMUser;
import com.reuters.rfa.session.omm.OMMItemEvent;
import com.reuters.rfa.session.omm.OMMItemIntSpec;

// This class is a Client implementation that is utilized to handle login activities
// between application and RFA.
// An instance of this class is created by QSConsumerDemo.
// This class performs the following functions:
// - Creates and encodes login request message (method encodeLoginReqMsg()).
// - Registers the client (itself) with RFA (method sendRequest()). The registration
// will cause RFA to send login request. RFA will return back a handle instance.
// - Unregisters the client in RFA (method closeRequest()).
// - Processes events for this client (method processEvent()). processEvent() method
// must be implemented by a class that implements Client interface.
//
// The class keeps the following members:
// Handle 	_loginHandle - a handle returned by RFA on registering the client
//							application uses this handle to identify this client
// QSConsumerDemo _mainApp - main application class

@Component
public class RFALoginClient implements Client
{
    Handle _loginHandle;
    @Resource
    QSConsumerProxy _mainApp;
    @Resource(name="emailNotifier")
    INotifier notifier;
    public static byte STREAM_STATE = 0;
    public static byte DATA_STATE = 0;
    static Logger _logger = Logger.getLogger(RFALoginClient.class.getName());
	public static String STATE = "";
//    static Logger _logger;
	Map<String, ServiceInfo> _services = new HashMap<String, ServiceInfo>();
	private HashMap<Handle, Integer> _dictHandles=new HashMap<Handle, Integer>();
	FieldDictionary _dictionary=QSConsumerProxy.dictionary;;
    // Gets encoded streaming request messages for Login and register it to RFA
    public void sendRequest()
    {
        OMMMsg ommmsg = encodeLoginReqMsg();
        OMMItemIntSpec ommItemIntSpec = new OMMItemIntSpec();
        ommItemIntSpec.setMsg(ommmsg);
        _logger.info("Sending login request");
        _loginHandle = _mainApp.getOMMConsumer().registerClient(
                        _mainApp.getEventQueue(), ommItemIntSpec, this, null);
    }

    // Encodes request message for login
    private OMMMsg encodeLoginReqMsg()
    {
        String username = "deve01";
//        try { username = System.getProperty("user.name"); }  catch( Exception e ) {}
        String application = "256";
        String position = "1.1.1.1/net";
        try { position = InetAddress.getLocalHost().getHostAddress() + "/" +
              InetAddress.getLocalHost().getHostName(); }  catch( Exception e ) {}

        OMMEncoder encoder = _mainApp.getEncoder();
        OMMPool pool = _mainApp.getPool();
        encoder.initialize(OMMTypes.MSG, 500);
        OMMMsg msg = pool.acquireMsg();
        msg.setMsgType(OMMMsg.MsgType.REQUEST);
        msg.setMsgModelType(RDMMsgTypes.LOGIN);
        msg.setIndicationFlags(OMMMsg.Indication.REFRESH);
        msg.setAttribInfo(null, username, RDMUser.NameType.USER_NAME);

        encoder.encodeMsgInit(msg, OMMTypes.ELEMENT_LIST, OMMTypes.NO_DATA);
        encoder.encodeElementListInit(OMMElementList.HAS_STANDARD_DATA, (short)0, (short) 0);
        encoder.encodeElementEntryInit(RDMUser.Attrib.ApplicationId, OMMTypes.ASCII_STRING);
        encoder.encodeString(application, OMMTypes.ASCII_STRING);
        encoder.encodeElementEntryInit(RDMUser.Attrib.Position, OMMTypes.ASCII_STRING);
        encoder.encodeString(position, OMMTypes.ASCII_STRING);
        encoder.encodeElementEntryInit(RDMUser.Attrib.Role, OMMTypes.UINT);
	    encoder.encodeUInt((long)RDMUser.Role.CONSUMER);
        encoder.encodeAggregateComplete();

        //Get the encoded message from the encoder
        OMMMsg encMsg = (OMMMsg)encoder.getEncodedObject();

        //Release the message that own by the application
        pool.releaseMsg(msg);

        return encMsg; //return the encoded message
    }

    // Unregisters/unsubscribes login handle
    public void closeRequest()
    {
    	if (_loginHandle != null) 
    	{
	        _mainApp.getOMMConsumer().unregisterClient(_loginHandle);
	        _loginHandle = null;
    	}
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
    		return;
    	}

//        _logger.info(_className+".processEvent: Received Login Response ");

        OMMItemEvent ie = (OMMItemEvent) event;
        OMMMsg respMsg = ie.getMsg();

        switch (respMsg.getMsgModelType())
        {
            case RDMMsgTypes.LOGIN:
//                processLoginMsg(respMsg);
                break;
            case RDMMsgTypes.DIRECTORY:
                processDirectoryMsg(respMsg);
                return;
            case RDMMsgTypes.DICTIONARY:
                processDictionaryMsg(respMsg, event.getHandle());
                return;
            default:
        }
        
        
        // The login is unsuccessful, RFA forwards the message from the network
        if (respMsg.isFinal()) 
        {
        	_logger.info(" Login Response message is final.");
        	GenericRFALoginParser.parse(respMsg,null);
        	_mainApp.loginFailure();
        	return;
        }
        
        //RFA Server status response, forward status to customer.
		if (respMsg.has(OMMMsg.HAS_STATE)) {
			RFALoginClient.STREAM_STATE = respMsg.getState().getStreamState();
			RFALoginClient.DATA_STATE = respMsg.getState().getDataState();
			RFALoginClient.STATE = respMsg.getState().toString();
			byte msgType =respMsg.getMsgType();
			Document responseMsg = XmlMessageUtil.generateStatusResp(RFALoginClient.STATE,RFALoginClient.STREAM_STATE,RFALoginClient.DATA_STATE,msgType);
			XmlMessageUtil.addStartHandleTime(responseMsg, startTime);
			GateWayResponser.brodcastStateResp(responseMsg);
		}
		// The login is successful, RFA forwards the message from the network
		if ((respMsg.getMsgType() == OMMMsg.MsgType.STATUS_RESP) && (respMsg.has(OMMMsg.HAS_STATE))
				&& (respMsg.getState().getStreamState() == OMMState.Stream.OPEN)
				&& (respMsg.getState().getDataState() == OMMState.Data.OK)) {
			_logger.info("Received Login STATUS OK Response");
			GenericRFALoginParser.parse(respMsg, "RFALogin");
			_mainApp.processLogin();
			_mainApp.registerDirectory(this);
		} else if(respMsg.getMsgType() == OMMMsg.MsgType.STATUS_RESP&&respMsg.has(OMMMsg.HAS_STATE)
				&& (respMsg.getState().getStreamState() == OMMState.Stream.OPEN)
				&& respMsg.getState().getDataState() == OMMState.Data.SUSPECT){
			_logger.error("RFA server has new state. The server has been suspect!\n Received Login Response - "
					+ OMMMsg.MsgType.toString(respMsg.getMsgType()));
			_mainApp.loginFailure();
			notifier.notifyAdmin(
					"RFA server new state\n",
					"RFA server has new state. The server state is "
							+ (respMsg.has(OMMMsg.HAS_STATE) ? respMsg.getState() : "has no stat"));
			GenericRFALoginParser.parse(respMsg, "RFALogin");
		}else{
			_logger.error("Login not success.Please check!\n Received Login Response - "
					+ OMMMsg.MsgType.toString(respMsg.getMsgType()));
			_mainApp.loginFailure();
			GenericRFALoginParser.parse(respMsg, "RFALogin");
		}
    }
    
    protected void processDirectoryMsg(OMMMsg msg)
    {
    	GenericRFALoginParser.parse(msg,"DIRECTORY");

        if (msg.getDataType() == OMMTypes.NO_DATA)
        {
            // probably status
            return;
        }
        if (msg.getDataType() != OMMTypes.MAP)
        {

            _logger.info("Directory payload must be a Map");
            return;
        }

        Set<String> dictionariesUsed = new HashSet<String>();
        OMMMap map = (OMMMap)msg.getPayload();
        for (Iterator<?> miter = map.iterator(); miter.hasNext();)
        {
            boolean isNew = false;
            OMMMapEntry mentry = (OMMMapEntry)miter.next();
            String serviceName = mentry.getKey().toString();
            if (mentry.getAction() == OMMMapEntry.Action.ADD)
            {
                RDMServiceInfo service = (RDMServiceInfo)_services.get(serviceName);
                if (service == null)
                {
                    service = new RDMServiceInfo(serviceName);
                    _services.put(serviceName, service);
                    isNew = true;
                }

                OMMFilterList flist = (OMMFilterList)mentry.getData();
                service.process(flist);

//                if (_directoryClient != null)
//                {
//                    if (isNew)
//                        _directoryClient.processNewService(service);
//                    else
//                        _directoryClient.processServiceUpdated(service);
//                }

                String[] dictArray = (String[])service.get(RDMService.Info.DictionariesUsed);
                if (dictArray != null)
                    for (int i = 0; i < dictArray.length; i++)
                        dictionariesUsed.add(dictArray[i]);
            }
            else if (mentry.getAction() == OMMMapEntry.Action.UPDATE)
            {
                RDMServiceInfo service = (RDMServiceInfo)_services.get(serviceName);
                if (service == null)
                    continue;
                OMMFilterList flist = (OMMFilterList)mentry.getData();
                service.process(flist);

//                if (_directoryClient != null)
//                {
//                    _directoryClient.processServiceUpdated(service);
//                }

                String[] dictArray = (String[])service.get(RDMService.Info.DictionariesUsed);
                for (int i = 0; i < dictArray.length; i++)
                    dictionariesUsed.add(dictArray[i]);
            }
            else
            // (mentry.getAction() == OMMMapEntry.DELETE_ACTION)
            {
                ServiceInfo si = _services.remove(serviceName);
//                if (_directoryClient != null)
//                    _directoryClient.processServiceRemoved(si);
            }
        }

        // download the dictionary used
        if (true)
        {
        	_mainApp.getDictionaries(dictionariesUsed,this);
        }

//        if (_pendingDictionaries.isEmpty() && !_isComplete
//                && (_serviceName.length() == 0 || _services.containsKey(_serviceName)))
//        {
//            _isComplete = true;
//            if (_client != null)
//                _client.processComplete();
//        }

    }
    
	protected void processDictionaryMsg(OMMMsg msg, Handle handle) {
		_logger.info("Received dictionary: " + msg.getAttribInfo().getName());

		int msgType = msg.getMsgType();

		if ((msgType == 7) || (msgType == 8)) {
			GenericRFALoginParser.parse(msg,"Dictionary");
			return;
		}

		OMMSeries series = (OMMSeries) msg.getPayload();

		Integer dictType = (Integer) this._dictHandles.get(handle);
		int dictionaryType;
		if (dictType == null) {
			dictionaryType = getDictionaryType(series);
			this._dictHandles.put(handle, new Integer(dictionaryType));
		} else {
			dictionaryType = dictType.intValue();
		}

		if (dictionaryType == 1) {
			FieldDictionary.decodeRDMFldDictionary(this._dictionary, series);
			if (msg.isSet(4)) {
				System.out.println("Field Dictionary Refresh is complete");
				this._dictHandles.remove(handle);
			}
		} else if (dictionaryType == 2) {
			FieldDictionary.decodeRDMEnumDictionary(this._dictionary, series);
			if (msg.isSet(4)) {
				System.out.println("Enum Dictionary Refresh is complete");
				this._dictHandles.remove(handle);
			}
		}

		if (!(msg.isSet(4)))
			return;
//		String dictionaryName = (String) this._pendingDictionaries.remove(handle);
//		this._loadedDictionaries.add(dictionaryName);
//		if ((this._pendingDictionaries.isEmpty()) && (!(this._isComplete))) {
//			this._isComplete = true;
//			if (this._client != null) {
//				this._client.processComplete();
//			}
//		}
//		GenericOMMParser.initializeDictionary(this._dictionary);
	}
	
	private int getDictionaryType(OMMSeries series) {
		OMMElementList elist = (OMMElementList) series.getSummaryData();
		OMMElementEntry eentry = elist.find("Type");
		if (eentry != null) {
			return (int) ((OMMNumeric) eentry.getData()).toLong();
		}
		return 0;
	}
    
    public Handle getHandle()
    {
    	return _loginHandle;
    }
}
