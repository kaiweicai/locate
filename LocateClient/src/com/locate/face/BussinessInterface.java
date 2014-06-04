package com.locate.face;

public interface BussinessInterface {

	public void handleException(Throwable e);

	public void handleMessage(String message);

	public void handleDisconnected();

}