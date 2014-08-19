package com.locate.gate.model;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.locate.rmds.RFAServerManager;

@XmlRootElement
public class LocateUnionMessage {
	@XmlAttribute
	private String itemName;//itemName将作为person的的一个属性
	@XmlElement
	private String generatetime;
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
	private String[] header = new String[]{"id","name","type","value"};
	@XmlElement
	private Set<String[]> payLoadSet = new HashSet<String[]>();
	
	public LocateUnionMessage() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		this.generatetime = dateFormat.format(new Date());
		this.locateSeqNumber = RFAServerManager.sequenceNo.getAndIncrement();
	}
	
	public LocateUnionMessage(String itemName) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		this.generatetime = dateFormat.format(new Date());
		this.locateSeqNumber = RFAServerManager.sequenceNo.getAndIncrement();
		this.itemName = itemName;
	}
	

	public String getItemName() {
		return itemName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public long getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(long seqNumber) {
		this.seqNumber = seqNumber;
	}

	public String getGeneratetime() {
		return generatetime;
	}

	public void setGeneratetime(String generatetime) {
		this.generatetime = generatetime;
	}

	public long getLocateSeqNumber() {
		return locateSeqNumber;
	}

	public void setLocateSeqNumber(long locateSeqNumber) {
		this.locateSeqNumber = locateSeqNumber;
	}

	public Set<String[]> getPayLoadSet() {
		return payLoadSet;
	}

	public void setPayLoadSet(Set<String[]> payLoadSet) {
		this.payLoadSet = payLoadSet;
	}
	
	public String[] getHeader() {
		return header;
	}

	public void setHeader(String[] header) {
		this.header = header;
	}

	public String getStreamingState() {
		return streamingState;
	}

	public void setStreamingState(String streamingState) {
		this.streamingState = streamingState;
	}

	public String getDataingState() {
		return dataingState;
	}

	public void setDataingState(String dataingState) {
		this.dataingState = dataingState;
	}

	@Override
	public String toString() {
		return "LocateUnionMessage [itemName=" + itemName + ", generatetime=" + generatetime + ", seqNumber="
				+ seqNumber + ", locateSeqNumber=" + locateSeqNumber + ", header=" + Arrays.toString(header)
				+ ", payLoadSet=" + payLoadSet + "]";
	}
	
	public static void main(String[] args) throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(LocateUnionMessage.class);
		// 下面代码演示将对象转变为xml
		Marshaller m = context.createMarshaller();
		LocateUnionMessage message = new LocateUnionMessage();
		m.marshal(jaxbElement, node);
		FileWriter fw = new FileWriter("E:\\test\\person.xml");
		m.marshal(message, fw);

		// 下面代码演示将上面生成的xml转换为对象
		FileReader fr = new FileReader("E:\\test\\person.xml");
		Unmarshaller um = context.createUnmarshaller();
		LocateUnionMessage p2 = (LocateUnionMessage) um.unmarshal(fr);
		System.out.println("Country:" + p2);
		}
}
