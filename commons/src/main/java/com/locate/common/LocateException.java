package com.locate.common;

public class LocateException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public LocateException(String errorInfo){
		super(errorInfo);
	}
	
	public LocateException(String errorInfo,Exception e){
		super(errorInfo,e);
	}
	
}
