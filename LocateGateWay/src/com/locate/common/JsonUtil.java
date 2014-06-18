package com.locate.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.dom4j.Document;
import org.junit.Test;

import sun.misc.IOUtils;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public class JsonUtil {
	/**
	 * ��xml�ַ���ת��ΪJSON����
	 * 
	 * @param xmlFile
	 *            xml�ַ���
	 * @return JSON����
	 */
	public static JSON getJSONFromXml(String xmlString) {
		XMLSerializer xmlSerializer = new XMLSerializer();
		JSON json = xmlSerializer.read(xmlString);
		return json;
	}
	
    /** 
     * ��xmlDocumentת��ΪJSON���� 
     * @param xmlDocument XML Document 
     * @return JSON���� 
     */  
    public JSON getJSONFromXml(Document xmlDocument) {  
        String xmlString = xmlDocument.toString();  
        return getJSONFromXml(xmlString);  
    }
    
    /** 
     * ��xml�ַ���ת��ΪJSON�ַ��� 
     * @param xmlString 
     * @return JSON�ַ��� 
     */  
    public String getJSONStringFromXml(String xmlString ) {  
        return getJSONFromXml(xmlString).toString();  
    }  
    
    /** 
     * ��xmlDocumentת��ΪJSON�ַ��� 
     * @param xmlDocument XML Document 
     * @return JSON�ַ��� 
     */  
    public String getXMLtoJSONString(Document xmlDocument) {  
        return getJSONStringFromXml(xmlDocument.toString());  
    }  
    
    /** 
     * ��ȡXML�ļ�׼��ΪJSON�ַ��� 
     * @param xmlFile  XML�ļ� 
     * @return JSON�ַ��� 
     */  
    public String getXMLFiletoJSONString(String xmlFile) {  
        InputStream is = JsonUtil.class.getResourceAsStream(xmlFile);  
        String xml;  
        JSON json = null;  
        try {  
            xml = new String(IOUtils.readFully(is,-1,true));  
            XMLSerializer xmlSerializer = new XMLSerializer();  
            json = xmlSerializer.read(xml);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return json.toString();  
    }
    
    /** 
     * ��Java����ת��ΪJSON��ʽ���ַ��� 
     *  
     * @param javaObj 
     *            POJO,������־��model 
     * @return JSON��ʽ��String�ַ��� 
     */  
    public static String getJsonStringFromJavaPOJO(Object javaObj) {  
        return JSONObject.fromObject(javaObj).toString(1);  
    }  
    
    /** 
     * ��Map׼��ΪJSON�ַ��� 
     * @param map 
     * @return JSON�ַ��� 
     */  
    public static  String getJsonStringFromMap(Map<?, ?> map) {  
        JSONObject object = JSONObject.fromObject(map);  
        return object.toString();  
    }  

	@Test
	public void testChangeJson() {
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rmds><response><item><name>MCU3=LX</name></item><fields><Field><id>5</id><name>TIMACT</name><type>Unknown</type><value>08:31</value></Field><Field><id>25</id><name>ASK</name><type>Double</type><value>6816.50</value></Field><Field><id>31</id><name>ASKSIZE</name><type>Double</type><value>1</value></Field><Field><id>346</id><name>ASK_TONE</name><type>String</type><value> </value></Field></fields></response></rmds>";
		JSON json=getJSONFromXml(xmlString);
		System.out.println(json);
	}
	
}
