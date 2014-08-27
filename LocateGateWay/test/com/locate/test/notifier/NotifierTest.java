package com.locate.test.notifier;

import org.junit.Test;

import com.locate.common.utils.SystemProperties;
import com.locate.rmds.processer.EmailNotifier;

public class NotifierTest {
	@Test
	public void testSendEmail(){
		SystemProperties.init("config/rfaConfig.properties");
		EmailNotifier emailNotifier = new EmailNotifier();
		emailNotifier.notifyAdmin("test","test email");
	}
}
