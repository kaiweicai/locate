package com.locate.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.locate.common.model.LocateUnionMessage;

public class LoggerAnalyzer {
	public String logPath = "LocateClient/log/biz";
	public static final String LOG_FILE_HEADER = "biz";
	public static final String LOG_FILE_ENDDER = "log";
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
	
	public List<LocateUnionMessage> readLogFile(String itemName){
		
	}
}
