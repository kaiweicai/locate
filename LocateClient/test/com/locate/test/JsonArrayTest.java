package com.locate.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import org.dom4j.io.DocumentResult;

import com.locate.common.SystemConstant;

public class JsonArrayTest {
	private List<String[]> payLoadSet = new ArrayList<String[]>();
	
	public JsonArrayTest(){
	}





	public List<String[]> getPayLoadSet() {
		return payLoadSet;
	}





	public void setPayLoadSet(List<String[]> payLoadSet) {
		this.payLoadSet = payLoadSet;
	}





	@Override
	public String toString() {
		return "JsonArrayTest [payLoadSet=" + payLoadSet + "]";
	}


	public static void main(String[] args) throws JAXBException, IOException {
		JsonArrayTest message = new JsonArrayTest();
		
		List<String[]> payloadSet = new LinkedList<String[]>();
		List<String> payload = new LinkedList<String>();
//		payload.add("25");
//		payload.add("ASK");
//		payload.add("Double");
//		payload.add("6816.50");
//		payloadSet.add(payload);
		String[] s1 = new String[]{"1788","Unknown","CP_ADJ_DAT",""};
		String[] s2 = new String[]{"1788","Unknown","CP_ADJ_DAT",""};
		if(s1==(s2)){
			System.out.println("s1 equals s2");
		}
		payloadSet.add(new String[]{"25","ASK","Double","6816.50"});
		String[] test = new String[]{"1788","Unknown","CP_ADJ_DAT",""};
		payloadSet.add(test);
		String[] test2=new String[]{"1788","Unknown","CP_ADJ_DAT",""};
		if (!payloadSet.contains(test2)) {
			payloadSet.add(test2);
		}
		
		payloadSet.add(new String[]{"26","BID","Double","6820.44"});
		message.setPayLoadSet(payloadSet);
		
		
		JSONObject jsonObject = JSONObject.fromObject(message);
		String jsonString = jsonObject.toString();
		JSONObject transJsonObject = JSONObject.fromObject(jsonString);
		JsonArrayTest myMessage = (JsonArrayTest)JSONObject.toBean( transJsonObject, JsonArrayTest.class);
		System.out.println(myMessage);
		System.out.println(jsonObject.toString());
		}
}
