package com.locate.client.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

@Deprecated
public class ItemNameLabel extends JLabel implements ListCellRenderer<String>{

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
			boolean isSelected, boolean cellHasFocus) {
		ListModel<? extends String> keys = list.getModel();
		for(int i=0;i<keys.getSize();i++){
			String key= keys.getElementAt(i);
			switch (key) {
			case "XAU=":
				key="金";
				break;
			default:
				break;
			}
		}
		return this;
	}
}
