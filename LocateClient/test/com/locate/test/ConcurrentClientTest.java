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
import com.locate.face.IBussiness;
import com.locate.face.IClientConnected;
import com.locate.gate.handler.ClientConnector;
import com.reuters.rfa.omm.OMMMsg.MsgType;

public class ConcurrentClientTest {
	
		
	@Test
	public void cuncrrentUserTest() throws Exception {
		RFASample sample = new RFASample();
		// �����������RIC����.BussinessHandler��handleMessage�����Ϳ��Խ��յ����������ص��г��۸���.
		FileReader fr = new FileReader(new File("testResource/Ric.txt"));
		BufferedReader br = new BufferedReader(fr);
		String ric = null;
		System.out.println("start to order all the product!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		List<RFASample> sampleList = new ArrayList<RFASample>();
		for (int i = 0; i < 20; i++) {
			sample = new RFASample();
			// �������ע��ͻ�����Ϣ.����1������ip,����2�������˿�,����3�ͻ����,����4�ͻ�����.
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
