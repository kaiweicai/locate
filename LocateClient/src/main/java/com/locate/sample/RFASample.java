package com.locate.sample;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.dom4j.io.DocumentResult;

import com.locate.common.LocateMessageTypes;
import com.locate.common.model.LocateUnionMessage;
import com.locate.face.IBussiness;
import com.locate.face.IClientConnector;
import com.locate.gate.handler.ClientConnector;

/**
 * 客户端接口调用示例
 * @author CloudWei kaiweicai@163.com
 * create time 2014年8月26日
 * @copyRight by Author
 */
public class RFASample {
	private Logger logger = Logger.getLogger(getClass());
	public static final String XAU_RIC = "XAU=";
	public static final String XAG_RIC = "XAG=";
	//客户端接口声明.
	public IClientConnector clientConnetor;
	public IBussiness bussinessHandler;
	private JAXBContext context;
	//实例化
	public RFASample(){
		//BussinessInterface该接口是接受服务器数据.客户需要实现该接口进行具体业务处理数据.
		bussinessHandler = new BussinessHandler();
		//ClientConnectedInterface向服务器发送请求的接口.这个接口客户无需实现.只要调用这个ClientConnector这个类里面的方法就好了.
		clientConnetor = new ClientConnector(bussinessHandler);
		try {
			context = JAXBContext.newInstance(LocateUnionMessage.class);
		} catch (JAXBException e) {
			logger.error("Json initial JAXBContext error!",e);
		}
	}
	
	class BussinessHandler implements IBussiness{
		/* 
		 * 客户端发生异常时的处理方法
		 */
		@Override
		public void handleException(Throwable e){
			System.out.println(e);
		}
		
		/* 
		 * 客户需要实现数据处理的代码.
		 * 参数message是服务器发送过来统一格式的数据.
		 * 客户可以根据需要将message转成XML或者JSON格式.
		 */
		@Override
		public void handleMessage(LocateUnionMessage message){
			byte msgType = message.getMsgType();
			String RIC = message.getRic();
			switch(msgType){
				//首先服务器会发送过来一个行情的全数据信息.里面包括该种类RIC对应的所有字段(报价商名称,昨日收盘价,	商品名称等).
				case LocateMessageTypes.REFRESH_RESP:
					//如果订阅的是多个产品,此处根据RIC对产品进行分类处理.
					switch(RIC){
					case XAU_RIC:
						System.out.println(RIC);
						System.out.println("handel the au message "+message);
						break;
					case XAG_RIC:
						System.out.println(RIC);
						System.out.println("handel the ag message "+message);
						break;
					}
					break;
				//然后服务器会发送不断更新的MarketPrice.该信息只包括需要更新的字段.
				//如RIC: XAU= 只会发送BID,BID1,BID2,ASK,ASK1,ASK2等行情变化的字段.
				case LocateMessageTypes.UPDATE_RESP:
					//如果订阅的是多个产品,此处根据RIC对产品进行分类处理.
					switch(RIC){
					case XAU_RIC:
						System.out.println(RIC);
						System.out.println("handel the au message "+message);
						break;
					case XAG_RIC:
						JSONObject jsonObject = JSONObject.fromObject(message);
						String jsonResult = jsonObject.toString();
						System.out.println(RIC);
						System.out.println("handel the XAG JSON format!"+jsonResult);
						try {
							// 下面代码演示将对象转变为xml
							Marshaller marshaller = context.createMarshaller();
							DocumentResult node = new DocumentResult();
							marshaller.marshal(message, node);
							System.out.println(node.getDocument().asXML().toString());
						} catch (JAXBException e) {
							e.printStackTrace();
						}
					}
					break;
				//向服务器发送请求后的返回信息.
				case LocateMessageTypes.SERVER_STATE:
					String errorDescription = message.getResultDes();
					System.out.println("向服务器放的请求出现了错误,请看下面的具体错误描述");
					String state = message.getState();
					System.out.println(state);
					System.out.println(errorDescription);
					break;
				//如果服务器有通知服务器状态改变的信息,会使用此状态信息.
//				case LocateMessageTypes.SERVER_STATE:
//					String state = message.getState();
//					System.out.println(state);
//					break;
				//服务器发送了未知的消息,一般这里不用处理.扔掉该消息就好了.
				default:
					System.out.println("Not should to here! message type is "+LocateMessageTypes.REFRESH_RESP);
			}
			
			// String content = response.asXML();
//			System.out.println("Received server's  message : " + message+"\n");
		}
		

		/**
		 * 与服务器的网络连接断开后处理方法
		 */
		public void handleDisconnected() {
			System.out.println("Locate Server disconnted!!! ");			
		}
	}
	
	public static void main(String[] args) {
		RFASample sample = new RFASample();
		//向服务器注册客户端信息.参数1服务器ip,参数2服务器端口,参数3客户名称,参数4客户密码.
//		sample.clientConnetor.conneteLocateGateWay("61.144.244.173", 8888, "ztcj", "ztcj2013");
		sample.clientConnetor.conneteLocateGateWay("127.0.0.1", 8888, "ztcj", "ztcj2013");
		//向服务器发送RIC请求.BussinessHandler的handleMessage方法就可以接收到服务器返回的市场价格了.
		sample.clientConnetor.openRICMarket(XAU_RIC);
		sample.clientConnetor.openRICMarket(XAG_RIC);
	}
}
