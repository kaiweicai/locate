package com.locate.rmds.client;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.jboss.netty.channel.Channel;

import com.locate.gate.GateWayServer;
import com.locate.gate.GatewayServerHandler;
import com.locate.gate.tcp.model.RFAUserResponse;
import com.locate.rmds.util.RFAExceptionTypes;
import com.locate.rmds.util.RFANodeconstant;

public class ClientUserLogin {

	static Logger _logger = Logger.getLogger(ClientUserLogin.class.getName());
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
		RFAExceptionTypes.RFAUserAuthentication authentication = null;
		// Judge user is lawful
		if (RFAUserManagement.hasUser(userName)) {
			if (RFAUserManagement.isCurrectPassword(userName, password)) {
//				InetSocketAddress sa = (InetSocketAddress) channel.getLocalAddress();
//				String address = String.valueOf(sa.getAddress().getAddress());
				RFAUserManagement.setUserAddress(userName, clientIp);
				GateWayServer._userConnection.put(clientIP, userName);
				_logger.info("User " + userName + " passed authentication");
			} else {
				// User's password is wrong
				authentication = RFAExceptionTypes.RFAUserAuthentication.PasswordIsWrong;
			}
		} else {
			// User not exist
			authentication = RFAExceptionTypes.RFAUserAuthentication.UserNotExist;
		}
		/**
		 * I think this authenticate is NOT work. 
		 */
		Document userResponse = RFAUserResponse.createAuthenResponse(authentication);
		_logger.error("User authenticate result for " + userName + userResponse.asXML());
		return userResponse;
	}

}
