package com.locate.rmds.parser;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.locate.gate.model.LocateUnionMessage;
import com.locate.rmds.gui.viewer.FieldValue;
import com.locate.rmds.parser.face.IOmmParser;
import com.locate.rmds.util.RFATypeConvert;
import com.locate.rmds.util.SystemProperties;
import com.reuters.rfa.ansipage.Page;
import com.reuters.rfa.ansipage.PageUpdate;
import com.reuters.rfa.dictionary.DictionaryException;
import com.reuters.rfa.dictionary.FidDef;
import com.reuters.rfa.dictionary.FieldDictionary;
import com.reuters.rfa.omm.OMMAttribInfo;
import com.reuters.rfa.omm.OMMData;
import com.reuters.rfa.omm.OMMEntry;
import com.reuters.rfa.omm.OMMException;
import com.reuters.rfa.omm.OMMFieldEntry;
import com.reuters.rfa.omm.OMMFieldList;
import com.reuters.rfa.omm.OMMIterable;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMState;
import com.reuters.rfa.omm.OMMMsg.MsgType;
import com.reuters.rfa.omm.OMMTypes;

/**

 */
/**
 * The GenericOMMParser is used to read and initialize dictionaries and parse
 * any OMM message that is passed to it using the parse() method.
 * 
 * This class is not thread safe due to the static variables. The "CURRENT"
 * variables save state between methods, so another thread cannot change the
 * values. CURRENT_DICTIONARY requires only one FieldDictionary to be used at a
 * time. CURRENT_PAGE requires only one page to be parsed at a time.
 * 
 * @author CloudWei kaiweicai@163.com create time 2014年8月18日
 * @copyRight by Author
 */
@Component
public final class LocateGenericOMMParser implements IOmmParser {
	private static boolean ripple = SystemProperties.getProperties(SystemProperties.RIPPLE).equalsIgnoreCase("true") ? true
			: false;
	private static HashMap<Integer, FieldDictionary> DICTIONARIES = new HashMap<Integer, FieldDictionary>();
	private static FieldDictionary CURRENT_DICTIONARY;
	private static Page CURRENT_PAGE;
	static Logger logger = Logger.getLogger(LocateGenericOMMParser.class.getName());

	/**
	 * This method should be called one before parsing and data.
	 * 
	 * @param fieldDictionaryFilename
	 * @param enumDictionaryFilename
	 * @throws DictionaryException
	 *             if an error has occurred
	 */
	public static FieldDictionary initializeDictionary(String fieldDictionaryFilename, String enumDictionaryFilename)
			throws DictionaryException {
		FieldDictionary dictionary = FieldDictionary.create();
		try {
			FieldDictionary.readRDMFieldDictionary(dictionary, fieldDictionaryFilename);
			logger.info("field dictionary read from RDMFieldDictionary file");

			FieldDictionary.readEnumTypeDef(dictionary, enumDictionaryFilename);
			logger.info("enum dictionary read from enumtype.def file");

			initializeDictionary(dictionary);
		} catch (DictionaryException e) {
			throw new DictionaryException("ERROR: Check if files " + fieldDictionaryFilename + " and "
					+ enumDictionaryFilename + " exist and are readable.", e);
		}
		CURRENT_DICTIONARY = dictionary;
		return dictionary;
	}

	// This method can be used to initialize a downloaded dictionary
	public synchronized static void initializeDictionary(FieldDictionary dict) {
		int dictId = dict.getDictId();
		if (dictId == 0)
			dictId = 1; // dictId == 0 is the same as dictId 1
		DICTIONARIES.put(new Integer(dictId), dict);
	}

	public static FieldDictionary getDictionary(int dictId) {
		if (dictId == 0)
			dictId = 1;
		return (FieldDictionary) DICTIONARIES.get(new Integer(dictId));
	}

	/**
	 * parse MSG and generate the common locate object and log it
	 * 这个方法有太多的业务逻辑.应该把这些业务逻辑提出来.让这个方法成为一个通用的方法.
	 */
	public final LocateUnionMessage parse(OMMMsg msg, String itemName) {
		if (itemName == null)
			itemName = "";
		LocateUnionMessage locateObject = new LocateUnionMessage(itemName);
		parseMsg(msg, locateObject);
		logger.info("Parse object is:\n" + locateObject);
		return locateObject;
	}

	final void parseMsg(OMMMsg msg, LocateUnionMessage locateObject) {
		String itemName = locateObject.getItemName();

		byte msgType = msg.getMsgType();
		
		// 初始化,记录该RIC的所有FiledValue到Map中.REFRESH_RESP means snapshot message.
		if (msgType == OMMMsg.MsgType.REFRESH_RESP && ITEM_FIELD_MAP.get(itemName) == null) {
			if (msg.getDataType() != OMMTypes.NO_DATA && msg.getDataType() == OMMTypes.FIELD_LIST) {
				OMMFieldList fieldList = (OMMFieldList) msg.getPayload();
				Map<Short, FieldValue> fieldListMap = new LinkedHashMap<Short, FieldValue>();
				ITEM_FIELD_MAP.put(itemName, fieldListMap);
				for (Iterator<OMMFieldEntry> fieldIterator = fieldList.iterator(); fieldIterator.hasNext();) {
					OMMFieldEntry fieldEntry = fieldIterator.next();
					FidDef fieldDefine = CURRENT_DICTIONARY.getFidDef(fieldEntry.getFieldId());
					if (fieldDefine != null) {
						FieldValue fieldValue = new FieldValue(null, fieldDefine);
						fieldValue.update(fieldEntry);
						fieldListMap.put(fieldDefine.getFieldId(), fieldValue);
					}
				}
			} else if (msg.getDataType() != OMMTypes.NO_DATA) {
				logger.error("OMM message data type not match the 'Field List' type. The data type is"
						+ msg.getDataType());
			}
		}

		if (msg.has(OMMMsg.HAS_SEQ_NUM)) {
			locateObject.setSeqNumber(msg.getSeqNum());
		}

		if (msg.has(OMMMsg.HAS_ATTRIB_INFO)) {
			OMMAttribInfo ai = msg.getAttribInfo();
			if (ai.has(OMMAttribInfo.HAS_ATTRIB)) {
				parseData(ai.getAttrib(), locateObject, msgType);
			}
		}

		if (msg.getDataType() != OMMTypes.NO_DATA) {
			// Payload has multiple kinds of data.
			parseData(msg.getPayload(), locateObject, msgType);
		}
	}

	/**
	 * parse data and print it in a table-nested format to the System.out
	 */

	// public static final void parse(OMMData data,Element field)
	// {
	// parseData(data, null, 0,field,false);
	// }

	public void handelLocateState(OMMMsg msg, LocateUnionMessage locateObject) {
		byte streamState = msg.getState().getStreamState();
		byte dataState = msg.getState().getDataState();
		String state = msg.getState().toString();
		String streamingState = OMMState.Stream.toString(streamState);
		String dataingState = OMMState.Data.toString(dataState);
		locateObject.setState(state);
		locateObject.setStreamingState(streamingState);
		locateObject.setDataingState(dataingState);
	}

	/**
	 * parse the payload data and put it into the locateUnionMessage.
	 */
	public final void parseData(OMMData data, LocateUnionMessage locateMessage, byte msgType) {
		if (OMMTypes.isAggregate(data.getType()))
			parseAggregate(data, locateMessage, msgType);
	}

	private final void parseAggregate(OMMData data, LocateUnionMessage locateMessage, byte msgType) {
		parseAggregateHeader(data, locateMessage);
		for (Iterator<?> iter = ((OMMIterable) data).iterator(); iter.hasNext();) {
			OMMEntry entry = (OMMEntry) iter.next();
			parseEntry(entry, locateMessage, msgType);
		}
	}

	private final void parseAggregateHeader(OMMData data, LocateUnionMessage locateMessage) {
		short dataType = data.getType();
		switch (dataType) {
		case OMMTypes.FIELD_LIST: {
			// set DICTIONARY to the dictId for this field list
			OMMFieldList fieldList = (OMMFieldList) data;
			int dictId = fieldList.getDictId();
			CURRENT_DICTIONARY = getDictionary(dictId);
		}
			break;
		}
	}

	/**
	 * Parse the OmmEntry to one record.
	 * 
	 * @param entry
	 * @param locateMessage
	 * @param ripple
	 * @param msgType
	 * @param rippleMap
	 */
	private final void parseEntry(OMMEntry entry, LocateUnionMessage locateMessage, byte msgType) {
		OMMFieldEntry fe = (OMMFieldEntry) entry;
		String itemName = locateMessage.getItemName();
		FidDef fiddef = CURRENT_DICTIONARY.getFidDef(fe.getFieldId());

		Short rippleId = fiddef.getRippleFieldId();
		// put the ripple values of this ommEntry into payLoadSet.
		if (msgType == MsgType.UPDATE_RESP) {
			if (ripple && rippleId != 0) {
				// add the ripple data
				FieldValue fieldValue = getValue(itemName, fiddef.getFieldId());
				if (fieldValue == null) {
					// Strange:在第一订阅该产品的map中无法找到该fieldId对应的fieldValue,不应该存在的逻辑
					fieldValue = new FieldValue(null, fiddef);
					fieldValue.update(fe);
					logger.debug("The fieldValue which can not be found is:" + fieldValue);
					ITEM_FIELD_MAP.get(itemName).put(fiddef.getFieldId(), fieldValue);
				}
				FidDef fieldDef = fiddef;
				// 得到当前列的值
				Object tmp = fieldValue.getStringValue();
				// 如果当前列有引用(ripple )的列,那从缓存中取出保存的引用列的上次的值对象.
				while ((fieldDef.getRippleFieldId() != 0)
						&& ((fieldValue = getValue(itemName, fieldDef.getRippleFieldId())) != null)) {
					short rippleFieldId = fieldValue.getFieldId();
					FidDef rippleDef = CURRENT_DICTIONARY.getFidDef(fieldDef.getRippleFieldId());
					tmp = fieldValue.setValue(tmp);
					putRippleValueIntoMessage(fieldValue, rippleDef, locateMessage);
					fieldDef = CURRENT_DICTIONARY.getFidDef(rippleFieldId);
				}
			}
		}

		// start to parse the entry and get the price value.
		try {
			switch (entry.getType()) {
			case OMMTypes.FIELD_ENTRY: {
				if (CURRENT_DICTIONARY != null) {
					if (fiddef != null) {
						OMMData data = null;
						if (fe.getDataType() == OMMTypes.UNKNOWN)
							data = fe.getData(fiddef.getOMMType());
						else
							// defined data already has type
							data = fe.getData();
						// 更新缓存,并存储数据对象.
						FieldValue fieldValue = ITEM_FIELD_MAP.get(itemName).get(fe.getFieldId());
						fieldValue.setValue(data.toString());
						putRippleValueIntoMessage(fieldValue, fiddef, locateMessage);
					}
				} else {
					logger.error("The CURRENT_DICTIONARY is null!");
				}
			}
				break;
			}
		} catch (OMMException e) {
			logger.error("ERROR Invalid data: " + e.getMessage());
		}
	}

	/**
	 * Build the payload data and put it into the locate union message data set.
	 * 
	 * @param fieldValue
	 * @param rippleFieldDef
	 * @param locateMessage
	 */
	private void putRippleValueIntoMessage(FieldValue fieldValue, FidDef rippleFieldDef,
			LocateUnionMessage locateMessage) {
		Set<String[]> payLoadSet = locateMessage.getPayLoadSet();
		String fieldType = RFATypeConvert.convertField(OMMTypes.toString(fieldValue.getOMMType()));
		String filedId = String.valueOf(rippleFieldDef.getFieldId());
		String[] rippleValue = new String[] { filedId, fieldType, rippleFieldDef.getName(), fieldValue.getStringValue() };
		payLoadSet.add(rippleValue);
	}

	private static FieldValue getValue(String itemName, short filedId) {
		Map<Short, FieldValue> filedId2FieldValueMap = ITEM_FIELD_MAP.get(itemName);
		FieldValue fieldValue = filedId2FieldValueMap.get(filedId);
		if (fieldValue == null) {
			logger.debug("FiledValue not found in map by filedId!!!!" + filedId);
		}
		return fieldValue;
	}

	public static final void parseAnsiPageData(OMMData data, StringBuffer logMsg, int tabLevel) {
		boolean newPage = false;
		if (CURRENT_PAGE == null) {
			CURRENT_PAGE = new Page();
			newPage = true;
		}

		Vector<PageUpdate> pageUpdates = new Vector<PageUpdate>();
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		CURRENT_PAGE.decode(bais, pageUpdates);
		if (newPage)
			logMsg.append(CURRENT_PAGE.toString()); // print the page if it is a
													// refresh message
		else {
			// print the update string
			Iterator<PageUpdate> iter = pageUpdates.iterator();
			while (iter.hasNext()) {
				PageUpdate u = (PageUpdate) iter.next();
				StringBuilder buf = new StringBuilder(80);
				for (short k = u.getBeginningColumn(); k < u.getEndingColumn(); k++) {
					buf.append(CURRENT_PAGE.getChar(u.getRow(), k));
				}
				if (!(buf.toString()).equalsIgnoreCase("")) {

					logMsg.append("\n Update String: " + buf.toString() + " (Row: " + u.getRow() + ", Begin Col: "
							+ u.getBeginningColumn() + ", End Col: " + u.getEndingColumn() + ")");
				}
			}
		}
	}

}
