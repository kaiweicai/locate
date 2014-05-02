package com.locate.rmds.client;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.jboss.netty.channel.Channel;

import com.locate.common.GateWayExceptionTypes;
import com.locate.gate.GateWayServer;
import com.locate.gate.hanlder.GatewayServerHandler;
import com.locate.gate.model.RFAUserResponse;
import com.locate.rmds.util.RFANodeconstant;

public class ClientUserLogin {

	static Logger logger = Logger.getLogger(ClientUserLogin.class.getName());
	private String clientIp;
	public ClientUserLogin(String clientIp) {
		this.clientIp = clientIp;
	}

	// Authenticate client login
	public Document authUserLogin(Document userRequest, String clientIP) {
		Node userNameNode = userRequest.selectSingleNode(RFANodeconstant.USER_LOGIN_USER_NAME);
		String userName = userNameNode.getText();
		Node userPwdNode = userRequest.selectSingleNode(RFANodeconstant.USER_LOGIN_USER_PASSWORD);
		String password = userPwdNode.getText();
		GateWayExceptionTypes.RFAUserAuthentication authentication = null;
		// Judge user is lawful
		if (RFAUserManagement.hasUser(userName)) {
			if (RFAUserManagement.isCurrectPassword(userName, password)) {
//				InetSocketAddress sa = (InetSocketAddress) channel.getLocalAddress();
//				String address = String.valueOf(sa.getAddress().getAddress());
				RFAUserManagement.setUserAddress(userName, clientIp);
				GateWayServer._userConnection.put(clientIP, userName);
				logger.info("User " + userName + " passed authentication");
			} else {
				// User's password is wrong
				authentication = GateWayExceptionTypes.RFAUserAuthentication.PasswordIsWrong;
				logger.warn("User's password is wrong , user name is "+userName+ " password is "+password);
			}
		} else {
			// User not exist
			authentication = GateWayExceptionTypes.RFAUserAuthentication.UserNotExist;
			logger.warn("User not exist , user name is "+userName+ " password is "+password);
		}
		/**
		 * I think this authenticate is NOT work. 
		 */
		Document userResponse = RFAUserResponse.createAuthenResponse(authentication);
		logger.info("User authenticate result for " + userName + userResponse.asXML());
		return userResponse;
	}

}
