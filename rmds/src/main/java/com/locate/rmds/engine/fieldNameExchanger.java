package com.locate.rmds.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import org.apache.commons.lang.StringUtils;

import com.locate.rmds.QSConsumerProxy;
import com.reuters.rfa.dictionary.FidDef;

public class fieldNameExchanger {
	public static final String FIELD_FILE_NAME = "config/fieldNameExchange";

	public void loadFieldExchange() {
		Class fidDefImplclazz =FidDef.class;
		fidDefImplclazz.getClasses();
		BufferedReader fieldExchangeReader = null;
		try {
			fieldExchangeReader = new BufferedReader(new FileReader(new File(FIELD_FILE_NAME)));

			String filedName = "";
			while ((filedName = fieldExchangeReader.readLine()) != null) {
				String[] fieldConfig = filedName.split(",");
				String id = fieldConfig[0];
				String exchangeName = fieldConfig[4];
				if (!StringUtils.isBlank(exchangeName)) {
					QSConsumerProxy.dictionary.getFidDef(id);
				}
			}
		} catch (Exception e) {

		}
	}
	
	public static void main(String[] args) throws Exception {
		ClassPool pool = ClassPool.getDefault();
//		CtClass cc=pool.get("com.reuters.rfa.internal.dictionary.FidDefImpl");
		CtClass cc = pool.get("com.locate.rmds.engine.Point");
//		cc.setSuperclass(pool.get("com.reuters.rfa.dictionary.FidDef"));
		cc.setSuperclass(pool.get("com.locate.rmds.engine.PointInterface"));
//		CtMethod mthd = CtNewMethod.make("public void setName(String name) { this._name=name; }", cc);
		System.out.println(cc);
		CtMethod mthd = CtNewMethod.make("public void setX(int b) { this.x1=b; }", cc);
		cc.addMethod(mthd);
		Class clazz = cc.toClass();
		com.locate.rmds.engine.PointInterface instance = (com.locate.rmds.engine.PointInterface)clazz.newInstance();
		instance.setX(333);
		System.out.println(instance);
	}
}
