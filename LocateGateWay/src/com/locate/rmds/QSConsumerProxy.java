package com.locate.rmds;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.springframework.web.util.HtmlUtils;

import com.locate.GateWayServer;
import com.locate.gate.GatewayServerHandler;
import com.locate.rmds.client.RFAUserManagement;
import com.locate.rmds.util.GenericOMMParser;
import com.locate.rmds.util.SystemProperties;
import com.reuters.rfa.common.Context;
import com.reuters.rfa.common.EventQueue;
import com.reuters.rfa.common.EventSource;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.config.ConfigDb;
import com.reuters.rfa.dictionary.DictionaryException;
import com.reuters.rfa.dictionary.FieldDictionary;
import com.reuters.rfa.omm.OMMEncoder;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.session.Session;
import com.reuters.rfa.session.omm.OMMConsumer;
/**  
*  作者:Cloud wei   
*  E-mail:kaiweicai@163.com   
*  创建时间：2014.5.26   
*  类说明  netty game  
*/  
public class QSConsumerProxy {
	static Logger _logger = Logger.getLogger(QSConsumerProxy.class.getName());

	// RFA objects
	protected Session _session;
	protected EventQueue _eventQueue;
	protected OMMConsumer _consumer;
	protected LoginClient _loginClient;
	protected ItemGroupManager _itemGroupManager;

	protected OMMEncoder _encoder;
	protected OMMPool _pool;
	protected String _serviceName = "DIRECT_FEED";
	private boolean all = true;
	protected static String _configFile = "config/rfaConfig.properties";
	FieldDictionary dictionary;
	DecimalFormat dataFormat = new DecimalFormat("0.00");

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
	public void init() {
		// Context.initialize();
		// 1. Initialize system config
//		ConfigDb configDb = new ConfigDb();
//		configDb.addVariable("myNamespace.Connections.consConnection.connectionType", "RSSL");
//		configDb.addVariable("myNamespace.Connections.consConnection.serverList", "10.34.9.91");
//		configDb.addVariable("myNamespace.Connections.consConnection.serverList", "192.168.6.1");
//		configDb.addVariable("myNamespace.Connections.consConnection.portNumber", "14002");
//		configDb.addVariable("myNamespace.Sessions.consSession.connectionList", "consConnection");
		Context.initialize();
		
		
		//
		SystemProperties.init(_configFile);
		
		try {
            Preferences.importPreferences(new DataInputStream(new FileInputStream(SystemProperties.getProperties(SystemProperties.RFA_CONFIG_FILE))));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (InvalidPreferencesFormatException e) {
            e.printStackTrace();
            System.exit(-1);
        }
		// 2. Create an Event Queue
		_eventQueue = EventQueue.create("myEventQueue");

		// 3. Acquire a Session
		_session = Session.acquire("myNamespace::consSession");
		if (_session == null) {
			_logger.info("Could not acquire session.");
			Context.uninitialize();
			System.exit(1);
		}

		// 4. Create an OMMConsumer event source
		_consumer = (OMMConsumer) _session.createEventSource(EventSource.OMM_CONSUMER, "myOMMConsumer", true);
		// Initialize item group manager
		_itemGroupManager = new ItemGroupManager(this);
		// 5. Load dictionaries
		// Application may choose to down-load the enumtype.def and
		// RWFFldDictionary
		// This example program loads the dictionaries from files.
		String fieldDictionaryFilename = SystemProperties.getProperties(SystemProperties.RFA_FIELD_FILE);
		String enumDictionaryFilename = SystemProperties.getProperties(SystemProperties.RFA_ENUM_FILE);
		try {
			dictionary = GenericOMMParser.initializeDictionary(	fieldDictionaryFilename, enumDictionaryFilename);
		} catch (DictionaryException ex) {
			_logger.info("ERROR: Unable to initialize dictionaries");
			_logger.info(ex.getMessage());
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
			this._serviceName = SystemProperties
					.getProperties(SystemProperties.RFA_SERVICE_NAME);
			// Start RFA socket server listener
//			try {
//				new GateWayServer();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				cleanup();
//				_logger.error("Initial RFA socket server failed.", e);
//			}
		}
		login();
		try {
			startDispatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// This method utilizes the LoginClient class to send login request
	public void login() {
		// Initialize client for login domain.
		_loginClient = new LoginClient(this);

		// Send login request
		_loginClient.sendRequest();
	}

	// This method is called by _loginClient upon receiving successful login
	// response.
	public void processLogin() {
		_logger.info("QSConsumerDemo" + " Login successful");
//		newsItemRequests();
	}

	// This method is called when the login was not successful
	// The application exits
	public void loginFailure() {
		_logger.info("OMMConsumerDemo"
				+ ": Login has been denied / rejected / closed");
		_logger.info("OMMConsumerDemo" + ": Preparing to clean up and exiting");
		_loginClient = null;
		cleanup();
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
	public ItemManager itemRequests(String itemName, byte responseMsgType) {
		// Initialize item manager for item domains
		ItemManager _itemManager = new ItemManager(this, _itemGroupManager);
		// Send requests
		_itemManager.sendRequest(itemName, responseMsgType);
		return _itemManager;
	}

//	public void linkItemRequests(String itemName, String clientName,
//			byte responseMsgType) {
//		// Initialize item manager for item domains
//		LinkItemManager linkItemManager = new LinkItemManager(this,
//				_itemGroupManager, clientName);
//		// Send requests
//		linkItemManager.sendRequest(itemName, responseMsgType);
//	}

	@Deprecated
	public void updateResponseStat(long processTimes, Document responseData) {
		RFApplication.totalResponseNumber++;
		RFApplication.totalProcessTime += processTimes;
		RFApplication.responseNumber.setText(String
				.valueOf(RFApplication.totalResponseNumber));
		double totalTimes = RFApplication.totalProcessTime;
		String avgTimes = dataFormat.format(totalTimes
				/ RFApplication.totalResponseNumber);
		RFApplication.avgTimes.setText(avgTimes);
		if (responseData != null) {
			RFApplication.showLog.insert( HtmlUtils.htmlUnescape(responseData.asXML()), 0);
		}
	}

//	public void newsItemRequests() {
//		// Initialize item manager for item domains
//		NewsItemManager newsItemManager = new NewsItemManager(this,
//				_itemGroupManager);
//		newsItemManager.initializeFids(dictionary);
//		// Send requests
//		newsItemManager.sendRequest(SystemProperties
//				.getProperties(SystemProperties.RFA_NEWS_CODE));
//
//		// StringBuffer newsContent = new StringBuffer();
//		// newsItemManager.sendRequest("nASA0590H");
//	}

	// This method dispatches events
	public void startDispatch() throws Exception {
		_eventQueue.dispatch(0);
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
	public void cleanup() {
		_logger.info(Context.string());

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

		_logger.info(getClass().toString() + " exiting");
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
		return _serviceName;
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
			demo._serviceName = argv[0];
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
		demo.itemRequests("APRE", type);
		// Dispatch events
		try {
			demo.startDispatch();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Shutdown and cleanup
		demo.cleanup();
	}
}
