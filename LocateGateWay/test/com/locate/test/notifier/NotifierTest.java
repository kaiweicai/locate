package com.locate.test.notifier;

import org.junit.Test;

import com.locate.rmds.processer.EmailNotifier;

public class NotifierTest {
	@Test
	public void testSendEmail(){
		EmailNotifier emailNotifier = new EmailNotifier();
		emailNotifier.notifyAdmin();
	}
}
