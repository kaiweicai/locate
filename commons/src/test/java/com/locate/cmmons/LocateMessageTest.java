package com.locate.cmmons;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.locate.common.model.LocateUnionMessage;

public class LocateMessageTest {
	@Test
	public void testMessageClone(){
		LocateUnionMessage message = new LocateUnionMessage();
		List<String[]> payloadSet = new ArrayList<String[]>();
		payloadSet.add(new String[]{"25","ASK","Double","6816.50"});
		message.setPayLoadSet(payloadSet);
		LocateUnionMessage cloneMessage = message.clone();
		message.setItemName("oringeRicName");
		message.getPayLoadSet().get(0)[3]="6900";
		Assert.assertTrue(message.getItemName().equalsIgnoreCase(cloneMessage.getItemName()));
		Assert.assertFalse(message.getPayLoadSet().get(0)[3].equals(cloneMessage.getPayLoadSet().get(0)[3]));
		System.out.println(message);
		System.out.println(cloneMessage);
	}
}
