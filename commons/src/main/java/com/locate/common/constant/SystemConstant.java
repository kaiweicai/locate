package com.locate.common.constant;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SystemConstant {
	public static AbstractXmlApplicationContext springContext;
	public static AtomicInteger sequenceNo = new AtomicInteger(0);
	public static AtomicInteger channelId = new AtomicInteger(0);
	public static String BOOLEAN_TRUE="true";
}
