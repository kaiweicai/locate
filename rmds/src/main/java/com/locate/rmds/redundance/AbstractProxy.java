package com.locate.rmds.redundance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.locate.rmds.IConsumerProxy;
import com.reuters.rfa.common.Handle;
import com.reuters.rfa.dictionary.FieldDictionary;

public abstract class AbstractProxy implements IConsumerProxy {
	public static Logger logger = LoggerFactory.getLogger(AbstractProxy.class);
	public static HashMap<Integer, FieldDictionary> DICTIONARIES = new HashMap<Integer, FieldDictionary>();
	public static FieldDictionary dictionary;
	public static List<String> _loadedDictionaries;
	public static Map<Handle, String> _pendingDictionaries;
}
