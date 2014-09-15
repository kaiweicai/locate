package com.locate.common.utils;

import com.locate.rmds.processer.EmailNotifier;
import com.locate.rmds.processer.face.INotifier;

public class ShutdownWorker extends Thread{
	public void run(){
		System.out.println("Shutdown worker is run!!!!");
		INotifier notifier = new EmailNotifier();
		String title = "Server info:Locate server shutdowning";
		String content = "The Locate server is shutdowned. Please check!!!";
		notifier.notifyAdmin(title, content);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
			System.out.println("Shutdown hook runner interrupt exception!");
		}
		
		System.out.println("Shutdown worker is end!!!!");
	}
}