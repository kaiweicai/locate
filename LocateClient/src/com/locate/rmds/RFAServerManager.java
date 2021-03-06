package com.locate.rmds;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class RFAServerManager extends Thread {

	static Logger _logger = Logger.getLogger(RFAServerManager.class);

	
	public static AtomicInteger sequenceNo = new AtomicInteger(0);
	
	private static boolean connectedDataSource;
	public void init() {
		// Startup and initialization
//		demo.init();
		// Login
		// and Item request
		// Item requests is done after application received login response.
		// The method itemRequests is called from processLogin method.
//		demo.login();
//		statisticDemo = new StatisticThread();
//		statisticDemo.start();
		this.start();
	}

	private void startServer() {
//		try {
//			
//			while(!RFApplication.stop){
//				//start Dispatch events
//				demo.startDispatch();
//			}
//		} catch (Exception e) {
//			_logger.error(e.getMessage(), e);
//			demo.cleanup();
//		}
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