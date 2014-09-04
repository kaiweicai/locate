package com.locate.rmds.engine.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.locate.common.LocateException;
import com.locate.common.utils.SystemProperties;

public class FilterManager {
	private static Logger logger = Logger.getLogger(FilterManager.class);
	private static String filterConfigFileName = SystemProperties.getProperties(SystemProperties.FILTER_CONFIG_NAME);
	public static Map<String, List<Integer>> filterMap = new HashMap<String, List<Integer>>();
	public static String filterIsAffect = SystemProperties.getProperties(SystemProperties.FILTER_AFFECT,"false");
	public static void loadFilter() {
		logger.info("start to load filterConfig using filter config file name:"+filterConfigFileName);
		BufferedReader filterConfigReader = null;
		try {
			filterConfigReader = new BufferedReader(new FileReader(new File(filterConfigFileName)));
			filterMap.clear();
			String config = filterConfigReader.readLine();
			if (StringUtils.isNotBlank(config)) {
				String[] filterconfig = config.split(":");
				String ric = filterconfig[0];
				String[] filter = filterconfig[1].split(",");
				List<Integer> filterIdList = new ArrayList<Integer>();
				for (int i = 0; i < filter.length; i++) {
					filterIdList.add(Integer.parseInt(filter[i]));
				}
				filterMap.put(ric, filterIdList);
			}
			logger.info("Load filterConfig success!");
		} catch (Exception e) {
			logger.error("Load filterConfig failed!",e);
			throw new LocateException("Load the filter config failed by exeption!", e);
		} finally {
			try {
				filterConfigReader.close();
			} catch (IOException ioe) {
				logger.error("Close the filter config failed by exeption",ioe);
				throw new LocateException("Close the filter config failed by exeption!", ioe);
			}
		}
	}
	
	//customer update the Filter configure.
	public static void updateFilter(){
		//TODO implement the this method in further;
	}
}
