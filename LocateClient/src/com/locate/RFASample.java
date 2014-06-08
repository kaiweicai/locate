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
	//向服务器发送请求的客户端接口.
	public ClientConnectedInterface clientConnetor;
	
	//实例化
	public RFASample(){
		//BussinessInterface该接口是接受服务器数据的接口.这个接口请客户一定要自己实现.
		BussinessInterface bussinessHandler = new BussinessHandler();
		//ClientConnectedInterface向服务器发送请求的接口.这个接口客户无需实现.只要调用这个ClientConnector这个类里面的方法就好了.
		clientConnetor = new ClientConnector(bussinessHandler);
	}
	
	class BussinessHandler implements BussinessInterface{
		/* 
		 * 客户自行实现实现处理任何网络异常的代码.
		 */
		@Override
		public void handleException(Throwable e){
			System.out.println(e);
		}
		
		/* 
		 * 客户实现数据处理的代码.参数message是服务器发送过来的字符串形式的数据.
		 * 可以用Dom4j直接转换成Dom形式.
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
				//首先服务器会发送过来一个snapshot的信息.里面包括该RIC对应的所有字段.
				case MsgType.REFRESH_RESP:
					System.out.println(document);
					break;
				//然后服务器会发送很多更新的MarketPrice.该信息只包括需要更新的字段.
				//如RIC: XAU= 只会发送BID,BID1,BID2,ASK,ASK1,ASK2等字段.
				case MsgType.UPDATE_RESP:
					System.out.println(document);
					break;
				//如果服务器有通知服务器状态改变的信息,会使用此状态信息.
				case GateWayMessageTypes.RESPONSE_LOGIN:
				case MsgType.STATUS_RESP:
					System.out.println("");
					break;
				//服务器发送了未知的消息,一般这里不用处理.扔掉该消息就好了.
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
		//向服务器注册客户端信息.参数1服务器ip,参数2服务器端口,参数3客户名称,参数4客户密码.
		sample.clientConnetor.conneteLocateGateWay("61.144.244.173", 8888, "ztcj", "ztcj2013");
		//向服务器发送RIC请求.BussinessHandler的handleMessage方法就可以接收到服务器返回的市场价格了.
		sample.clientConnetor.openRICMarket("XAU=");
		sample.clientConnetor.openRICMarket("XAG=");
	}
	
	@Test
	public void cuncrrentTest(){
		
	}
}
