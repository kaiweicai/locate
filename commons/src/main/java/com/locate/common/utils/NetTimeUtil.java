package com.locate.common.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Impl;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.datacache.DataBaseCache;

public class NetTimeUtil {
	static Logger logger = LoggerFactory.getLogger(NetTimeUtil.class);
	public static final long NET_SUB_LOCAL_TIME = getNetTime() - System.currentTimeMillis();
	private static String timeServerUrl1 = "ntp.sjtu.edu.cn";
	private static String timeServerUrl2 = "ntp.nasa.gov";

	public static long getNetTime() {
		long time = 0;
		try {
			NTPUDPClient timeClient = new NTPUDPClient();
			// String timeServerUrl = "ntp.sjtu.edu.cn";
			InetAddress timeServerAddress = InetAddress.getByName(timeServerUrl1);
			TimeInfo timeInfo = getTime(timeServerAddress, 123);
			TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
			time = timeStamp.getTime();
		} catch (UnknownHostException e) {
			logger.error("malfFormat URl:ntp.sjtu.edu.cn", e.getCause());
		} catch (IOException e) {
			logger.error("IOException ", e);
			try {
				InetAddress timeServerAddress = InetAddress.getByName(timeServerUrl2);
				TimeInfo timeInfo = getTime(timeServerAddress, 123);
				TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
				time = timeStamp.getTime();
			} catch (IOException ioe) {
				logger.error("IOException ", ioe);
			}
		}
		return time;
	}

	public static long getCurrentNetTime() {
		logger.debug("checkTime is " + NET_SUB_LOCAL_TIME);
		return NET_SUB_LOCAL_TIME + System.currentTimeMillis();
	}

	public static TimeInfo getTime(InetAddress host, int port) throws IOException {
		DatagramSocket _socket_ = new DatagramSocket();
		try {
			NtpV3Packet message = new NtpV3Impl();
			message.setMode(3);
			message.setVersion(3);
			DatagramPacket sendPacket = message.getDatagramPacket();
			sendPacket.setAddress(host);
			sendPacket.setPort(port);

			NtpV3Packet recMessage = new NtpV3Impl();
			DatagramPacket receivePacket = recMessage.getDatagramPacket();

			TimeStamp now = TimeStamp.getCurrentTime();

			message.setTransmitTime(now);
			_socket_.setSoTimeout(1000);
			_socket_.send(sendPacket);
			_socket_.receive(receivePacket);

			long returnTime = System.currentTimeMillis();

			TimeInfo info = new TimeInfo(recMessage, returnTime, false);

			return info;
		} finally {
			_socket_.close();
		}
	}
}
