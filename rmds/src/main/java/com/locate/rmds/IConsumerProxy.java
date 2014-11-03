package com.locate.rmds;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.locate.rmds.processer.ItemManager;
import com.reuters.rfa.common.Client;
import com.reuters.rfa.common.EventQueue;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.omm.OMMEncoder;
import com.reuters.rfa.omm.OMMPool;
import com.reuters.rfa.session.Session;
import com.reuters.rfa.session.omm.OMMConsumer;

public interface IConsumerProxy {
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
	public abstract void init();

	// This method utilizes the LoginClient class to send login request
	public abstract void login();

	public abstract Handle registerDirectory(Client client);

	/*
	 * get all the dictionaries included in the argument Set
	 */
	public abstract void getDictionaries(Set<String> dictionariesUsed, Client client);

	public abstract void addNewService(String serviceName);

	// This method is called by _loginClient upon receiving successful login
	// response.
	public abstract void loginSuccess();

	// This method is called when the login was not successful
	// The application exits
	public abstract void loginFailure();

	// This method utilizes ItemManager class to request items
	public abstract void itemRequests(String itemName, byte responseMsgType, int channelId);

	public abstract void newsItemRequests();

	/**
	 * 订阅指定的集中产品
	 */
	public abstract void makeOrder();

	public abstract void itemPersistentRequests(String ric);

	// This method dispatches events
	public abstract void startDispatch();

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
	public abstract void cleanup();

	public abstract EventQueue getEventQueue();

	public abstract OMMConsumer getOMMConsumer();

	public abstract OMMEncoder getEncoder();

	public abstract OMMPool getPool();

	public abstract Handle getLoginHandle();

	public abstract boolean isDispath();

	public abstract void setDispath(boolean dispath);

	public abstract String getServiceName();
	
	public Session getBackupSession();

	public void setBackupSession(Session backupSession);

	public EventQueue getBackupEventQueue();

	public void setBackupEventQueue(EventQueue backupEventQueue);

	public OMMConsumer getBackupConsumer();

	public void setBackupConsumer(OMMConsumer backupConsumer);
}