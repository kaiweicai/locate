package com.locate.cmmons;

import net.sf.json.JSON;

import org.junit.Test;

import com.locate.common.utils.JsonUtil;

public class JsonUtilTest {
	@Test
	public void testChangeJson() {
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rmds><response><item><name>MCU3=LX</name></item><fields><Field><id>5</id><name>TIMACT</name><type>Unknown</type><value>08:31</value></Field><Field><id>25</id><name>ASK</name><type>Double</type><value>6816.50</value></Field><Field><id>31</id><name>ASKSIZE</name><type>Double</type><value>1</value></Field><Field><id>346</id><name>ASK_TONE</name><type>String</type><value> </value></Field></fields></response></rmds>";
		JSON json = JsonUtil.getJSONFromXml(xmlString);
		System.out.println(json);
	}
}
