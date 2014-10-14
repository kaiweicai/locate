package com.locate.client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.locate.RFApplication;
import com.locate.common.model.HistoryTableModel;
import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.SystemProperties;
import com.locate.logging.LoggerAnalyzer;

public class HistoryFrame extends JFrame {
	private String itemName;
	private JComboBox<ComboItemName> itemNameComboBox;
	private CalendarPanel calendarPanel;
	private JTextField dateInput;
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
		contentPanel.add(getReloadButton(new Rectangle(270,10,100,20)));
		contentPanel.add(getDateInput(new Rectangle(10,10,100,20)));
		contentPanel.add(getRicTextField(new Rectangle( 120, 10, 150, 22)));
		contentPanel.add(this.calendarPanel);
		contentPanel.add(getHistoryPane(new Rectangle(10,50,750,600)));
		
		this.pack();
	}

	public JComboBox<ComboItemName> getRicTextField(Rectangle r) {
		if (itemNameComboBox == null) {
			String[] itemNames = SystemProperties.getProperties(SystemProperties.ITEM_NAME).split(",");
			ComboItemName[] comboItemName = new ComboItemName[itemNames.length];
			for(int i=0;i<comboItemName.length;i++){
				comboItemName[i]= new ComboItemName(itemNames[i]);
			}
			itemNameComboBox = new JComboBox<ComboItemName>(comboItemName);
//			itemNameComboBox.addItemListener(new ItemListener(){
//				@Override
//				public void itemStateChanged(ItemEvent e) {
//					Object object = e.getItem();
//					System.out.println(object);
//				}
//			});
			itemNameComboBox.setEditable(true);
			itemNameComboBox.setBounds(r);
		}
		return itemNameComboBox;
	}
	
	private JTextField getDateInput(Rectangle r){
		if(dateInput==null){
			String patern = "yyyyMMdd";
			SimpleDateFormat sdf = new SimpleDateFormat(patern);
			String nowDatetime = sdf.format(new Date());
			dateInput=new JTextField(nowDatetime);
			// 定义日历控件面板类
			calendarPanel = new CalendarPanel(dateInput, "yyyyMMdd");
			calendarPanel.initCalendarPanel();
			dateInput.setBounds(r);
		}
		return dateInput;
	}
	
	private JButton getReloadButton(Rectangle r){
		if(this.reloadButton == null){
			reloadButton = new JButton("重新载入");
			reloadButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					String itemName = "";
					Object object = itemNameComboBox.getSelectedItem();
					if(object instanceof String){
						itemName = (String)object;
					}else if(object instanceof ComboItemName){
						itemName = ((ComboItemName)object).getItemValue();
					}
					String date = dateInput.getText();
					List<LocateUnionMessage> historyList = null;
					try{
						historyList = analyzer.readLogFile(itemName,date);
						historyTableModel = new HistoryTableModel(historyList);
					}catch(Exception exception){
						JOptionPane.showMessageDialog(null, exception.getMessage());
						return;
					}
					histtoryTable.setModel(historyTableModel);
					if(historyList.isEmpty()){
						JOptionPane.showMessageDialog(null, "该产品没有历史记录");
					}else{
						JOptionPane.showMessageDialog(null, "产品装载完成.");
					}
				}
			});
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
