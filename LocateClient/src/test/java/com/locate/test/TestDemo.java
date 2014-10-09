package com.locate.test;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import com.locate.client.gui.Tab;
import com.locate.client.gui.TabbedPane;
import com.locate.client.gui.TabbedPaneListener;

public class TestDemo {
	public static void main(String[] args) {
		try {
			String feel = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(feel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame();
		frame.setTitle("可关闭Tab测试");
		frame.setSize(300, 400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		TabbedPane tabbedPane = new TabbedPane();
		tabbedPane.enableDragAndDrop();
		tabbedPane.setCloseButtonEnabled(true);
		tabbedPane.addTab("测试一", null, new JLabel("测试一"));
		tabbedPane.addTab("测试二", null, new JLabel("测试二"));
		tabbedPane.addTab("测试三", null, new JLabel("测试三"));
		tabbedPane.addTab("测试四", null, new JLabel("测试四"));
		tabbedPane.addTabbedPaneListener(new TabbedPaneListener() {
			@Override
			public void allTabsRemoved() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean canTabClose(Tab tab, Component component) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void tabAdded(Tab tab, Component component, int index) {
				// TODO Auto-generated method stub

			}

			@Override
			public void tabRemoved(Tab tab, Component component, int index) {
				// TODO Auto-generated method stub
				System.out.println("close");
			}

			@Override
			public void tabSelected(Tab tab, Component component, int index) {
				// TODO Auto-generated method stub

			}
		});

		frame.add(tabbedPane);
		frame.setVisible(true);
	}
}
