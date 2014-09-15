package com.locate.common.utils;

public class LogbackShutdownHook extends Thread{
	@Override
	public void run() {
		System.err.println("LogbackShutdownHook started");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
			System.out.println("Logback shutdown hook interrupt!");
		}
		System.err.println("LogbackShutdownHook finished");
	}
}
