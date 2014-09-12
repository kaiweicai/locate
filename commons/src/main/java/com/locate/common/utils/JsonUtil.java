package com.locate.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.dom4j.Document;

import sun.misc.IOUtils;

import com.locate.common.model.LocateUnionMessage;

public class JsonUtil {
	/**
	 * 将xml字符串转换为JSON对象
	 * 
	 * @param xmlFile
	 *            xml字符串
	 * @return JSON对象
	 */
	public static JSON getJSONFromXml(String xmlString) {
		XMLSerializer xmlSerializer = new XMLSerializer();
		JSON json = xmlSerializer.read(xmlString);
		return json;
	}

	/**
	 * 将xmlDocument转换为JSON对象
	 * 
	 * @param xmlDocument
	 *            XML Document
	 * @return JSON对象
	 */
	public JSON getJSONFromXml(Document xmlDocument) {
		String xmlString = xmlDocument.toString();
		return getJSONFromXml(xmlString);
	}

	/**
	 * 将xml字符串转换为JSON字符串
	 * 
	 * @param xmlString
	 * @return JSON字符串
	 */
	public String getJSONStringFromXml(String xmlString) {
		return getJSONFromXml(xmlString).toString();
	}

	/**
	 * 将xmlDocument转换为JSON字符串
	 * 
	 * @param xmlDocument
	 *            XML Document
	 * @return JSON字符串
	 */
	public String getXMLtoJSONString(Document xmlDocument) {
		return getJSONStringFromXml(xmlDocument.toString());
	}

	/**
	 * 读取XML文件准换为JSON字符串
	 * 
	 * @param xmlFile
	 *            XML文件
	 * @return JSON字符串
	 */
	public String getXMLFiletoJSONString(String xmlFile) {
		InputStream is = JsonUtil.class.getResourceAsStream(xmlFile);
		String xml;
		JSON json = null;
		try {
			xml = new String(IOUtils.readFully(is, -1, true));
			XMLSerializer xmlSerializer = new XMLSerializer();
			json = xmlSerializer.read(xml);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	/**
	 * 将Java对象转换为JSON格式的字符串
	 * 
	 * @param javaObj
	 *            POJO,例如日志的model
	 * @return JSON格式的String字符串
	 */
	public static String getJsonStringFromJavaPOJO(Object javaObj) {
		return JSONObject.fromObject(javaObj).toString(1);
	}

	/**
	 * 将Map准换为JSON字符串
	 * 
	 * @param map
	 * @return JSON字符串
	 */
	public static String getJsonStringFromMap(Map<?, ?> map) {
		JSONObject object = JSONObject.fromObject(map);
		return object.toString();
	}

	public static LocateUnionMessage translateJsonToUionMessage(JSONObject jsonObject){
		LocateUnionMessage message = (LocateUnionMessage)JSONObject.toBean( jsonObject, LocateUnionMessage.class);
		List<String[]> newPayloadSet = new ArrayList<String[]>();
		for(Object payLoadObject : message.getPayLoadSet()){
			String[] payLoadString = new String[4];
			List<String> payLoadList = (ArrayList<String>)payLoadObject;
			for(int i =0 ;i<payLoadList.size();i++){
				payLoadString[i]=payLoadList.get(i);
			}
			newPayloadSet.add(payLoadString);
		}
		message.setPayLoadSet(newPayloadSet);
		return message;
	}
}
