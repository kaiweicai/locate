package com.locate;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.locate.client.gui.StatusBar;
import com.locate.common.LocateMessageTypes;
import com.locate.common.model.LocateUnionMessage;
import com.locate.common.utils.NetTimeUtil;
import com.locate.face.IBussiness;
import com.locate.face.IClientConnector;
import com.locate.gate.handler.ClientConnector;
import com.locate.gate.handler.ClientHandler;
import com.locate.gate.model.CustomerFiled;

/**
 * A swing window panel to monitor RFALocateGateWay Server
 * 
 * @author cloudWei
 * 
 */
public class RFApplication extends JFrame {
	private Logger logger = Logger.getLogger(RFApplication.class);
	private boolean conLocate;
	private static final long serialVersionUID = 1L;
	private JLabel currentUserTitle;
	public static JLabel currentUserNumber;
	private JLabel currentRequestTitle;
	public static JLabel currentRequestNumber;
	private JLabel responseTitle;
	public static JLabel responseNumber;
	public static JTextArea showLog;
	private JScrollPane jScrollPane0;
	private JScrollPane tableScrollPane;
	private JLabel avgTitle;
	public static JLabel avgTimes;
	private JButton closeButton;
	private DefaultTableCellRenderer cellRanderer; 
	private List<Integer> chanedRowList;
	
	public static boolean stop = false;

	
	//add by Cloud Wei
	private JLabel userNameLabel;
	private JTextField userNameTextField;
	private JLabel passwordLabel;
	private JTextField passwordTextField;
	private JLabel serverAddressLabel;
	private JTextField serverAddressTextField;
	private JLabel portLabel;
	private JTextField portTextField;
	private JButton connetedButton;
	private JLabel ricLabel;
	private JTextField ricTextField;
	private JButton openButton;
	private JTable marketPriceTable;
	private TableModel tableModel;
	private StatusBar statusBar;
	private JLabel useTimeTextLabel;
	private RedRenderer redRenderer =new RedRenderer();
	private BlueRenderer blueRenderer =new BlueRenderer();
	private Map<String,Integer> IdAtRowidMap = new HashMap<String,Integer>();
	StringBuilder sBuilder = new StringBuilder();
	public static long totalResponseNumber = 0;
	public static long totalProcessTime = 0;
	UpdateTableColore updateTablePriceThread = new UpdateTableColore();
	private IClientConnector clientConnetor;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public RFApplication() {
		DOMConfigurator.configureAndWatch("config/log4j.xml");
		initComponents();
		IBussiness bussinessHandler = new UIHandler();
		SimpleChannelHandler clientHandler = new ClientHandler(bussinessHandler);
		clientConnetor = new ClientConnector(bussinessHandler);
//		initNettyClient();
	}


	private void initComponents() {
		
		GroupLayout mainGroupLayout = new GroupLayout();
//		setLayout(mainGroupLayout);
		JPanel panel = new JPanel();
		panel.setLayout(mainGroupLayout);
		JScrollPane sp = new JScrollPane(panel);
//		sp.setLayout(mainGroupLayout);
		getContentPane().add(sp);
		panel.add(getUserNameLabel(), new Constraints(new Leading(550, 100, 12, 12), new Leading(19, 12, 12)));
		panel.add(getUserNameTextField(), new Constraints(new Leading(670, 100, 12, 12), new Leading(19, 12, 12)));
		panel.add(getPasswordLabel(), new Constraints(new Leading(550, 100, 12, 12), new Leading(50, 12, 12)));
		panel.add(getPasswordTextField(), new Constraints(new Leading(670, 12, 12), new Leading(50, 12, 12)));
		panel.add(getServerAddressLabel(), new Constraints(new Leading(550, 100, 12, 12), new Leading(80, 12, 12)));
		panel.add(getServerAddressTextField(), new Constraints(new Leading(670, 100, 12, 12), new Leading(80, 12, 12)));
		panel.add(getPortLabel(), new Constraints(new Leading(550, 100, 12, 12), new Leading(120, 12, 12)));
		panel.add(getPortTextField(), new Constraints(new Leading(670, 100, 12, 12), new Leading(120, 12, 12)));
		
		panel.add(getConnetedButton(), new Constraints(new Leading(550, 12, 12), new Leading(150, 12, 12)));
		
		panel.add(getRicLabel(), new Constraints(new Leading(550, 100, 12, 12), new Leading(190, 12, 12)));
		panel.add(getRicTextField(), new Constraints(new Leading(670, 100, 12, 12), new Leading(190, 12, 12)));
		panel.add(getOpenButton(), new Constraints(new Leading(550, 12, 12), new Leading(220, 12, 12)));
		panel.add(getTableScrollPane(), new Constraints(new Leading(550, 12, 12), new Leading(280, 12, 12)));
		panel.add(getUseTimeTextLabel(), new Constraints(new Leading(550, 12, 12), new Leading(720, 12, 12)));
		
		panel.add(getCloseButton(), new Constraints(new Leading(1063, 12, 12), new Leading(10, 12, 12)));
		
		panel.add(getJScrollPane0(), new Constraints(new Leading(30, 500, 10, 10), new Leading(51, 700, 10, 10)));
		panel.add(getStatusBar(), new Constraints(new Leading(30, 500, 10, 10), new Leading(770, 50, 10, 10)));
		setSize(1024, 600);
	}
	
	public void initialPriceModel(){
	}
	
	/**
	 * add by cloud
	 * @return
	 */
	private JLabel getUserNameLabel() {
		if (userNameLabel == null) {
			userNameLabel = new JLabel();
			userNameLabel.setText("Username:");
		}
		return userNameLabel;
	}
	
	private JTextField getUserNameTextField(){
		if(userNameTextField == null){
			userNameTextField = new JTextField("ztcj");
		}
		return userNameTextField;
	}
	
	private JLabel getPasswordLabel() {
		if (passwordLabel == null) {
			passwordLabel = new JLabel();
			passwordLabel.setText("password:");
		}
		return passwordLabel;
	}
	
	private JTextField getPasswordTextField(){
		if(passwordTextField == null){
			passwordTextField = new JTextField("ztcj2013");
		}
		return passwordTextField;
	}
	
	private JLabel getServerAddressLabel() {
		if (serverAddressLabel == null) {
			serverAddressLabel = new JLabel();
			serverAddressLabel.setText("serverAddress:");
		}
		return serverAddressLabel;
	}
	
	private JTextField getServerAddressTextField(){
		if(serverAddressTextField == null){
			serverAddressTextField = new JTextField("61.144.244.173");
		}
		return serverAddressTextField;
	}
	
	
	private JLabel getPortLabel() {
		if (portLabel == null) {
			portLabel = new JLabel();
			portLabel.setText("Port:");
		}
		return portLabel;
	}
	
	private JTextField getPortTextField(){
		if(portTextField == null){
			portTextField = new JTextField("8888");
		}
		return portTextField;
	}
	
	private JLabel getRicLabel() {
		if (ricLabel == null) {
			ricLabel = new JLabel();
			ricLabel.setText("RIC:");
		}
		return ricLabel;
	}
	
	public JTextField getRicTextField(){
		if(ricTextField == null){
			ricTextField = new JTextField();
			ricTextField.setText("MCU3=LX");
		}
		return ricTextField;
	}
	
	private JButton getOpenButton() {
		if (openButton == null) {
			openButton = new JButton();
			openButton.setText("open");
			openButton.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent event) {
					String ric = ricTextField.getText();
					clientConnetor.openRICMarket(ric);
				}
			});
		}
		return openButton;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("close locate client");
			closeButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					shutdownLocateGateWay();
				}
			});
		}
		return closeButton;
	}
	
	private JButton getConnetedButton() {
		if (connetedButton == null) {
			connetedButton = new JButton();
			connetedButton.setText("connet");
			connetedButton.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent event) {
					String serverAddress = serverAddressTextField.getText();
					int port = Integer.parseInt(portTextField.getText());
					String userName = userNameTextField.getText();
					String password = passwordTextField.getText();
					clientConnetor.conneteLocateGateWay(serverAddress,port,userName,password);
				}
			});
		}
		return connetedButton;
	}
	
	class TableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		Map<Integer,CustomerFiled> data = new HashMap<Integer,CustomerFiled>();
		String[] columns = { "id", "name", "value" };

		public TableModel(LocateUnionMessage message) {
			List<String[]> palyLoadSet = message.getPayLoadSet();
			Integer rowid=0;
			for (String[] filed : palyLoadSet) {
					String id = "";
					if(filed[0]!=null){
						id = filed[0];
					}
					String name = "";
					
					if(filed[1]!=null){
						name = filed[1];
					}
					String value ="";
					if(filed[3]!=null){
						value = filed[3];
					}
					CustomerFiled customerFiled = new CustomerFiled(id, name, value);
					IdAtRowidMap.put(id, rowid);
					data.put(rowid++,customerFiled);
					
			}
		}
		
		public void update(CustomerFiled field,int rowIndex){
			String id = field.getId();
			String name =field.getName();
			String value = field.getValue();
			CustomerFiled customerField=data.get(rowIndex);
//			customerField.setId(id);
//			customerField.setName(name);
			customerField.setValue(value);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Object.class;
			case 1:
				return Object.class;
			case 2: 
				return String.class;
			default:
				return Object.class;
			}
		}
		
		@Override
		public int getRowCount() {
			if (this.data == null)
				return 0;
			return this.data.size();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CustomerFiled customerFiled = data.get(rowIndex);

			if(customerFiled==null){
				logger.info("error rowindex is "+rowIndex);
				return "";
			}
			String r = "";
			switch (columnIndex) {
			case 0:
				r = customerFiled.getId();
				break;
			case 1:
				r = customerFiled.getName();
				break;
			case 2:
				if(customerFiled.getValue()!=null){
					r = customerFiled.getValue();
				}
				break;
			}
			return r;
		}

		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}
	
	private JTable getMarketPriceTable() {
		if (marketPriceTable == null) {
			marketPriceTable = new JTable();
//			marketPriceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			marketPriceTable.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
//					conneteLocateGateWay();
				}
			});
		}
		return marketPriceTable;
	}

	

	private JScrollPane getTableScrollPane() {
		if (tableScrollPane == null) {
			tableScrollPane = new JScrollPane();
			tableScrollPane.setViewportView(getMarketPriceTable());
		}
		return tableScrollPane;
	}
	
	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getShowLog());
		}
		return jScrollPane0;
	}
	
	private StatusBar getStatusBar() {
		if (statusBar == null) {
			statusBar = new StatusBar("", true);
			statusBar.setFont(UIManager.getFont("Label.font"));
			statusBar.setBackground(UIManager.getColor("Panel.background"));
		}
		return statusBar;
	}
	
	private JLabel getUseTimeTextLabel() {
		if (useTimeTextLabel == null) {
			useTimeTextLabel = new JLabel();
			useTimeTextLabel.setFont(UIManager.getFont("Label.font"));
			useTimeTextLabel.setBackground(UIManager.getColor("Panel.background"));
			useTimeTextLabel.setText("");
		}
		return useTimeTextLabel;
	}
	
	private JTextArea getShowLog() {
		if (showLog == null) {
			showLog = new JTextArea();
			showLog.setText("");
			showLog.setRows(10);
		}
		return showLog;
	}
	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
//			if (lnfClassname == null)
//				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL + " on this platform:" + e.getMessage());
		}
	}

	/**
	 * Main entry of the class. Note: This class is only created so that you can
	 * easily preview the result at runtime. It is not expected to be managed by
	 * the designer. You can modify it as you like.
	 */
	public static void main(String[] argv) {
		// load properties file

		// SystemProperties.init("rfaConfig.properties");

		RFApplication frame = new RFApplication();
		frame.setDefaultCloseOperation(RFApplication.EXIT_ON_CLOSE);
		frame.setTitle("RFApplication");
		frame.getContentPane().setPreferredSize(frame.getSize());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		installLnF();
//		RFAServerManager proxy = new RFAServerManager();
//		frame.demo = proxy.getDemo();
		// frame.demo._configFile = "rfaConfig.properties";
//		proxy.init();
//		proxy.start();
	}

	private void shutdownLocateGateWay() {
		// load properties file
		stop = true;
//		demo.cleanup();
		System.exit(0);
	}
	
	class UIHandler implements IBussiness{
		/* (non-Javadoc)
		 * @see com.locate.client.gui.BussinessInterface#handleException(java.lang.Throwable)
		 */
		@Override
		public void handleException(Throwable e){
			sBuilder.append("Client has been occure Exception. Please contact the developer! "+e);
			updateLog(sBuilder.toString());
			statusBar.setStatusFixed("Client has been occure Exception. Please contact the developer!");
		}
		
		/* (non-Javadoc)
		 * @see com.locate.client.gui.BussinessInterface#handleMessage(java.lang.String)
		 */
		@Override
		public void handleMessage(LocateUnionMessage message){
			if (message == null) {
				logger.warn("Received server's  message is null \n");
				return;
			}
			long startTime = message.getStartTime();
			long endTime = System.currentTimeMillis();
			logger.info("original message -------"+message);
			byte msgType = message.getMsgType();
			sBuilder.append("Received message type:" + LocateMessageTypes.toString(msgType)+"\n");
			getUseTimeTextLabel().setText("From Locate Server to client use time:"+String.valueOf(NetTimeUtil.getCheckTime()-startTime)+" millseconds");
			logger.info("The message From RFA to user use time"+(startTime-endTime)+"milliseconds");
			switch(msgType){
				//first the Locate send the snapshot of market price
				case LocateMessageTypes.REFRESH_RESP:
					tableModel = new TableModel(message);
					marketPriceTable.setModel(tableModel);
					updateTablePriceThread.setMarketPriceTable(marketPriceTable);
					break;
				//Locate send the update market price.
				case LocateMessageTypes.UPDATE_RESP:
					updateMarketPriceTable(tableModel,message);
					updateTablePriceThread.setUpdate(true);
					break;
				//Locate send the state info to client
				case LocateMessageTypes.ERROR:
					String errorDescription = message.getResultDes();
					statusBar.setStatusFixed(errorDescription);
					break;
				case LocateMessageTypes.STATUS_RESP:
					String newStatus = message.getState();
					statusBar.setStatusFixed(newStatus);
					break;
				//Locate send the undefined message.
				default:
					logger.error("Not should to here! message type is "+msgType);
					statusBar.setStatusFixed("The Message can not be handle according with correct message type match.",Color.RED);
			}
			
			// String content = response.asXML();
			sBuilder.append("Received server's  message : " + message+"\n");
			updateLog(sBuilder.toString());
		}
		
		private void updateMarketPriceTable(TableModel tableModel,LocateUnionMessage message) {
			
			List<String[]> payLoadSet = message.getPayLoadSet();
			
			chanedRowList = new ArrayList<Integer>();
			for (String[] filed : payLoadSet) {
				String id = "";
				if(filed[0]!=null){
					id=filed[0];
				}
				int rowIndex = IdAtRowidMap.get(id);
				chanedRowList.add(rowIndex);
				String name = "";
				if (filed[1] != null) {
					name = filed[1];
				}
				String value = "";
				if (filed[3] != null){
					value = filed[3];
				}
				CustomerFiled customerFiled = new CustomerFiled(id, name, value);
				tableModel.update(customerFiled, rowIndex);
			}
		}

		@Override
		public void handleDisconnected() {
			sBuilder.append("Locate Server disconnted!!! ");
			updateLog(sBuilder.toString());
			statusBar.setStatusFixed(sBuilder.toString());
			System.out.println("Locate Server disconnted!!! ");			
		}
	}
	
	public void updateLog(String logContent){
		showLog.setText(logContent);
	}
	
	class RedRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			JLabel jl = new JLabel();
			if(chanedRowList.contains(row)){
				jl.setForeground(Color.RED);
			}
			jl.setBackground(Color.WHITE);
			jl.setOpaque(true);
			jl.setText(value.toString());
			return jl;
		}
	}
	
	class UpdateTableColore extends Thread {
		private boolean update = false;

		public boolean isUpdate() {
			return update;
		}

		public void setUpdate(boolean update) {
			this.update = update;
		}

		private JTable marketPriceTable;

		public JTable getMarketPriceTable() {
			return marketPriceTable;
		}

		public void setMarketPriceTable(JTable marketPriceTable) {
			this.marketPriceTable = marketPriceTable;
		}

		public UpdateTableColore() {
			update = false;
			this.start();
		}

		public void run() {
			while (true) {
				try {
					if (update) {
						repaintMarketPrice(marketPriceTable);
						update = false;
					} else {
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void repaintMarketPrice(JTable marketPriceTable) throws InterruptedException{
			marketPriceTable.repaint();
			marketPriceTable.setDefaultRenderer(String.class,redRenderer);
			Thread.sleep(500);
			marketPriceTable.setDefaultRenderer(String.class,blueRenderer);
			marketPriceTable.repaint();
		}
	}
	
	class BlueRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			JLabel jl = new JLabel();
			if(chanedRowList.contains(row)){
				jl.setForeground(Color.blue);
			}
			jl.setBackground(Color.WHITE);
			jl.setOpaque(true);
			jl.setText(value.toString());
			return jl;
		}
	}
	
	public class RemoveMoreData extends Thread {

		public void run() {
			int currentRow = 0;
			while (!stop) {

				try {
					while (showLog.getLineCount() > 500) {
						currentRow = showLog.getLineCount();
						showLog.setText("");

					}
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
