package com.locate.test;

import org.apache.commons.lang.StringUtils;

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
			if (StringUtils.isBlank(needNotify) || !needNotify.equalsIgnoreCase("true")) {
				return;
			}
//			notifier.notifyAdmin("test","test");
		}
	}
}
