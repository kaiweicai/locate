package com.locate.persistent;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.locate.LocateGateWayMain;
import com.locate.common.SystemConstant;
import com.locate.rmds.QSConsumerProxy;

/**
 * 
 * @author cloudwei Locate 
 * ��Ҫ��������. һ�д����￪ʼ. 
 * ��������RFA���������Locate Gate Way ��������.
 * ʹ��spring��������.
 */
public class LocatePersistentMain {
	static {
		DOMConfigurator.configureAndWatch("config/log4j.xml");
	}
	static Logger logger = Logger.getLogger(LocateGateWayMain.class);

	public static void main(String[] args) {
		logger.info("start LocateGateWay!");
		SystemConstant.springContext = new FileSystemXmlApplicationContext("config/propholder.xml");
		QSConsumerProxy proxy = SystemConstant.springContext.getBean("qSConsumerProxy", QSConsumerProxy.class);
		proxy.makeOrder();
	}
}
