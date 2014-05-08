package com.locate.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

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
	
	public static void main(String[] args) throws Exception {
		// ȡ����Դ����
		URL url = new URL("http://www.bjtime.cn");
		// �������Ӷ���
		URLConnection uc = url.openConnection();
		// ��������
		uc.connect();
		long time = uc.getDate();
		System.out.println("long time:" + time);
		Date date = new Date(time);
		System.out.println("date:" + date.toString());
		System.out.println(new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(date));
	}
}
