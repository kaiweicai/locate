package com.reuters.rfa.internal.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.locate.rmds.QSConsumerProxy;

public class FieldNameExchanger {
	public static final String FIELD_FILE_NAME = "config/fieldNameExchange";
	public static Logger logger = Logger.getLogger(FieldNameExchanger.class);
	public static void loadFieldExchange() {
		BufferedReader fieldExchangeReader = null;
		try {
			fieldExchangeReader = new BufferedReader(new FileReader(new File(FIELD_FILE_NAME)));
			String filedName = "";
			while ((filedName = fieldExchangeReader.readLine()) != null) {
				String[] fieldConfig = filedName.split(",");
				if (fieldConfig.length>4) {
					short id = Short.parseShort(fieldConfig[0]);
					String exchangeName = fieldConfig[4];
					((FidDefImpl)QSConsumerProxy.dictionary.getFidDef(id))._name=exchangeName;
				}
			}
		} catch (Exception e) {
			logger.error("exchange field name error"+e);
			System.exit(-1);
		}
	}
	
	public static void main(String[] args) throws Exception {
		QSConsumerProxy.initializeDictionary("../LocateGateWay/config/RDM/RDMFieldDictionary",
				"../LocateGateWay/config/RDM/enumtype.def");
		FieldNameExchanger.loadFieldExchange();
	}
}
