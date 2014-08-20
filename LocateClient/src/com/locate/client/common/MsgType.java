package com.locate.client.common;

public final class MsgType {
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

	public static String toString(byte paramByte) {
		switch (paramByte) {
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
		}
		return "Unknown MsgType: " + paramByte;
	}
}
