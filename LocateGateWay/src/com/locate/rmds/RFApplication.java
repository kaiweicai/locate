package com.locate.rmds;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

/**
 * A swing window panel to monitor RFALocateGateWay Server
 * 
 * @author cloudWei
 * 
 */
public class RFApplication extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel currentUserTitle;
	public static JLabel currentUserNumber;
	private JLabel currentRequestTitle;
	public static JLabel currentRequestNumber;
	private JLabel responseTitle;
	public static JLabel responseNumber;
	public static JTextArea showLog;
	private JScrollPane jScrollPane0;
	private JLabel avgTitle;
	public static JLabel avgTimes;
	private QSConsumerProxy demo;
	private JButton closeButton;
	public static boolean stop = false;

	/**
	 * @todo add the 
	 */
	public static long totalResponseNumber = 0;
	public static long totalProcessTime = 0;

	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public RFApplication() {
		initComponents();
	}

	private void initComponents() {

		setLayout(new GroupLayout());

		add(getCurrentUserTitle(), new Constraints(new Leading(40, 111, 12, 12), new Leading(19, 12, 12)));
		add(getCurrentUserNumber(), new Constraints(new Leading(169, 91, 10, 10), new Leading(19, 12, 12)));
		add(getCurrentRequestNumber(), new Constraints(new Leading(402, 85, 10, 10), new Leading(19, 12, 12)));
		add(getCurrentRequestTitle(), new Constraints(new Leading(272, 112, 12, 12), new Leading(19, 12, 12)));
		add(getAvgTitle(), new Constraints(new Leading(801, 148, 10, 10), new Leading(19, 10, 10)));
		add(getResponseNumber(), new Constraints(new Leading(682, 95, 10, 10), new Leading(19, 12, 12)));
		add(getResponseTitle(), new Constraints(new Leading(555, 99, 10, 10), new Leading(19, 12, 12)));
		add(getJScrollPane0(), new Constraints(new Leading(30, 1081, 10, 10), new Leading(51, 403, 10, 10)));
		add(getAvgTimes(), new Constraints(new Leading(972, 73, 10, 10), new Leading(15, 10, 10)));
		add(getCloseButton(), new Constraints(new Leading(1063, 12, 12), new Leading(10, 12, 12)));
		setSize(1137, 473);
		// this.pack();
		(new RemoveMoreData()).start();
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
		RFAServerManager proxy = new RFAServerManager();
		frame.demo = proxy.getDemo();
		// frame.demo._configFile = "rfaConfig.properties";
		proxy.init();
		proxy.start();
	}

	private void shutdownLocateGateWay() {
		// load properties file
		stop = true;
		demo.cleanup();
		System.exit(0);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
