package com.locate.rmds.processer.face;

public interface INotifier {
	/**
	 * notify user the important message
	 * @param title
	 * @param content
	 */
	public void notifyAdmin(String title,String content);

	/**
	 * notify user the important message. 
	 * send the message even the configure file set the false;
	 * @param title
	 * @param content
	 */
	void notifyAdminIgnoreConfig(String title, String content);
}
