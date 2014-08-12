package com.locate;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.locate.common.SystemConstant;
import com.locate.rmds.processer.EmailNotifier;
import com.locate.rmds.processer.face.INotifier;
import com.locate.rmds.util.SystemProperties;

/**
 * 
 * @author cloudwei Locate ��Ҫ��������. һ�д����￪ʼ. ��������RFA���������Locate Gate Way ��������.
 *         ʹ��spring��������.
 */
public class LocateGateWayMain {
	static {
		DOMConfigurator.configureAndWatch("config/log4j.xml");
	}
	static Logger logger = Logger.getLogger(LocateGateWayMain.class);

	public static void main(String[] args) {
		logger.info("start LocateGateWay!");
		SystemConstant.springContext = new FileSystemXmlApplicationContext(new String[] { "config/propholder.xml" });
		ShutdownWorker shutdownWorker = new ShutdownWorker();
		shutdownWorker.setName("shutdownWorker");
		Runtime.getRuntime().addShutdownHook(shutdownWorker);
	}

	static class ShutdownWorker extends Thread{
		public void run(){
			System.out.println("Shutdown is run!!!!");
			INotifier notifier = new EmailNotifier();
			String needNotify = SystemProperties.getProperties(SystemProperties.ADMIN_NEED_NOTIFY);
			if (needNotify.equalsIgnoreCase("true")) {
				notifier.notifyAdmin();
			}
			System.out.println("Shutdown is end!!!!");
		}
	}
}
