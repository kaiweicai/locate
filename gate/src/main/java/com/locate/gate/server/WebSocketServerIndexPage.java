package com.locate.gate.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.locate.common.logging.err.ErrorLogHandler;

@Service
public class WebSocketServerIndexPage {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static ErrorLogHandler errorLogHandler = ErrorLogHandler.getLogger(WebSocketServerIndexPage.class);
	private static final String MARKET_WEB_SOCKET_PAGE = "web/MarkpriceWebSocket.html";
	private static final String NEWLINE = "\r\n";

//	public ChannelBuffer getContent(String webSocketLocation) {
//		File file = new File(MARKET_WEB_SOCKET_PAGE);
//		return ChannelBuffers.copiedBuffer(readHtmlFile2String(file), CharsetUtil.UTF_8);
//	}

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
			errorLogHandler.error("the html file not exist!" + file.getName() + fnfe);
			sBuilder.append("the marketprice page not found!");
		} catch (IOException ioe) {
			errorLogHandler.error("can not read the file" + file.getName() + ioe);
			sBuilder.append("the marketprice page not found!");
		} finally {
			try {
				br.close();
			} catch (IOException ioe) {
				errorLogHandler.error("file close error!");
			}
		}
		return sBuilder.toString();

	}
}
