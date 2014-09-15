package com.locate.common.constant;

/**
 * Locate gate way result code and descriptions
 * 
 * @author CloudWei kaiweicai@163.com create time 2014年8月24日
 * @copyRight by Author
 */
public class LocateResultCode {
	public static final int SUCCESS_RESULT = 10;

	public static final int USER_NOT_LOGIN = 100;
	public static final int USER_NOT_EXIST = 101;
	public static final int USER_WRONG_PASSEORD = 102;
	public static final int USER_BUSINESS_NUMBER_OUT = 103;
	public static final int USER_BUSINESS_NO_SUBSCRIBE = 104;
	public static final int BUSINESS_REQUEST_FAILED = 105;
	public static final int USER_REQUEST_FAILTED = 106;

	public static final int CHANNEL_IDLE_TIMEOUT = 200;

	public static final int RFA_SERVER_NOT_READY = 2000;

	public static final int NETTY_UNKONW_ERROR = 1000;

	public static String getResultDescription(int resultCode) {
		switch (resultCode) {
		case USER_BUSINESS_NUMBER_OUT:
			return "User's business number out";
		case USER_BUSINESS_NO_SUBSCRIBE:
			return "User has not right for this option";
		case BUSINESS_REQUEST_FAILED:
			return "Request business failed";
		case USER_REQUEST_FAILTED:
			return "Request data is wrong, Please check!";
		case RFA_SERVER_NOT_READY:
			return "The server can not conneted to Data Source of RFA Server!";
		case CHANNEL_IDLE_TIMEOUT:
			return "No message write to client for a long time,May be the RFA server not work correctly.";
		case SUCCESS_RESULT:
			return "Operate success.";
		case USER_NOT_LOGIN:
			return "User didn't login!";
		case USER_WRONG_PASSEORD:
			return "User's password is wrong!";
		case USER_NOT_EXIST:
			return "User not exist!";
		case NETTY_UNKONW_ERROR:
			return "Netty NIO framework error, Please contact the developer!";
		default:
			return "Not found the correctly result code description!";
		}
	}
}