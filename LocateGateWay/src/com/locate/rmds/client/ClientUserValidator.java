package com.locate.rmds.client;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;

import com.locate.common.DataBaseCache;
import com.locate.common.LocateMessageTypes;
import com.locate.common.LocateResultCode;
import com.locate.common.RFANodeconstant;
import com.locate.common.model.ClientRequest;
import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.XmlMessageUtil;
import com.locate.rmds.processer.RFALoginClient;
import com.reuters.rfa.omm.OMMState;

public class ClientUserValidator {

	static Logger logger = Logger.getLogger(ClientUserValidator.class.getName());
	private String clientIp;
	public ClientUserValidator(String clientIp) {
		this.clientIp = clientIp;
	}

	// Authenticate client login
	public int authUserLogin(ClientRequest userRequest, String clientIP) {
		String userName = userRequest.getUserName();
		String password = userRequest.getPassword();
		int resultCode = LocateResultCode.SUCCESS_RESULT;
		// Judge user is lawful
		if (RFAUserManagement.hasUser(userName)) {
			if (RFAUserManagement.validateUser(userName, password)) {
				RFAUserManagement.setUserAddress(userName, clientIp);
				DataBaseCache._userConnection.put(clientIP, userName);
				resultCode=LocateResultCode.SUCCESS_RESULT;
				logger.info("User " + userName + " passed authentication");
			} else {
				// User's password is wrong
				resultCode=LocateResultCode.USER_WRONG_PASSEORD;
				logger.warn("User's password is wrong , user name is "+userName+ " password is "+password);
			}
		} else {
			// User not exist
			resultCode=LocateResultCode.USER_NOT_EXIST;
			logger.warn("User not exist , user name is "+userName+ " password is "+password);
		}
		/**
		 * I think this authenticate is NOT work. 
		 */
		String resultDes=LocateResultCode.LocateResponseEnum.getResultDescription(resultCode);
		logger.info("User authenticate result for " + resultDes);
		return resultCode;
	}

}
