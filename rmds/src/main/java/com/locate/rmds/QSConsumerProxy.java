package com.locate.rmds;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.locate.common.LocateMessageTypes;
import com.locate.common.RmdsDataCache;
import com.locate.common.SystemConstant;
import com.locate.common.utils.SystemProperties;
import com.locate.rmds.client.RFAUserManagement;
import com.locate.rmds.dict.DirectoryClient;
import com.locate.rmds.dict.RDMServiceInfo;
import com.locate.rmds.dict.ServiceInfo;
import com.locate.rmds.processer.ItemGroupManager;
import com.locate.rmds.processer.ItemManager;
import com.locate.rmds.processer.NewsItemManager;
import com.locate.rmds.processer.OneTimeItemManager;
import com.locate.rmds.processer.PersistentItemManager;
import com.locate.rmds.processer.RFALoginClient;
import com.locate.rmds.processer.face.IProcesser;
import com.reuters.rfa.common.Client;
import com.reuters.rfa.common.Context;
import com.reuters.rfa.common.DeactivatedException;
import com.reuters.rfa.common.DispatchQueueInGroupException;
import com.reuters.rfa.common.EventQueue;
import com.reuters.rfa.common.EventSource;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.config.ConfigDb;
import com.reuters.rfa.dictionary.DictionaryException;
import com.reuters.rfa.dictionary.FieldDictionary;
import com.reuters.rfa.omm.OMMAttribInfo;
import com.reuters.rfa.omm.OMMEncoder;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.rdm.RDMDictionary;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.rdm.RDMService;
import com.reuters.rfa.session.Session;
import com.reuters.rfa.session.omm.OMMConsumer;
import com.reuters.rfa.session.omm.OMMItemIntSpec;
/**  
*  作者:Cloud wei   
*  E-mail:kaiweicai@163.com   
*  创建时间：2014.5.26   
*  类说明  netty game  
*/  
@Service
public class QSConsumerProxy{
	static Logger logger = Logger.getLogger(QSConsumerProxy.class.getName());

	// RFA objects
	protected Session _session;
	protected EventQueue _eventQueue;
	protected OMMConsumer _consumer;
	@Resource
	protected RFALoginClient _loginClient;
	@Resource
	protected ItemGroupManager _itemGroupManager;
	ItemManager itemManager;
	protected OMMEncoder _encoder;
	protected OMMPool _pool;
	public String serviceName;
	private boolean all = true;
	protected static String _configFile = "config/rfaConfig.properties";
	public static HashMap<Integer, FieldDictionary> DICTIONARIES = new HashMap<Integer, FieldDictionary>();
	public static FieldDictionary dictionary;
	DecimalFormat dataFormat = new DecimalFormat("0.00");
	List<String> _loadedDictionaries;
	Map<Handle, String> _pendingDictionaries;
	Map<String, ServiceInfo> _services;
	private boolean dispath = true;
	DirectoryClient _directoryClient;
	// class constructor
	public QSConsumerProxy() {
		System.out
				.println("*****************************************************************************");
		System.out
				.println("*          Begin Start locate gate way Consumer Program                     *");
		System.out
				.println("*****************************************************************************");
	}

	// This method is responsible for initialization
	// As shown in QuickStartGuide, the initialization includes the following
	// steps:
	// 1. Initialize context
	// 2. Create event queue
	// 3. Initialize logger
	// 4. Acquire session
	// 5. Create event source
	// 6. Load dictionaries
	// It also instantiates application specific objects: memory pool, encoder.
	@PostConstruct
	public void init() {
		// Context.initialize();
		// 1. Initialize system config
//		ConfigDb configDb = new ConfigDb();
//		Context.initialize();
		_loadedDictionaries = new LinkedList<String>();
		_pendingDictionaries = new HashMap<Handle, String>();
		_services = new HashMap<String, ServiceInfo>();
		//
		SystemProperties.init(_configFile);
		
		
		// Context.initialize();
		// 1. Initialize system config
		ConfigDb configDb = new ConfigDb();
		configDb.addVariable("myNamespace.Connections.mySession.connectionType",
				SystemProperties.getProperties(SystemProperties.RFA_CONNETION_TYPE));
		configDb.addVariable("myNamespace.Connections.mySession.serverList", SystemProperties.getProperties(SystemProperties.RFA_CONNETION_SERVER_LIST));
		configDb.addVariable("myNamespace.Connections.mySession.portNumber", SystemProperties.getProperties(SystemProperties.RFA_CONNETION_PORTNUMBER));
		// configDb.addVariable("myNamespace.Connections.consConnection.userName",
		// "");
		configDb.addVariable("myNamespace.Sessions.mySession.connectionList", "mySession");
		Context.initialize(configDb);
		
		
//		try {
//            Preferences.importPreferences(new FileInputStream(SystemProperties.getProperties(SystemProperties.RFA_CONFIG_FILE)));
//        } catch (IOException e) {
//            logger.error("RFA file import error!",e);
//            System.exit(-1);
//        } catch (InvalidPreferencesFormatException e) {
//        	logger.error("preference format error!",e);
//            System.exit(-1);
//        }
		// 2. Create an Event Queue
		_eventQueue = EventQueue.create("myEventQueue");

		// 3. Acquire a Session
		_session = Session.acquire("myNamespace::mySession");
//		_session = Session.acquire("myNamespace::mySession");
		
		if (_session == null) {
			logger.error("Could not acquire session.");
			Context.uninitialize();
			System.exit(1);
		}

		// 4. Create an OMMConsumer event source
		_consumer = (OMMConsumer) _session.createEventSource(EventSource.OMM_CONSUMER, "myOMMConsumer", true);
		// Initialize item group manager
		// 5. Load dictionaries
		// Application may choose to down-load the enumtype.def and
		// RWFFldDictionary
		// This example program loads the dictionaries from files.
		String fieldDictionaryFilename = SystemProperties.getProperties(SystemProperties.RFA_FIELD_FILE);
		String enumDictionaryFilename = SystemProperties.getProperties(SystemProperties.RFA_ENUM_FILE);
		try {
			dictionary = initializeDictionary(fieldDictionaryFilename, enumDictionaryFilename);
			_loadedDictionaries.add("RWFFld");
			_loadedDictionaries.add("RWFEnum");
		} catch (DictionaryException ex) {
			logger.error("ERROR: Unable to initialize dictionaries");
			logger.error(ex.getMessage());
			if (ex.getCause() != null)
				System.err.println(": " + ex.getCause().getMessage());
			cleanup();
			return;
		}

		// Create a OMMPool.
		_pool = OMMPool.create();

		// Create an OMMEncoder
		_encoder = _pool.acquireEncoder();

		if (all) {
			// Load RFA user config
			RFAUserManagement.init();
			this.serviceName = SystemProperties
					.getProperties(SystemProperties.RFA_SERVICE_NAME);
		}
		login();
		//newsItemRequests();
	}

	// This method utilizes the LoginClient class to send login request
	public void login() {
		// Send login request
		_loginClient.sendRequest();
	}

	public Handle registerDirectory(Client client)
    {
        OMMItemIntSpec spec = new OMMItemIntSpec();
        OMMMsg msg = _pool.acquireMsg();
        msg.setMsgType(OMMMsg.MsgType.REQUEST);
        msg.setMsgModelType(RDMMsgTypes.DIRECTORY);
        msg.setIndicationFlags(OMMMsg.Indication.REFRESH);
        OMMAttribInfo ai = _pool.acquireAttribInfo();
        if (serviceName.length() > 0)
            ai.setServiceName(serviceName);
        ai.setFilter(RDMService.Filter.INFO | RDMService.Filter.STATE);
        msg.setAttribInfo(ai);
        spec.setMsg(msg);
        Handle handle = _consumer.registerClient(_eventQueue, spec, client, null);
        return handle;
    }
	
	 /*
     * get all the dictionaries included in the argument Set
     */
    public void getDictionaries(Set<String> dictionariesUsed,Client client)
    {
        for (Iterator<String> iter = dictionariesUsed.iterator(); iter.hasNext();)
        {
            String dictionaryName = iter.next();
            if (!_loadedDictionaries.contains(dictionaryName)
                    && !_pendingDictionaries.containsValue(dictionaryName))
            {
                // find which service provides the dictionary
                String serviceName = findServiceForDictionary(dictionaryName);
                if (serviceName == null)
                {
                    logger.info("No service provides dictionary: " + dictionaryName);
                    continue;
                }
                openFullDictionary(serviceName, dictionaryName,client);
            }
        }
    }
    
    /*
     * Create a dictionary request message and register the request
     */
	private void openFullDictionary(String serviceName, String dictionaryName , Client client) {
		OMMItemIntSpec spec = new OMMItemIntSpec();
		OMMMsg msg = _pool.acquireMsg();
		msg.setMsgType(OMMMsg.MsgType.REQUEST);
		msg.setMsgModelType(RDMMsgTypes.DICTIONARY);
		msg.setIndicationFlags(OMMMsg.Indication.REFRESH | OMMMsg.Indication.NONSTREAMING);
		OMMAttribInfo ai = _pool.acquireAttribInfo();
		ai.setServiceName(serviceName);
		ai.setName(dictionaryName);
		ai.setFilter(RDMDictionary.Filter.NORMAL);
		msg.setAttribInfo(ai);
		spec.setMsg(msg);
		Handle handle = _consumer.registerClient(_eventQueue, spec, client, null);
		_pool.releaseMsg(msg);
		_pendingDictionaries.put(handle, dictionaryName);
	}
	
	public void addNewService(String serviceName) {
		ServiceInfo service = new RDMServiceInfo(serviceName);
		_services.put(serviceName, service);
		if (_directoryClient != null) {
			_directoryClient.processNewService(service);
		}
	}
	
	 private String findServiceForDictionary(String dictionaryName)
	    {
	        for (Iterator<ServiceInfo> iter = _services.values().iterator(); iter.hasNext();)
	        {
	            ServiceInfo service = iter.next();

	            // stop, if serviceState is DOWN (0) or not available
	            Object oServiceState = service.get(RDMService.SvcState.ServiceState);
	            if(oServiceState == null)
	            	continue;
	            int serviceState = Integer.parseInt((String) oServiceState);
	            if( serviceState == 0 )
	            	continue;
	            
	            // stop, if acceptRequests is false (0) or not available
	            Object oAcceptingRequests = service.get(RDMService.SvcState.ServiceState);
	            if(oAcceptingRequests == null)
	            	continue;
	            int acceptingRequests = Integer.parseInt((String) oAcceptingRequests);
	            if( acceptingRequests == 0 )
	            	continue;

	            String[] dictionariesProvided = (String[])service
	                    .get(RDMService.Info.DictionariesProvided);
	            if (dictionariesProvided == null)
	                continue;
	            
	            for (int i = 0; i < dictionariesProvided.length; i++)
	            {
	                if (dictionariesProvided[i].equals(dictionaryName))
	                    return service.getServiceName();
	            }
	        }
	        return null;
	    }
	
	// This method is called by _loginClient upon receiving successful login
	// response.
	public void loginSuccess() {
		logger.info("QSConsumerDemo Login successful");
		RFAServerManager.setConnectedDataSource(true);
	}
	

	// This method is called when the login was not successful
	// The application exits
	public void loginFailure() {
		logger.error("Login has been denied / rejected / closed ");
		RFAServerManager.setConnectedDataSource(false);
	}

	// This method utilizes ItemManager class to request items
	// public ItemManager itemRequests(String itemName,String clientName,byte
	// responseMsgType)
	// {
	// //Initialize item manager for item domains
	// ItemManager _itemManager = new ItemManager(this,
	// _itemGroupManager,clientName);
	// // Send requests
	// _itemManager.sendRequest(itemName,responseMsgType);
	// return _itemManager;
	// }

	// This method utilizes ItemManager class to request items
	public ItemManager itemRequests(String itemName, byte responseMsgType,int channelId) {
		Map<String,ItemManager> subscribeItemManagerMap = RmdsDataCache.RIC_ITEMMANAGER_Map;
		boolean needRenewSubscribeItem=checkSubscribeStatus(itemName);
		if(needRenewSubscribeItem){
			//已经订阅过该产品,只需要发送一个一次订阅请求,返回一个snapshot即可.
			OneTimeItemManager oneTimeItemManager =  new OneTimeItemManager(this, _itemGroupManager,channelId);
			oneTimeItemManager.sendOneTimeRequest(itemName, responseMsgType);
//			ItemManager subscibeItemManager =  subscribeItemManagerMap.get(itemName);
//			subscibeItemManager.sendInitialDocument(channelId);
			return null;
		}else{
			//一个产品对应一个itemManager对象
			itemManager=SystemConstant.springContext.getBean("itemManager",ItemManager.class);
			subscribeItemManagerMap.put(itemName, itemManager);
			// Send requests
			itemManager.sendRicRequest(itemName, responseMsgType);
			return itemManager;
		}
		
	}

//	public void linkItemRequests(String itemName, String clientName,
//			byte responseMsgType) {
//		// Initialize item manager for item domains
//		LinkItemManager linkItemManager = new LinkItemManager(this,
//				_itemGroupManager, clientName);
//		// Send requests
//		linkItemManager.sendRequest(itemName, responseMsgType);
//	}

//	@Deprecated
//	public void updateResponseStat(long processTimes, Document responseData) {
//		RFApplication.totalResponseNumber++;
//		RFApplication.totalProcessTime += processTimes;
//		RFApplication.responseNumber.setText(String
//				.valueOf(RFApplication.totalResponseNumber));
//		double totalTimes = RFApplication.totalProcessTime;
//		String avgTimes = dataFormat.format(totalTimes
//				/ RFApplication.totalResponseNumber);
//		RFApplication.avgTimes.setText(avgTimes);
//		if (responseData != null) {
//			RFApplication.showLog.insert( HtmlUtils.htmlUnescape(responseData.asXML()), 0);
//		}
//	}

	/**
	 * 检查RIC_ITEMMANAGER_Map是否包含该产品,如果没有该产品则直接订阅该产品.
	 * 检查该订阅产品下对应的itemHandler是否是激活状态.如果不是激活状态.需要重新订阅.
	 * @param itemName
	 * @return
	 */
	private boolean checkSubscribeStatus(String itemName) {
		Map<String, ItemManager> subscribeItemManagerMap = RmdsDataCache.RIC_ITEMMANAGER_Map;
		ItemManager itemManager = subscribeItemManagerMap.get(itemName);
		if(itemManager!=null){
			Handle itemHandle = subscribeItemManagerMap.get(itemName).getItemHandle();
			if(itemHandle!=null&&!itemHandle.isActive()){
				return false;
			}
		}
		if (!subscribeItemManagerMap.containsKey(itemName)) {
			return false;
		}
		return true;
	}

	public void newsItemRequests() {
		// Initialize item manager for item domains
		NewsItemManager newsItemManager = new NewsItemManager(this,
				_itemGroupManager);
		NewsItemManager.initializeFids();
		// Send requests
		newsItemManager.sendRequest(SystemProperties
				.getProperties(SystemProperties.RFA_NEWS_CODE));

		// StringBuffer newsContent = new StringBuffer();
		// newsItemManager.sendRequest("nASA0590H");
	}

	/**
	 * 订阅指定的集中产品
	 */
	public void makeOrder() {
		String ricArray = SystemProperties.getProperties(SystemProperties.RIC_ARRAY);
		String[] rics = ricArray.split(",");
		for (String ric : rics) {
			itemRequests(ric);
		}
	}
	
	public void itemRequests(String ric) {
		// Initialize item manager for item domains
		IProcesser persistentItemManager = SystemConstant.springContext.getBean("persistentItemManager",PersistentItemManager.class);
		// Send requests
		persistentItemManager.sendRicRequest(ric, LocateMessageTypes.RESPONSE_FUTURE);
	}
	
	public void run(){
		startDispatch();
	}
	
	// This method dispatches events
	public void startDispatch(){
		while(dispath){
			try {
				_eventQueue.dispatch(5000);
			} catch (DeactivatedException e) {
				logger.error("event queue not activate"+e);
			} catch (DispatchQueueInGroupException e) {
				logger.error("event queue is dispatched."+e);
			}
		}
	}

	// This method cleans up resources when the application exits
	// As shown in QuickStartGuide, the shutdown and cleanup includes the
	// following steps:
	// 1. Deactivate event queue
	// 2. Unregister item interest
	// 3. Unregister login
	// 4. Destroy event queue
	// 5. Destroy event source
	// 6. Release session
	// 7. Uninitialize context
	@PreDestroy
	public void cleanup() {
		logger.info(Context.string());
		dispath = false;
		// 1. Deactivate event queue
		if(_eventQueue!=null)
			_eventQueue.deactivate();

		// 2. Unregister item interest
		// if (_itemManager != null)
		// _itemManager.closeRequest();

		// 3. Unregister login
		if (_loginClient != null)
			_loginClient.closeRequest();

		// 4. Destroy event queue
		if(_eventQueue!=null)
			_eventQueue.destroy();

		// 5. Destroy event source
		if (_consumer != null)
			_consumer.destroy();

		// 6. Release session
		if(_session!=null)
		_session.release();

		// 7. Uninitialize context
		Context.uninitialize();

		logger.info("Application is cleanuping and exiting");
		System.exit(0);
		// RFApplication.stop = true;
	}

	public EventQueue getEventQueue() {
		return _eventQueue;
	}

	public OMMConsumer getOMMConsumer() {
		return _consumer;
	}

	public OMMEncoder getEncoder() {
		return _encoder;
	}

	public OMMPool getPool() {
		return _pool;
	}

	public Handle getLoginHandle() {
		if (_loginClient != null) {
			return _loginClient.getHandle();
		}

		return null;
	}

	protected String getServiceName() {
		return serviceName;
	}

	// This is a main method of the QuickStartConsumer application.
	// A serviceName is hard coded to "DIRECT_FEED" but can be overridden by a
	// string
	// passed to the demo as a command line parameter.
	// Refer to QuickStartGuide for the RFA OMM Consumer application life cycle.
	// The steps shown in the guide are as follows:
	// Startup and initialization
	// Login
	// Item requests
	// Dispatch events
	// Shutdown and cleanup
	public static void main(String argv[]) {
		// Create a demo instance
		QSConsumerProxy demo = new QSConsumerProxy();

		// If the user is connecting to the Enterprise Platform, the serviceName
		// should be set to the service name that is offered by the provider.
		// The name is passed as a command line parameter.
		if ((argv != null) && (argv.length > 1)) {
			demo.serviceName = argv[0];
			demo._configFile = argv[1];
		} else {
			System.exit(0);
		}

		// Startup and initialization
		demo.init();

		// Login
		// and Item request
		// Item requests is done after application received login response.
		// The method itemRequests is called from processLogin method.
		demo.login();

		byte type = 4;
		// demo.itemRequests("AFX=", "test", type);
//		demo.itemRequests("APRE", type);
		// Dispatch events
		try {
			demo.startDispatch();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Shutdown and cleanup
		demo.cleanup();
	}

	public boolean isDispath() {
		return dispath;
	}

	public void setDispath(boolean dispath) {
		this.dispath = dispath;
	}
	
	/**
	 * This method should be called one before parsing and data.
	 * 
	 * @param fieldDictionaryFilename
	 * @param enumDictionaryFilename
	 * @throws DictionaryException
	 *             if an error has occurred
	 */
	public static FieldDictionary initializeDictionary(String fieldDictionaryFilename, String enumDictionaryFilename)
			throws DictionaryException {
		FieldDictionary dictionary = FieldDictionary.create();
		try {
			FieldDictionary.readRDMFieldDictionary(dictionary, fieldDictionaryFilename);
			logger.info("field dictionary read from RDMFieldDictionary file");

			FieldDictionary.readEnumTypeDef(dictionary, enumDictionaryFilename);
			logger.info("enum dictionary read from enumtype.def file");

			initializeDictionary(dictionary);
		} catch (DictionaryException e) {
			throw new DictionaryException("ERROR: Check if files " + fieldDictionaryFilename + " and "
					+ enumDictionaryFilename + " exist and are readable.", e);
		}
		return dictionary;
	}

	// This method can be used to initialize a downloaded dictionary
	public synchronized static void initializeDictionary(FieldDictionary dict) {
		int dictId = dict.getDictId();
		if (dictId == 0)
			dictId = 1; // dictId == 0 is the same as dictId 1
		DICTIONARIES.put(new Integer(dictId), dict);
	}
}
