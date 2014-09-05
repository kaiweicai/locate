package com.locate.cmmons;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class NetTimeUtilTest {
	@Test
	public void testNetTime() throws Exception {
		long localTime = System.currentTimeMillis();
		// 取得资源对象
		URL url = new URL("http://www.bjtime.cn");
		// 生成连接对象
		URLConnection uc = url.openConnection();
		// 发出连接
		uc.connect();
		long netTime = uc.getDate();
		System.out.println("long time:" + netTime);
		Date date = new Date(netTime);
		System.out.println("date:" + date.toString());
		System.out.println(new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(date));
		long endTime = System.currentTimeMillis();
		System.out.println("user time =" + (netTime - localTime));
	}
}
