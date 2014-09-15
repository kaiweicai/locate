package com.locate.test;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

public class Hello {
	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		JoranConfigurator configurator = new JoranConfigurator();
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		LoggerContext loggerContext = (LoggerContext) loggerFactory;
		loggerContext.reset();
		configurator.setContext(loggerContext);
		configurator.doConfigure("config/logback.xml");

		Logger log = LoggerFactory.getLogger(Hello.class);
		log.info("hello world");

		Thread logbackShutdownHook = new Thread(new LogbackShutdownHook());
		Runtime.getRuntime().addShutdownHook(logbackShutdownHook);

		Thread appShutdownHook = new Thread(new AppShutdownHook());
		Runtime.getRuntime().addShutdownHook(appShutdownHook);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}
		System.err.println("TestApplication exited normally");
	}

	private static class LogbackShutdownHook implements Runnable {
		@Override
		public void run() {
			System.err.println("LogbackShutdownHook started");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
			}
			System.err.println("LogbackShutdownHook finished");
		}
	}

	private static class AppShutdownHook implements Runnable {

		@Override
		public void run() {
			System.err.println("AppShutdownHook started");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
			}
			System.err.println("AppShutdownHook finished");
		}
	}
}
