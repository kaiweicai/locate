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

	static Logger _logger = Logger.getLogger(RFAServerManager.class);

	@Resource
	private QSConsumerProxy demo;
	public static boolean stop = false;
	StatisticThread statisticDemo;
	
	public static AtomicInteger sequenceNo = new AtomicInteger(0);
	
	private static boolean connectedDataSource;
	@PostConstruct
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
		try {
			
			while(!stop){
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

	public static boolean isConnectedDataSource() {
		return connectedDataSource;
	}

	public static void setConnectedDataSource(boolean connectedDataSource) {
		RFAServerManager.connectedDataSource = connectedDataSource;
	}
	
	public QSConsumerProxy getDemo() {
		return demo;
	}

	public void setDemo(QSConsumerProxy demo) {
		this.demo = demo;
	}
}