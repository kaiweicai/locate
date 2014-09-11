package com.locate.rmds.engine;

import java.math.BigDecimal;
import java.util.List;

import com.locate.common.model.LocateUnionMessage;

public class CurrencyEngine implements Engine {
	public static float currency;
	@Override
	public LocateUnionMessage doEngine(LocateUnionMessage message) {
		List<String[]> payLoadList = message.getPayLoadSet();
		for(String[] payLoad:payLoadList){
			String id = payLoad[0];
			//TODO select the filed to change to the CYN.
			if(id.equals("6")||id.equals("25")){
				BigDecimal payLoadValue = new BigDecimal(payLoad[3]);
				BigDecimal currencyValue = new BigDecimal(Double.toString(currency));
				BigDecimal resultValue = payLoadValue.multiply(currencyValue);
				double exchangValue = resultValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				payLoad[3] = "" + exchangValue;
//				float exchangValue=Float.parseFloat(payLoad[3])*currency;
				payLoad[3]=""+exchangValue;
			}
		}
		return message;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		BigDecimal payLoadValue = new BigDecimal("65225.584");
		BigDecimal currencyValue = new BigDecimal(Double.toString(6.235125456f));
		BigDecimal resultValue = payLoadValue.multiply(currencyValue);
		double exchangValue = resultValue.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("" + exchangValue); 
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
		
	}
}
