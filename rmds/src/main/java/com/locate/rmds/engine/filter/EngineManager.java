package com.locate.rmds.engine.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.constant.SystemConstant;
import com.locate.rmds.engine.Engine;
import com.locate.rmds.engine.EngineLine;

public class EngineManager {
	private static Logger logger = LoggerFactory.getLogger(EngineManager.class);
	public static Map<String, EngineLine> engineLineCache = new HashMap<String, EngineLine>();

	/**
	 * 根据衍生商品名称后面的字符生成engineList.
	 * 不可将源商品作为参数.
	 * @param derivactiveItemName
	 * @return
	 */
	public static Map<String,Engine> genEgines(String derivactiveItemName) {
		if(StringUtils.isBlank(derivactiveItemName)){
			logger.error("Derivacted item name can not be BLANK!");
			return null;
		}
		Map<String,Engine> engineMap = new HashMap<String,Engine>();
		String[] itemSplice = derivactiveItemName.split("_");
		for(int i = 2 ;i <itemSplice.length;i++){
			String splice = itemSplice[i];
			if(StringUtils.isBlank(splice)){
				logger.error("splice engine name is blank");
			}else{
				engineMap.put(splice,SystemConstant.springContext.getBean(splice, Engine.class));
			}
		}
		return engineMap;
	}
}
