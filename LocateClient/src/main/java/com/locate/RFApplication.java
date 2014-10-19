package com.locate;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.locate.client.gui.ComboCellRenderer;
import com.locate.client.gui.ComboItemName;
import com.locate.client.gui.ComboName;
import com.locate.client.gui.HistoryFrame;
import com.locate.client.gui.LogoPanel;
import com.locate.client.gui.StatusBar;
import com.locate.client.gui.Tab;
import com.locate.client.gui.TabbedPane;
import com.locate.client.gui.TabbedPaneListener;
import com.locate.common.ClientConstant;
import com.locate.common.constant.LocateMessageTypes;
import com.locate.common.exception.LocateException;
import com.locate.common.logging.biz.BizLogHandler;
import com.locate.common.model.CustomerFiled;
import com.locate.common.model.LocateUnionMessage;
import com.locate.common.model.PriceTableModel;
import com.locate.common.utils.NetTimeUtil;
import com.locate.common.utils.SystemProperties;
import com.locate.face.IBussiness;
import com.locate.face.IClientConnector;
import com.locate.gate.handler.ClientConnector;
import com.locate.stock.chart.RealTimeChart;

/**
 * A swing window panel to monitor RFALocateGateWay Server
 * 
 * @author cloudWei
 * 
 */
public class RFApplication extends JFrame {
	private Logger logger = LoggerFactory.getLogger(RFApplication.class);
	private BizLogHandler bizLogger = BizLogHandler.getLogger(RFApplication.class);
	private boolean conLocate;
	private static final long serialVersionUID = 1L;
	private JLabel currentUserTitle;
	public static JLabel currentUserNumber;
	private JLabel currentRequestTitle;
	public static JLabel currentRequestNumber;
	private JLabel responseTitle;
	public static JLabel responseNumber;
	public static JTextArea showLog;
	private JScrollPane logScrollPanel;
	private JScrollPane priceTableScrollPane;
	private JScrollPane chartPanel;
	private JLabel avgTitle;
	public static JLabel avgTimes;
	private JButton closeButton;
	private TabbedPane priceTableTabbedPane;
	private TabbedPane priceChartTabbedPane;
	private DefaultTableCellRenderer cellRanderer;
	private List<Integer> chanedRowList = new ArrayList<Integer>();
	
	public static boolean stop = false;

	
	//add by Cloud Wei
	private Image ptLogo;
	private JLabel userNameLabel;
	private JTextField userNameTextField;
	private JLabel passwordLabel;
	private JTextField passwordTextField;
	private JLabel serverAddressLabel;
	private JComboBox<ComboName> serverAddressCombox;
	private JLabel portLabel;
	private JTextField portTextField;
	private JButton connetedButton;
	private JLabel ricLabel;
	private JComboBox<ComboItemName> itemNameComboBox;
	private JButton openButton;
	private JButton openHistoryButton;
//	private JTable marketPriceTable;
//	private TableModel tableModel;
	private StatusBar statusBar;
	private StatusBar serverBar;
	private JLabel useTimeTextLabel;
	private RedRenderer redRenderer =new RedRenderer();
	private BlueRenderer blueRenderer =new BlueRenderer();
	
	StringBuilder sBuilder = new StringBuilder();
	public static long totalResponseNumber = 0;
	public static long totalProcessTime = 0;
//	UpdateTableColore updateTablePriceThread = new UpdateTableColore();
	private IClientConnector clientConnetor;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	
	public RFApplication() {
		JoranConfigurator configurator = new JoranConfigurator();
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		LoggerContext loggerContext = (LoggerContext) loggerFactory;
		loggerContext.reset();
		configurator.setContext(loggerContext);
		try {
			configurator.doConfigure("config/logback.xml");
		} catch (JoranException e) {
			logger.error("initial logback.xml error!");
			throw new LocateException("initial logback.xml error!",e);
		}
		initComponents();
		finalSetting();
		IBussiness bussinessHandler = new UIHandler();
//		ClientHandler clientHandler = new ClientHandler(bussinessHandler);
		clientConnetor = new ClientConnector(bussinessHandler);
//		initNettyClient();
	}


	private void initComponents() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SubstanceLookAndFeel.setSkin(new org.pushingpixels.substance.api.skin.ModerateSkin());
				try {
					setSize(1344, 720);
					LogoPanel logoPanel = new LogoPanel(1150, 40);
//					logoPanel.setImagePath(ClientConstant.iamgeDirectory + "PTLOGO.png");
					logoPanel.setPreferredSize(new Dimension(logoPanel.getImgWidth(), logoPanel.getImgHeight()));

					logoPanel.setLayout(null);
					JScrollPane sp = new JScrollPane(logoPanel);
					sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
					sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					// sp.setLayout(mainGroupLayout);
					getContentPane().add(sp);
					int inputX = 870;
					int inputY = 20;
					logoPanel.add(getCloseButton(new Rectangle(inputX + 200, inputY, 100, 20)));
					logoPanel.add(getUserNameLabel(new Rectangle(inputX, inputY, 100, 20)));
					logoPanel.add(getUserNameTextField(new Rectangle(inputX + 70, inputY, 100, 20)));
					logoPanel.add(getPasswordLabel(new Rectangle(inputX, inputY += 30, 100, 20)));
					logoPanel.add(getPasswordTextField(new Rectangle(inputX + 70, inputY, 100, 20)));
					logoPanel.add(getServerAddressLabel(new Rectangle(inputX, inputY += 30, 100, 20)));
					logoPanel.add(getServerAddressTextField(new Rectangle(inputX + 120, inputY, 100, 20)));
					logoPanel.add(getPortLabel(new Rectangle(inputX, inputY += 30, 100, 20)));
					logoPanel.add(getPortTextField(new Rectangle(inputX + 120, inputY, 100, 20)));

					logoPanel.add(getConnetedButton(new Rectangle(inputX, inputY += 30, 100, 20)));
					logoPanel.add(getRicLabel(new Rectangle(inputX, inputY += 30, 100, 20)));
					logoPanel.add(getRicTextField(new Rectangle(inputX + 120, inputY, 150, 22)));
					logoPanel.add(getOpenButton(new Rectangle(inputX, inputY += 30, 100, 20)));
					logoPanel.add(getOpenHistoryButton(new Rectangle(inputX+120, inputY , 100, 20)));
					logoPanel.add(getTableScrollPane(new Rectangle(inputX, inputY += 30, 400, 400)));
					logoPanel.add(getUseTimeTextLabel(new Rectangle(inputX, 620, 500, 50)));
					logoPanel.add(getLogPanel(new Rectangle(30, 10, 820, 280)));
					logoPanel.add(getChartScrollPanel(new Rectangle(30, 300, 820, 250)));
					logoPanel.add(getStatusBar(new Rectangle(30, 570, 820, 50)));
					logoPanel.add(getServerBar(new Rectangle(30, 620, 820, 50)));
				} catch (Exception e) {
					logger.error("Initial Locate Application error!",e);
				}
			}
		});
	}
	
	private void finalSetting() {
		setDefaultCloseOperation(RFApplication.EXIT_ON_CLOSE);
		setTitle("Price Tech application");
		getContentPane().setPreferredSize(this.getSize());
		Image image = Toolkit.getDefaultToolkit().getImage(ClientConstant.iamgeDirectory+"PTLOGO.png");
		setIconImage(image);
		int windowWidth = getWidth(); // 获得窗口宽
		int windowHeight = getHeight(); // 获得窗口高
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width; // 获取屏幕的宽
		int screenHeight = screenSize.height; // 获取屏幕的高
		setLocation(screenWidth / 4 - windowWidth / 4, screenHeight / 4 - windowHeight /4);// 设置窗口居中显示
		setVisible(true);
		setResizable(true);
		pack();
	}
	
	/**
	 * add by cloud
	 * @return
	 */
	private JLabel getUserNameLabel(Rectangle r) {
		if (userNameLabel == null) {
			userNameLabel = new JLabel();
			userNameLabel.setText("用户名:");
		}
		userNameLabel.setBounds(r);
		return userNameLabel;
	}
	
	private JTextField getUserNameTextField(Rectangle r){
		if(userNameTextField == null){
			userNameTextField = new JTextField("demo");
		}
		userNameTextField.setBounds(r);
		return userNameTextField;
	}
	
	private JLabel getPasswordLabel(Rectangle r) {
		if (passwordLabel == null) {
			passwordLabel = new JLabel();
			passwordLabel.setText("密码:");
		}
		passwordLabel.setBounds(r);
		return passwordLabel;
	}
	
	private JTextField getPasswordTextField(Rectangle r){
		if(passwordTextField == null){
			passwordTextField = new JTextField("demo");
		}
		passwordTextField.setBounds(r);
		return passwordTextField;
	}
	
	private JLabel getServerAddressLabel(Rectangle r) {
		if (serverAddressLabel == null) {
			serverAddressLabel = new JLabel();
			serverAddressLabel.setText("服务器IP地址:");
			serverAddressLabel.setBounds(r);
		}
		return serverAddressLabel;
	}
	
	private JComboBox<ComboName> getServerAddressTextField(Rectangle r){
		if(serverAddressCombox == null){
			serverAddressCombox = new JComboBox<ComboName>();
			String[] serverArray = SystemProperties.getProperties(SystemProperties.SERVER_LIST).split(",");
			ComboName[] comboNames = new ComboName[serverArray.length];
			for(int i=0;i<serverArray.length;i++){
				comboNames[i] = new ComboName(serverArray[i]);
			}
			ComboCellRenderer comboCellRenderer = new ComboCellRenderer();
			serverAddressCombox.setModel(new DefaultComboBoxModel<ComboName>(comboNames));
			serverAddressCombox.setRenderer(comboCellRenderer);
		}
		serverAddressCombox.setBounds(r);
		serverAddressCombox.setEditable(true);
		return serverAddressCombox;
	}
	
	
	private JLabel getPortLabel(Rectangle r) {
		if (portLabel == null) {
			portLabel = new JLabel();
			portLabel.setText("服务器端口:");
		}
		portLabel.setBounds(r);
		return portLabel;
	}
	
	private JTextField getPortTextField(Rectangle r){
		if(portTextField == null){
			portTextField = new JTextField("8888");
			portTextField.setBounds(r);
		}
		return portTextField;
	}
	
	private JLabel getRicLabel(Rectangle r) {
		if (ricLabel == null) {
			ricLabel = new JLabel();
			ricLabel.setText("商品代码:");
			ricLabel.setBounds(r);
		}
		return ricLabel;
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
	
	private JButton getOpenButton(Rectangle r) {
		if (openButton == null) {
			openButton = new JButton();
			openButton.setText("请求报价");
			openButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					try{
						String ricName = "";
						Object object = itemNameComboBox.getSelectedItem();
						if(object instanceof String){
							ricName = (String)object;
						}else if(object instanceof ComboItemName){
							ricName = ((ComboItemName)object).getItemValue();
						}
						clientConnetor.openRICMarket(ricName);
					}catch(LocateException le){
						serverBar.setStatusFixed("Send the RIC to server error!");
						JOptionPane.showMessageDialog(null, "请先登录到服务器", "未登录", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			openButton.setBounds(r);
		}
		return openButton;
	}
	
	private JButton getOpenHistoryButton(Rectangle r) {
		if (openHistoryButton == null) {
			openHistoryButton = new JButton();
			openHistoryButton.setText("历史消息");
			openHistoryButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					HistoryFrame historyFrame = null;
					try{
						String itemName = "";
						String productName = "";
						Object object = itemNameComboBox.getSelectedItem();
						if(object instanceof String){
							itemName = (String)object;
						}else if(object instanceof ComboItemName){
							itemName = ((ComboItemName)object).getItemValue();
						}
						historyFrame = new HistoryFrame();
						historyFrame.loadHistory(itemName);
						historyFrame.setTitle(itemName+"历史记录");
					}catch(LocateException le){
						JOptionPane.showMessageDialog(null, le.getMessage());
//						historyFrame.dispose();
					}
				}
			});
			openHistoryButton.setBounds(r);
		}
		return openHistoryButton;
	}

	
	
	private JButton getCloseButton(Rectangle r) {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("关闭应用");
			closeButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					
					int flag = JOptionPane.showConfirmDialog(closeButton, "确定?", "注意!",
							JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if (JOptionPane.YES_OPTION == flag) {
						clientConnetor.getClientchannel().close();
						System.exit(0);
					} else {
						return;
					}
					shutdownLocateGateWay();
				}
			});
			closeButton.setBounds(r);
		}
		return closeButton;
	}
	
	private JButton getConnetedButton(Rectangle r) {
		if (connetedButton == null) {
			connetedButton = new JButton();
			connetedButton.setText("连接服务器");
			connetedButton.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent event) {
					try{
						Object selectedItem = serverAddressCombox.getSelectedItem();
						String serverAddress="";
						if(selectedItem instanceof ComboName){
							serverAddress = ((ComboName)selectedItem).getValue();
						}else if(selectedItem instanceof String){
							serverAddress = (String)selectedItem;
						}
						int port = Integer.parseInt(portTextField.getText());
						String userName = userNameTextField.getText();
						String password = passwordTextField.getText();
						clientConnetor.conneteLocateGateWay(serverAddress,port,userName,password);
					}catch(Exception e){
						serverBar.setStatusFixed("连接服务器错误.请检查服务器配置!");
						JOptionPane.showMessageDialog(null, "连接服务器错误,请检查服务器配置!", "连接错误!", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			connetedButton.setBounds(r);
		}
		return connetedButton;
	}
	
	private JTable getMarketPriceTable() {
		JTable marketPriceTable = new JTable();
//		marketPriceTable.setBounds(0, 0, 200, 30);
//		marketPriceTable.getColumnModel().getColumn(3).setPreferredWidth(460);
		marketPriceTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
//					conneteLocateGateWay();
			}
		});
		return marketPriceTable;
	}
	
	private JTable getRealTimeChart() {
		JTable marketPriceTable = new JTable();
//		marketPriceTable.setBounds(0, 0, 200, 30);
//		marketPriceTable.getColumnModel().getColumn(3).setPreferredWidth(460);
		marketPriceTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
//					conneteLocateGateWay();
			}
		});
		return marketPriceTable;
	}

	
	/**
	 * 得到报价窗口
	 * @param r
	 * @return
	 */
	private JScrollPane getTableScrollPane(Rectangle r) {
		if (priceTableScrollPane == null) {
			priceTableScrollPane = new JScrollPane();
			priceTableScrollPane.setViewportView(getPriceTableTabbedPane());
			priceTableScrollPane.setBounds(r);
		}
		return priceTableScrollPane;
	}
	
	private TabbedPane getPriceTableTabbedPane() {
		if (priceTableTabbedPane == null) {
			priceTableTabbedPane = new TabbedPane();
			priceTableTabbedPane.setCloseButtonEnabled(true);
//			tabbedPane.addTab("日志", null, getLogPanel(null));
			priceTableTabbedPane.addTabbedPaneListener(new TabbedPaneListener() {
				@Override
				public void allTabsRemoved() {

				}

				@Override
				public boolean canTabClose(Tab tab, Component component) {
					return false;
				}

				@Override
				public void tabAdded(Tab tab, Component component, int index) {

				}

				@Override
				public void tabRemoved(Tab tab, Component component, int index) {
					logger.debug("close");
				}

				@Override
				public void tabSelected(Tab tab, Component component, int index) {
//					priceTableTabbedPane.setSelectedIndex(index);
				}
			});
		}
		return priceTableTabbedPane;
	}
	
	private TabbedPane getPriceChartTabbedPane() {
		if (priceChartTabbedPane == null) {
			priceChartTabbedPane = new TabbedPane();
			priceChartTabbedPane.setCloseButtonEnabled(true);
//			tabbedPane.addTab("日志", null, getLogPanel(null));
			priceChartTabbedPane.addTabbedPaneListener(new TabbedPaneListener() {
				@Override
				public void allTabsRemoved() {

				}

				@Override
				public boolean canTabClose(Tab tab, Component component) {
					return false;
				}

				@Override
				public void tabAdded(Tab tab, Component component, int index) {

				}

				@Override
				public void tabRemoved(Tab tab, Component component, int index) {
					logger.debug("close");
				}

				@Override
				public void tabSelected(Tab tab, Component component, int index) {
//					priceTableTabbedPane.setSelectedIndex(index);
				}
			});
		}
		return priceChartTabbedPane;
	}
	
	private JScrollPane getLogPanel(Rectangle r) {
		if (logScrollPanel == null) {
			logScrollPanel = new JScrollPane();
			logScrollPanel.setViewportView(getShowLog());
			if(r!=null){
				logScrollPanel.setBounds(r);
			}else{
				logScrollPanel.setBounds(0,0,400,400);
			}
		}
		return logScrollPanel;
	}
	
	private JScrollPane getChartScrollPanel(Rectangle r) {
		if (chartPanel == null) {
			chartPanel = new JScrollPane();
			chartPanel.setViewportView(getPriceChartTabbedPane());
			chartPanel.setBounds(r);
//			chartPanel.setBackground(Color.WHITE);
		}
		return chartPanel;
	}
	
	
	private StatusBar getStatusBar(Rectangle r) {
		if (statusBar == null) {
			statusBar = new StatusBar("", true);
			statusBar.setFont(UIManager.getFont("Label.font"));
			statusBar.setBackground(UIManager.getColor("Panel.background"));
			statusBar.setBounds(r);
		}
		return statusBar;
	}
	
	private StatusBar getServerBar(Rectangle r) {
		if (serverBar == null) {
			serverBar = new StatusBar("", true);
			serverBar.setFont(UIManager.getFont("Label.font"));
			serverBar.setBackground(UIManager.getColor("Panel.background"));
			serverBar.setBounds(r);
		}
		return serverBar;
	}
	
	private JLabel getUseTimeTextLabel(Rectangle r) {
		if (useTimeTextLabel == null) {
			useTimeTextLabel = new JLabel();
			useTimeTextLabel.setFont(UIManager.getFont("Label.font"));
			useTimeTextLabel.setBackground(UIManager.getColor("Panel.background"));
			useTimeTextLabel.setText("");
			useTimeTextLabel.setBounds(r);
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

		SystemProperties.init("config/config.properties");
		RFApplication frame = new RFApplication();
//		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
//		installLnF();
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
		private Map<String,BigDecimal[]> bidAskMap = new HashMap<String,BigDecimal[]>();
		/* (non-Javadoc)
		 * @see com.locate.client.gui.BussinessInterface#handleException(java.lang.Throwable)
		 */
		@Override
		public void handleException(Throwable e){
			if(e instanceof ConnectException){
				sBuilder.append("Connection refused! please check the server info!");
				logger.error("Connection refused! please check the server info!",e);
				updateLog(sBuilder);
				serverBar.setStatusFixed("Connection refused! please check the server info!",Color.RED);
			}else if(e instanceof IOException){
				sBuilder.append("Connection close! Locate Server is down!");
				logger.error("Connection close! Locate Server is down!",e);
				updateLog(sBuilder);
				serverBar.setStatusFixed("Connection close! Locate Server is down!",Color.RED);
			}else{
				sBuilder.append("Client has been occure Exception. Please contact the developer! ");
				logger.error("Client has been occure Exception. Please contact the developer!",e);
	//			updateLog(sBuilder.toString());
				serverBar.setStatusFixed("Client has been occure Exception. Please contact the developer!",Color.RED);
			}
		}
		
		/* (non-Javadoc)
		 * @see com.locate.client.gui.BussinessInterface#handleMessage(java.lang.String)
		 */
		@Override
		public void handleMessage(final LocateUnionMessage message){
			if (message == null) {
				logger.warn("Received server's  message is null \n");
				return;
			}
			long startTime = message.getStartTime();
			long endTime = NetTimeUtil.getCurrentNetTime();
			bizLogger.info(message);
			byte msgType = message.getMsgType();
			final String itemName = message.getItemName();
			sBuilder.append("Received message type:" + LocateMessageTypes.toString(msgType)+"\n");
			useTimeTextLabel.setText("From Locate Server to client use time:"+(endTime-startTime)+" millseconds");
			logger.info("The message From RFA to user use time "+(endTime-startTime)+" milliseconds");
			BigDecimal bid = null;
			BigDecimal ask = null;
			double average = 0d;
			List<String[]> payLoadList = message.getPayLoadSet();
			if (payLoadList != null && !payLoadList.isEmpty()) {
				for (String[] payLoad : payLoadList) {
					if (payLoad[0].equals("22")) {
						if(StringUtils.isNotBlank(payLoad[3])){
							bid = new BigDecimal(payLoad[3]);
						}
					}
					if (payLoad[0].equals("25")) {
						if(StringUtils.isNotBlank(payLoad[3])){
							ask = new BigDecimal(payLoad[3]);
						}
					}
				}
				BigDecimal[] bidAsk=bidAskMap.get(itemName);
				if(bidAsk==null){
					bidAsk = new BigDecimal[]{bid,ask};
					bidAskMap.put(itemName, bidAsk);
				}else{
					if(bid == null){
						bid =bidAsk[0];
					}
					if(ask==null){
						ask = bidAsk[1];
					}
					if(bid!=null){
						bidAsk[0]=bid;
					}
					if(ask !=null ){
						bidAsk[1]=ask;
					}
				}
				if(bid!=null&&ask!=null){
					average = bid.add(ask).divide(new BigDecimal("2")).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				}else{
					average = 0;
				}
//				DecimalFormat df = new DecimalFormat("#.000");
				payLoadList.add(new String[] { "100000", "中间价", "double", String.valueOf(average) });
			}
			
			
			switch(msgType){
				//first the Locate send the snapshot of market price
				case LocateMessageTypes.REFRESH_RESP:
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							SubstanceLookAndFeel.setSkin(new org.pushingpixels.substance.api.skin.ModerateSkin());
							PriceTableModel tableModel = new PriceTableModel(message);
							JTable marketPriceTable = getMarketPriceTable();
							marketPriceTable.setModel(tableModel);
							//add the item tab
							priceTableTabbedPane.addTab(ComboItemName.exhangeCode2Name(itemName), null, marketPriceTable);
							marketPriceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
							marketPriceTable.getColumnModel().getColumn(0).setPreferredWidth(20);
							marketPriceTable.getColumnModel().getColumn(1).setPreferredWidth(20);
							marketPriceTable.getColumnModel().getColumn(2).setPreferredWidth(20);
							UpdateTableColore updateThread = new UpdateTableColore();
							updateThread.setMarketPriceTable(marketPriceTable);
								ClientConstant.updateThreadMap.put(itemName, updateThread);
							ClientConstant.itemName2PriceTableModeMap.put(itemName, tableModel);
							
							List<String[]> dataPayLoad = message.getPayLoadSet();
							double price = 0d;
							int amount = 0;
							for(String[] payLoad :dataPayLoad){
								if("100000".equals(payLoad[0])){
									if(StringUtils.isBlank(payLoad[3])){
										return;
									}else{
										price = Double.parseDouble(payLoad[3]);
									}
								}
								if("32".equals(payLoad[0])){
									if(StringUtils.isBlank(payLoad[3])){
										return;
									}else{
										amount = Integer.parseInt(payLoad[3]);
									}
								}
							}
							
							RealTimeChart realTimeChart = new RealTimeChart();
							priceChartTabbedPane.addTab(ComboItemName.exhangeCode2Name(itemName), null,
							realTimeChart.initialChart(ComboItemName.exhangeCode2Name(itemName),price, amount));
							ClientConstant.itemName2RealTimeChartMap.put(itemName, realTimeChart);
						}
					});
					
					chartPanel.getGraphics().setColor(Color.RED);
					break;
				//Locate send the update market price.
				case LocateMessageTypes.UPDATE_RESP:
					PriceTableModel tModel = ClientConstant.itemName2PriceTableModeMap.get(itemName);
					if(tModel==null){
						logger.error("tModel is null");
						tModel = new PriceTableModel(message); 
					}
					updateMarketPriceTable(tModel,message);
					if(ClientConstant.updateThreadMap.get(itemName)!=null){
						ClientConstant.updateThreadMap.get(itemName).setUpdate(true);
					}
					RealTimeChart realTimeChart = ClientConstant.itemName2RealTimeChartMap.get(itemName);
					long currentTime = System.currentTimeMillis();
					
					if(realTimeChart==null){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						realTimeChart = ClientConstant.itemName2RealTimeChartMap.get(itemName);
					}
					
					if(currentTime-realTimeChart.getLastUpdateTime()<=1000){
						return;
					}
					List<String[]> dataPayLoad = message.getPayLoadSet();
					double price = 0d;
					int amount = 0;
					for(String[] payLoad :dataPayLoad){
						if("100000".equals(payLoad[0])){
							if(StringUtils.isBlank(payLoad[3])){
								return;
							}else
							price = Double.parseDouble(payLoad[3]);
						}
						if("32".equals(payLoad[0])){
							if(StringUtils.isBlank(payLoad[3])){
								return;
							}else
							amount = Integer.parseInt(payLoad[3]);
						}
					}
					realTimeChart.updatePriceChart(ComboItemName.exhangeCode2Name(itemName),currentTime, price, amount);
//					chartPanel.getGraphics().drawLine(12, 21, 54, 99);
					break;
				//Locate send the state info to client
				case LocateMessageTypes.SERVER_STATE:
					String errorDescription = message.getResultDes();
					if(StringUtils.isNotBlank(errorDescription)){
						serverBar.setStatusFixed(errorDescription);
					}
					String newStatus = message.getState();
					if(StringUtils.isNotBlank(newStatus)){
						statusBar.setStatusFixed(newStatus);
					}
					break;
				case LocateMessageTypes.STATUS_RESP:
					errorDescription = message.getResultDes();
					if(StringUtils.isNotBlank(errorDescription)){
						serverBar.setStatusFixed(errorDescription);
					}
					newStatus = message.getState();
					if(StringUtils.isNotBlank(newStatus)){
						statusBar.setStatusFixed(newStatus);
					}
					break;
//				Locate send the undefined message.
				default:
					logger.error("Not should to here! message type is "+message);
					statusBar.setStatusFixed("The Message can not be handle according with correct message type match.",Color.RED);
			}
			
			// String content = response.asXML();
			sBuilder.append("Received server's  message : " + message+"\n");
			sBuilder.ensureCapacity(100);
			updateLog(sBuilder);
		}
		
		private void updateMarketPriceTable(PriceTableModel tableModel,LocateUnionMessage message) {
			Map<String,Integer> IdAtRowidMap=tableModel.getIdAtRowidMap();
			List<String[]> payLoadSet = message.getPayLoadSet();
			for (String[] filed : payLoadSet) {
				String id = "";
				if(filed[0]!=null){
					id=filed[0];
				}
				int rowIndex = IdAtRowidMap.get(id);
				if(!chanedRowList.contains(rowIndex)){
					chanedRowList.add(rowIndex);
				}
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
			String state = "Locate Server disconnted!!! ";
			sBuilder.append(state);
			updateLog(sBuilder);
			serverBar.setStatusFixed(state,Color.RED);
			System.out.println("Locate Server disconnted!!! ");			
		}
	}
	
	public void updateLog(StringBuilder log){
		if(log.length()>6000){
			this.sBuilder=log.delete(0 , log.length()-6000);
		}
		showLog.setText(log.toString());
	}
	
	class RedRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			JLabel jl = new JLabel();
			if(chanedRowList.contains(row)){
				jl.setForeground(Color.RED);
			}
//			jl.setBackground(Color.WHITE);
			jl.setOpaque(true);
			jl.setText(value.toString());
			return jl;
		}
	}
	
	class BlueRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			JLabel jl = new JLabel();
			if(chanedRowList.contains(row)){
				jl.setForeground(Color.blue);
			}
//			jl.setBackground(Color.WHITE);
			jl.setOpaque(true);
			jl.setText(value.toString());
			return jl;
		}
	}
	
	public class UpdateTableColore extends Thread {
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
