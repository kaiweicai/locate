package com.locate.common.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.locate.common.datacache.DataBaseCache;

public class NetTimeUtil {
	static Logger logger = Logger.getLogger(NetTimeUtil.class);
	public static long getNetTime(){
		long time=0;
		try {
			URL url = new URL("http://www.bjtime.cn");
			URLConnection uc = url.openConnection();
			uc.connect();
			time = uc.getDate();
		} catch (MalformedURLException e) {
			logger.error("malfFormat URl:http://www.bjtime.cn",e.getCause());
		}catch(IOException ioe){
			logger.error("IOException ");
		}
		return time;
	}
	
	public static long getCurrentNetTime(){
		logger.info("checkTime is "+DataBaseCache.NET_SUB_LOCAL_TIME);
		return DataBaseCache.NET_SUB_LOCAL_TIME+System.currentTimeMillis();
	}
}
