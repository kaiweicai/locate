package com.locate.rmds.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.locate.rmds.QSConsumerProxy;
import com.locate.rmds.parser.GenericOMMParser;
import com.reuters.rfa.common.Event;
import com.reuters.rfa.dictionary.FidDef;
import com.reuters.rfa.dictionary.FieldDictionary;
import com.reuters.rfa.omm.OMMAttribInfo;
import com.reuters.rfa.omm.OMMData;
import com.reuters.rfa.omm.OMMEnum;
import com.reuters.rfa.omm.OMMFieldEntry;
import com.reuters.rfa.omm.OMMFieldList;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMNumeric;
import com.reuters.rfa.omm.OMMTypes;
import com.reuters.rfa.session.omm.OMMItemEvent;

public class EventFieldUtils {

	 static Logger _logger = Logger.getLogger(EventFieldUtils.class.getName());
	   
    public static FieldDictionary dictionary = QSConsumerProxy.dictionary;
    
    public static short getFidDef(String defineName){
    	short filedId = 0;
    	FidDef def = dictionary.getFidDef(defineName);
    	if (def != null) {
    		filedId = def.getFieldId();
		}
    	return filedId;
    }
    
	public static String getEventField(Event event,short fid){
    	OMMData data = getFieldData(event,fid);
    	String value = null;
        if(data != null)
        {
        	FidDef fiddef = dictionary.getFidDef(fid);
        	String type = OMMTypes.toString(data.getType());
        	if(fiddef.getOMMType() == OMMTypes.ENUM){
        		value = dictionary.expandedValueFor(fid, ((OMMEnum)data).getValue());
        	}else
        		value = data.toString();
        	type = RFATypeConvert.convertField(type);
        	return value;
        }

        return null;
    }
    
	public static OMMData getFieldData(Event event,short fid)
    {
        OMMItemEvent ie = (OMMItemEvent) event;
        OMMMsg msg = ie.getMsg();
        if (msg.getDataType() == OMMTypes.FIELD_LIST)
        {
            OMMFieldEntry fe = ((OMMFieldList)msg.getPayload()).find(fid);
            if (fe == null)
                return null;
            short type = dictionary.getFidDef(fid).getOMMType();
            return fe.getData(type);
        }
        _logger.error("OMMMsg type not find defined for "+fid);
        throw new IllegalArgumentException("OMMMsg payload must be field list");
    }
    
    public static int getFieldBytes(Event event,short fid, byte[] dest, int offset)
    {
        OMMData data = getFieldData(event,fid);
        if (data != null)
            return data.getBytes(dest, offset);
        return 0;
    }
    
    
    public static byte[] getPayloadBytes(OMMItemEvent event)
    {
        OMMMsg msg = event.getMsg();
        if (msg.getDataType() != OMMTypes.NO_DATA)
            return msg.getPayload().getBytes();
        return new byte[0];
    }

    public static OMMData getFieldData(OMMItemEvent event,short fid)
    {
        OMMMsg msg = event.getMsg();
        if (msg.getDataType() == OMMTypes.FIELD_LIST)
        {
            OMMFieldEntry fe = ((OMMFieldList)msg.getPayload()).find(fid);
            if (fe == null)
                return null;
            short type = dictionary.getFidDef(fid).getOMMType();
            return fe.getData(type);
        }
        throw new IllegalArgumentException("OMMMsg payload must be field list");
    }

    public static String getFieldString(OMMItemEvent event,short fid)
    {
        OMMData data = getFieldData(event,fid);
        if(data != null)
        {
        	FidDef fiddef = dictionary.getFidDef(fid);
        	if(fiddef.getOMMType() == OMMTypes.ENUM)
        		return dictionary.expandedValueFor(fid, ((OMMEnum)data).getValue());
        	else
        		return data.toString();
        }

        return null;
    }

    public static int getFieldInt(OMMItemEvent event,short fid, int defaultValue)
    {
        OMMData data = getFieldData(event,fid);
        if (data != null)
            return (int)((OMMNumeric)data).toLong();
        return defaultValue;
    }

    public static double getFieldDouble(OMMItemEvent event,short fid, double defaultValue)
    {
        OMMData data = getFieldData(event,fid);
        if (data != null)
            return ((OMMNumeric)data).toDouble();
        return defaultValue;
    }

    public static int getFieldBytes(OMMItemEvent event,short fid, byte[] dest, int offset)
    {
        OMMData data = getFieldData(event,fid);
        if (data != null)
            return data.getBytes(dest, offset);
        return 0;
    }
    
    public static String getItemName(OMMItemEvent event)
    {
        OMMMsg msg = event.getMsg();
        if (msg.has(OMMMsg.HAS_ATTRIB_INFO))
        {
            OMMAttribInfo ai = msg.getAttribInfo();
            if (ai.has(OMMAttribInfo.HAS_NAME))
                return ai.getName();
        }
        return "";
    }

    
    @Test
    public void testDictionary(){
    	PropertyConfigurator.configureAndWatch("config/log4j.properties");
    	dictionary = GenericOMMParser.initializeDictionary(
				"config/RDM/RDMFieldDictionary", "config/RDM/enumtype.def");
    	
    	
    	FidDef def = dictionary.getFidDef("BCAST_TEXT");
		if (def != null) {
			short BCAST_TEXT = def.getFieldId();
			int BCAST_TEXT_LENGTH = dictionary.isOMM() ? def.getMaxOMMLengthAsInt() : def
					.getMaxMfeedLengthAsInt();
			String longName = def.getLongName();
			String name= def.getName();
			String rippleName = def.getRippleName();
			
			System.out.println(BCAST_TEXT);
			System.out.println(BCAST_TEXT_LENGTH);
			System.out.println(longName);
			System.out.println(name);
			System.out.println(rippleName);
		}
		

//		def = dictionary.getFidDef("DSPLY_NAME");
//		if (def != null) {
//			DSPLY_NAME = def.getFieldId();
//		}
//
//		def = dictionary.getFidDef("AREA_ID");
//		if (def != null) {
//			AREA_ID = def.getFieldId();
//		}
//
//		def = dictionary.getFidDef("PNAC");
//		if (def != null) {
//			PNAC = def.getFieldId();
//		}
//
//		def = dictionary.getFidDef("ATTRIBTN");
//		if (def != null) {
//			ATTRIBTN = def.getFieldId();
//		}
//
//		def = dictionary.getFidDef("PROD_CODE");
//		if (def != null) {
//			PROD_CODE = def.getFieldId();
//		}
//
//		def = dictionary.getFidDef("TOPIC_CODE");
//		if (def != null) {
//			TOPIC_CODE = def.getFieldId();
//		}
//
//		def = dictionary.getFidDef("CO_IDS");
//		if (def != null) {
//			CO_IDS = def.getFieldId();
//		}
//
//		def = dictionary.getFidDef("LANG_IND");
//		if (def != null) {
//			LANG_IND = def.getFieldId();
//		}
//
//		def = dictionary.getFidDef("STORY_TIME");
//		if (def != null) {
//			STORY_TIME = def.getFieldId();
//		}
//		
//		def = dictionary.getFidDef("STORY_DATE");
//		if (def != null) {
//			STORY_DATE = def.getFieldId();
//		}
//		
//		def = dictionary.getFidDef("SF_NAME");
//		if (def != null) {
//			SF_NAME = def.getFieldId();
//		}
    }
}
