package com.locate.common.constant;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.ApplicationContext;

public class SystemConstant {
	public static ApplicationContext springContext;
	public static AtomicInteger sequenceNo = new AtomicInteger(0);
	public static AtomicInteger channelId = new AtomicInteger(0);
	public static String BOOLEAN_TRUE="true";
}
