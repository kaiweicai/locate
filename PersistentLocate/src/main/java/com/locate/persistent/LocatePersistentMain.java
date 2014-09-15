package com.locate.persistent;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.locate.common.constant.SystemConstant;
import com.locate.common.exception.LocateException;
import com.locate.common.utils.ShutdownWorker;
import com.locate.rmds.QSConsumerProxy;

/**
 * 
 * @author cloudwei Locate 
 * 主要启动程序. 一切从这里开始. 
 * 负责启动RFA的主程序和Locate Gate Way 的主程序.
 * 使用spring来管理类.
 */
public class LocatePersistentMain {
	static Logger logger = LoggerFactory.getLogger(LocatePersistentMain.class);

	static {
		JoranConfigurator configurator = new JoranConfigurator();
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		LoggerContext loggerContext = (LoggerContext) loggerFactory;
		loggerContext.reset();
		configurator.setContext(loggerContext);
		try {
			configurator.doConfigure("config/logback.xml");
		} catch (JoranException e) {
			logger.error("initial logback.xml error!");
			throw new LocateException("initial logback.xml error!", e);
		}
	}
	
	public static void main(String[] args) {
		logger.info("start LocateGateWay!");
		SystemConstant.springContext = new FileSystemXmlApplicationContext("config/propholder.xml");
		QSConsumerProxy proxy = SystemConstant.springContext.getBean("qSConsumerProxy", QSConsumerProxy.class);
		ShutdownWorker shutdownWorker = new ShutdownWorker();
		shutdownWorker.setName("shutdownWorker");
		Runtime.getRuntime().addShutdownHook(shutdownWorker);
		proxy.makeOrder();
	}
}
