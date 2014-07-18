package com.locate.gate.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JsonModel {
	private String item;
	private String generatetime;
	private long seqNumber;
	private long locateSeqNumber;
	private Set<PayLoad> payLoadSet=new HashSet<PayLoad>();
	public JsonModel(){
		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		setGeneratetime(dateFormat.format(new Date()));
	}
	public class PayLoad{
		private short id;
		private String name;
		private String dataType;
		private String value;
		
		public int hashCode() {
			return id;
		}
		
		public boolean equeals(PayLoad payLoad) {
			if (this.id == payLoad.getId()) {
				return true;
			} else {
				return false;
			}
		}
		
		public short getId() {
			return id;
		}
		public void setId(short id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}

		public String getDataType() {
			return dataType;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}
		
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

	public Set<PayLoad> getPayLoadSet() {
		return payLoadSet;
	}

	public void setPayLoadSet(Set<PayLoad> payLoadSet) {
		this.payLoadSet = payLoadSet;
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
}
