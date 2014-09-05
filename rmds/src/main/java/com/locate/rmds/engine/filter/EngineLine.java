package com.locate.rmds.engine.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("engineLine")@Scope("prototype")
public class EngineLine {
	private Map<String,Engine> engineMap;
	
	private Map<String,Engine> swapEngineMap;
	
	public EngineLine(){
		init();
	}
	
	public void init(){
		engineMap = new HashMap<String,Engine>();
		swapEngineMap=new HashMap<String,Engine>();
	}
	
	public void addEngine(String engineName,Engine engine){
		engineMap.put(engineName, engine);
	}
	
	public void revomeEngine(String engineName){
		engineMap.remove(engineName);
	}
	
	public Map<String,Engine> swap(){
		swapEngineMap
	}
}
