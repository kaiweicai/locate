package com.locate.rmds.engine;

import com.locate.common.model.LocateUnionMessage;

public interface Engine {
	public abstract LocateUnionMessage doEngine(LocateUnionMessage message);
}
