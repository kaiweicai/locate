package com.locate.rmds.engine.filter;

import com.locate.common.model.LocateUnionMessage;

public interface Engine {
	public abstract LocateUnionMessage doEngine(LocateUnionMessage message);
}
