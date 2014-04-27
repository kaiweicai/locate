package com.locate.rmds.util;

public class RFAExceptionTypes {
	public static int USER_NOT_LOGIN = 100;
	public static int USER_NOT_EXIST = 101;
	public static int USER_WRONG_PASSEORD = 102;
	public static int USER_BUSINESS_NUMBER_OUT = 103;
	public static int USER_BUSINESS_NO_SUBSCRIBE = 104;
	public static int BUSINESS_REQUEST_FAILED = 105;
	public static int USER_REQUEST_FAILTED = 106;

	public enum RFAExceptionEnum {
		BusinessNumberOut("User's business number out", USER_BUSINESS_NUMBER_OUT), 
		NotSubscribeBusiness("User has not right for this option", USER_BUSINESS_NO_SUBSCRIBE),
		BusinessReqFailed("Request business failed", BUSINESS_REQUEST_FAILED), 
		UserRequestFailed("Request data is wrong, Please check!", USER_REQUEST_FAILTED);

		String exception;
		int errorCode;

		private RFAExceptionEnum(String exception, int errorCode) {
			this.exception = exception;
			this.errorCode = errorCode;
		}

		public static String getExceptionDescription(int code) {
			for (RFAExceptionEnum c : RFAExceptionEnum.values()) {
				if (c.getErrorCode() == code) {
					return c.exception;
				}
			}
			return null;
		}

		public String getException() {
			return exception;
		}

		public void setException(String exception) {
			this.exception = exception;
		}

		public int getErrorCode() {
			return errorCode;
		}

		public void setErrorCode(int errorCode) {
			this.errorCode = errorCode;
		}

	}

	public enum RFAUserAuthentication {
		NotLogin("User didn't login", USER_NOT_LOGIN), 
		PasswordIsWrong("User's password is wrong", USER_WRONG_PASSEORD), 
		UserNotExist("User not exist", USER_NOT_EXIST);

		String exception;
		int errorCode;

		private RFAUserAuthentication(String exception, int errorCode) {
			this.exception = exception;
			this.errorCode = errorCode;
		}

		public static String getDescription(int code) {
			for (RFAExceptionEnum c : RFAExceptionEnum.values()) {
				if (c.getErrorCode() == code) {
					return c.exception;
				}
			}
			return null;
		}

		public String getException() {
			return exception;
		}

		public void setException(String exception) {
			this.exception = exception;
		}

		public int getErrorCode() {
			return errorCode;
		}

		public void setErrorCode(int errorCode) {
			this.errorCode = errorCode;
		}

	}

}