package com.locate.rmds;

import org.apache.log4j.Logger;

public class RFAServerManager extends Thread {

	static Logger _logger = Logger.getLogger(RFAServerManager.class.getName());

	QSConsumerProxy demo = new QSConsumerProxy();
	

	public void init() {
		// Startup and initialization
		demo.init();

		// Login
		// and Item request
		// Item requests is done after application received login response.
		// The method itemRequests is called from processLogin method.
		demo.login();
	}

	private void startServer() {
		try {
			
			while(!RFApplication.stop){
				//start Dispatch events
				demo.startDispatch();
			}
		} catch (Exception e) {
			_logger.error(e.getMessage(), e);
			demo.cleanup();
		}
	}

	public void run() {
		System.out.println("Start execute init event.");
		// Startup and initialization
		startServer();
	}
}