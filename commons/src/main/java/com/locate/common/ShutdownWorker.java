package com.locate.common;

import com.locate.rmds.processer.EmailNotifier;
import com.locate.rmds.processer.face.INotifier;

public class ShutdownWorker extends Thread{
	public void run(){
		System.out.println("Shutdown worker is run!!!!");
		INotifier notifier = new EmailNotifier();
		String title = "Server info:Locate server shutdowning";
		String content = "The Locate server is shutdowned. Please check!!!";
		notifier.notifyAdmin(title, content);
		System.out.println("Shutdown worker is end!!!!");
	}
}