package com.locate.client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.locate.RFApplication;
import com.locate.common.model.HistoryTableModel;
import com.locate.common.model.LocateUnionMessage;
import com.locate.logging.LoggerAnalyzer;

public class HistoryFrame extends JFrame {
	private JScrollPane historyPanel;
	private JTable histtoryTable;
	private HistoryTableModel historyTableModel;
	private LoggerAnalyzer analyzer = new LoggerAnalyzer();
	private static final long serialVersionUID = 1L;
	public HistoryFrame(){
		initComponents();
	}
	
	public HistoryFrame(String itemName){
		initComponents();
		loadHistory(itemName);
	}
	
	private void initComponents() {
		historyPanel = getHistoryPane(new Rectangle(10,10,800,600));
		setSize(1024, 640);
		setDefaultCloseOperation(RFApplication.DISPOSE_ON_CLOSE);
		getContentPane().setPreferredSize(this.getSize());
		int windowWidth = getWidth(); // 获得窗口宽
		int windowHeight = getHeight(); // 获得窗口高
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width; // 获取屏幕的宽
		int screenHeight = screenSize.height; // 获取屏幕的高
		setLocation(screenWidth / 4 - windowWidth / 4, screenHeight / 4 - windowHeight /4);// 设置窗口居中显示
		setVisible(true);
		setResizable(true);
		getContentPane().add(historyPanel);
		this.pack();
	}
	

	private JScrollPane getHistoryPane(Rectangle r) {
		if (historyPanel == null) {
			historyPanel = new JScrollPane();
			historyPanel.setViewportView(getHistoryTable());
			historyPanel.setBounds(r);
		}
		return historyPanel;
	}
	
	private Component getHistoryTable() {
		if(histtoryTable==null){
			histtoryTable = new JTable();
		}
		return histtoryTable;
	}

	public void loadHistory(String itemName){
		List<LocateUnionMessage> historyList = analyzer.readLogFile(itemName);
		historyTableModel = new HistoryTableModel(historyList);
		histtoryTable.setModel(historyTableModel);
	}
}
