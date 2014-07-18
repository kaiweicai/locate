package com.locate.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class RedRowCellRenderer extends JFrame {
	public RedRowCellRenderer() {
		TableModel tableModel = new MyTableModel();
		JTable table = new JTable(tableModel);
		table.setDefaultRenderer(Integer.class, new MyRenderer());

		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new RedRowCellRenderer();
	}
}

class MyTableModel extends AbstractTableModel {
	private Object[][] cells = { { "some", 0 }, { "any", 1 }, { "even", 2 }, { "text", 0 }, { "and", 1 }, { "text", 2 } };
	private String[] columnNames = { "Column 1", "Column 2" };

	public String getColumnName(int c) {
		return columnNames[c];
	}

	public Class<?> getColumnClass(int c) {
		return cells[0][c].getClass();
	}

	public int getColumnCount() {
		return cells[0].length;
	}

	public int getRowCount() {
		return cells.length;
	}

	public Object getValueAt(int r, int c) {
		return cells[r][c];
	}

	public void setValueAt(Object obj, int r, int c) {
		cells[r][c] = obj;
	}

}

class MyRenderer implements TableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel jl = new JLabel();
//		if ((Integer) value > 0) {
			jl.setForeground(Color.RED);
//		}
		jl.setBackground(Color.WHITE);
		jl.setOpaque(true);
		jl.setText(value.toString());
		return jl;
	}
}
