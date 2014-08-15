package com.locate.face;
/**
 * Client implements this Interface 
 * Handle the data which transport from Locate.
 * @author CloudWei kaiweicai@163.com
 * create time 2014年8月15日
 * @copyRight by Author
 */
public interface IBussiness {
	/**
	 * Handle the network Exception
	 * such as the network layer link broken.
	 * @param e
	 */
	public void handleException(Throwable e);

	/**
	 * handle the message content.
	 * @param message The JSON format data
	 */
	public void handleMessage(String message);

	/**
	 * Handle the network disconnected
	 */
	public void handleDisconnected();

}