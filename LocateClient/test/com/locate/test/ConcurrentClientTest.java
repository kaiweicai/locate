package com.locate.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.locate.RFASample;

public class ConcurrentClientTest {
	
		
	@Test
	public void cuncrrentUserTest() throws Exception {
		RFASample sample = new RFASample();
		// 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟絉IC锟斤拷锟斤拷.BussinessHandler锟斤拷handleMessage锟斤拷锟斤拷锟酵匡拷锟皆斤拷锟秸碉拷锟斤拷锟斤拷锟斤拷锟斤拷锟截碉拷锟叫筹拷锟桔革拷锟斤拷.
		FileReader fr = new FileReader(new File("testResource/Ric.txt"));
		BufferedReader br = new BufferedReader(fr);
		String ric = null;
		System.out.println("start to order all the product!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		List<RFASample> sampleList = new ArrayList<RFASample>();
		for (int i = 0; i < 20; i++) {
			sample = new RFASample();
			// 锟斤拷锟斤拷锟斤拷锟阶拷锟酵伙拷锟斤拷锟斤拷息.锟斤拷锟斤拷1锟斤拷锟斤拷锟斤拷ip,锟斤拷锟斤拷2锟斤拷锟斤拷锟斤拷锟剿匡拷,锟斤拷锟斤拷3锟酵伙拷锟斤拷锟?锟斤拷锟斤拷4锟酵伙拷锟斤拷锟斤拷.
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
