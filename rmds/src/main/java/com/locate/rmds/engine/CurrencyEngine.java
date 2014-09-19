package com.locate.rmds.engine;

import java.math.BigDecimal;
import java.util.List;

import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.SystemProperties;

public class CurrencyEngine implements Engine {
	public static float currency;
	public static String currencyFiled = SystemProperties.getProperties(SystemProperties.CURRENCY_FIELD);
	private static String[] curFields = currencyFiled.split(",");
	@Override
	public LocateUnionMessage doEngine(LocateUnionMessage message) {
		List<String[]> payLoadList = message.getPayLoadSet();
		for(String[] payLoad:payLoadList){
			String id = payLoad[0];
			if(fieldInChange(id,curFields)){
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

	public boolean fieldInChange(String exchangeField,String[] curFields){
		boolean result = false;
		for(String filed:curFields){
			if(exchangeField.equals(filed)){
				result = true;
				break;
			}
		}
		return result;
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
