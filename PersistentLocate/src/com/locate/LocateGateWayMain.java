package com.locate;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.locate.common.SystemConstant;

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
		SystemConstant.springContext = new FileSystemXmlApplicationContext("config/propholder.xml");
	}
}
