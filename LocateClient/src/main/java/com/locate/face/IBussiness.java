package com.locate.face;

import com.locate.common.model.ClientLocateUnionMessage;
import com.locate.common.model.LocateUnionMessage;

/**
 * Client implements this Interface 
 * Handle the data which transport from Locate.
 * @author CloudWei kaiweicai@163.com
 * create time 20142014年8月15日
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
	public void handleMessage(ClientLocateUnionMessage message);

	/**
	 * Handle the network disconnected
	 */
	public void handleDisconnected();

}