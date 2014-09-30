package com.locate.common.model;

import io.netty.channel.Channel;

public class ClientSession {
	public ClientSession(int sessionID,Channel channel){
		this.sessionID = sessionID;
		this.channel = channel;
	}
	private int sessionID;
	private Channel channel;
	
	public int getSessionID() {
		return sessionID;
	}
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
}
