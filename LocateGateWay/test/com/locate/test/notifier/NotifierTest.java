package com.locate.test.notifier;

import org.junit.Test;

import com.locate.rmds.processer.EmailNotifier;
import com.locate.rmds.util.SystemProperties;

public class NotifierTest {
	@Test
	public void testSendEmail(){
		SystemProperties.init("config/rfaConfig.properties");
		EmailNotifier emailNotifier = new EmailNotifier();
		emailNotifier.notifyAdmin("test","test email");
	}
}
