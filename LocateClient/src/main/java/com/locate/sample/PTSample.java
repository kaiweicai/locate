package com.locate.sample;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sf.json.JSONObject;

import org.dom4j.io.DocumentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.common.constant.LocateMessageTypes;
import com.locate.common.logging.err.ErrorLogHandler;
import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.NetTimeUtil;
import com.locate.face.IBussiness;
import com.locate.face.IClientConnector;
import com.locate.gate.handler.ClientConnector;

/**
 * 客户端接口调用示例
 * @author CloudWei kaiweicai@163.com
 * create time 2014年8月26日
 * @copyRight by Author
 */
public class PTSample {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private ErrorLogHandler errorLogHandler = ErrorLogHandler.getLogger(getClass());
	public static final String COMEX_GOLD_ITEM = "PT_XAU";
	public static final String COMEX_SILVER_ITEM = "PT_XAG";
	//客户端接口声明.
	public IClientConnector clientConnetor;
	public IBussiness bussinessHandler;
	//XML对象处理器上下文.
	private JAXBContext context;
	//实例化
	public PTSample(){
		//客户端收到数据后调用该接口的方法进行数据处理.因为每个客户数据处理的需求不同,所以客户需要实现该接口进行具体处理数据业务.
		bussinessHandler = new BussinessHandler();
		//向服务器发送请求的接口.这个接口客户无需实现.只要调用这个ClientConnector这个类里面的方法就好了.
		clientConnetor = new ClientConnector(bussinessHandler);
		//数据格式转换前置工作.客户可以不使用.
		try {
			context = JAXBContext.newInstance(LocateUnionMessage.class);
		} catch (JAXBException e) {
			errorLogHandler.error("Json initial JAXBContext error!",e);
		}
	}
	
	class BussinessHandler implements IBussiness{
		/* 
		 * 客户端与服务器交互发生异常时的处理方法
		 */
		@Override
		public void handleException(Throwable e){
			//简单的打印处理,可以显示,通知,邮件告警该异常,并联系普兰泰科技术支持
			System.out.println(e);
		}
		
		/* 
		 * 客户需要实现数据处理的代码.
		 * 参数message是服务器发送过来统一格式的数据.
		 * 客户可以根据需要将message转成XML或者JSON格式.
		 */
		@Override
		public void handleMessage(LocateUnionMessage message){
			long starTime = message.getStartTime();
			long endTime = NetTimeUtil.getCurrentNetTime();
			//计算从服务器到收到该消息总共耗费的时间,由于使用的是网络时间,所以存在一定的误差.
			System.out.println("Recieve this message use time:"+(endTime-starTime)+" microseconds");
			byte msgType = message.getMsgType();
			String itemName = message.getItemName();
			switch(msgType){
				//首先服务器会发送过来一个行情的全数据信息.里面包括该种类RIC对应的所有字段(报价商名称,昨日收盘价,	昨日开盘价).
				case LocateMessageTypes.REFRESH_RESP:
					//如果订阅的是多个产品,此处根据RIC对产品进行分类处理.
					switch(itemName){
					case COMEX_GOLD_ITEM:
						System.out.println(itemName);
						System.out.println("handel the au message "+message);
						break;
					case COMEX_SILVER_ITEM:
						System.out.println(itemName);
						System.out.println("handel the ag message "+message);
						break;
					}
					break;
				//然后服务器会发送不断更新的MarketPrice.该信息只包括需要更新的字段.
				//如RIC: XAU= 只会发送BID,BID1,BID2,ASK,ASK1,ASK2等行情变化的字段.
				case LocateMessageTypes.UPDATE_RESP:
					//如果订阅的是多个产品,此处根据RIC对产品进行分类处理.
					switch(itemName){
					case COMEX_GOLD_ITEM:
						System.out.println(itemName);
						System.out.println("handle the au message "+message);
						//具体解析一个报价出来看看是什么样子的.
						List<String[]> payloadList = message.getPayLoadSet();
						for(String[] payLoad:payloadList){
							System.out.println("payload id is:"+payLoad[0]);
							System.out.println("payload name is:"+payLoad[1]);
							System.out.println("payload type is:"+payLoad[2]);
							System.out.println("payload value is:"+payLoad[3]);
						}
						break;
					case COMEX_SILVER_ITEM:
						//将消息对象转换成JSON型字符串使用.
						JSONObject jsonObject = JSONObject.fromObject(message);
						String jsonResult = jsonObject.toString();
						System.out.println(itemName);
						System.out.println("handle the XAG JSON format!"+jsonResult);
						try {
							// 下面代码演示将对象转变为xml
							Marshaller marshaller = context.createMarshaller();
							DocumentResult node = new DocumentResult();
							marshaller.marshal(message, node);
							System.out.println("handel the XML format!"+node.getDocument().asXML().toString());
						} catch (JAXBException e) {
							e.printStackTrace();
						}
					}
					break;
				//向服务器发送请求后的返回信息.
				case LocateMessageTypes.SERVER_STATE:
					String errorDescription = message.getResultDes();
					System.out.println("服务器处理了请求并返回了服务器的处理结果.");
					String state = message.getState();
					System.out.println(state);
					System.out.println(errorDescription);
					break;
				//如果服务器有通知服务器状态改变的信息,会使用此状态信息.
				case LocateMessageTypes.STATUS_RESP:
					errorDescription = message.getResultDes();
					System.out.println("服务器出现了新的状态.");
					state = message.getState();
					System.out.println(state);
					System.out.println(errorDescription);
					break;
				//服务器发送了未知的消息,一般这里不用处理.扔掉该消息就好了.或者打印一个警告.
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
		PTSample sample = new PTSample();
		//向服务器注册客户端信息.参数1服务器ip,参数2服务器端口,参数3客户名称,参数4客户密码.
//		sample.clientConnetor.conneteLocateGateWay("61.144.244.173", 8888, "demo", "demo");
		sample.clientConnetor.conneteLocateGateWay("127.0.0.1", 8888, "demo", "demo");
		//向服务器发送RIC请求.BussinessHandler的handleMessage方法就可以接收到服务器返回的市场价格了.
		sample.clientConnetor.openRICMarket(COMEX_GOLD_ITEM);
		sample.clientConnetor.openRICMarket(COMEX_SILVER_ITEM);
	}
}
