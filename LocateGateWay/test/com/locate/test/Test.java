package com.locate.test;

import com.locate.common.utils.SystemProperties;
import com.locate.rmds.processer.EmailNotifier;
import com.locate.rmds.processer.face.INotifier;

public class Test {
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new ShutdownWorker());
	}
	static class ShutdownWorker extends Thread{
		public void run(){
			INotifier notifier = new EmailNotifier();
			SystemProperties.init("config/rfaConfig.properties");
			String needNotify = SystemProperties.getProperties(SystemProperties.ADMIN_NEED_NOTIFY);
			if (needNotify.equalsIgnoreCase("true")) {
				notifier.notifyAdmin("test","test");
			}
		}
	}
}
