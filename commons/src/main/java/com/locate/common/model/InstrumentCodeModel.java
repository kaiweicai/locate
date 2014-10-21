package com.locate.common.model;

public class InstrumentCodeModel {
	private String instrumentCode;
	private String chineseName;
	private String sourceCode;

	public InstrumentCodeModel(String instrumentCode, String chineseName, String sourceCode) {
		this.instrumentCode = instrumentCode;
		this.chineseName = chineseName;
		this.sourceCode = sourceCode;
	}

	public String getInstrumentCode() {
		return instrumentCode;
	}

	public void setInstrumentCode(String instrumentCode) {
		this.instrumentCode = instrumentCode;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	@Override
	public String toString() {
		return "InstrumentCode [instrumentCode=" + instrumentCode + ", chineseName=" + chineseName + ", sourceCode="
				+ sourceCode + "]";
	}
}
