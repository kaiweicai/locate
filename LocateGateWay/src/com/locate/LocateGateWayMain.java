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
 * @author cloudwei Locate 主要启动程序. 一切从这里开始. 负责启动RFA的主程序和Locate Gate Way 的主程序.
 *         使用spring来管理类.
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
