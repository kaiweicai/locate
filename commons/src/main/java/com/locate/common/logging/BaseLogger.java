package com.locate.common.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.sf.json.JSONObject;

import com.locate.common.model.LocateUnionMessage;

/**
 * 日志基类
 * 
 * @author lijh
 * 
 */
public class BaseLogger {
	protected BaseLogger(Class<?> clazz) {
		this.initSystemParams(clazz);
	}

	protected BaseLogger(Class<?> clazz, boolean replace, Object params) {
		this.needReplace = replace;
		String parameters = "";
		this.params = parameters;
		this.initSystemParams(clazz);
	}

	protected boolean needReplace = false;
	protected Object params;
	// 日志级别
	public static final String LOG_LEVEL_DEBUG = "DEBUG";
	public static final String LOG_LEVEL_INFO = "INFO";
	public static final String LOG_LEVEL_WARNING = "WARN";
	public static final String LOG_LEVEL_ERROR = "ERROR";

	/******* 系统信息 *******/
	// 类名
	private String classname;

	/**
	 * 日志系统属性初始化
	 * 
	 * @param clazz
	 */
	protected void initSystemParams(final Class<?> clazz) {
		// 设置包名+类名
		this.setClassname(clazz.getName());
	}

	public String getDateyyyyMMddHHmmss() {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar calendar = Calendar.getInstance();
		return myFormat.format(calendar.getTime());
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String convMessage(String logLevelInfo, LocateUnionMessage unionMsgBean) {
		JSONObject jsonObject = JSONObject.fromObject(unionMsgBean);
		String jsonResult = jsonObject.toString();
		return jsonResult;
	}

}
