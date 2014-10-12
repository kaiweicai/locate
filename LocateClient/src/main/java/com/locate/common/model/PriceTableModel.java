package com.locate.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriceTableModel extends AbstractTableModel{
	Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;
	private Map<String,Integer> IdAtRowidMap = new HashMap<String,Integer>();
	Map<Integer,CustomerFiled> data = new HashMap<Integer,CustomerFiled>();
	String[] columns = { "编号", "域名", "值" };

	public PriceTableModel(LocateUnionMessage message) {
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
//		customerField.setId(id);
//		customerField.setName(name);
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
			String exchangeName = CustomerFiled.filedExchangeMap.get(customerFiled.getId());
			if(!StringUtils.isBlank(exchangeName)){
				r = exchangeName;
			}
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
	
	public Map<String, Integer> getIdAtRowidMap() {
		return IdAtRowidMap;
	}

	public void setIdAtRowidMap(Map<String, Integer> idAtRowidMap) {
		IdAtRowidMap = idAtRowidMap;
	}
}
