package com.locate.rmds.user;

import java.util.Map;

public class RFAUser {
	
	String userName;
	String password;
	String userIP;
	
	Map<String,RFAUserPermission> permission ;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserIP() {
		return userIP;
	}

	public void setUserIP(String userIP) {
		this.userIP = userIP;
	}

	public Map<String,RFAUserPermission> getPermission() {
		return permission;
	}

	public void setPermission(Map<String,RFAUserPermission> permission) {
		this.permission = permission;
	}
}
