package com.locate.test;

import com.locate.rmds.processer.EmailNotifier;
import com.locate.rmds.processer.face.INotifier;
import com.locate.rmds.util.SystemProperties;

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
				notifier.notifyAdmin();
			}
		}
	}
}
