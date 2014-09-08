package com.locate.rmds.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.locate.bridge.GateWayResponser;
import com.locate.common.model.LocateUnionMessage;

@Component("engineLine")
@Scope("prototype")
public class EngineLine {
	private static Logger logger = Logger.getLogger(EngineLine.class);
	public static ExecutorService executeService = Executors.newCachedThreadPool();
	private Map<String, Engine> engineMap;
	public Map<String, Engine> swapEngineMap;

	public EngineLine() {
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

	public void revomeEngine(String engineName) {
		engineMap = new HashMap<String, Engine>();
		engineMap.putAll(swapEngineMap);
		engineMap.remove(engineName);
		swap();
	}

	public void swap() {
		swapEngineMap = engineMap;
	}

	public void applyStrategy(final LocateUnionMessage locateMessage) {
		Callable<LocateUnionMessage> engineTask = new Callable<LocateUnionMessage>() {
			@Override
			public LocateUnionMessage call() throws Exception {
				for (final Engine engine : swapEngineMap.values()) {
					engine.doEngine(locateMessage);
				}
				GateWayResponser.sentMrketPriceToSubsribeChannel(locateMessage);
				return locateMessage;
			}
		};
		executeService.submit(engineTask);
	}
	
	public void applyStrategy(final LocateUnionMessage locateMessage,final int channelId) {
		if (swapEngineMap != null) {
			Callable<LocateUnionMessage> engineTask = new Callable<LocateUnionMessage>() {
				@Override
				public LocateUnionMessage call() throws Exception {
					for (final Engine engine : swapEngineMap.values()) {
						engine.doEngine(locateMessage);
					}
					GateWayResponser.sendSnapShotToChannel(locateMessage, channelId);
					return locateMessage;
				}
			};
			executeService.submit(engineTask);
		}
	}
}