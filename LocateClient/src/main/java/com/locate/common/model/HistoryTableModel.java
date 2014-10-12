package com.locate.common.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class HistoryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] columns = {"时间", "买价", "卖价" };
	private String[][] historyList;
	@Override
	public int getRowCount() {
		return historyList.length;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return historyList[rowIndex][columnIndex];
	}
	
	public HistoryTableModel(List<LocateUnionMessage> locateMessageList) {
		historyList = new String[locateMessageList.size()][];
		for(int i = 0 ;i<locateMessageList.size();i++){
			historyList[i]=new String[3];
			LocateUnionMessage unionMessage = locateMessageList.get(i);
			historyList[i][0]=unionMessage.getGeneratetime();
			historyList[i][1]=unionMessage.getTradeRecodeMap().get(22)[3];
			historyList[i][2]=unionMessage.getTradeRecodeMap().get(25)[3];
		}
	}
	
	public void update(CustomerFiled field,int rowIndex){
		String id = field.getId();
		String name =field.getName();
		String value = field.getValue();
//		customerField.setId(id);
//		customerField.setName(name);
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
	public String getColumnName(int column) {
		return columns[column];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}
