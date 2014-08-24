package com.locate.common;


public class LocateMessageTypes {
	public static final byte ERROR = -1;	 
	
	//Remote Server message type
	/** @deprecated */
	public static final byte STREAMING_REQ = 1;

	/** @deprecated */
	public static final byte NONSTREAMING_REQ = 2;

	/** @deprecated */
	public static final byte PRIORITY_REQ = 3;
	public static final byte REQUEST = 4;
	public static final byte CLOSE_REQ = 5;
	public static final byte REFRESH_RESP = 6;
	public static final byte STATUS_RESP = 7;
	public static final byte UPDATE_RESP = 8;
	public static final byte ACK_RESP = 9;
	public static final byte GENERIC = 10;
	public static final byte POST = 11;
	
	//Locate client msgType
	public static final byte LOGIN = 21;	               //
	public static final byte STOCK_REQUEST = 22;        //
	public static final byte CURRENCY_REQUEST = 23;     //
	public static final byte FUTURE_REQUEST = 24;       //
	public static final byte OPTION_REQUEST = 25;       //
	public static final byte INDEX_REQUEST = 26;       //
	public static final byte NEWS_REQUEST = 27;       //
	public static final byte NEWS_COMPOSE_REQUEST = 28;       //
	public static final byte ONE_TIMES_REQUEST = 29;       //
	public static final byte LOGINWRONGUSER = 30;	 
	public static final byte LOGINWRONGPASSWORD = 31;	 //
	public static final byte UNREGISTER_REQUEST = 32;
	
	public static final byte STOCK_LINK_REQUEST = 42;  //
	public static final byte CURRENCY_LINK_REQUEST = 43;//
	public static final byte FUTURE_LINK_REQUEST = 44;  //
	public static final byte OPTION_LINK_REQUEST = 45;  //
	public static final byte INDEX_LINK_REQUEST = 46;  //

	//locate response to client message type.
	public static final byte RESPONSE_LOGIN = 50;
	public static final byte RESPONSE_STOCK = 51;
	public static final byte RESPONSE_CURRENCY = 52;
	public static final byte RESPONSE_FUTURE = 53;
	public static final byte RESPONSE_OPTION = 54;
	public static final byte RESPONSE_INDEX = 55;
	public static final byte RESPONSE_NEWS = 56;
	public static final byte RESPONSE_NEWS_COMPOSE = 57;
	public static final byte RESPONSE_ONE_TIMES = 58;
	public static final byte RESPONSE_UNREGISTER = 59;
	

	public static final byte RESPONSE_STOCK_LINK = 61;
	public static final byte RESPONSE_CURRENCY_LINK = 62;
	public static final byte RESPONSE_FUTURE_LINK = 63;
	public static final byte RESPONSE_OPTION_LINK = 64;
	public static final byte RESPONSE_INDEX_LINK = 65;
	
	public static final byte REQUEST_EOCH = 81;

	public static String toString(byte paramByte) {
		switch (paramByte) {
		case -1:
			return "MsgType.ERROR";
		case 1:
			return "MsgType.STREAMING_REQ";
		case 2:
			return "MsgType.NONSTREAMING_REQ";
		case 3:
			return "MsgType.PRIORITY_REQ";
		case 4:
			return "MsgType.REQUEST";
		case 5:
			return "MsgType.CLOSE_REQ";
		case 6:
			return "MsgType.REFRESH_RESP";
		case 7:
			return "MsgType.STATUS_RESP";
		case 8:
			return "MsgType.UPDATE_RESP";
		case 9:
			return "MsgType.ACK_RESP";
		case 10:
			return "MsgType.GENERIC";
		case 11:
			return "MsgType.POST";
		case 21:
			return "MsgType.LOGIN";
		case 22:
			return "MsgType.STOCK_REQUEST";
		case 23:
			return "MsgType.CURRENCY_REQUEST";
		case 24:
			return "MsgType.FUTURE_REQUEST";
		case 25:
			return "MsgType.OPTION_REQUEST";
		case 26:
			return "MsgType.INDEX_REQUEST";
		case 27:
			return "MsgType.NEWS_REQUEST";
		case 28:
			return "MsgType.NEWS_COMPOSE_REQUEST";
		case 29:
			return "MsgType.ONE_TIMES_REQUEST";
		case 30:
			return "MsgType.LOGINWRONGUSER";
		case 31:
			return "MsgType.LOGINWRONGPASSWORD";
		case 32:
			return "MsgType.UNREGISTER_REQUEST";
		case 42:
			return "MsgType.STOCK_LINK_REQUEST";
		case 43:
			return "MsgType.CURRENCY_LINK_REQUEST";
		case 44:
			return "MsgType.FUTURE_LINK_REQUEST";
		case 45:
			return "MsgType.OPTION_LINK_REQUEST";
		case 46:
			return "MsgType.INDEX_LINK_REQUEST";
		case 50:
			return "MsgType.RESPONSE_LOGIN";
		case 51:
			return "MsgType.RESPONSE_STOCK";
		case 52:
			return "MsgType.RESPONSE_CURRENCY";
		case 53:
			return "MsgType.RESPONSE_FUTURE";
		case 54:
			return "MsgType.RESPONSE_OPTION";
		case 55:
			return "MsgType.RESPONSE_INDEX";
		case 56:
			return "MsgType.RESPONSE_NEWS";
		case 57:
			return "MsgType.RESPONSE_NEWS_COMPOSE";
		case 58:
			return "MsgType.RESPONSE_ONE_TIMES";
		case 59:
			return "MsgType.RESPONSE_UNREGISTER";
		case 61:
			return "MsgType.RESPONSE_STOCK_LINK";
		case 62:
			return "MsgType.RESPONSE_CURRENCY_LINK";
		case 63:
			return "MsgType.RESPONSE_FUTURE_LINK";
		case 64:
			return "MsgType.RESPONSE_OPTION_LINK";
		case 65:
			return "MsgType.RESPONSE_INDEX_LINK";
		}
		return "Unknown MsgType: " + paramByte;
	}
}
