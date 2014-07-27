package com.locate.gate.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JsonModel {
	private String item;
	private String generatetime;
	private long seqNumber;
	private long locateSeqNumber;
	private Set<String[]> payLoadSet=new HashSet<String[]>();
	private String[] header= new String[]{"id","name","type","value"};

	public JsonModel(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		setGeneratetime(dateFormat.format(new Date()));
	}
	

	public long getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(long seqNumber) {
		this.seqNumber = seqNumber;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
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

//	public class PayLoad{
//	private short id;
//	private String name;
//	private String dataType;
//	private String value;
//	
//	public int hashCode() {
//		return id;
//	}
//	
//	public boolean equeals(PayLoad payLoad) {
//		if (this.id == payLoad.getId()) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//	
//	public short getId() {
//		return id;
//	}
//	public void setId(short id) {
//		this.id = id;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getValue() {
//		return value;
//	}
//	public void setValue(String value) {
//		this.value = value;
//	}
//
//	public String getDataType() {
//		return dataType;
//	}
//
//	public void setDataType(String dataType) {
//		this.dataType = dataType;
//	}
//	
//}

}
