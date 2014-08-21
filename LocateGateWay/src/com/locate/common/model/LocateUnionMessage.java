package com.locate.common.model;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

@XmlRootElement(name="rmds")
public class LocateUnionMessage {
	@XmlAttribute(name="itemName")
	private String itemName;//itemName将作为person的的一个属性
	@XmlElement
	private long startTime;
	@XmlElement
	private String generatetime;
	@XmlElement
	private byte msgType;
	@XmlElement
	private long seqNumber;
	@XmlElement
	private long locateSeqNumber;
	@XmlElement
	private String state;
	@XmlElement
	private String streamingState;
	@XmlElement
	private String dataingState;
	@XmlElement
	private int resultCode;
	@XmlElement
	private String resultDes;
	@XmlElement()
	private String[] header = new String[]{"id","name","type","value"};
	
	@XmlElement(name="Field")
	private List<String[]> payLoadSet = new ArrayList<String[]>();
	
	public LocateUnionMessage() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.generatetime = dateFormat.format(new Date());
		this.locateSeqNumber = SystemConstant.sequenceNo.getAndIncrement();
	}
	
	public LocateUnionMessage(String itemName) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.generatetime = dateFormat.format(new Date());
		this.locateSeqNumber = SystemConstant.sequenceNo.getAndIncrement();
		this.itemName = itemName;
	}
	
	@XmlTransient
	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	@XmlTransient
	public String getResultDes() {
		return resultDes;
	}

	public void setResultDes(String resultDes) {
		this.resultDes = resultDes;
	}

	@XmlTransient
	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}
	
	@XmlTransient
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	@XmlTransient
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	@XmlTransient
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@XmlTransient
	public long getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(long seqNumber) {
		this.seqNumber = seqNumber;
	}

	@XmlTransient
	public String getGeneratetime() {
		return generatetime;
	}

	public void setGeneratetime(String generatetime) {
		this.generatetime = generatetime;
	}

	@XmlTransient
	public long getLocateSeqNumber() {
		return locateSeqNumber;
	}

	public void setLocateSeqNumber(long locateSeqNumber) {
		this.locateSeqNumber = locateSeqNumber;
	}

	@XmlTransient
	public List<String[]> getPayLoadSet() {
		return payLoadSet;
	}

	public void setPayLoadSet(List<String[]> payLoadSet) {
		this.payLoadSet = payLoadSet;
	}

	@XmlTransient
	public String[] getHeader() {
		return header;
	}

	public void setHeader(String[] header) {
		this.header = header;
	}

	@XmlTransient
	public String getStreamingState() {
		return streamingState;
	}

	public void setStreamingState(String streamingState) {
		this.streamingState = streamingState;
	}
	
	@XmlTransient
	public String getDataingState() {
		return dataingState;
	}

	public void setDataingState(String dataingState) {
		this.dataingState = dataingState;
	}

	
	
	@Override
	public String toString() {
		return "LocateUnionMessage [itemName=" + itemName + ", startTime=" + startTime + ", generatetime="
				+ generatetime + ", msgType=" + msgType + ", seqNumber=" + seqNumber + ", locateSeqNumber="
				+ locateSeqNumber + ", state=" + state + ", streamingState=" + streamingState + ", dataingState="
				+ dataingState + ", resultCode=" + resultCode + ", resultDes=" + resultDes + ", header="
				+ Arrays.toString(header) + ", payLoadSet=" + payLoadSet + "]";
	}

	public static void main(String[] args) throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(LocateUnionMessage.class);
		// 下面代码演示将对象转变为xml
		Marshaller m = context.createMarshaller();
		LocateUnionMessage message = new LocateUnionMessage("XAU=");
		
		List<String[]> payloadSet = new ArrayList<String[]>();
		payloadSet.add(new String[]{"25","ASK","Double","6816.50"});
		payloadSet.add(new String[]{"26","BID","Double","6820.44"});
		message.setPayLoadSet(payloadSet);
		
		
		JSONObject jsonObject = JSONObject.fromObject(message);
		String jsonString = jsonObject.toString();
		JSONObject transJsonObject = JSONObject.fromObject(jsonString);
		LocateUnionMessage myMessage = (LocateUnionMessage)JSONObject.toBean( transJsonObject, LocateUnionMessage.class);
		System.out.println(myMessage);
		System.out.println(jsonObject.toString());
		DocumentResult node = new DocumentResult();
		m.marshal(message, node);
		System.out.println(node.getDocument().asXML());
//		FileWriter fw = new FileWriter("E:\\test\\person.xml");
//		m.marshal(message, fw);
//
//		// 下面代码演示将上面生成的xml转换为对象
//		FileReader fr = new FileReader("E:\\test\\person.xml");
//		Unmarshaller um = context.createUnmarshaller();
//		LocateUnionMessage p2 = (LocateUnionMessage) um.unmarshal(fr);
//		System.out.println("Country:" + p2);
		}
}
