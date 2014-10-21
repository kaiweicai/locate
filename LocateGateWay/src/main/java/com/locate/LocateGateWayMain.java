package com.locate;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.locate.common.constant.SystemConstant;
import com.locate.common.exception.LocateException;
import com.locate.common.logging.err.ErrorLogHandler;
import com.locate.common.model.InstrumentCodeData;
import com.locate.common.utils.LogbackShutdownHook;
import com.locate.common.utils.ShutdownWorker;
import com.locate.common.utils.SystemProperties;

/**
 * 
 * @author cloudwei Locate 主启动程序. 负责连接RFA的主程序和LocateGateWay的主程序.
 *         使用spring容器加载并管理所有的类.
 */
public class LocateGateWayMain {
	static Logger logger = LoggerFactory.getLogger(LocateGateWayMain.class);
	private static ErrorLogHandler errorLogHandler = ErrorLogHandler.getLogger(LocateGateWayMain.class);
	protected static final String configFile = "config/rfaConfig.properties";
	static {
		SystemProperties.init(configFile);
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
		SystemConstant.springContext = new FileSystemXmlApplicationContext(new String[] { "config/propholder.xml" });
		Thread logbackShutdownHook = new LogbackShutdownHook();
		logbackShutdownHook.setName("logbackshutdownWork!");
		Runtime.getRuntime().addShutdownHook(logbackShutdownHook);
		ShutdownWorker shutdownWorker = new ShutdownWorker();
		shutdownWorker.setName("shutdownWorker");
		Runtime.getRuntime().addShutdownHook(shutdownWorker);
		SystemConstant.springContext.registerShutdownHook();
		logger.info("start LocateGateWay success!");
	}
}
