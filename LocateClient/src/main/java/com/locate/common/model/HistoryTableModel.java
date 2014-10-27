package com.locate.common.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;

public class HistoryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] columns = {"时间", "买价", "卖价","成交价" };
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
		if(locateMessageList!=null&&locateMessageList.size()!=0){
			historyList = new String[locateMessageList.size()][];
		}else{
			historyList = new String[0][];
			return;
		}
		for (int i = 0; i < locateMessageList.size(); i++) {
			historyList[i] = new String[columns.length];
			LocateUnionMessage unionMessage = locateMessageList.get(i);
			String gernerateTime = unionMessage.getGeneratetime();
			historyList[i][0] = gernerateTime == null ? "" : gernerateTime;
			String bidPrice[] = unionMessage.getPriceMap().get("22");
			historyList[i][1] = bidPrice == null ? "" : bidPrice[3];
			String askPrice[] = unionMessage.getPriceMap().get("25");
			historyList[i][2] = askPrice == null ? "" : askPrice[3];
			String[] lastTradePrice = unionMessage.getPriceMap().get("6");
			historyList[i][3] = lastTradePrice == null ? "" : lastTradePrice[3];
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
