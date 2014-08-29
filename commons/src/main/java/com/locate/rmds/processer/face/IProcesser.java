package com.locate.rmds.processer.face;


public interface IProcesser{
	public void closeRequest();

	public void sendRicRequest(String ric, byte responseFuture);
}