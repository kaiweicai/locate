package com.locate.client.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComboCellRenderer extends JLabel implements ListCellRenderer<ComboName> {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends ComboName> list, ComboName value, int index,
			boolean isSelected, boolean cellHasFocus) {
		setText(value.getKey());
//		setToolTipText(value.getValue());
		return this;
	}
}
