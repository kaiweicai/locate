package com.locate.common.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.logging.err.ErrorLogHandler;

public class CustomerFiled {
	private static Logger logger = LoggerFactory.getLogger(CustomerFiled.class);
	private static ErrorLogHandler errorLogHandler = ErrorLogHandler.getLogger(CustomerFiled.class);
	public static Map<String,String> filedExchangeMap = new HashMap<String,String>();
	static{
		loadExchangFile();
	}
	
	public static void loadExchangFile(){
		BufferedReader bufferReader = null;
		
		try{
			FileInputStream filedNameExchangeFileStream = new FileInputStream("config/fieldNameExchange");
			bufferReader = new BufferedReader(new InputStreamReader(filedNameExchangeFileStream,"UTF-8"));
			String line = "";
			while((line = bufferReader.readLine())!=null){
				String[] lineContent = line.split(",");
				if(lineContent.length>4){
					filedExchangeMap.put(lineContent[0], lineContent[4]);
				}
			}
		}catch(Exception e){
			errorLogHandler.error("read exhange file error!",e);
		}finally{
			try {
				bufferReader.close();
			} catch (IOException e) {
				errorLogHandler.error("close buffer reader error!",e);
			}
		}
	}
	private String id;
	private String name;
	private String type;
	private String value;
	
	public CustomerFiled(String id, String name, String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
