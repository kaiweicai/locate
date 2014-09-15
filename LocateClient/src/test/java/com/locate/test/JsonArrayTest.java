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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.processors.PropertyNameProcessor;

import org.dom4j.io.DocumentResult;

import com.locate.common.constant.SystemConstant;
import com.locate.common.model.LocateUnionMessage;

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
		JsonConfig config = new JsonConfig();
//		Set<String[]> payloadSet = new HashSet<String[]>();
		List<String[]> payloadSet = new ArrayList<String[]>();
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
		JSONObject transJsonObject = (JSONObject)JSONSerializer.toJSON(jsonString);
		JSONArray payLoadSetArray = transJsonObject.getJSONArray("payLoadSet");
		config.setRootClass(JsonArrayTest.class);
		
		
		JsonArrayTest jsonArrayTest = (JsonArrayTest)JSONObject.toBean(transJsonObject, config);
//		JsonArrayTest jsonArrayTest = new JsonArrayTest();
		
//		String[] object=jsonArrayTest.getPayLoadSet().get(1);
//		System.out.println(object);
//		for(Object s:jsonArrayTest.getPayLoadSet()){
//			List<String> l =(ArrayList<String>)s;
//			System.out.println(l.get(0));
//			System.out.println(l.get(1));
//			System.out.println(l.get(2));
//			System.out.println(l.get(3));
//		}
		
//		JsonArrayTest jsonArrayTest = new JsonArrayTest();
		List<String[]> newPayloadSet = new ArrayList<String[]>();
		for(Object ooo:jsonArrayTest.getPayLoadSet()){
			String[] payLoadString = new String[4];
			List<String> lll = (ArrayList<String>)ooo;
			for(int j=0;j<lll.size();j++){
				payLoadString[j]=lll.get(j);
			}
			newPayloadSet.add(payLoadString);
		}
		jsonArrayTest.setPayLoadSet(newPayloadSet);
		System.out.println(newPayloadSet);
//		jsonArrayTest.setPayLoadSet(newPayloadSet);
//		for(int i =0;i<payLoadSetArray.size();i++){
//			String[] payLoadString = new String[4];
//			JSONArray priceEntry=payLoadSetArray.getJSONArray(i);
//			for(int j=0;j<priceEntry.size();j++){
//				payLoadString[j]=(String)priceEntry.get(j);
//			}
//			newPayloadSet.add(payLoadString);
//		}
		jsonArrayTest.setPayLoadSet(newPayloadSet);
		System.out.println(jsonArrayTest);
//		config.registerJavaPropertyNameProcessor(Set.class, propertyNameProcessor);
//		JsonArrayTest myMessage = (JsonArrayTest)JSONObject.toBean( transJsonObject, JsonArrayTest.class);
		System.out.println(jsonArrayTest);
		System.out.println("--------------------------------");
		for(String[] xx:jsonArrayTest.getPayLoadSet()){
			for(String bbb:xx){
				System.out.println(bbb);
			}
			System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^");
		}
		System.out.println(jsonObject.toString());
		}
	
//	 class payLoadSetProcessor implements PropertyNameProcessor{
//		@Override
//		public String processPropertyName(Class arg0, String arg1) {
//			arg0 palyLoadeSet = new  arg0();
//			return null;
//		}
//		 
//	 }
}
