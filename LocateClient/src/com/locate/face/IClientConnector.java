package com.locate.face;

/**
 * Customers main usage interface
 * @author CloudWei kaiweicai@163.com create time 2014年8月14日
 * @copyRight by Author
 */
public interface IClientConnector {

	/**
	 * 向服务器申请注册客户端信息
	 * 
	 * @param serverAddress
	 *            服务器ip
	 * @param port
	 *            服务器端口
	 * @param userName
	 *            客户名称
	 * @param password
	 *            客户密码
	 */
	public abstract void conneteLocateGateWay(String serverAddress, int port, String userName, String password);

	/**
	 * 向Locate服务器发起Ric数据请求.
	 * @param ric 单个Ric字符串
	 */
	public abstract void openRICMarket(String ric);

}