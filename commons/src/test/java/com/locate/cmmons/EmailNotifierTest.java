package com.locate.cmmons;

import org.junit.Test;

import com.locate.common.utils.SystemProperties;
import com.locate.rmds.processer.EmailNotifier;
import com.locate.rmds.processer.face.INotifier;

public class EmailNotifierTest {
	
	@Test
	public void test() {
		INotifier notifier = new EmailNotifier();
		SystemProperties.init("src/main/resources/rfaConfig.properties");
//		notifier.notifyAdmin("testEmail", "testEmail");
	}

}
