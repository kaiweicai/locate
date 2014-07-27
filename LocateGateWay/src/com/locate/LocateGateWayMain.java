package com.locate;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.chainsaw.Main;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.locate.common.SystemConstant;
import com.locate.rmds.RFAServerManager;

/**
 * 
 * @author cloudwei Locate 
 * ��Ҫ��������. һ�д����￪ʼ. 
 * ��������RFA���������Locate Gate Way ��������.
 * ʹ��spring��������.
 */
public class LocateGateWayMain {
	static {
		DOMConfigurator.configureAndWatch("config/log4j.xml");
	}
	static Logger logger = Logger.getLogger(LocateGateWayMain.class);

	public static void main(String[] args) {
		logger.info("start LocateGateWay!");
		SystemConstant.springContext = new FileSystemXmlApplicationContext(new String[] { "config/propholder.xml" });
	}
}
