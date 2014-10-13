package com.locate.client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.locate.RFApplication;
import com.locate.common.model.HistoryTableModel;
import com.locate.common.model.LocateUnionMessage;
import com.locate.logging.LoggerAnalyzer;

public class HistoryFrame extends JFrame {
	private String itemName;
	private CalendarPanel calendarPanel;
	private JScrollPane contentPanel;
	private JScrollPane historyPanel;
	private JTable histtoryTable;
	private HistoryTableModel historyTableModel;
	private LoggerAnalyzer analyzer = new LoggerAnalyzer();
	private static final long serialVersionUID = 1L;
	private JButton reloadButton;
	public HistoryFrame(){
		initComponents();
	}
	
	public HistoryFrame(String itemName){
		this.itemName=itemName;
		initComponents();
		loadHistory(itemName);
	}
	
	private void initComponents() {
		contentPanel = getContentPanne(new Rectangle(10,50,750,700));
		setSize(1024, 680);
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
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		contentPanel.add(getReloadButton(new Rectangle(10,10,100,20)));
		contentPanel.add(getCelendarPanel(new Rectangle(50,10,100,20)));
		contentPanel.add(getHistoryPane(new Rectangle(10,50,750,600)));
		
		this.pack();
	}

	private CalendarPanel getCelendarPanel(Rectangle r){
		if(calendarPanel==null){
			calendarPanel=new CalendarPanel();
			calendarPanel.setBounds(r);
		}
		return calendarPanel;
	}
	
	private JButton getReloadButton(Rectangle r){
		if(this.reloadButton == null){
			reloadButton = new JButton("重新载入");
			reloadButton.setBounds(r);
		}
		return reloadButton;
	}

	private JScrollPane getContentPanne(Rectangle r){
		if(contentPanel == null){
			contentPanel = new JScrollPane();
			contentPanel.setBounds(r);
		}
		return contentPanel;
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
		this.itemName = itemName;
		List<LocateUnionMessage> historyList = analyzer.readLogFile(itemName);
		historyTableModel = new HistoryTableModel(historyList);
		histtoryTable.setModel(historyTableModel);
		if(historyList.isEmpty()){
			JOptionPane.showMessageDialog(null, "该产品没有历史记录");
		}
	}
}
