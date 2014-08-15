package com.locate.gate.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JsonModel {
	private String item;
	private String generatetime;
	private long seqNumber;
	private long locateSeqNumber;
	private Set<String[]> payLoadSet=new HashSet<String[]>();
	private String[] header = new String[]{"id","name","type","value"};

	public JsonModel() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		this.generatetime = dateFormat.format(new Date());
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
}