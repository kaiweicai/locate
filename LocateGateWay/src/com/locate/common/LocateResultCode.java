package com.locate.common;

/**
 * Locate gate way result code and descriptions
 * @author CloudWei kaiweicai@163.com
 * create time 2014年8月24日
 * @copyRight by Author
 */
public class LocateResultCode {
	public static final int SUCCESS_RESULT = 10;
	
	public static int USER_NOT_LOGIN = 100;
	public static int USER_NOT_EXIST = 101;
	public static int USER_WRONG_PASSEORD = 102;
	public static int USER_BUSINESS_NUMBER_OUT = 103;
	public static int USER_BUSINESS_NO_SUBSCRIBE = 104;
	public static int BUSINESS_REQUEST_FAILED = 105;
	public static int USER_REQUEST_FAILTED = 106;
	
	
	public static int CHANNEL_IDLE_TIMEOUT = 200;
	
	public static int RFA_SERVER_NOT_READY= 2000;
	
	public static int NETTY_UNKONW_ERROR = 1000;

	public enum LocateResponseEnum {
		BusinessNumberOut(USER_BUSINESS_NUMBER_OUT, "User's business number out"), 
		NotSubscribeBusiness(USER_BUSINESS_NO_SUBSCRIBE, "User has not right for this option"),
		BusinessReqFailed(BUSINESS_REQUEST_FAILED, "Request business failed"), 
		UserRequestFailed(USER_REQUEST_FAILTED, "Request data is wrong, Please check!"),
		RFANotConnneted(RFA_SERVER_NOT_READY, "The server can not conneted to Data Source of RFA Server!"),
		ChannelIdleTimeOut(CHANNEL_IDLE_TIMEOUT,
				"No message write to client for a long time,May be the RFA server not work correctly."),
		
		LoginSuccess(SUCCESS_RESULT,"Operate success."),
		NotLogin(USER_NOT_LOGIN, "User didn't login"), 
		PasswordIsWrong(USER_WRONG_PASSEORD, "User's password is wrong"), 
		UserNotExist(USER_NOT_EXIST, "User not exist"),
		
		NettyUnknowError(NETTY_UNKONW_ERROR, "Netty NIO framework error, Please contact the developer!");
		
		String description;
		int resultCode;

		private LocateResponseEnum(int resultCode, String description) {
			this.description = description;
			this.resultCode = resultCode;
		}

		public static String getResultDescription(int code) {
			for (LocateResponseEnum c : LocateResponseEnum.values()) {
				if (c.getErrorCode() == code) {
					return c.description;
				}
			}
			return "Not redefine error!";
		}

		public String getException() {
			return description;
		}

		public void setException(String exception) {
			this.description = exception;
		}

		public int getErrorCode() {
			return resultCode;
		}

		public void setErrorCode(int errorCode) {
			this.resultCode = errorCode;
		}

	}

//	public enum RFAUserAuthentication {
//
//		String exception;
//		int errorCode;
//
//		private RFAUserAuthentication(String exception, int errorCode) {
//			this.exception = exception;
//			this.errorCode = errorCode;
//		}
//
//		public static String getDescription(int code) {
//			for (RFAExceptionEnum c : RFAExceptionEnum.values()) {
//				if (c.getErrorCode() == code) {
//					return c.exception;
//				}
//			}
//			return null;
//		}
//
//		public String getException() {
//			return exception;
//		}
//
//		public void setException(String exception) {
//			this.exception = exception;
//		}
//
//		public int getErrorCode() {
//			return errorCode;
//		}
//
//		public void setErrorCode(int errorCode) {
//			this.errorCode = errorCode;
//		}
//
//	}
//	
//	public enum NETTYExceptionEnum {
//		
//		
//		String exception;
//		int errorCode;
//
//		private NETTYExceptionEnum(String exception, int errorCode) {
//			this.exception = exception;
//			this.errorCode = errorCode;
//		}
//
//		public static String getExceptionDescription(int code) {
//			for (RFAExceptionEnum c : RFAExceptionEnum.values()) {
//				if (c.getErrorCode() == code) {
//					return c.exception;
//				}
//			}
//			return null;
//		}
//
//		public String getException() {
//			return exception;
//		}
//
//		public void setException(String exception) {
//			this.exception = exception;
//		}
//
//		public int getErrorCode() {
//			return errorCode;
//		}
//
//		public void setErrorCode(int errorCode) {
//			this.errorCode = errorCode;
//		}
//
//	}

}