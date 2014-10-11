package com.locate.client.gui;

import java.util.List;

import javax.swing.JFrame;

import com.locate.common.model.LocateUnionMessage;
import com.locate.logging.LoggerAnalyzer;

public class HistoryFrame extends JFrame {
	private LoggerAnalyzer analyzer = new LoggerAnalyzer();
	private static final long serialVersionUID = 1L;
	public HistoryFrame(){
		initComponents();
	}
	
	private void initComponents() {
		setSize(1344, 720);
	}
	
	public void loadHistory(String itemName){
		List<LocateUnionMessage> historyList = analyzer.readLogFile(itemName);
		System.out.println(historyList);
	}
}
