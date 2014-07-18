package com.locate.rmds.processer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import net.sf.json.JSON;

import org.apache.log4j.Logger;

public class FilePriceKeeper implements IPriceKeeper {
	public static final String PERSSITENT_DIRECTORY = "persistent/";
	Logger logger = Logger.getLogger(getClass());
	private File keepFile;
	private String ric;
	private FileWriter fileWriter;
	private ExecutorService executorService;

	public FilePriceKeeper(String clientRequestItemName) {
		ric = clientRequestItemName;
		init();
	}

	private void init() {
		executorService = Executors.newSingleThreadExecutor();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		String dateEnd = dateFormat.format(date);
		keepFile = new File(PERSSITENT_DIRECTORY + ric.replace('=', '_') + "." + dateEnd);
		try {
			if (!keepFile.exists()) {
				keepFile.createNewFile();
			}
			fileWriter = new FileWriter(keepFile);
		} catch (IOException e) {
			logger.error("can not create persistent file " + keepFile);
			logger.warn("System error and will exit! Please contact the developer!");
			System.exit(-1);
		}
	}

	public void persistentThePrice(final JSON jsonObjectt) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					fileWriter.write(jsonObjectt.toString()+"\n");
				} catch (IOException e) {
					logger.error("write file error!", e);
				}
			}
		});
	}
}
