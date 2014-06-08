package com.locate;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.junit.Test;

import com.locate.common.GateWayMessageTypes;
import com.locate.common.RFANodeconstant;
import com.locate.common.XmlMessageUtil;
import com.locate.common.GateWayMessageTypes.RFAMessageName;
import com.locate.face.BussinessInterface;
import com.locate.face.ClientConnectedInterface;
import com.locate.gate.handler.ClientConnector;
import com.locate.gate.handler.ClientHandler;
import com.locate.gate.model.CustomerFiled;
import com.reuters.rfa.omm.OMMMsg.MsgType;

public class RFASample {
	//���������������Ŀͻ��˽ӿ�.
	public ClientConnectedInterface clientConnetor;
	
	//ʵ����
	public RFASample(){
		//BussinessInterface�ýӿ��ǽ��ܷ��������ݵĽӿ�.����ӿ���ͻ�һ��Ҫ�Լ�ʵ��.
		BussinessInterface bussinessHandler = new BussinessHandler();
		//ClientConnectedInterface���������������Ľӿ�.����ӿڿͻ�����ʵ��.ֻҪ�������ClientConnector���������ķ����ͺ���.
		clientConnetor = new ClientConnector(bussinessHandler);
	}
	
	class BussinessHandler implements BussinessInterface{
		/* 
		 * �ͻ�����ʵ��ʵ�ִ����κ������쳣�Ĵ���.
		 */
		@Override
		public void handleException(Throwable e){
			System.out.println(e);
		}
		
		/* 
		 * �ͻ�ʵ�����ݴ���Ĵ���.����message�Ƿ��������͹������ַ�����ʽ������.
		 * ������Dom4jֱ��ת����Dom��ʽ.
		 */
		@Override
		public void handleMessage(String message){
			Document document = XmlMessageUtil.convertDocument(message);
			byte msgType = XmlMessageUtil.getMsgType(document);
			if (document == null) {
				System.out.println("Received server's  message is null \n");
				return;
			}
			switch(msgType){
				//���ȷ������ᷢ�͹���һ��snapshot����Ϣ.���������RIC��Ӧ�������ֶ�.
				case MsgType.REFRESH_RESP:
					System.out.println(document);
					break;
				//Ȼ��������ᷢ�ͺܶ���µ�MarketPrice.����Ϣֻ������Ҫ���µ��ֶ�.
				//��RIC: XAU= ֻ�ᷢ��BID,BID1,BID2,ASK,ASK1,ASK2���ֶ�.
				case MsgType.UPDATE_RESP:
					System.out.println(document);
					break;
				//�����������֪ͨ������״̬�ı����Ϣ,��ʹ�ô�״̬��Ϣ.
				case GateWayMessageTypes.RESPONSE_LOGIN:
				case MsgType.STATUS_RESP:
					System.out.println("");
					break;
				//������������δ֪����Ϣ,һ�����ﲻ�ô���.�ӵ�����Ϣ�ͺ���.
				default:
					System.out.println("Not should to here! message type is "+MsgType.REFRESH_RESP);
			}
			
			// String content = response.asXML();
			System.out.println("Received server's  message : " + message+"\n");
		}
		

		@Override
		public void handleDisconnected() {
			System.out.println("Locate Server disconnted!!! ");			
		}
	}
	
	public static void main(String[] args) {
		RFASample sample = new RFASample();
		//�������ע��ͻ�����Ϣ.����1������ip,����2�������˿�,����3�ͻ�����,����4�ͻ�����.
		sample.clientConnetor.conneteLocateGateWay("61.144.244.173", 8888, "ztcj", "ztcj2013");
		//�����������RIC����.BussinessHandler��handleMessage�����Ϳ��Խ��յ����������ص��г��۸���.
		sample.clientConnetor.openRICMarket("XAU=");
		sample.clientConnetor.openRICMarket("XAG=");
	}
	
	@Test
	public void cuncrrentTest(){
		
	}
}
