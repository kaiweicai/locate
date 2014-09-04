package com.locate.rmds.engine.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("engineLine")@Scope("prototype")
public class EngineLine {
	private List<Engine> engineList = new ArrayList<Engine>();
	public void addEngine(Engine engine){
		engineList.add(engine);
	}
	
	public void revomeEngine(Engine engine){
		engineList.remove(engine);
	}
}
