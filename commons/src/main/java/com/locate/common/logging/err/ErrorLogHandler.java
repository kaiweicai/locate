package com.locate.common.logging.err;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.logging.BaseLogger;

public class ErrorLogHandler extends BaseLogger {

	private static Logger logger = LoggerFactory.getLogger(ErrorLogHandler.class);

	public static ErrorLogHandler getLogger(final Class<?> clazz) {
		return new ErrorLogHandler(clazz);
	}

	private ErrorLogHandler(final Class<?> clazz) {
		super(clazz);
	}

	public static ErrorLogHandler getLogger(final Class<?> clazz, boolean replace, Object params) {
		return new ErrorLogHandler(clazz, replace, params);
	}

	public ErrorLogHandler(Class<?> clazz, boolean replace, Object params) {
		super(clazz, replace, params);
	}

	public void error(String msgContext){
		logger.error(msgContext);
	}
	
	public void error(String msgContext, Throwable t) {
		if (t == null) {
			t = new Throwable();
		}
		logger.error(msgContext, t);
	}
}
