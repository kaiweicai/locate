package com.locate.common;

import java.util.HashMap;
import java.util.Map;

import com.locate.RFApplication.PriceTableModel;
import com.locate.RFApplication.UpdateTableColore;

public class ClientConstant {
	public static final String iamgeDirectory = "config/img/";
	public static Map<String, UpdateTableColore> updateThreadMap = new HashMap<String, UpdateTableColore>();
	public static Map<String, PriceTableModel> itemName2PriceTableModeMap = new HashMap<String, PriceTableModel>();
}
