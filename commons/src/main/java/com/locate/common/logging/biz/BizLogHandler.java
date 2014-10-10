package com.locate.common.logging.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.logging.BaseLogger;
import com.locate.common.model.LocateUnionMessage;

/**
 * 业务日志
 * @author Cloud.Wei
 *
 */
public class BizLogHandler extends BaseLogger{

	private static Logger logger = LoggerFactory.getLogger(BizLogHandler.class);

	public static BizLogHandler getLogger(final Class<?> clazz) {
		return new BizLogHandler(clazz);
	}
	
	public static BizLogHandler getLogger(final Class<?> clazz, boolean replace, Object params) {
		return new BizLogHandler(clazz,replace,params);
	}

	private BizLogHandler(final Class<?> clazz) {
		super(clazz);
	}
	
	private BizLogHandler(final Class<?> clazz, boolean replace, Object params) {
		super(clazz,replace,params);
	}

	public void info(LocateUnionMessage unionMsgBean) {
		String tmp = super.convMessage(LOG_LEVEL_INFO,unionMsgBean);
		if(needReplace){
			logger.info(tmp,params);
		}else{
			logger.info(tmp);
		}
	}
}
