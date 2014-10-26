package com.locate.rmds.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.locate.bridge.GateWayResponser;
import com.locate.common.logging.err.ErrorLogHandler;
import com.locate.common.model.LocateUnionMessage;

@Component("engineLine")
@Scope("prototype")
public class EngineLine {
	private String itemName;
	private static Logger logger = LoggerFactory.getLogger(EngineLine.class);
	private ErrorLogHandler errorLogHandler = ErrorLogHandler.getLogger(getClass());
	public static ExecutorService executeService = Executors.newCachedThreadPool();
	private Map<String, Engine> engineMap;
	public Map<String, Engine> swapEngineMap;
	private LocateUnionMessage lastMessage;
//	public EngineLine() {
//		init();
//	}
	
	public EngineLine(String itemName){
		this.itemName = itemName;
		init();
	}

	public void init() {
		engineMap = new HashMap<String, Engine>();
		swapEngineMap = new HashMap<String, Engine>();
	}

	public void addEngine(String engineName, Engine engine) {
		engineMap = new HashMap<String, Engine>();
		engineMap.putAll(swapEngineMap);
		engineMap.put(engineName, engine);
		swap();
	}
	
	public void addEngine(Map<String,Engine> addEngineMap) {
		engineMap = new HashMap<String, Engine>();
		engineMap.putAll(swapEngineMap);
		engineMap.putAll(addEngineMap);
		swap();
	}

	public void revomeEngine(String engineName) {
		engineMap = new HashMap<String, Engine>();
		engineMap.putAll(swapEngineMap);
		engineMap.remove(engineName);
		swap();
	}

	public void swap() {
		swapEngineMap = engineMap;
	}

	public Future<LocateUnionMessage> applyStrategy(final LocateUnionMessage locateMessage) {
		lastMessage = locateMessage.clone();
		Callable<LocateUnionMessage> engineTask = new Callable<LocateUnionMessage>() {
			@Override
			public LocateUnionMessage call() throws Exception {
				for (final String engineName : swapEngineMap.keySet()) {
					Engine engine=swapEngineMap.get(engineName);
					engine.doEngine(locateMessage);
				}
				GateWayResponser.sentMrketPriceToSubsribeChannel(locateMessage);
				return locateMessage;
			}
		};
		try{
			return executeService.submit(engineTask);
		}catch(Exception e){
			errorLogHandler.error("ocurrer error !!!!",e);
			return null;
		}
	}
	
	public Future<LocateUnionMessage> applyStrategy() {
		if (lastMessage != null) {
			final LocateUnionMessage locateMessage = lastMessage.clone();
			String locateItemName = locateMessage.getItemName();
			if (!locateItemName.equalsIgnoreCase(this.itemName)) {
				errorLogHandler.error("The strategy forbbiden apply to this product!The engineLine item name is "
						+ this.itemName + " The locate itemName is " + locateItemName);
				return null;
			}
			Callable<LocateUnionMessage> engineTask = new Callable<LocateUnionMessage>() {
				@Override
				public LocateUnionMessage call() throws Exception {
					for (final String engineName : swapEngineMap.keySet()) {
						Engine engine = swapEngineMap.get(engineName);
						engine.doEngine(locateMessage);
					}
					GateWayResponser.sentMrketPriceToSubsribeChannel(locateMessage);
					return locateMessage;
				}
			};
			try {
				return executeService.submit(engineTask);
			} catch (Exception e) {
				errorLogHandler.error("ocurrer error !!!!", e);
				return null;
			}
		}
		return null;
	}
	
	
	public Future<LocateUnionMessage> applyStrategy(final LocateUnionMessage locateMessage,final int channelId) {
		Future<LocateUnionMessage> future = null;
		if (swapEngineMap != null) {
			Callable<LocateUnionMessage> engineTask = new Callable<LocateUnionMessage>() {
				@Override
				public LocateUnionMessage call() throws Exception {
					for (final String engineName : swapEngineMap.keySet()) {
						Engine engine=swapEngineMap.get(engineName);
						engine.doEngine(locateMessage);
					}
					GateWayResponser.sendSnapShotToChannel(locateMessage, channelId);
					return locateMessage;
				}
			};
			future = executeService.submit(engineTask);
		}
		return future;
	}
}
