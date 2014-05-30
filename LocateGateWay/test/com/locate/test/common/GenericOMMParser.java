/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.locate.test.common;

import com.locate.rmds.util.ExampleUtil;
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
import com.reuters.rfa.omm.OMMFilterEntry.Action;
import com.reuters.rfa.omm.OMMFilterList;
import com.reuters.rfa.omm.OMMIterable;
import com.reuters.rfa.omm.OMMMap;
import com.reuters.rfa.omm.OMMMapEntry;
import com.reuters.rfa.omm.OMMMsg;
import com.reuters.rfa.omm.OMMMsg.Indication;
import com.reuters.rfa.omm.OMMMsg.MsgType;
import com.reuters.rfa.omm.OMMMsg.RespType;
import com.reuters.rfa.omm.OMMPriority;
import com.reuters.rfa.omm.OMMSeries;
import com.reuters.rfa.omm.OMMTypes;
import com.reuters.rfa.omm.OMMVector;
import com.reuters.rfa.omm.OMMVectorEntry;
import com.reuters.rfa.rdm.RDMDictionary;
import com.reuters.rfa.rdm.RDMDictionary.Filter;
import com.reuters.rfa.rdm.RDMInstrument;
import com.reuters.rfa.rdm.RDMInstrument.NameType;
import com.reuters.rfa.rdm.RDMInstrument.Update;
import com.reuters.rfa.rdm.RDMMsgTypes;
import com.reuters.rfa.rdm.RDMService;
import com.reuters.rfa.rdm.RDMUser;
import com.reuters.rfa.utility.HexDump;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public final class GenericOMMParser
{
  private static HashMap<Integer, FieldDictionary> DICTIONARIES = new HashMap();
  private static FieldDictionary CURRENT_DICTIONARY;
  private static Page CURRENT_PAGE;
  private static boolean INTERNAL_DEBUG = false;

  public static void initializeDictionary(String fieldDictionaryFilename, String enumDictionaryFilename)
    throws DictionaryException
  {
    FieldDictionary dictionary = FieldDictionary.create();
    try
    {
      FieldDictionary.readRDMFieldDictionary(dictionary, fieldDictionaryFilename);
      System.out.println("field dictionary read from RDMFieldDictionary file");

      FieldDictionary.readEnumTypeDef(dictionary, enumDictionaryFilename);
      System.out.println("enum dictionary read from enumtype.def file");

      initializeDictionary(dictionary);
    }
    catch (DictionaryException e)
    {
      throw new DictionaryException(new StringBuilder().append("ERROR: Check if files ").append(fieldDictionaryFilename).append(" and ").append(enumDictionaryFilename).append(" exist and are readable.").toString(), e);
    }
  }

  public static synchronized void initializeDictionary(FieldDictionary dict)
  {
    int dictId = dict.getDictId();
    if (dictId == 0)
      dictId = 1;
    DICTIONARIES.put(new Integer(dictId), dict);
  }

  public static FieldDictionary getDictionary(int dictId)
  {
    if (dictId == 0)
      dictId = 1;
    return ((FieldDictionary)DICTIONARIES.get(new Integer(dictId)));
  }

  public static final void parse(OMMMsg msg)
  {
    parseMsg(msg, System.out);
  }

  private static final String hintString(OMMMsg msg)
  {
    StringBuilder buf = new StringBuilder(60);

    boolean bAppend = true;

    if (msg.has(4))
    {
      bAppend = append(buf, "HAS_ATTRIB_INFO", bAppend);
    }
    if (msg.has(512))
    {
      bAppend = append(buf, "HAS_CONFLATION_INFO", bAppend);
    }
    if (msg.has(1))
    {
      bAppend = append(buf, "HAS_HEADER", bAppend);
    }
    if (msg.has(32))
    {
      bAppend = append(buf, "HAS_ITEM_GROUP", bAppend);
    }
    if (msg.has(128))
    {
      bAppend = append(buf, "HAS_PERMISSION_DATA", bAppend);
    }
    if (msg.has(2))
    {
      bAppend = append(buf, "HAS_PRIORITY", bAppend);
    }
    if (msg.has(8))
    {
      bAppend = append(buf, "HAS_QOS", bAppend);
    }
    if (msg.has(16))
    {
      bAppend = append(buf, "HAS_QOS_REQ", bAppend);
    }
    if (msg.has(1024))
    {
      bAppend = append(buf, "HAS_RESP_TYPE_NUM", bAppend);
    }
    if (msg.has(256))
    {
      bAppend = append(buf, "HAS_SEQ_NUM", bAppend);
    }
    if (msg.has(4096))
    {
      bAppend = append(buf, "HAS_ID", bAppend);
    }
    if (msg.has(8192))
    {
      bAppend = append(buf, "HAS_PUBLISHER_INFO", bAppend);
    }
    if (msg.has(64))
    {
      bAppend = append(buf, "HAS_STATE", bAppend);
    }

    return buf.toString();
  }

  private static boolean append(StringBuilder buf, String str, boolean first)
  {
    if (!(first))
    {
      buf.append(" | ");
      first = false;
    }
    else {
      first = false;
    }
    buf.append(str);
    return first;
  }

  public static final void parseMsg(OMMMsg msg, PrintStream ps)
  {
    parseMsg(msg, ps, 0);
  }

  static final void parseMsg(OMMMsg msg, PrintStream ps, int tabLevel)
  {
    msg.getMsgType();
    dumpIndent(ps, tabLevel);
    ps.println("MESSAGE");
    dumpIndent(ps, tabLevel + 1);
    ps.println(new StringBuilder().append("Msg Type: ").append(OMMMsg.MsgType.toString(msg.getMsgType())).toString());
    dumpIndent(ps, tabLevel + 1);
    ps.println(new StringBuilder().append("Msg Model Type: ").append(RDMMsgTypes.toString(msg.getMsgModelType())).toString());
    dumpIndent(ps, tabLevel + 1);
    ps.println(new StringBuilder().append("Indication Flags: ").append(OMMMsg.Indication.indicationString(msg)).toString());

    dumpIndent(ps, tabLevel + 1);
    ps.println(new StringBuilder().append("Hint Flags: ").append(hintString(msg)).toString());

    if ((msg.getDataType() == 134) && (msg.isSet(8)))
    {
      CURRENT_PAGE = null;
    }

    if (msg.has(64))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.println(new StringBuilder().append("State: ").append(msg.getState()).toString());
    }
    if (msg.has(2))
    {
      dumpIndent(ps, tabLevel + 1);
      OMMPriority p = msg.getPriority();
      if (p != null)
        ps.println(new StringBuilder().append("Priority: ").append(p.getPriorityClass()).append(",").append(p.getCount()).toString());
      else
        ps.println("Priority: Error flag recieved but there is not priority present");
    }
    if (msg.has(8))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.println(new StringBuilder().append("Qos: ").append(msg.getQos()).toString());
    }
    if (msg.has(16))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.println(new StringBuilder().append("QosReq: ").append(msg.getQosReq()).toString());
    }
    if (msg.has(32))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.println(new StringBuilder().append("Group: ").append(msg.getItemGroup()).toString());
    }
    if (msg.has(128))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.print(new StringBuilder().append("PermissionData: ").append(HexDump.toHexString(msg.getPermissionData(), false)).toString());
    }
    if (msg.has(256))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.println(new StringBuilder().append("SeqNum: ").append(msg.getSeqNum()).toString());
    }

    if (msg.has(512))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.println(new StringBuilder().append("Conflation Count: ").append(msg.getConflationCount()).toString());
      dumpIndent(ps, tabLevel + 1);
      ps.println(new StringBuilder().append("Conflation Time: ").append(msg.getConflationTime()).toString());
    }

    if (msg.has(1024))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.print(new StringBuilder().append("RespTypeNum: ").append(msg.getRespTypeNum()).toString());
      dumpRespTypeNum(msg, ps);
    }

    if (msg.has(4096))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.println(new StringBuilder().append("Id: ").append(msg.getId()).toString());
    }

    if ((msg.has(8192)) || (msg.getMsgType() == 11))
    {
      PublisherPrincipalIdentity pi = (PublisherPrincipalIdentity)msg.getPrincipalIdentity();
      if (pi != null)
      {
        dumpIndent(ps, tabLevel + 1);
        ps.println(new StringBuilder().append("Publisher Address: 0x").append(Long.toHexString(pi.getPublisherAddress())).toString());
        dumpIndent(ps, tabLevel + 1);
        ps.println(new StringBuilder().append("Publisher Id: ").append(pi.getPublisherId()).toString());
      }
    }

    if (msg.has(4))
    {
      dumpIndent(ps, tabLevel + 1);
      ps.println("AttribInfo");
      OMMAttribInfo ai = msg.getAttribInfo();
      if (ai.has(1))
      {
        dumpIndent(ps, tabLevel + 2);
        ps.println(new StringBuilder().append("ServiceName: ").append(ai.getServiceName()).toString());
      }
      if (ai.has(64))
      {
        dumpIndent(ps, tabLevel + 2);
        ps.println(new StringBuilder().append("ServiceId: ").append(ai.getServiceID()).toString());
      }
      if (ai.has(2))
      {
        dumpIndent(ps, tabLevel + 2);
        ps.println(new StringBuilder().append("Name: ").append(ai.getName()).toString());
      }
      if (ai.has(4))
      {
        dumpIndent(ps, tabLevel + 2);
        ps.print(new StringBuilder().append("NameType: ").append(ai.getNameType()).toString());
        if (msg.getMsgModelType() == 1)
        {
          ps.println(new StringBuilder().append(" (").append(RDMUser.NameType.toString(ai.getNameType())).append(")").toString());
        }
        else if (RDMInstrument.isInstrumentMsgModelType(msg.getMsgModelType()))
        {
          ps.println(new StringBuilder().append(" (").append(RDMInstrument.NameType.toString(ai.getNameType())).append(")").toString());
        }
        else
        {
          ps.println();
        }
      }
      if (ai.has(8))
      {
        dumpIndent(ps, tabLevel + 2);
        ps.print(new StringBuilder().append("Filter: ").append(ai.getFilter()).toString());
        if (msg.getMsgModelType() == 4)
        {
          ps.println(new StringBuilder().append(" (").append(RDMService.Filter.toString(ai.getFilter())).append(")").toString());
        }
        else if (msg.getMsgModelType() == 5)
        {
          ps.println(new StringBuilder().append(" (").append(RDMDictionary.Filter.toString(ai.getFilter())).append(")").toString());
        }
        else
        {
          ps.println();
        }
      }
      if (ai.has(16))
      {
        dumpIndent(ps, tabLevel + 2);
        ps.println(new StringBuilder().append("ID: ").append(ai.getId()).toString());
      }
      if (ai.has(32))
      {
        dumpIndent(ps, tabLevel + 2);
        ps.println("Attrib");
        parseData(ai.getAttrib(), ps, tabLevel + 2);
      }
    }

    dumpIndent(ps, tabLevel + 1);
    ps.print("Payload: ");
    if (msg.getDataType() != 128)
    {
      ps.println(new StringBuilder().append(msg.getPayload().getEncodedLength()).append(" bytes").toString());
      parseData(msg.getPayload(), ps, tabLevel + 1);
    }
    else
    {
      ps.println("None");
    }
  }

  public static final void parseDataDefinition(OMMDataDefs datadefs, short dbtype, PrintStream ps, int tabLevel)
  {
    DataDefDictionary listDefDb = DataDefDictionary.create(dbtype);
    DataDefDictionary.decodeOMMDataDefs(listDefDb, datadefs);

    ps.print("DATA_DEFINITIONS ");
    for (Iterator listDefDbIter = listDefDb.iterator(); listDefDbIter.hasNext(); )
    {
      DataDef listdef = (DataDef)listDefDbIter.next();

      ps.print("Count: ");
      ps.print(listdef.getCount());
      ps.print(" DefId: ");
      ps.println(listdef.getDataDefId());
      Iterator listdefIter;
      if (dbtype == 268)
      {
        for (listdefIter = listdef.iterator(); listdefIter.hasNext(); )
        {
          ElementEntryDef ommEntry = (ElementEntryDef)listdefIter.next();
          dumpIndent(ps, tabLevel + 1);
          ps.print("ELEMENT_ENTRY_DEF ");
          ps.print("Name: ");
          ps.print(ommEntry.getName());
          ps.print(" Type: ");
          ps.println(OMMTypes.toString(ommEntry.getDataType()));
        }

      }
      else
        for (listdefIter = listdef.iterator(); listdefIter.hasNext(); )
        {
          FieldEntryDef ommEntry = (FieldEntryDef)listdefIter.next();
          dumpIndent(ps, tabLevel + 1);
          ps.print("FIELD_ENTRY_DEF ");
          ps.print("FID: ");
          ps.print(ommEntry.getFieldId());
          ps.print(" Type: ");
          ps.println(OMMTypes.toString(ommEntry.getDataType()));
        }
    }
    Iterator listdefIter;
  }

  private static void dumpRespTypeNum(OMMMsg msg, PrintStream ps)
  {
    if (msg.getMsgType() == 6)
    {
      ps.println(new StringBuilder().append(" (").append(OMMMsg.RespType.toString(msg.getRespTypeNum())).append(")").toString());
    }
    else
    {
      if ((msg.getMsgModelType() < 6) || (msg.getMsgModelType() > 12)) {
        return;
      }
      ps.println(new StringBuilder().append(" (").append(RDMInstrument.Update.toString(msg.getRespTypeNum())).append(")").toString());
    }
  }

  public static final void parse(OMMData data)
  {
    parseData(data, System.out, 0);
  }

  private static final void parseAggregate(OMMData data, PrintStream ps, int tabLevel)
  {
    parseAggregateHeader(data, ps, tabLevel);
    for (Iterator iter = ((OMMIterable)data).iterator(); iter.hasNext(); )
    {
      OMMEntry entry = (OMMEntry)iter.next();
      parseEntry(entry, ps, tabLevel + 1);
    }
  }

  public static final void parseData(OMMData data, PrintStream ps, int tabLevel)
  {
    if (data.isBlank()) {
      dumpBlank(ps);
    } else if (OMMTypes.isAggregate(data.getType())) {
      parseAggregate(data, ps, tabLevel + 1);
    } else if ((data.getType() == 19) && (((OMMDataBuffer)data).hasPartialUpdates()))
    {
      Iterator iter = ((OMMDataBuffer)data).partialUpdateIterator();
      while (true)
      {
        OMMDataBuffer partial = (OMMDataBuffer)iter.next();
        ps.print("hpos: ");
        ps.print(partial.horizontalPosition());
        ps.print(", ");
        ps.print(partial.toString());
        if (!(iter.hasNext())) break;
        ps.print("  |  ");
      }

      ps.println();
    }
    else if (data.getType() == 134)
    {
      parseAnsiPageData(data, ps, tabLevel);
    }
    else if ((data.getType() == 16) || (data.getType() == 130))
    {
      if (data.getEncodedLength() <= 20)
      {
        dumpIndent(ps, tabLevel + 1);

        ps.print(HexDump.toHexString(((OMMDataBuffer)data).getBytes(), false));
        ps.print(" | ");
        ps.println(data);
      }
      else
      {
        if (INTERNAL_DEBUG)
        {
          ps.println("Hex Format and Data Bytes: ");
          ps.println(HexDump.hexDump(((OMMDataBuffer)data).getBytes(), 50));

          ps.println("Hex Format: ");
        }

        int lineSize = 32;
        String s = HexDump.toHexString(((OMMDataBuffer)data).getBytes(), false);

        int j = 0;
        while (j < s.length())
        {
          if (j != 0) {
            ps.println();
          }
          dumpIndent(ps, 1);

          int end = j + lineSize;
          if (end >= s.length()) {
            end = s.length();
          }
          for (int i = j; i < end; ++i)
          {
            ps.print(s.charAt(i));
          }
          j += lineSize;
        }

        ps.println("\nData Bytes: ");
        dumpIndent(ps, 1);
        ps.println(data);
      }
    }
    else if (data.getType() == 270)
    {
      parseMsg((OMMMsg)data, ps, tabLevel + 1);
    }
    else
    {
      try
      {
        ps.println(data);
      }
      catch (Exception e)
      {
        byte[] rawdata = data.getBytes();
        ps.println(HexDump.hexDump(rawdata));
      }
    }
  }

  private static final void parseAggregateHeader(OMMData data, PrintStream ps, int tabLevel)
  {
    dumpIndent(ps, tabLevel);
    short dataType = data.getType();
    ps.println(OMMTypes.toString(dataType));
    switch (dataType)
    {
    case OMMTypes.FIELD_LIST:{
      OMMFieldList fieldList = (OMMFieldList)data;
      int dictId = fieldList.getDictId();
      CURRENT_DICTIONARY = getDictionary(dictId);

      break;
    }
    case OMMTypes.SERIES:{
      OMMSeries s = (OMMSeries)data;
      if (s.has(2))
      {
        dumpIndent(ps, tabLevel + 1);
        ps.println("SUMMARY");
        parseData(s.getSummaryData(), ps, tabLevel + 1);
      }
      if (!(s.has(1)))
      dumpIndent(ps, tabLevel + 1);
      short dbtype = (s.getDataType() == (short)132) ? (short)269 : (short)268;
      parseDataDefinition(s.getDataDefs(), dbtype, ps, tabLevel + 1);

      break;
    }
    case 137:{
      OMMMap s = (OMMMap)data;
      String flagsString = ExampleUtil.mapFlagsString(s);
      dumpIndent(ps, tabLevel);
      ps.print("flags: ");
      ps.println(flagsString);

      if (!(s.has(2)))
      dumpIndent(ps, tabLevel + 1);
      ps.println("SUMMARY");
      parseData(s.getSummaryData(), ps, tabLevel + 1);

      break;
    }
    case 136:{
      OMMVector s = (OMMVector)data;

      String flagsString = ExampleUtil.vectorFlagsString(s);
      dumpIndent(ps, tabLevel);
      ps.print("flags: ");
      ps.println(flagsString);

      if (!(s.has(2)))
      dumpIndent(ps, tabLevel + 1);
      ps.println("SUMMARY");
      parseData(s.getSummaryData(), ps, tabLevel + 1);

      break;
    }
    case 135:
      OMMFilterList s = (OMMFilterList)data;
      String flagsString = ExampleUtil.filterListFlagsString(s);
      dumpIndent(ps, tabLevel);
      ps.print("flags: ");
      ps.println(flagsString);
    case 133:
    case 134:
    }
  }

  private static final void dumpBlank(PrintStream ps)
  {
    ps.println();
  }

  private static final void dumpIndent(PrintStream ps, int tabLevel)
  {
    for (int i = 0; i < tabLevel; ++i)
      ps.print('\t');
  }

  private static final void parseEntry(OMMEntry entry, PrintStream ps, int tabLevel)
  {
    try
    {
      switch (entry.getType())
      {
      case 257:
        OMMFieldEntry fe = (OMMFieldEntry)entry;
        if (CURRENT_DICTIONARY != null)
        {
          FidDef fiddef = CURRENT_DICTIONARY.getFidDef(fe.getFieldId());
          if (fiddef != null)
          {
            dumpFieldEntryHeader(fe, fiddef, ps, tabLevel);
            OMMData data = null;
            if (fe.getDataType() == 0)
              data = fe.getData(fiddef.getOMMType());
            else
              data = fe.getData();
            if (data.getType() == 14)
            {
              ps.print(CURRENT_DICTIONARY.expandedValueFor(fiddef.getFieldId(), ((OMMEnum)data).getValue()));
              ps.print(" (");
              ps.print(data);
              ps.println(")");
            }
            else {
              parseData(data, ps, tabLevel);
            }
          }
          else {
            ps.println(new StringBuilder().append("Received field id: ").append(fe.getFieldId()).append(" - Not defined in dictionary").toString());
          }
        }
        else
        {
          dumpFieldEntryHeader(fe, null, ps, tabLevel);
          if (fe.getDataType() == 0)
          {
            OMMDataBuffer data = (OMMDataBuffer)fe.getData();
            ps.println(HexDump.toHexString(data.getBytes(), false));
          }
          else
          {
            OMMData data = fe.getData();
            parseData(data, ps, tabLevel);
          }
        }
        ps.flush();

        break;
      case 258:
        dumpElementEntryHeader((OMMElementEntry)entry, ps, tabLevel);
        parseData(entry.getData(), ps, tabLevel);
        break;
      case 262:
        dumpMapEntryHeader((OMMMapEntry)entry, ps, tabLevel);
        if ((((OMMMapEntry)entry).getAction() != 3) && (entry.getDataType() != 128))
        {
          parseData(entry.getData(), ps, tabLevel); } break;
      case 260:
        dumpVectorEntryHeader((OMMVectorEntry)entry, ps, tabLevel);
        if ((((OMMVectorEntry)entry).getAction() != 5) && (((OMMVectorEntry)entry).getAction() != 3))
        {
          parseData(entry.getData(), ps, tabLevel); } break;
      case 259:
        dumpFilterEntryHeader((OMMFilterEntry)entry, ps, tabLevel);
        if (((OMMFilterEntry)entry).getAction() != 3)
          parseData(entry.getData(), ps, tabLevel); break;
      case 261:
      default:
        dumpEntryHeader(entry, ps, tabLevel);
        parseData(entry.getData(), ps, tabLevel);
      }

    }
    catch (OMMException e)
    {
      ps.println(new StringBuilder().append("ERROR Invalid data: ").append(e.getMessage()).toString());
    }
  }

  private static final void dumpEntryHeader(OMMEntry entry, PrintStream ps, int tabLevel)
  {
    dumpIndent(ps, tabLevel);
    ps.print(OMMTypes.toString(entry.getType()));
    ps.print(": ");
    if (entry.getType() == 261)
      ps.println();
  }

  private static final void dumpFieldEntryHeader(OMMFieldEntry entry, FidDef def, PrintStream ps, int tabLevel)
  {
    dumpIndent(ps, tabLevel);
    ps.print(OMMTypes.toString(entry.getType()));
    ps.print(" ");
    ps.print(entry.getFieldId());
    if (def == null)
    {
      ps.print(": ");
    }
    else
    {
      ps.print("/");
      ps.print(def.getName());
      ps.print(": ");
      if ((def.getOMMType() >= 128) || (def.getOMMType() == 15))
        ps.println();
    }
  }

  private static final void dumpElementEntryHeader(OMMElementEntry entry, PrintStream ps, int tabLevel)
  {
    dumpIndent(ps, tabLevel);
    ps.print(OMMTypes.toString(entry.getType()));
    ps.print(" ");
    ps.print(entry.getName());
    ps.print(": ");
    if ((entry.getDataType() >= 128) || (entry.getDataType() == 15))
      ps.println();
  }

  private static final void dumpFilterEntryHeader(OMMFilterEntry entry, PrintStream ps, int tabLevel)
  {
    dumpIndent(ps, tabLevel);
    ps.print(OMMTypes.toString(entry.getType()));
    ps.print(" ");
    ps.print(entry.getFilterId());
    ps.print(" (");
    ps.print(OMMFilterEntry.Action.toString(entry.getAction()));
    if (entry.has(1))
      ps.print(", HasPermissionData");
    if (entry.has(2))
      ps.print(", HasDataFormat");
    ps.println(") : ");

    String flagsString = ExampleUtil.filterEntryFlagsString(entry);
    dumpIndent(ps, tabLevel);
    ps.print("flags: ");
    ps.println(flagsString);
  }

  private static final void dumpMapEntryHeader(OMMMapEntry entry, PrintStream ps, int tabLevel)
  {
    dumpIndent(ps, tabLevel);
    ps.print(OMMTypes.toString(entry.getType()));
    ps.print(" (");
    ps.print(OMMMapEntry.Action.toString(entry.getAction()));
    if (entry.has(1))
      ps.print(", HasPermissionData");
    ps.println(") : ");

    String flagsString = ExampleUtil.mapEntryFlagsString(entry);
    dumpIndent(ps, tabLevel);
    ps.print("flags: ");
    ps.println(flagsString);

    dumpIndent(ps, tabLevel);
    ps.print("Key: ");
    parseData(entry.getKey(), ps, 0);
    dumpIndent(ps, tabLevel);
    ps.println("Value: ");
  }

  private static final void dumpVectorEntryHeader(OMMVectorEntry entry, PrintStream ps, int tabLevel)
  {
    dumpIndent(ps, tabLevel);
    ps.print(OMMTypes.toString(entry.getType()));
    ps.print(" ");
    ps.print(entry.getPosition());
    ps.print(" (");
    ps.print(OMMVectorEntry.Action.vectorActionString(entry.getAction()));
    if (entry.has(1))
      ps.print(", HasPermissionData");
    ps.println(") : ");

    String flagsString = ExampleUtil.vectorEntryFlagsString(entry);
    dumpIndent(ps, tabLevel);
    ps.print("flags: ");
    ps.println(flagsString);
  }

  public static final void parseAnsiPageData(OMMData data, PrintStream ps, int tabLevel)
  {
    boolean newPage = false;
    if (CURRENT_PAGE == null)
    {
      CURRENT_PAGE = new Page();
      newPage = true;
    }

    Vector pageUpdates = new Vector();
    ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
    CURRENT_PAGE.decode(bais, pageUpdates);
    if (newPage) {
      ps.println(CURRENT_PAGE.toString());
    }
    else
    {
      Iterator iter = pageUpdates.iterator();
      while (iter.hasNext())
      {
        PageUpdate u = (PageUpdate)iter.next();
        StringBuilder buf = new StringBuilder(80);
        for (short k = u.getBeginningColumn(); k < u.getEndingColumn(); k = (short)(k + 1))
        {
          buf.append(CURRENT_PAGE.getChar(u.getRow(), k));
        }
        if (!(buf.toString().equalsIgnoreCase("")))
        {
          dumpIndent(ps, tabLevel);
          ps.println(new StringBuilder().append("Update String: ").append(buf.toString()).append(" (Row: ").append(u.getRow()).append(", Begin Col: ").append(u.getBeginningColumn()).append(", End Col: ").append(u.getEndingColumn()).append(")").toString());
        }
      }
    }
  }
}