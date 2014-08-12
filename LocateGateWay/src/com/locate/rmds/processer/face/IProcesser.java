package com.locate.rmds.processer.face;

import com.reuters.rfa.common.Client;

public interface IProcesser extends Client{
	public void closeRequest();

	public void sendRicRequest(String ric, byte responseFuture);
}