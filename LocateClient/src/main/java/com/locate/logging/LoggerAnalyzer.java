package com.locate.logging;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.JsonUtil;

public class LoggerAnalyzer {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public String logPath = "log/biz/";
	public static final String LOG_FILE_HEADER = "biz.";
	public static final String LOG_FILE_ENDDER = ".log";
	public LoggerAnalyzer(){
		
	}
	
	public LoggerAnalyzer(String loggerPath){
		this.logPath = loggerPath;
	}
	
	public String getLoggerFileName() {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		String logFileName = LOG_FILE_HEADER + myFormat.format(calendar.getTime()) + LOG_FILE_ENDDER;
		return logFileName;
	}
	
	public List<LocateUnionMessage> readLogFile(String itemName) {
		List<LocateUnionMessage> resultList = new ArrayList<LocateUnionMessage>();
		String logFileName = logPath+getLoggerFileName();
		BufferedReader bufferReader = null;
		try {
			bufferReader = new BufferedReader(new FileReader(logFileName));
			String content = "";
			while((content = bufferReader.readLine())!=null){
				LocateUnionMessage message = JsonUtil.translateJsonToUionMessage(JSONObject.fromObject(content));
				if(itemName.equals(message.getItemName().trim())){
					resultList.add(message);
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("File not exist, logFileName is " + logFileName, e);
		} catch (IOException e) {
			logger.error("read log file error,",e);
		}finally{
			try {
				bufferReader.close();
			} catch (IOException e) {
				logger.error("close buffer error!",e);
			}
		}
		return resultList;
	}
	
	public static void main(String[] args) {
		LoggerAnalyzer analyzer = new LoggerAnalyzer();
		List<LocateUnionMessage> messageList = analyzer.readLogFile("XPD=");
		for (LocateUnionMessage message : messageList) {
			System.out.println(message);
		}
	}
}
