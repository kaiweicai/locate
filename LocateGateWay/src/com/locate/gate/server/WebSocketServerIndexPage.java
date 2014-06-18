package com.locate.gate.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServerIndexPage {
	private Logger logger = Logger.getLogger(getClass());

	private static final String NEWLINE = "\r\n";

	public ChannelBuffer getContent(String webSocketLocation) {
		File file = new File("web/MarkpriceWebSocket.html");
		return ChannelBuffers.copiedBuffer(readHtmlFile2String(file), CharsetUtil.UTF_8);
	}

	private String readHtmlFile2String(File file) {
		StringBuilder sBuilder = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String lienContent = null;
			while ((lienContent = br.readLine()) != null) {
				sBuilder.append(lienContent);
				sBuilder.append(NEWLINE);
			}
		} catch (FileNotFoundException fnfe) {
			logger.error("the html file not exist!" + file.getName() + fnfe);
			return null;
		} catch (IOException ioe) {
			logger.error("can not read the file" + file.getName() + ioe);
			return null;
		} finally {
			try {
				br.close();
			} catch (IOException ioe) {
				logger.error("file close error!");
			}
		}
		return sBuilder.toString();

	}
}
