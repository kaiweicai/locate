package com.locate.common.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.locate.common.logging.err.ErrorLogHandler;
import com.locate.common.utils.DerivedUtils;

/**
 * price tech instument code data structrue
 * @author CloudWei kaiweicai@163.com
 * create time 2014年10月21日
 * @copyRight by Author
 */
public class InstrumentCodeData {
	public static Map<String, InstrumentCodeModel> chineseNameMap = new HashMap<String, InstrumentCodeModel>();
	public static Map<String, InstrumentCodeModel> instrumentCodeMap = new HashMap<String, InstrumentCodeModel>();
	public static Map<String, InstrumentCodeModel> sourceCodeMap = new HashMap<String, InstrumentCodeModel>();
	public static final String instrumentCodeFileName = "config/PTInstrumentCode";
	public static ErrorLogHandler errorLogHandler = ErrorLogHandler.getLogger(InstrumentCodeData.class);

	public static String exchangeInstrumentCodeToSourceCode(String intrumentCode) {
		InstrumentCodeModel instrumentCodeModel = instrumentCodeMap.get(intrumentCode);
		if(instrumentCodeModel==null){
			return intrumentCode;
		}
		String sourceCode = instrumentCodeModel.getSourceCode();
		if ("empty".equals(sourceCode)) {
			return intrumentCode;
		}
		return sourceCode;
	}
	
	public static String exchangeSourceCode2InstrumentCode(String sourceCode){
		String instrumentCode = null;
		InstrumentCodeModel instrumentCodeModel = sourceCodeMap.get(sourceCode);
		if(instrumentCodeModel==null){
			instrumentCode = sourceCode;
		}else{
			instrumentCode = instrumentCodeModel.getInstrumentCode();
		}
		return instrumentCode;
	}
	
	public static void loadInstrumentData() {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(instrumentCodeFileName));
			String instrumentCodeLine = null;
			while ((instrumentCodeLine = bufferedReader.readLine()) != null) {
				if (StringUtils.isNotBlank(instrumentCodeLine)) {
					String[] instrumentArray = instrumentCodeLine.split(",");
					String instrumentCode = instrumentArray[1];
					String chineseName = instrumentArray[0];
					String sourceCode = instrumentArray[2];
					InstrumentCodeModel instrumentCodeModel = new InstrumentCodeModel(instrumentCode, chineseName,
							sourceCode);
					chineseNameMap.put(chineseName, instrumentCodeModel);
					instrumentCodeMap.put(instrumentCode, instrumentCodeModel);
					sourceCodeMap.put(sourceCode, instrumentCodeModel);
				}
			}
		} catch (FileNotFoundException e) {
			errorLogHandler.error("IntrumentCode file not found, file name is " + instrumentCodeFileName, e);
		} catch (IOException e) {
			errorLogHandler.error("IntrumentCode file read error! ", e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					errorLogHandler.error("Close bufferedReader error!", e);
				}
			}
		}
	}
}
