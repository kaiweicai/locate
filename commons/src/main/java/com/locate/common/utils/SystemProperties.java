package com.locate.common.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemProperties {

	static Logger _logger = LoggerFactory.getLogger(SystemProperties.class.getName());

	static Map<String, String> systemProperties = new HashMap<String, String>();
	public static final String RFA_USER_FILE = "client.user.file";
	public static final String SOCKET_PORT = "socket.port";
	public static final String WEB_PORT = "web.port";
	public static final String RFA_FIELD_FILE = "rfa.field.file";
	public static final String RFA_ENUM_FILE = "rfa.enum.file";
	public static final String RFA_NEWS_CODE = "news.code";
	public static final String RFA_SERVICE_NAME = "rfa.service.name";
	public static final String USE_SOCKET_MINA = "socket.mina";
	public static final String DEFAULT_ENCODE = "default.encode";
	
	
	public static final String RFA_CONFIG_FILE = "rfa.config.file";
	
	public static final String RFA_CONNETION_TYPE = "rfa.connetion.type";
	public static final String RFA_CONNETION_SERVER_LIST = "rfa.connetion.serverList";
	public static final String RFA_CONNETION_PORTNUMBER = "rfa.connetion.portNumber";
	public static final String RFA_USER_NAME = "rfa.user.name";
	public static final String RFA_APPLICATION = "rfa.application";
	public static final String RFA_POSITION = "rfa.position";
	
	public static final String ADMIN_NEED_NOTIFY = "admin.need.notify";
	public static final String ADMIN_USER_EMAIL = "admin.user.email";
	public static final String ADMIN_EMAIL_FROM = "admin.email.from";
	public static final String SMTP_ADMIN_USER_EMAIL = "smtp.admin.user.email";
	public static final String SMTP_MAIL_USER_NAME = "smtp.mail.user.name";
	public static final String SMTP_MAIL_PASSWORD = "smtp.mail.password";
	public static final String RIPPLE = "ripple";

	public static final String RIC_ARRAY = "ric.array";

	//filter config
	public static final String FILTER_CONFIG_NAME = "filter.config.name";
	public static final String FILTER_AFFECT = "filter.affect";

	public static final String CUR_US_CYN = "cur.us.cyn";
	public static final String CURRENCY_FIELD="currency.field";
	
	public static void init(String propertiesFile) {
		// InputStream inputStream = this.getClass().getResourceAsStream(propertiesFile);
		Properties p = new Properties();
		try {
//			p.load(ClassLoader.getSystemResource(propertiesFile).openStream());
			p.load(new FileReader( propertiesFile));
		} catch (IOException e) {
			_logger.error("Initialize system config file failed", e);
		}
		Iterator<Object> keys = p.keySet().iterator();
		String key;
		while (keys.hasNext()) {
			key = (String) keys.next();
			systemProperties.put((String) key, p.getProperty(key));
		}
	}

	public static String getProperties(String name) {
		if (StringUtils.isNotEmpty(System.getProperty(name))) {
			return System.getProperty(name);
		} else {
			if (StringUtils.isEmpty(systemProperties.get(name))) {
				_logger.error("Can't find configuration for " + name);
				return "";
			}
			return systemProperties.get(name);
		}
	}
	
	public static String getProperties(String name,String defaultValue) {
		if (StringUtils.isNotEmpty(System.getProperty(name))) {
			return System.getProperty(name);
		} else {
			if (StringUtils.isEmpty(systemProperties.get(name))) {
				_logger.error("Can't find configuration for " + name);
				return defaultValue;
			}
			return systemProperties.get(name);
		}
	}
}
