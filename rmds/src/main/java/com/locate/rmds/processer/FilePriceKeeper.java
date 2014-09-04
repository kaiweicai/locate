package com.locate.rmds.processer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import net.sf.json.JSON;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.locate.rmds.processer.face.IPriceKeeper;

@Service("filePriceKeeper")
@Scope("prototype")
public class FilePriceKeeper implements IPriceKeeper {
	Logger logger = Logger.getLogger(getClass());
	private File keepFile;
	private String ric;
	private BufferedWriter bufferedWriter;
	public FilePriceKeeper() {
//		ric = clientRequestItemName;
//		init();
	}

	public void init(String ric) {
		DateFormat dateFormat = DateFormat.getDateInstance();
		Date date = new Date();
		String dateEnd = dateFormat.format(date);
		File persistentDirectory = new File("persistent");
		if(!persistentDirectory.exists()){
			persistentDirectory.mkdir();
		}
		keepFile = new File("persistent/" + ric + "." + dateEnd);
		try {
			if (!keepFile.exists()) {
				keepFile.createNewFile();
			}
			bufferedWriter = new BufferedWriter(new FileWriter(keepFile));
		} catch (IOException e) {
			logger.error("can not create persistent file " + keepFile);
			logger.warn("System error and will exit! Please contact the developer!");
			System.exit(-1);
		}
	}
	
	public void persistentThePrice(JSON jsonObjectt){
		try {
			bufferedWriter.append(jsonObjectt.toString());
			bufferedWriter.flush();
		} catch (IOException e) {
			logger.error("write file error!",e);
		}
	}
}
