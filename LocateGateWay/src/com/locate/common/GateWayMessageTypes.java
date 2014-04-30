package com.locate.common;


public class GateWayMessageTypes {
	public static final byte ERROR = 0;	               //
	public static final byte LOGIN = 1;	               //
	public static final byte STOCK_REQUEST = 2;        //
	public static final byte CURRENCY_REQUEST = 3;     //
	public static final byte FUTURE_REQUEST = 4;       //
	public static final byte OPTION_REQUEST = 5;       //
	public static final byte INDEX_REQUEST = 6;       //
	public static final byte NEWS_REQUEST = 7;       //
	public static final byte NEWS_COMPOSE_REQUEST = 8;       //
	public static final byte ONE_TIMES_REQUEST = 9;       //
	public static final byte LOGINWRONGUSER = 10;	 
	public static final byte LOGINWRONGPASSWORD = 11;	 //
	
	
	public static final byte STOCK_LINK_REQUEST = 22;  //
	public static final byte CURRENCY_LINK_REQUEST = 23;//
	public static final byte FUTURE_LINK_REQUEST = 24;  //
	public static final byte OPTION_LINK_REQUEST = 25;  //
	public static final byte INDEX_LINK_REQUEST = 26;  //

	public static final byte RESPONSE_LOGIN = 40;
	public static final byte RESPONSE_STOCK = 41;
	public static final byte RESPONSE_CURRENCY = 42;
	public static final byte RESPONSE_FUTURE = 43;
	public static final byte RESPONSE_OPTION = 44;
	public static final byte RESPONSE_INDEX = 45;
	public static final byte RESPONSE_NEWS = 46;
	public static final byte RESPONSE_NEWS_COMPOSE = 47;
	public static final byte RESPONSE_ONE_TIMES = 48;
	

	public static final byte RESPONSE_STOCK_LINK = 61;
	public static final byte RESPONSE_CURRENCY_LINK = 62;
	public static final byte RESPONSE_FUTURE_LINK = 63;
	public static final byte RESPONSE_OPTION_LINK = 64;
	public static final byte RESPONSE_INDEX_LINK = 65;
	
	
	public enum RFAMessageName{
		LOGIN("LOGIN",40),STOCK("STOCK",41),CURRENT("CURRENCY",42),FUTURE("FUTURE",43),OPTION("OPTION",44),INDEX("INDEX",45),NEWS("NEWS",7),ECONCN("ECONCN",48);
		
		String businessName;
		int businessCode;
		
		

		private RFAMessageName(String businessName,int businessCode){
			this.businessName = businessName;
			this.businessCode = businessCode;
		}
		
		public   static  String getRFAMessageName( int  code) {  
	        for  (RFAMessageName c : RFAMessageName.values()) {  
	            if  (c.getBusinessCode() == code) {  
	                return  c.businessName;  
	            }  
	        }  
	        return   null ;  
	    }  
		public String getBusinessName() {
			return businessName;
		}

		public void setBusinessName(String businessName) {
			this.businessName = businessName;
		}

		public int getBusinessCode() {
			return businessCode;
		}

		public void setBusinessCode(int businessCode) {
			this.businessCode = businessCode;
		}
	}
}
