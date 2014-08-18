package com.locate.gate.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class LocateUnionMessage {
	//RIC name
	private String itemName;
	private String generatetime;
	private long seqNumber;
	private long locateSeqNumber;
	private String[] header = new String[]{"id","name","type","value"};
	private Set<String[]> payLoadSet = new HashSet<String[]>();
	
	public LocateUnionMessage() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		this.generatetime = dateFormat.format(new Date());
	}
	
	public LocateUnionMessage(String itemName) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		this.generatetime = dateFormat.format(new Date());
		this.itemName = itemName;
	}
	

	public String getItemName() {
		return itemName;
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

	@Override
	public String toString() {
		return "LocateUnionMessage [itemName=" + itemName + ", generatetime=" + generatetime + ", seqNumber="
				+ seqNumber + ", locateSeqNumber=" + locateSeqNumber + ", header=" + Arrays.toString(header)
				+ ", payLoadSet=" + payLoadSet + "]";
	}
	
	
}
