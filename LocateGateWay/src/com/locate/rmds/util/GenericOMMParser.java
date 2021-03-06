package com.locate.rmds.util;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.locate.common.DataBaseCache;
import com.locate.common.RFANodeconstant;
import com.locate.rmds.gui.viewer.FieldValue;
import com.reuters.rfa.ansipage.Page;
import com.reuters.rfa.ansipage.PageUpdate;
import com.reuters.rfa.common.PublisherPrincipalIdentity;
import com.reuters.rfa.dictionary.DataDef;
import com.reuters.rfa.dictionary.DataDefDictionary;
import com.reuters.rfa.dictionary.DictionaryException;
import com.reuters.rfa.dictionary.ElementEntryDef;
import com.reuters.rfa.dictionary.FidDef;
import com.reuters.rfa.dictionary.FieldDictionary;
import com.reuters.rfa.dictionary.FieldEntryDef;
import com.reuters.rfa.omm.OMMAttribInfo;
import com.reuters.rfa.omm.OMMData;
import com.reuters.rfa.omm.OMMDataBuffer;
import com.reuters.rfa.omm.OMMDataDefs;
import com.reuters.rfa.omm.OMMElementEntry;
import com.reuters.rfa.omm.OMMEntry;
import com.reuters.rfa.omm.OMMEnum;
import com.reuters.rfa.omm.OMMException;
import com.reuters.rfa.omm.OMMFieldEntry;
import com.reuters.rfa.omm.OMMFieldList;
import com.reuters.rfa.omm.OMMFilterEntry;
import com.reuters.rfa.omm.OMMFilterList;
import com.reuters.rfa.omm.OMMIterable;
import com.reuters.rfa.omm.OMMMap;
import com.reuters.rfa.omm.OMMMapEntry;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMPriority;
import com.reuters.rfa.omm.OMMSeries;
import com.reuters.rfa.omm.OMMTypes;
import com.reuters.rfa.omm.OMMVector;
import com.reuters.rfa.omm.OMMVectorEntry;
import com.reuters.rfa.omm.OMMMsg.MsgType;
import com.reuters.rfa.rdm.RDMDictionary;
import com.reuters.rfa.rdm.RDMInstrument;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.rdm.RDMService;
import com.reuters.rfa.rdm.RDMUser;
import com.reuters.rfa.utility.HexDump;

/**
 * The GenericOMMParser is used to read and initialize dictionaries and parse
 * any OMM message that is passed to it using the parse() method.
 * 
 * This class is not thread safe due to the static variables. The "CURRENT"
 * variables save state between methods, so another thread cannot change the
 * values. CURRENT_DICTIONARY requires only one FieldDictionary to be used at a
 * time. CURRENT_PAGE requires only one page to be parsed at a time.
 */
public final class GenericOMMParser
{
    private static HashMap<Integer, FieldDictionary> DICTIONARIES = new HashMap<Integer, FieldDictionary>();
    private static FieldDictionary CURRENT_DICTIONARY= FieldDictionary.create();
    private static Page CURRENT_PAGE;
    static Logger _logger = Logger.getLogger(GenericOMMParser.class.getName());
//    static Logger _logger;
    private static boolean INTERNAL_DEBUG = false;

//    public static void setLogger(Logger logger)
//    {
//    	_logger = logger;
//    }
    /**
     * This method should be called one before parsing and data.
     * 
     * @param fieldDictionaryFilename
     * @param enumDictionaryFilename
     * @throws DictionaryException if an error has occurred
     */
    public static FieldDictionary initializeDictionary(String fieldDictionaryFilename,
            String enumDictionaryFilename) throws DictionaryException
    {
        FieldDictionary dictionary = FieldDictionary.create();
        try
        {
            FieldDictionary.readRDMFieldDictionary(dictionary, fieldDictionaryFilename);
            _logger.info("field dictionary read from RDMFieldDictionary file");

            FieldDictionary.readEnumTypeDef(dictionary, enumDictionaryFilename);
            _logger.info("enum dictionary read from enumtype.def file");

            initializeDictionary(dictionary);
        }
        catch (DictionaryException e)
        {
            throw new DictionaryException("ERROR: Check if files " + fieldDictionaryFilename
                    + " and " + enumDictionaryFilename + " exist and are readable.", e);
        }
        CURRENT_DICTIONARY=dictionary;
        return dictionary;
    }

    // This method can be used to initialize a downloaded dictionary
    public synchronized static void initializeDictionary(FieldDictionary dict)
    {
        int dictId = dict.getDictId();
        if (dictId == 0)
            dictId = 1; // dictId == 0 is the same as dictId 1
        DICTIONARIES.put(new Integer(dictId), dict);
    }

    public static FieldDictionary getDictionary(int dictId)
    {
        if (dictId == 0)
            dictId = 1;
        return (FieldDictionary)DICTIONARIES.get(new Integer(dictId));
    }

    /**
     * parse msg and print it in a table-nested format to System.out
     * 这个方法有太多的业务逻辑.应该把这些业务逻辑提出来.让这个方法成为一个通用的方法.
     */
    public static final Document parse(OMMMsg msg,String itemName)
    {
//    	_logger.info("orignal OMMMsg is "+ msg);
    	if(itemName == null)
    		itemName = "";
    	StringBuffer logMsg = new StringBuffer();
    	DocumentFactory factory = DocumentFactory.getInstance();
    	Document responseMsg =  factory.createDocument();
    	Element rmdsElement = responseMsg.addElement(RFANodeconstant.RESPONSE_ROOT_NODE);
    	rmdsElement.addElement(RFANodeconstant.LOCATE_NODE);
    	Element response = rmdsElement.addElement(RFANodeconstant.RESPONSE_RESPONSE_NODE);
    	Element reqItem = response.addElement(RFANodeconstant.RESPONSE_ITEM_NODE);
    	reqItem.addElement(RFANodeconstant.RESPONSE_ITEM_NAME_NODE).addText(itemName);
    	Element fields = response.addElement(RFANodeconstant.RESPONSE_FIELDS_NODE);
    	
    	parseMsg(msg, logMsg,fields,itemName);
        _logger.info(logMsg.toString());
//        _logger.info(responseMsg.asXML());
//        if(fields.elements().size() > 0){
        	return responseMsg;
//        }else{
//        	return null;
//        }
    }

    private static final String hintString(OMMMsg msg)
    {
        StringBuilder buf = new StringBuilder(60);

        boolean bAppend = true;

        if (msg.has(OMMMsg.HAS_ATTRIB_INFO))
        {
            bAppend = append(buf, "HAS_ATTRIB_INFO", bAppend);
        }
        if (msg.has(OMMMsg.HAS_CONFLATION_INFO))
        {
            bAppend = append(buf, "HAS_CONFLATION_INFO", bAppend);
        }
        if (msg.has(OMMMsg.HAS_HEADER))
        {
            bAppend = append(buf, "HAS_HEADER", bAppend);
        }
        if (msg.has(OMMMsg.HAS_ITEM_GROUP))
        {
            bAppend = append(buf, "HAS_ITEM_GROUP", bAppend);
        }
        if (msg.has(OMMMsg.HAS_PERMISSION_DATA))
        {
            bAppend = append(buf, "HAS_PERMISSION_DATA", bAppend);
        }
        if (msg.has(OMMMsg.HAS_PRIORITY))
        {
            bAppend = append(buf, "HAS_PRIORITY", bAppend);
        }
        if (msg.has(OMMMsg.HAS_QOS))
        {
            bAppend = append(buf, "HAS_QOS", bAppend);
        }
        if (msg.has(OMMMsg.HAS_QOS_REQ))
        {
            bAppend = append(buf, "HAS_QOS_REQ", bAppend);
        }
        if (msg.has(OMMMsg.HAS_RESP_TYPE_NUM))
        {
            bAppend = append(buf, "HAS_RESP_TYPE_NUM", bAppend);
        }
        if (msg.has(OMMMsg.HAS_SEQ_NUM))
        {
            bAppend = append(buf, "HAS_SEQ_NUM", bAppend);
        }
        if (msg.has(OMMMsg.HAS_ID))
        {
            bAppend = append(buf, "HAS_ID", bAppend);
        }
        if (msg.has(OMMMsg.HAS_PUBLISHER_INFO))
        {
            bAppend = append(buf, "HAS_PUBLISHER_INFO", bAppend);
        }
        if (msg.has(OMMMsg.HAS_STATE))
        {
            bAppend = append(buf, "HAS_STATE", bAppend);
        }
        if (msg.has(OMMMsg.HAS_USER_RIGHTS))
        {
            bAppend = append(buf, "HAS_USER_RIGHTS", bAppend);
        }

        return buf.toString();
    }

    private static boolean append(StringBuilder buf, String str, boolean first)
    {
        if (!first)
        {
            buf.append(" | ");
            first = false;
        }
        else
            first = false;

        buf.append(str);
        return first;
    }

    /**
     * parse msg and print it in a table-nested format to the provided
     * PrintStream
     */
    public static final void parseMsg(OMMMsg msg, StringBuffer logMsg,Element fieldsElement,String itemName)
    {
        parseMsg(msg, logMsg, 0,fieldsElement,itemName);
    }

	static final void parseMsg(OMMMsg msg, StringBuffer logMsg, int tabLevel, Element fieldsElement,String itemName) {
		boolean ripple = (msg.getMsgType() == OMMMsg.MsgType.UPDATE_RESP)
				&& !msg.isSet(OMMMsg.Indication.DO_NOT_RIPPLE);
		byte msgType = msg.getMsgType();
		
		// 初始化,记录该item的所有FiledValue到Map中.
		if (msgType == OMMMsg.MsgType.REFRESH_RESP && DataBaseCache.filedValueMap.get(itemName) == null) {
			if (msg.getDataType() == OMMTypes.FIELD_LIST) {
				OMMFieldList fieldList = (OMMFieldList) msg.getPayload();
				Map<Short, FieldValue> fieldListMap = new LinkedHashMap<Short, FieldValue>();
				DataBaseCache.filedValueMap.put(itemName, fieldListMap);
				for (Iterator<?> fiter = fieldList.iterator(); fiter.hasNext();) {
					OMMFieldEntry fentry = (OMMFieldEntry) fiter.next();
					FidDef fiddef = CURRENT_DICTIONARY.getFidDef(fentry.getFieldId());
					if (fiddef != null) {
						// FieldValue field = getValue(itemName,
						// fiddef.getFieldId());
						// if (field == null) {
						short type = fentry.getDataType();
						if (type == OMMTypes.UNKNOWN)
							type = fiddef.getOMMType();
						FieldValue field = new FieldValue(null, fiddef);
						field.update(fentry);
						fieldListMap.put(fiddef.getFieldId(), field);
						// }
					}
				}
			}
		}
		
		
		logMsg.append('\n');
		dumpIndent(logMsg, tabLevel);
		logMsg.append("MESSAGE");
		dumpIndent(logMsg, tabLevel + 1);
		logMsg.append("Msg Type: " + OMMMsg.MsgType.toString(msg.getMsgType()));
		dumpIndent(logMsg, tabLevel + 1);
		logMsg.append("Msg Model Type: " + RDMMsgTypes.toString(msg.getMsgModelType()));
		dumpIndent(logMsg, tabLevel + 1);
		logMsg.append("Indication Flags: " + OMMMsg.Indication.indicationString(msg));
		dumpIndent(logMsg, tabLevel + 1);
		logMsg.append("Hint Flags: " + hintString(msg));

		if ((msg.getDataType() == OMMTypes.ANSI_PAGE) && msg.isSet(OMMMsg.Indication.CLEAR_CACHE)) {
			CURRENT_PAGE = null;
		}

		if (msg.has(OMMMsg.HAS_STATE)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("State: " + msg.getState());
		}
		if (msg.has(OMMMsg.HAS_PRIORITY)) {
			dumpIndent(logMsg, tabLevel + 1);
			OMMPriority p = msg.getPriority();
			if (p != null)
				logMsg.append("Priority: " + p.getPriorityClass() + "," + p.getCount());
			else
				logMsg.append("Priority: Error flag recieved but there is not priority present");
		}
		if (msg.has(OMMMsg.HAS_QOS)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("Qos: " + msg.getQos());
		}
		if (msg.has(OMMMsg.HAS_QOS_REQ)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("QosReq: " + msg.getQosReq());
		}
		if (msg.has(OMMMsg.HAS_ITEM_GROUP)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("Group: " + msg.getItemGroup());
		}
		if (msg.has(OMMMsg.HAS_PERMISSION_DATA)) {
			byte[] permdata = msg.getPermissionData();
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("PermissionData: " + HexDump.toHexString(permdata, false) + " ( "
					+ HexDump.formatHexString(permdata) + " ) ");
		}
		if (msg.has(OMMMsg.HAS_SEQ_NUM)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("SeqNum: " + msg.getSeqNum());
		}

		if (msg.has(OMMMsg.HAS_CONFLATION_INFO)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("Conflation Count: " + msg.getConflationCount());
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("Conflation Time: " + msg.getConflationTime());
		}

		if (msg.has(OMMMsg.HAS_RESP_TYPE_NUM)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("RespTypeNum: " + msg.getRespTypeNum());
			dumpRespTypeNum(msg, logMsg);
		}

		if (msg.has(OMMMsg.HAS_ID)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("Id: " + msg.getId());
		}

		if ((msg.has(OMMMsg.HAS_PUBLISHER_INFO)) || (msg.getMsgType() == OMMMsg.MsgType.POST)) {
			PublisherPrincipalIdentity pi = (PublisherPrincipalIdentity) msg.getPrincipalIdentity();
			if (pi != null) {
				dumpIndent(logMsg, tabLevel + 1);
				logMsg.append("Publisher Address: 0x" + Long.toHexString(pi.getPublisherAddress()));
				dumpIndent(logMsg, tabLevel + 1);
				logMsg.append("Publisher Id: " + pi.getPublisherId());
			}
		}

		if (msg.has(OMMMsg.HAS_USER_RIGHTS)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("User Rights Mask: " + OMMMsg.UserRights.userRightsString(msg.getUserRightsMask()));
		}

		if (msg.has(OMMMsg.HAS_ATTRIB_INFO)) {
			dumpIndent(logMsg, tabLevel + 1);
			logMsg.append("AttribInfo");
			OMMAttribInfo ai = msg.getAttribInfo();
			if (ai.has(OMMAttribInfo.HAS_SERVICE_NAME)) {
				dumpIndent(logMsg, tabLevel + 2);
				logMsg.append("ServiceName: " + ai.getServiceName());
			}
			if (ai.has(OMMAttribInfo.HAS_SERVICE_ID)) {
				dumpIndent(logMsg, tabLevel + 2);
				logMsg.append("ServiceId: " + ai.getServiceID());
			}
			if (ai.has(OMMAttribInfo.HAS_NAME)) {
				dumpIndent(logMsg, tabLevel + 2);
				logMsg.append("Name: " + ai.getName());
			}
			if (ai.has(OMMAttribInfo.HAS_NAME_TYPE)) {
				dumpIndent(logMsg, tabLevel + 2);
				logMsg.append("NameType: " + ai.getNameType());
				dumpIndent(logMsg, tabLevel + 3);
				if (msg.getMsgModelType() == RDMMsgTypes.LOGIN) {
					logMsg.append("(" + RDMUser.NameType.toString(ai.getNameType()) + ")");
				}else if (RDMInstrument.isInstrumentMsgModelType(msg.getMsgModelType())) {
					logMsg.append("(" + RDMInstrument.NameType.toString(ai.getNameType()) + ")");
				}else{
					logMsg.append('\n');
				}
			}
			if (ai.has(OMMAttribInfo.HAS_FILTER)) {
				dumpIndent(logMsg, tabLevel + 2);
				logMsg.append(" Filter: " + ai.getFilter());
				dumpIndent(logMsg, tabLevel + 3);
				if (msg.getMsgModelType() == RDMMsgTypes.DIRECTORY) {
					logMsg.append(" (" + RDMService.Filter.toString(ai.getFilter()) + ")");
				} else if (msg.getMsgModelType() == RDMMsgTypes.DICTIONARY) {
					logMsg.append(" (" + RDMDictionary.Filter.toString(ai.getFilter()) + ")");
				}else{
					logMsg.append('\n');
				}
			}
			if (ai.has(OMMAttribInfo.HAS_ID)) {
				dumpIndent(logMsg, tabLevel + 2);
				logMsg.append("ID: " + ai.getId());
			}
			if (ai.has(OMMAttribInfo.HAS_ATTRIB)) {
				dumpIndent(logMsg, tabLevel + 2);
				logMsg.append("Attrib");
				parseData(ai.getAttrib(), logMsg, tabLevel + 2, fieldsElement,false);
				logMsg.append("\n");
			}
		}
		dumpIndent(logMsg, tabLevel + 1);
		logMsg.append(" Payload: ");
		if (msg.getDataType() != OMMTypes.NO_DATA) {
			logMsg.append(" " + msg.getPayload().getEncodedLength() + " bytes");
			parseData(msg.getPayload(), logMsg, tabLevel + 1, fieldsElement,ripple,itemName,msgType);
			logMsg.append("\n ");
		} else {
			dumpIndent(logMsg, tabLevel + 2);
			logMsg.append(" None ");
		}
	}

    /**
     * parse msg and print it in a table-nested format to the provided
     * PrintStream
     * @param sb TODO
     */
    public static final void parseDataDefinition(OMMDataDefs datadefs, short dbtype, StringBuffer sb, int tabLevel)
    {
        DataDefDictionary listDefDb = DataDefDictionary.create(dbtype);
        DataDefDictionary.decodeOMMDataDefs(listDefDb, datadefs);

        sb.append("DATA_DEFINITIONS ");
        for (Iterator listDefDbIter = listDefDb.iterator(); listDefDbIter.hasNext();)
        {
            DataDef listdef = (DataDef)listDefDbIter.next();

            sb.append("Count: "+listdef.getCount()+" DefId: "+listdef.getDataDefId());

            if (dbtype == OMMTypes.ELEMENT_LIST_DEF_DB)
            {
                for (Iterator listdefIter = listdef.iterator(); listdefIter.hasNext();)
                {
                    ElementEntryDef ommEntry = (ElementEntryDef)listdefIter.next();
                    dumpIndent(sb, tabLevel + 1);
                    sb.append("ELEMENT_ENTRY_DEF "
                    		+"Name: "+ommEntry.getName()
                    		+" Type: "+OMMTypes.toString(ommEntry.getDataType()));
                }
            }
            else
            {
                for (Iterator listdefIter = listdef.iterator(); listdefIter.hasNext();)
                {
                    FieldEntryDef ommEntry = (FieldEntryDef)listdefIter.next();
                    
                    sb.append("FIELD_ENTRY_DEF "
                    		+	"FID: "+ommEntry.getFieldId()
                    		+" Type: "+OMMTypes.toString(ommEntry.getDataType()));
                }
            }
        }
    }

    private static void dumpRespTypeNum(OMMMsg msg, StringBuffer logMsg)
    {
        if (msg.getMsgType() == OMMMsg.MsgType.REFRESH_RESP)
        {
        	logMsg.append(" (" + OMMMsg.RespType.toString(msg.getRespTypeNum()) + ")");
        }
        else
        // msg.getMsgType() == OMMMsg.OMMMsg.MsgType.UPDATE_RESP
        {
            if ((msg.getMsgModelType() >= RDMMsgTypes.MARKET_PRICE)
                    && (msg.getMsgModelType() <= RDMMsgTypes.HISTORY))
            {
            	logMsg.append(" (" + RDMInstrument.Update.toString(msg.getRespTypeNum()) + ")");
            }
        }
    }

    /**
     * parse data and print it in a table-nested format to the System.out
     */

//    public static final void parse(OMMData data,Element field)
//    {
//        parseData(data, null, 0,field,false);
//    }

    private static final void parseAggregate(OMMData data, StringBuffer logMsg, int tabLevel,Element fields,boolean ripple,String itemName,byte msgType)
    {
    	Map<Short,Element> rippleMap = new HashMap<Short,Element>();
        parseAggregateHeader(data, logMsg, tabLevel,fields);
        int fieldNum = 0;
        for (Iterator iter = ((OMMIterable)data).iterator(); iter.hasNext();)
        {
        	fieldNum++;
//        	field = fields.addElement("Field");
//        	logMsg.append("\n");
            OMMEntry entry = (OMMEntry)iter.next();
            parseEntry(entry, logMsg, tabLevel + 1,fields,ripple,itemName,msgType,rippleMap);
        }
        _logger.debug("test for cloud wei```````````````````````fieldNub is "+fieldNum);
    }

    /**
     * parse data and print it in a table-nested format to the provided
     * PrintStream
     * data is OMMMessage Attribute
     */
    public static final void parseData(OMMData data, StringBuffer logMsg, int tabLevel,Element fieldsElement,boolean ripple,String itemName,byte msgType)
    {
        if (data.isBlank())
        	logMsg.append("\n");
        else if (OMMTypes.isAggregate(data.getType()))
            parseAggregate(data, logMsg, tabLevel + 1,fieldsElement,ripple,itemName,msgType);
        else if ((data.getType() == OMMTypes.RMTES_STRING)
                && ((OMMDataBuffer)data).hasPartialUpdates())
        {
            Iterator iter = ((OMMDataBuffer)data).partialUpdateIterator();
            while (true)
            {
                OMMDataBuffer partial = (OMMDataBuffer)iter.next();
                logMsg.append("hpos: "+partial.horizontalPosition()+", "+partial.toString());
                if (iter.hasNext())
                	logMsg.append("  |  ");
                else
                    break;
            }
            logMsg.append("\n");
        }
        else if (data.getType() == OMMTypes.ANSI_PAGE)
        {
            // process ANSI with com.reuters.rfa.ansipage
            parseAnsiPageData(data, logMsg, tabLevel);
        }
        else if (data.getType() == OMMTypes.BUFFER || data.getType() == OMMTypes.OPAQUE_BUFFER)
        {
            if (data.getEncodedLength() <= 20)
            {
            	dumpIndent(logMsg, tabLevel + 1);
                // for small strings, print hex and try to print ASCII
            	logMsg.append(HexDump.toHexString(((OMMDataBuffer)data).getBytes(), false)+" | "+data);
            }
            else
            {
                if (INTERNAL_DEBUG)
                {
                	logMsg.append("Hex Format and Data Bytes: ");
                	logMsg.append(HexDump.hexDump(((OMMDataBuffer)data).getBytes(), 50));

                	logMsg.append("Hex Format: ");
                }

                int lineSize = 32;
                String s = HexDump.toHexString(((OMMDataBuffer)data).getBytes(), false);

                int j = 0;
                while (j < s.length())
                {
                    if (j != 0){
                    	logMsg.append("\n");
                    }

                    int end = j + lineSize;
                    if (end >= s.length())
                        end = s.length();

                    for (int i = j; i < end; i++)
                    {
                    	logMsg.append(s.charAt(i));
                    }
                    j = j + lineSize;
                }

                logMsg.append("Data Bytes: ");
                logMsg.append(data.toString());
            }
        }
        else if (data.getType() == OMMTypes.MSG)
        {
            parseMsg((OMMMsg)data, logMsg, tabLevel + 1,fieldsElement,itemName);
        }
        else
        {
            try
            {
            	fieldsElement.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_VALUE_NODE).addText(data.toString());
            	logMsg.append(data.toString());
            }
            catch (Exception e)
            {
                byte[] rawdata = data.getBytes();
                logMsg.append(HexDump.hexDump(rawdata));
            }
        }
    }
    
    /**
     * parse data and print it in a table-nested format to the provided
     * PrintStream
     * data is OMMMessage Attribute
     */
    public static final void parseData(OMMData data, StringBuffer logMsg, int tabLevel,Element fieldsElement,boolean ripple)
    {
        if (data.isBlank())
        	logMsg.append("\n");
        else if (OMMTypes.isAggregate(data.getType()))
            parseAggregate(data, logMsg, tabLevel + 1,fieldsElement,ripple,"",(byte)0);
        else if ((data.getType() == OMMTypes.RMTES_STRING)
                && ((OMMDataBuffer)data).hasPartialUpdates())
        {
            Iterator iter = ((OMMDataBuffer)data).partialUpdateIterator();
            while (true)
            {
                OMMDataBuffer partial = (OMMDataBuffer)iter.next();
                logMsg.append("hpos: "+partial.horizontalPosition()+", "+partial.toString());
                if (iter.hasNext())
                	logMsg.append("  |  ");
                else
                    break;
            }
            logMsg.append("\n");
        }
        else if (data.getType() == OMMTypes.ANSI_PAGE)
        {
            // process ANSI with com.reuters.rfa.ansipage
            parseAnsiPageData(data, logMsg, tabLevel);
        }
        else if (data.getType() == OMMTypes.BUFFER || data.getType() == OMMTypes.OPAQUE_BUFFER)
        {
            if (data.getEncodedLength() <= 20)
            {
            	dumpIndent(logMsg, tabLevel + 1);
                // for small strings, print hex and try to print ASCII
            	logMsg.append(HexDump.toHexString(((OMMDataBuffer)data).getBytes(), false)+" | "+data);
            }
            else
            {
                if (INTERNAL_DEBUG)
                {
                	logMsg.append("Hex Format and Data Bytes: ");
                	logMsg.append(HexDump.hexDump(((OMMDataBuffer)data).getBytes(), 50));

                	logMsg.append("Hex Format: ");
                }

                int lineSize = 32;
                String s = HexDump.toHexString(((OMMDataBuffer)data).getBytes(), false);

                int j = 0;
                while (j < s.length())
                {
                    if (j != 0){
                    	logMsg.append("\n");
                    }

                    int end = j + lineSize;
                    if (end >= s.length())
                        end = s.length();

                    for (int i = j; i < end; i++)
                    {
                    	logMsg.append(s.charAt(i));
                    }
                    j = j + lineSize;
                }

                logMsg.append("Data Bytes: ");
                logMsg.append(data.toString());
            }
        }
        else if (data.getType() == OMMTypes.MSG)
        {
            parseMsg((OMMMsg)data, logMsg, tabLevel + 1,fieldsElement,"");
        }
        else
        {
            try
            {
            	fieldsElement.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_VALUE_NODE).addText(data.toString());
            	logMsg.append(data.toString());
            }
            catch (Exception e)
            {
                byte[] rawdata = data.getBytes();
                logMsg.append(HexDump.hexDump(rawdata));
            }
        }
    }

    private static final void parseAggregateHeader(OMMData data, StringBuffer logMsg, int tabLevel,Element fields )
    {
    	dumpIndent(logMsg, tabLevel);
        short dataType = data.getType();
        logMsg.append(OMMTypes.toString(dataType));
        switch (dataType)
        {
            case OMMTypes.FIELD_LIST:
            {
                // set DICTIONARY to the dictId for this field list
                OMMFieldList fieldList = (OMMFieldList)data;
                int dictId = fieldList.getDictId();
                CURRENT_DICTIONARY = getDictionary(dictId);
            }
                break;
            case OMMTypes.SERIES:
            {
                OMMSeries s = (OMMSeries)data;
                if (s.has(OMMSeries.HAS_SUMMARY_DATA))
                {
                	dumpIndent(logMsg, tabLevel+1);
                	logMsg.append("SUMMARY");
                    parseData(s.getSummaryData(), logMsg, tabLevel + 1,fields,false);
                }
                if (s.has(OMMSeries.HAS_DATA_DEFINITIONS))
                {
                	dumpIndent(logMsg, tabLevel+1);
                    short dbtype = s.getDataType() == OMMTypes.FIELD_LIST ? OMMTypes.FIELD_LIST_DEF_DB
                            : OMMTypes.ELEMENT_LIST_DEF_DB;
                    parseDataDefinition(s.getDataDefs(), dbtype,  logMsg, tabLevel + 1);
                }
            }
                break;
            case OMMTypes.MAP:
            {
                OMMMap s = (OMMMap)data;

                String flagsString = ExampleUtil.mapFlagsString(s);
                dumpIndent(logMsg, tabLevel);
                logMsg.append("flags: "+flagsString);

                if (s.has(OMMMap.HAS_SUMMARY_DATA))
                {
                	dumpIndent(logMsg, tabLevel);
                	logMsg.append("SUMMARY");
                    parseData(s.getSummaryData(), logMsg, tabLevel + 1,fields,false);
                }
            }
                break;
            case OMMTypes.VECTOR:
            {
                OMMVector s = (OMMVector)data;

                String flagsString = ExampleUtil.vectorFlagsString(s);
                dumpIndent(logMsg, tabLevel);
                logMsg.append("flags: "+flagsString);

                if (s.has(OMMVector.HAS_SUMMARY_DATA))
                {
                	dumpIndent(logMsg, tabLevel+1);
                	logMsg.append("SUMMARY");
                    parseData(s.getSummaryData(), logMsg, tabLevel + 1,fields,false);
                }
            }
                break;
            case OMMTypes.FILTER_LIST:
            {
                OMMFilterList s = (OMMFilterList)data;

                String flagsString = ExampleUtil.filterListFlagsString(s);
                dumpIndent(logMsg, tabLevel);
                logMsg.append("flags: "+flagsString);
            }
                break;
        }
    }

    private static final void dumpBlank(StringBuilder sb)
    {
    	sb.append('\n');
    }

    private static final void dumpIndent(StringBuffer sb, int tabLevel)
    {
    	sb.append("\n");
        for (int i = 0; i < tabLevel; i++)
        	sb.append("\t");
    }
    
    

    private static final void parseEntry(OMMEntry entry, StringBuffer logMsg, int tabLevel,Element fields,boolean ripple,String itemName,byte msgType,Map<Short,Element> rippleMap)
 {
		OMMFieldEntry fe = (OMMFieldEntry) entry;
		FidDef fiddef = CURRENT_DICTIONARY.getFidDef(fe.getFieldId());
		// add the ripple data
		FieldValue fieldValue = getValue(itemName, fiddef.getFieldId());
		if(fieldValue==null){
			//Strange:在第一订阅该产品的map中无法找到该fieldId对应的fieldValue,不应该存在的逻辑
			fieldValue=new FieldValue(null, fiddef);
			fieldValue.update(fe);
			_logger.debug("The fieldValue which can not be found is:"+fieldValue);
			DataBaseCache.filedValueMap.get(itemName).put(fiddef.getFieldId(), fieldValue);
		}
		short fId = fieldValue.getFieldId();
		Element ele =rippleMap.get(fId);
		if(ele !=null){
			fields.remove(ele);
		}
		Short rippleId = fiddef.getRippleFieldId();

		if (msgType == MsgType.UPDATE_RESP) {
			if (ripple && rippleId != 0) {
				FidDef rippleFieldDef = fiddef;
				//得到当前列的值
				Object tmp = fieldValue.getStringValue();
				//如果当前列有引用(ripple	)的列,那从缓存中取出保存的引用列的上次的值对象.
				while ((rippleFieldDef.getRippleFieldId() != 0)
						&& ((fieldValue = getValue(itemName, rippleFieldDef.getRippleFieldId())) != null)) {
					
					short fieldId = fieldValue.getFieldId();
					//避免重复,删除掉原有的可能存在缓存中的Element的值.即使没有删除.该值也不影响客户端数据的有效性.
					Element e = rippleMap.get(fieldId);
					if(e !=null){
						rippleMap.remove(fieldId);
						boolean removeResult = fields.remove(e);
						_logger.info("remove result "+removeResult);
					}
					Element rippleField = fields.addElement("Field");
					FidDef rippleDef = CURRENT_DICTIONARY.getFidDef(rippleFieldDef.getRippleFieldId());
					dumpFieldEntryHeader(fieldValue, rippleDef, logMsg, tabLevel + 1, rippleField);
					rippleField.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_TYPE_NODE).addText(
							RFATypeConvert.convertField(OMMTypes.toString(fieldValue.getOMMType())));
					logMsg.append(tmp.toString());
					//使用XAU=时,Bid1和Ask1既是Ripple的field,RFA又同时传送过来了相应的值.避免重复.
					Element tmpElement=rippleField.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_VALUE_NODE).addText(tmp.toString());
					rippleMap.put(fieldId, rippleField);
					tmp = fieldValue.setValue(tmp);
					rippleFieldDef = CURRENT_DICTIONARY.getFidDef(fieldId);
				}
			}
		}
	
		
		Element elementField = fields.addElement("Field");
		try {
			switch (entry.getType()) {
			case OMMTypes.FIELD_ENTRY: {
				if (CURRENT_DICTIONARY != null) {
					if (fiddef != null) {
						dumpFieldEntryHeader(fe, fiddef, logMsg, tabLevel + 1, elementField);
						OMMData data = null;
						if (fe.getDataType() == OMMTypes.UNKNOWN)
							data = fe.getData(fiddef.getOMMType());
						else
							// defined data already has type
							data = fe.getData();
						if (rippleId > 0) {
							// 这里需要记录该field最新的值到map中,方便下次直接读取
							FieldValue firstFieldValue = DataBaseCache.filedValueMap.get(itemName).get(
									fe.getFieldId());
							firstFieldValue.setValue(data.toString());
						}
						// DictionaryConverter.
						// logMsg.append(" data type="+OMMTypes.toString(data.getType())+" ");
						elementField.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_TYPE_NODE).addText(
								RFATypeConvert.convertField(OMMTypes.toString(data.getType())));
						if (data.getType() == OMMTypes.ENUM) {
							String aa = CURRENT_DICTIONARY.expandedValueFor(fiddef.getFieldId(),
									((OMMEnum) data).getValue());
							dumpIndent(logMsg, tabLevel);
							logMsg.append(aa);
							elementField.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_VALUE_NODE).addText(aa);
							// logMsg.append(CURRENT_DICTIONARY.expandedValueFor(fiddef.getFieldId(),
							// ((OMMEnum)data).getValue()));
						} else
							parseData(data, logMsg, tabLevel, elementField, ripple);
					} else {
						dumpIndent(logMsg, tabLevel);
						logMsg.append("Received field id: " + fe.getFieldId() + " - Not defined in dictionary");
					}
				} else {
					dumpFieldEntryHeader(fe, null, logMsg, tabLevel, elementField);
					if (fe.getDataType() == OMMTypes.UNKNOWN) {
						OMMDataBuffer data = (OMMDataBuffer) fe.getData();
						dumpIndent(logMsg, tabLevel);
						logMsg.append(HexDump.toHexString(data.getBytes(), false));
					} else
					// defined data already has type
					{
						OMMData data = fe.getData();
						parseData(data, logMsg, tabLevel, elementField, ripple);
					}
				}
			}
				break;
			case OMMTypes.ELEMENT_ENTRY:
				dumpElementEntryHeader((OMMElementEntry) entry, logMsg, tabLevel + 1, elementField);
				parseData(entry.getData(), logMsg, tabLevel, elementField, false);
				break;
			case OMMTypes.MAP_ENTRY:
				dumpMapEntryHeader((OMMMapEntry) entry, logMsg, tabLevel, elementField);
				if ((((OMMMapEntry) entry).getAction() != OMMMapEntry.Action.DELETE)
						&& entry.getDataType() != OMMTypes.NO_DATA)
					parseData(entry.getData(), logMsg, tabLevel, elementField, false);
				break;
			case OMMTypes.VECTOR_ENTRY:
				dumpVectorEntryHeader((OMMVectorEntry) entry, logMsg, tabLevel);
				if ((((OMMVectorEntry) entry).getAction() != OMMVectorEntry.Action.DELETE)
						&& (((OMMVectorEntry) entry).getAction() != OMMVectorEntry.Action.CLEAR))
					parseData(entry.getData(), logMsg, tabLevel, elementField, false);
				break;
			case OMMTypes.FILTER_ENTRY:
				dumpFilterEntryHeader((OMMFilterEntry) entry, logMsg, tabLevel);
				if (((OMMFilterEntry) entry).getAction() != OMMFilterEntry.Action.CLEAR)
					parseData(entry.getData(), logMsg, tabLevel, elementField, false);
				break;
			default:
				dumpEntryHeader(entry, logMsg, tabLevel);
				parseData(entry.getData(), logMsg, tabLevel, elementField, false);
				break;
			}
		} catch (OMMException e) {
			logMsg.append("ERROR Invalid data: " + e.getMessage());
		}
 }

    private static FieldValue getValue(String itemName,short filedId) {
    	Map<Short, FieldValue> filedId2FieldValueMap=DataBaseCache.filedValueMap.get(itemName);
    	FieldValue fieldValue = filedId2FieldValueMap.get(filedId);
    	if(fieldValue == null){
			_logger.debug("FiledValue not found in map by filedId!!!!"+filedId);
		}
    	return fieldValue;
	}

	private static final void dumpEntryHeader(OMMEntry entry, StringBuffer logMsg, int tabLevel)
    {
        
    	logMsg.append(" \n"+OMMTypes.toString(entry.getType())+": ");
        if (entry.getType() == OMMTypes.SERIES_ENTRY)
        	logMsg.append("  ");
        // else array entry value is on same line
    }

    private static final void dumpFieldEntryHeader(OMMFieldEntry entry, FidDef def, StringBuffer logMsg,
            int tabLevel,Element field)
    {
    	
    	field.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_ID_NODE).addText(String.valueOf(entry.getFieldId()));
    	dumpIndent(logMsg,tabLevel);
        logMsg.append(OMMTypes.toString(entry.getType())+" "+entry.getFieldId());
        if (def == null)
        {
        	logMsg.append(": ");
        }
        else
        {
        	field.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_NAME_NODE).addText(def.getName());
        	dumpIndent(logMsg,tabLevel);
        	logMsg.append("/");
        	logMsg.append(def.getName()); 
        	logMsg.append(": ");
            if ((def.getOMMType() >= OMMTypes.BASE_FORMAT) || (def.getOMMType() == OMMTypes.ARRAY))
            	dumpIndent(logMsg,tabLevel);
        }
    }
    
    private static final void dumpFieldEntryHeader(FieldValue fieldValue, FidDef def, StringBuffer logMsg,
            int tabLevel,Element field)
    {
    	
    	field.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_ID_NODE).addText(String.valueOf(fieldValue.getFieldId()));
    	dumpIndent(logMsg,tabLevel);
        logMsg.append(OMMTypes.toString(fieldValue.getOMMType())+" "+fieldValue.getFieldId());
        if (def == null)
        {
        	logMsg.append(": ");
        }
        else
        {
        	field.addElement(RFANodeconstant.RESPONSE_FIELDS_FIELD_NAME_NODE).addText(def.getName());
        	dumpIndent(logMsg,tabLevel);
        	logMsg.append("/");
        	logMsg.append(def.getName()); 
        	logMsg.append(": ");
            if ((def.getOMMType() >= OMMTypes.BASE_FORMAT) || (def.getOMMType() == OMMTypes.ARRAY))
            	dumpIndent(logMsg,tabLevel);
        }
    }
    

	private static final void dumpElementEntryHeader(OMMElementEntry entry, StringBuffer logMsg, int tabLevel,
			Element field) {
		dumpIndent(logMsg, tabLevel);
		logMsg.append(OMMTypes.toString(entry.getType()) + " " + entry.getName() + ": ");
		field.addAttribute(OMMTypes.toString(entry.getType()), entry.getName());
		if ((entry.getDataType() >= OMMTypes.BASE_FORMAT) || (entry.getDataType() == OMMTypes.ARRAY))
			logMsg.append("\n  ");
	}

    private static final void dumpFilterEntryHeader(OMMFilterEntry entry, StringBuffer logMsg,
            int tabLevel)
    {
        
        logMsg.append(OMMTypes.toString(entry.getType()));
        logMsg.append(" ");
        logMsg.append(entry.getFilterId());
        logMsg.append(" (");
        logMsg.append(OMMFilterEntry.Action.toString(entry.getAction()));
        if (entry.has(OMMFilterEntry.HAS_PERMISSION_DATA))
        	logMsg.append(", HasPermissionData");
        if (entry.has(OMMFilterEntry.HAS_DATA_FORMAT))
            logMsg.append(", HasDataFormat");
        logMsg.append(") : ");
        logMsg.append("\n ");
        String flagsString = ExampleUtil.filterEntryFlagsString(entry);
        
        logMsg.append("\n  flags: "+flagsString);

    }

    private static final void dumpMapEntryHeader(OMMMapEntry entry, StringBuffer logMsg, int tabLevel,Element field)
    {
        
        logMsg.append(OMMTypes.toString(entry.getType()));
        logMsg.append(" (");
        logMsg.append(OMMMapEntry.Action.toString(entry.getAction()));
        if (entry.has(OMMMapEntry.HAS_PERMISSION_DATA))
        	logMsg.append(", HasPermissionData");
        logMsg.append(") : ");
        logMsg.append("\n ");
        String flagsString = ExampleUtil.mapEntryFlagsString(entry);
        
        logMsg.append("\n flags: "+flagsString);

        
        logMsg.append("\nKey: ");
        parseData(entry.getKey(), logMsg, 0,field,false);
        
        logMsg.append("\n Value: ");
    }

    private static final void dumpVectorEntryHeader(OMMVectorEntry entry, StringBuffer logMsg,
            int tabLevel)
    {
        
        logMsg.append(OMMTypes.toString(entry.getType()));
        logMsg.append(" ");
        logMsg.append(entry.getPosition());
        logMsg.append(" (");
        logMsg.append(OMMVectorEntry.Action.vectorActionString(entry.getAction()));
        if (entry.has(OMMVectorEntry.HAS_PERMISSION_DATA))
        	logMsg.append(", HasPermissionData");
        logMsg.append(") : ");
        logMsg.append("\n ");
        String flagsString = ExampleUtil.vectorEntryFlagsString(entry);
        

        logMsg.append("\n flags: "+flagsString);

    }

    public static final void parseAnsiPageData(OMMData data, StringBuffer logMsg, int tabLevel)
    {
        boolean newPage = false;
        if (CURRENT_PAGE == null)
        {
            CURRENT_PAGE = new Page();
            newPage = true;
        }

        Vector<PageUpdate> pageUpdates = new Vector<PageUpdate>();
        ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
        CURRENT_PAGE.decode(bais, pageUpdates);
        if (newPage)
        	logMsg.append(CURRENT_PAGE.toString()); // print the page if it is a refresh message
        else
        {
            // print the update string
            Iterator<PageUpdate> iter = pageUpdates.iterator();
            while (iter.hasNext())
            {
                PageUpdate u = (PageUpdate)iter.next();
                StringBuilder buf = new StringBuilder(80);
                for (short k = u.getBeginningColumn(); k < u.getEndingColumn(); k++)
                {
                    buf.append(CURRENT_PAGE.getChar(u.getRow(), k));
                }
                if (!(buf.toString()).equalsIgnoreCase(""))
                {
                    
                	logMsg.append("\n Update String: " + buf.toString() + " (Row: " + u.getRow()
                            + ", Begin Col: " + u.getBeginningColumn() + ", End Col: "
                            + u.getEndingColumn() + ")");
                }
            }
        }
    }

}
