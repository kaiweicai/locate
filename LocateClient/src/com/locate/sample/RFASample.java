package com.locate.sample;

import org.junit.Test;

import com.locate.common.LocateMessageTypes;
import com.locate.common.model.LocateUnionMessage;
import com.locate.face.IBussiness;
import com.locate.face.IClientConnector;
import com.locate.gate.handler.ClientConnector;

public class RFASample {
	//向服务器发送请求的客户端接口.
	public IClientConnector clientConnetor;
	
	//实例化
	public RFASample(){
		//BussinessInterface该接口是接受服务器数据的接口.这个接口请客户一定要自己实现.
		IBussiness bussinessHandler = new BussinessHandler();
		//ClientConnectedInterface向服务器发送请求的接口.这个接口客户无需实现.只要调用这个ClientConnector这个类里面的方法就好了.
		clientConnetor = new ClientConnector(bussinessHandler);
	}
	
	class BussinessHandler implements IBussiness{
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
		public void handleMessage(LocateUnionMessage message){
			byte msgType = message.getMsgType();
			if (message == null) {
				System.out.println("Received server's  message is null \n");
				return;
			}
			switch(msgType){
				//首先服务器会发送过来一个snapshot的信息.里面包括该RIC对应的所有字段.
				case LocateMessageTypes.REFRESH_RESP:
					System.out.println(message);
					break;
				//然后服务器会发送很多更新的MarketPrice.该信息只包括需要更新的字段.
				//如RIC: XAU= 只会发送BID,BID1,BID2,ASK,ASK1,ASK2等字段.
				case LocateMessageTypes.UPDATE_RESP:
					System.out.println(message);
					break;
				//如果服务器有通知服务器状态改变的信息,会使用此状态信息.
				case LocateMessageTypes.RESPONSE_LOGIN:
				case LocateMessageTypes.STATUS_RESP:
					System.out.println("");
					break;
				//服务器发送了未知的消息,一般这里不用处理.扔掉该消息就好了.
				default:
					System.out.println("Not should to here! message type is "+LocateMessageTypes.REFRESH_RESP);
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
