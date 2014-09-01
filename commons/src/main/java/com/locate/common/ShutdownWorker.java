package com.locate.common;

import org.apache.commons.lang.StringUtils;

import com.locate.common.utils.SystemProperties;
import com.locate.rmds.processer.EmailNotifier;
import com.locate.rmds.processer.face.INotifier;

public class ShutdownWorker extends Thread{
	public void run(){
		System.out.println("Shutdown worker is run!!!!");
		INotifier notifier = new EmailNotifier();
		String needNotify = SystemProperties.getProperties(SystemProperties.ADMIN_NEED_NOTIFY);
		if (StringUtils.isBlank(needNotify) || !needNotify.equalsIgnoreCase("true")) {
			String title="Locate server shutdowning";
			String content="The Locate server is shutdowned. Please check!!!";
			notifier.notifyAdmin(title,content);
		}
		System.out.println("Shutdown worker is end!!!!");
	}
}