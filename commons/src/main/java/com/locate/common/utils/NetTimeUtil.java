package com.locate.common.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.datacache.DataBaseCache;

public class NetTimeUtil {
	static Logger logger = LoggerFactory.getLogger(NetTimeUtil.class);
	public static final long NET_SUB_LOCAL_TIME = getNetTime() - System.currentTimeMillis();
	public static long getNetTime() {
		long time = 0;
		// try {
		// URL url = new URL("http://www.bjtime.cn");
		// URLConnection uc = url.openConnection();
		// uc.connect();
		// time = uc.getDate();
		// } catch (MalformedURLException e) {
		// logger.error("malfFormat URl:http://www.bjtime.cn",e.getCause());
		// }catch(IOException ioe){
		// logger.error("IOException ");
		// }
		try {
			NTPUDPClient timeClient = new NTPUDPClient();
			String timeServerUrl = "ntp.nasa.gov";
			// String timeServerUrl = "ntp.sjtu.edu.cn";
			InetAddress timeServerAddress = InetAddress.getByName(timeServerUrl);
			TimeInfo timeInfo = timeClient.getTime(timeServerAddress);
			TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
			time = timeStamp.getTime();
		} catch (UnknownHostException e) {
			logger.error("malfFormat URl:ntp.sjtu.edu.cn", e.getCause());
		} catch (IOException e) {
			logger.error("IOException ", e);
		}
		return time;
	}

	public static long getCurrentNetTime() {
		logger.debug("checkTime is " + NET_SUB_LOCAL_TIME);
		return NET_SUB_LOCAL_TIME + System.currentTimeMillis();
	}

	public static void main(String[] args) {
		// for(int i=0;i<100;i++){
		// System.out.println(getNetTime()-System.currentTimeMillis());
		// }
		try {
			for (int i = 0; i < 100; i++) {
				NTPUDPClient timeClient = new NTPUDPClient();
//				 String timeServerUrl = "time-a.nist.gov";
				String timeServerUrl = "ntp.nasa.gov";
				InetAddress timeServerAddress = InetAddress.getByName(timeServerUrl);
				TimeInfo timeInfo = timeClient.getTime(timeServerAddress);
				TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
				long diffTime = timeStamp.getTime() - System.currentTimeMillis();
				System.out.println(diffTime);
			}

			// DateFormat dateFormat = new
			// SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			// System.out.println(dateFormat.format(timeStamp.getDate()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
