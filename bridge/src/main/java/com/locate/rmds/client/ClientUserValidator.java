package com.locate.rmds.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.constant.LocateResultCode;
import com.locate.common.datacache.DataBaseCache;
import com.locate.common.model.ClientRequest;

public class ClientUserValidator {

	static Logger logger = LoggerFactory.getLogger(ClientUserValidator.class.getName());
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
		String resultDes=LocateResultCode.getResultDescription(resultCode);
		logger.info("User authenticate result for " + resultDes);
		return resultCode;
	}

}
