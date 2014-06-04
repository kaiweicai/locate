package com.locate.face;

public interface ClientConnectedInterface {

	public abstract void conneteLocateGateWay(String serverAddress, int port, String userName, String password);

	public abstract void openRICMarket(String ric);

}