package com.locate;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.chainsaw.Main;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.locate.rmds.RFAServerManager;

/**
 * 
 * @author cloudwei Locate 
 * 主要启动程序. 一切从这里开始. 
 * 负责启动RFA的主程序和Locate Gate Way 的主程序.
 * 使用spring来管理类.
 */
public class LocateGateWayMain {
	static {
		PropertyConfigurator.configureAndWatch("config/log4j.properties");
	}
	static Logger logger = Logger.getLogger(RFAServerManager.class.getName());
	public static ApplicationContext springContext;

	public static void main(String[] args) {
		logger.info("start LocateGateWay!");
		springContext = new FileSystemXmlApplicationContext(new String[] { "config/propholder.xml" });
	}
}
