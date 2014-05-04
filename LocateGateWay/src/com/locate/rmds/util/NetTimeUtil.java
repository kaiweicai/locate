package com.locate.rmds.util;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NetTimeUtil {
	public static void main(String[] args) throws Exception {
		// 取得资源对象
		URL url = new URL("http://www.bjtime.cn");
		// 生成连接对象
		URLConnection uc = url.openConnection();
		// 发出连接
		uc.connect();
		long time = uc.getDate();
		System.out.println("long time:" + time);
		Date date = new Date(time);
		System.out.println("date:" + date.toString());
		System.out.println(new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(date));
	}
}
