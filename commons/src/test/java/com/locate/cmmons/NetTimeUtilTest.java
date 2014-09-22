package com.locate.cmmons;

import java.net.InetAddress;

import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.junit.Test;

import com.locate.common.utils.NetTimeUtil;

public class NetTimeUtilTest {
	@Test
	public void testNetTime() throws Exception {
		// NTPUDPClient timeClient = new NTPUDPClient();
//		 String timeServerUrl = "time-a.nist.gov";
		 String timeServerUrl = "s2c.time.edu.cn";
//		String timeServerUrl = "ntp.sjtu.edu.cn";
//		String timeServerUrl = "s2g.time.edu.cn";
		InetAddress timeServerAddress = InetAddress.getByName(timeServerUrl);
		TimeInfo timeInfo = NetTimeUtil.getTime(timeServerAddress, 123);
		for (int i = 0; i < 100; i++) {
			TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
			long diffTime = timeStamp.getTime() - System.currentTimeMillis();
			System.out.println(diffTime);
		}
		// DateFormat dateFormat = new
		// SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// System.out.println(dateFormat.format(timeStamp.getDate()));
	}
}
