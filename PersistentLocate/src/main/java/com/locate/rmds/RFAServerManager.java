package com.locate.rmds;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.locate.rmds.statistic.StatisticThread;

@Service
public class RFAServerManager extends Thread {
	private static boolean connectedDataSource;
	static Logger _logger = Logger.getLogger(RFAServerManager.class);

	@Resource
	private QSConsumerProxy proxy;
	public static boolean stop = false;
	
	public static AtomicInteger sequenceNo = new AtomicInteger(0);
	
	@PostConstruct
	public void init() {
		this.start();
	}

	private void startServer() {
		try {
			
			while(!stop){
				//start Dispatch events
				proxy.startDispatch();
			}
		} catch (Exception e) {
			_logger.error("System error occure, Please contact with Cloud.Wei",e);
//			demo.cleanup();
		}
	}

	public void run() {
		System.out.println("Start execute init event.");
		// Startup and initialization
		startServer();
	}

	public static boolean isConnectedDataSource() {
		return connectedDataSource;
	}

	public static void setConnectedDataSource(boolean connectedDataSource) {
		RFAServerManager.connectedDataSource = connectedDataSource;
	}
}