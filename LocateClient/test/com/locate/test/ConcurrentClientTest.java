package com.locate.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.junit.Test;

import com.locate.RFASample;
import com.locate.common.GateWayMessageTypes;
import com.locate.common.XmlMessageUtil;
import com.locate.face.BussinessInterface;
import com.locate.face.ClientConnectedInterface;
import com.locate.gate.handler.ClientConnector;
import com.reuters.rfa.omm.OMMMsg.MsgType;

public class ConcurrentClientTest {
	
		
	@Test
	public void cuncrrentUserTest() throws Exception {
		RFASample sample = new RFASample();
		// 向服务器发送RIC请求.BussinessHandler的handleMessage方法就可以接收到服务器返回的市场价格了.
		FileReader fr = new FileReader(new File("testResource/Ric.txt"));
		BufferedReader br = new BufferedReader(fr);
		String ric = null;
		System.out.println("start to order all the product!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		List<RFASample> sampleList = new ArrayList<RFASample>();
		for (int i = 0; i < 20; i++) {
			sample = new RFASample();
			// 向服务器注册客户端信息.参数1服务器ip,参数2服务器端口,参数3客户名称,参数4客户密码.
			sample.clientConnetor.conneteLocateGateWay("61.144.244.173", 8888, "ztcj", "ztcj2013");
			sampleList.add(sample);
			for (int j = 0; j < 50; j++) {
				ric = br.readLine();
				sample.clientConnetor.openRICMarket(ric);
			}
		}
		System.out.println("end to send the order--------------------------------------");
		Thread.sleep(600000);
	}
}
