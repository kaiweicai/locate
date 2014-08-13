package com.locate;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.locate.common.SystemConstant;
import com.locate.rmds.processer.EmailNotifier;
import com.locate.rmds.processer.face.INotifier;

/**
 * 
 * @author cloudwei Locate 
 * ����������. 
 * ��������RFA���������LocateGateWay��������.
 * ʹ��spring�������ز��������е���.
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
			System.out.println("Shutdown worker is run!!!!");
			INotifier notifier = new EmailNotifier();
			String title="Locate server shutdowning";
			String content="The Locate server is shutdowned. Please check!!!";
			notifier.notifyAdmin(title,content);
			System.out.println("Shutdown worker is end!!!!");
		}
	}
}
