package com.locate.common;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.ApplicationContext;

public class SystemConstant {
	public static ApplicationContext springContext;
	public static AtomicInteger sequenceNo = new AtomicInteger(0);
}