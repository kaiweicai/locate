package com.locate.rmds;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.swing.XMLTableModel;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.springframework.web.util.HtmlUtils;

import com.locate.common.GateWayMessageTypes;
import com.locate.common.GateWayMessageTypes.RFAMessageName;
import com.locate.gate.coder.GateWayDecoder;
import com.locate.gate.coder.GateWayEncoder;
import com.locate.gate.model.CustomerFiled;
import com.locate.gate.model.LocateMessage;
import com.locate.rmds.util.RFANodeconstant;
import com.reuters.rfa.omm.OMMMsg.MsgType;

/**
 * A swing window panel to monitor RFALocateGateWay Server
 * 
 * @author cloudWei
 * 
 */
public class RFApplication extends JFrame {
	private Logger logger = Logger.getLogger(RFApplication.class);
	private Channel channel;
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
	
	StringBuilder sb = new StringBuilder();
	public static long totalResponseNumber = 0;
	public static long totalProcessTime = 0;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public RFApplication() {
		DOMConfigurator.configureAndWatch("config/test/log4j.xml");
		initComponents();
	}

	private void initComponents() {
		
		GroupLayout mainGroupLayout = new GroupLayout();
		setLayout(mainGroupLayout);
		
		add(getUserNameLabel(), new Constraints(new Leading(1180, 100, 12, 12), new Leading(19, 12, 12)));
		add(getUserNameTextField(), new Constraints(new Leading(1300, 100, 12, 12), new Leading(19, 12, 12)));
		add(getPasswordLabel(), new Constraints(new Leading(1180, 100, 12, 12), new Leading(50, 12, 12)));
		add(getPasswordTextField(), new Constraints(new Leading(1300, 12, 12), new Leading(50, 12, 12)));
		add(getServerAddressLabel(), new Constraints(new Leading(1180, 100, 12, 12), new Leading(80, 12, 12)));
		add(getServerAddressTextField(), new Constraints(new Leading(1300, 100, 12, 12), new Leading(80, 12, 12)));
		add(getPortLabel(), new Constraints(new Leading(1180, 100, 12, 12), new Leading(120, 12, 12)));
		add(getPortTextField(), new Constraints(new Leading(1300, 100, 12, 12), new Leading(120, 12, 12)));
		
		add(getConnetedButton(), new Constraints(new Leading(1180, 12, 12), new Leading(150, 12, 12)));
		
		add(getRicLabel(), new Constraints(new Leading(1180, 100, 12, 12), new Leading(190, 12, 12)));
		add(getRicTextField(), new Constraints(new Leading(1300, 100, 12, 12), new Leading(190, 12, 12)));
		add(getOpenButton(), new Constraints(new Leading(1180, 12, 12), new Leading(220, 12, 12)));
		add(getTableScrollPane(), new Constraints(new Leading(1180, 12, 12), new Leading(280, 12, 12)));
		
		
		add(getCurrentUserTitle(), new Constraints(new Leading(40, 111, 12, 12), new Leading(19, 12, 12)));
		add(getCurrentUserNumber(), new Constraints(new Leading(169, 91, 10, 10), new Leading(19, 12, 12)));
		add(getCurrentRequestNumber(), new Constraints(new Leading(402, 85, 10, 10), new Leading(19, 12, 12)));
		add(getCurrentRequestTitle(), new Constraints(new Leading(272, 112, 12, 12), new Leading(19, 12, 12)));
		add(getAvgTitle(), new Constraints(new Leading(801, 148, 10, 10), new Leading(19, 10, 10)));
		add(getResponseNumber(), new Constraints(new Leading(682, 95, 10, 10), new Leading(19, 12, 12)));
		add(getResponseTitle(), new Constraints(new Leading(555, 99, 10, 10), new Leading(19, 12, 12)));
		add(getAvgTimes(), new Constraints(new Leading(972, 73, 10, 10), new Leading(15, 10, 10)));
		add(getCloseButton(), new Constraints(new Leading(1063, 12, 12), new Leading(10, 12, 12)));
		
		add(getJScrollPane0(), new Constraints(new Leading(30, 1081, 10, 10), new Leading(51, 403, 10, 10)));
		
		setSize(1700, 800);
		// this.pack();
//		(new RemoveMoreData()).start();
	}
	
	public void initialPriceModel(){
	}
	
	/**
	 * add by cloud
	 * 得到用户名称标签
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
			serverAddressTextField = new JTextField("127.0.0.1");
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
			ricTextField.setText("XAU=");
		}
		return ricTextField;
	}
	
	private JButton getOpenButton() {
		if (openButton == null) {
			openButton = new JButton();
			openButton.setText("open");
			openButton.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent event) {
					openRICMarket();
				}
			});
		}
		return openButton;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("关闭程序");
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
					conneteLocateGateWay();
				}
			});
		}
		return connetedButton;
	}
	
	class TableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		Map<String,CustomerFiled> data = new HashMap<String,CustomerFiled>();
		String[] columns = { "id", "name", "value" };

		public TableModel(Document document) {
			Element rmds = document.getRootElement();
			Element fields = rmds.element(RFANodeconstant.RESPONSE_RESPONSE_NODE)
					.element(RFANodeconstant.RESPONSE_FIELDS_NODE);
			List<Element> filedList = fields.elements();
			for (Element filed : filedList) {
					String id = filed.element(RFANodeconstant.RESPONSE_FIELDS_FIELD_ID_NODE).getText();
					String name = filed.element(RFANodeconstant.RESPONSE_FIELDS_FIELD_NAME_NODE).getText();
					Element valueField = filed.element(RFANodeconstant.RESPONSE_FIELDS_FIELD_VALUE_NODE);
					String value ="";
					if(valueField!=null)
						value = filed.element(RFANodeconstant.RESPONSE_FIELDS_FIELD_VALUE_NODE).getText();
					CustomerFiled customerFiled = new CustomerFiled(id, name, value);
					data.put(id,customerFiled);
			}
		}
		
		public void update(CustomerFiled field){
			String id = field.getId();
			String name =field.getName();
			String value = field.getValue();
			CustomerFiled customerField=data.get(id);
			customerField.setId(id);
			customerField.setName(name);
			customerField.setValue(value);
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

	
	private void initialDataModel(TableModel tableModel) {
		tableModel.setValueAt("productName", 0, 0);
		tableModel.setValueAt("MarketPrice", 1, 0);
		
		tableModel.setValueAt("黄金期货", 0, 1);
		tableModel.setValueAt(100, 1, 1);
	}

	private JLabel getAvgTimes() {
		if (avgTimes == null) {
			avgTimes = new JLabel();
			avgTimes.setText("0");
		}
		return avgTimes;
	}

	private JLabel getAvgTitle() {
		if (avgTitle == null) {
			avgTitle = new JLabel();
			avgTitle.setText("平均数据处理时间(MS):");
		}
		return avgTitle;
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

	private JTextArea getShowLog() {
		if (showLog == null) {
			showLog = new JTextArea();
			showLog.setText("");
			showLog.setRows(10);
		}
		return showLog;
	}

	private JLabel getResponseNumber() {
		if (responseNumber == null) {
			responseNumber = new JLabel();
			responseNumber.setText("0");
		}
		return responseNumber;
	}

	private JLabel getResponseTitle() {
		if (responseTitle == null) {
			responseTitle = new JLabel();
			responseTitle.setText("返回数据个数:");
		}
		return responseTitle;
	}

	private JLabel getCurrentRequestNumber() {
		if (currentRequestNumber == null) {
			currentRequestNumber = new JLabel();
			currentRequestNumber.setText("0");
		}
		return currentRequestNumber;
	}

	private JLabel getCurrentRequestTitle() {
		if (currentRequestTitle == null) {
			currentRequestTitle = new JLabel();
			currentRequestTitle.setText("当前请求代码数:");
		}
		return currentRequestTitle;
	}

	private JLabel getCurrentUserNumber() {
		if (currentUserNumber == null) {
			currentUserNumber = new JLabel();
			currentUserNumber.setText("0");
		}
		return currentUserNumber;
	}

	private JLabel getCurrentUserTitle() {
		if (currentUserTitle == null) {
			currentUserTitle = new JLabel();
			currentUserTitle.setText("当前连接用户数:");
		}
		return currentUserTitle;
	}
	
	public void updateLog(String logContent){
		showLog.setText(logContent);
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
	
	private void openRICMarket(){
		DocumentFactory documentFactory = DocumentFactory.getInstance();
	    Document requestDoc =  documentFactory.createDocument();
	    String ric = ricTextField.getText();
    	createFutureRequest(requestDoc,ric);
    	sentMessageToServer(GateWayMessageTypes.FUTURE_REQUEST,requestDoc);
	}
	
	
	private void conneteLocateGateWay() {
		String serverAddress = serverAddressTextField.getText();
		int port = Integer.parseInt(portTextField.getText());
		// 创建客户端channel的辅助类,发起connection请求
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("encoder", new GateWayEncoder());
				pipeline.addLast("decoder", new GateWayDecoder());
				pipeline.addLast("hanlder", new ClientHandler());
//				pipeline.addLast("timeout", new IdleStateHandler(new HashedWheelTimer(), 0, 0, 10));
//				pipeline.addLast("heartBeat", new ClientIdleHandler());
				return pipeline;
			}
		});
		logger.info("start to conneted to server");
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(serverAddress,port));
		try{
			channel = future.getChannel();
			future.awaitUninterruptibly();
		}catch(Exception e){
			logger.error("NIO error "+e.getCause());
		}
		this.conLocate = true;
		logger.info("conneted to server "+serverAddress+" port:" + port);
		
		DocumentFactory documentFactory = DocumentFactory.getInstance();
	    Document requestDoc =  documentFactory.createDocument();
	    
	    createLoginRequest(requestDoc);
    	sentMessageToServer(GateWayMessageTypes.LOGIN, requestDoc);
	}
	
	class ClientIdleHandler extends IdleStateAwareChannelHandler implements ChannelHandler {
		@Override
		public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
			if(e.getState()==IdleState.ALL_IDLE){
				logger.debug("链路空闲,发送心跳 S:{" + e.getChannel().getRemoteAddress() + "} - C:{"
						+ e.getChannel().getLocalAddress() + "} idleState:{" + e.getState() + "}");
//				createEcho();
			}
			super.channelIdle(ctx, e);
		}
	}
	
	private void createLoginRequest(Document doc){
		Element rmds = doc.addElement("rmds");
		Element login = rmds.addElement("login");
		login.addElement("userName").addText(userNameTextField.getText());
		login.addElement("password").addText(passwordTextField.getText());
	}
	
	private void sentMessageToServer(byte msgType,Document doc){
		LocateMessage message = new LocateMessage(msgType, doc, 0);
		ChannelFuture future = channel.write(message);
		future.awaitUninterruptibly();
	}

	private void createFutureRequest(Document doc,String ric){
		Element rmds = doc.addElement("rmds");
		Element request = rmds.addElement("request");
		Element item = request.addElement("item");
		item.addElement("name").addText(ric);
	}
	
	private void createEcho(){
		DocumentFactory documentFactory = DocumentFactory.getInstance();
	    Document requestDoc =  documentFactory.createDocument();
	    String ric = ricTextField.getText();
    	createFutureRequest(requestDoc,ric);
    	sentMessageToServer(GateWayMessageTypes.REQUEST_EOCH,requestDoc);
	}
	
	class ClientHandler extends SimpleChannelHandler {

		long t0, t1;

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			sb.append("NIO error "+e.getCause());
			updateLog(sb.toString());
			System.out.println(e.getCause());
		}

		public Document parseFile() {
			SAXReader reader = new SAXReader();
			String userFile = "D:/rfa/testData.xml";
			Document userData = null;
			try {
				userData = reader.read(userFile);
			} catch (DocumentException e) {
				System.out.println("Inital RFA user data error.");
			}
			return userData;
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			super.messageReceived(ctx, e);
			LocateMessage message = (LocateMessage) e.getMessage();
			byte msgType = message.getMsgType();
			int length = message.getMsgLength();
			
			sb.append("receive messages length:" + length+"\n");
			sb.append("Received message type:" + RFAMessageName.getRFAMessageName(msgType)+"\n");
			
			Document document = message.getDocument();
			if (document == null) {
				sb.append("Received server's  message is null \n");
				return;
			}
			if(msgType==MsgType.REFRESH_RESP){
				tableModel = new TableModel(document);
				marketPriceTable.setModel(tableModel);
			}else if(msgType==MsgType.UPDATE_RESP){
				updateMarketPriceTable(tableModel,document);
			}
			
			
			
			String content = HtmlUtils.htmlUnescape(document.asXML());
			// String content = response.asXML();
			sb.append("Received server's  message : " + content+"\n");
			
			updateLog(sb.toString());
			
			t1 = System.currentTimeMillis();

			System.out.println("Sent messages delay : " + (t1 - t0));
			System.out.println();
		}

		private void updateMarketPriceTable(TableModel tableModel,Document document) {
			Element rmds = document.getRootElement();
			Element fields = rmds.element(RFANodeconstant.RESPONSE_RESPONSE_NODE)
					.element(RFANodeconstant.RESPONSE_FIELDS_NODE);
			List<Element> filedList = fields.elements();
			for (Element filed : filedList) {
					String id = filed.element(RFANodeconstant.RESPONSE_FIELDS_FIELD_ID_NODE).getText();
					String name = filed.element(RFANodeconstant.RESPONSE_FIELDS_FIELD_NAME_NODE).getText();
					Element valueField = filed.element(RFANodeconstant.RESPONSE_FIELDS_FIELD_VALUE_NODE);
					String value ="";
					if(valueField!=null)
						value = filed.element(RFANodeconstant.RESPONSE_FIELDS_FIELD_VALUE_NODE).getText();
					CustomerFiled customerFiled = new CustomerFiled(id, name, value);
					tableModel.update(customerFiled);
			}
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
