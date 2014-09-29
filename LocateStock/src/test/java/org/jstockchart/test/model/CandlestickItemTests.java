/* ===========================================================
 * JStockChart : an extension of JFreeChart for financial market
 * ===========================================================
 *
 * Copyright (C) 2009, by Sha Jiang.
 *
 * Project Info:  http://code.google.com/p/jstockchart
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 */

package org.jstockchart.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.locate.stock.model.CandlestickItem;
import com.locate.stock.util.DateUtils;

/**
 * Tests for <code>CandlestickItem</code> class.
 * 
 * @author Sha Jiang
 */
public class CandlestickItemTests {

	private static final double EPSILON = 0.0000000001;

	@Test
	public void testConstructor() {
		CandlestickItem item = new CandlestickItem(DateUtils.createDate(2008,
				1, 1), 10.1, 10.2, 9.02, 9.01, 2008.8);
		assertTrue(item.getTime().equals(DateUtils.createDate(2008, 1, 1)));
		assertEquals(item.getOpen(), 10.1, EPSILON);
		assertEquals(item.getHigh(), 10.2, EPSILON);
		assertEquals(item.getLow(), 9.02, EPSILON);
		assertEquals(item.getClose(), 9.01, EPSILON);
		assertEquals(item.getVolume(), 2008.8, EPSILON);
	}

	@Test
	public void testEquals() {
		CandlestickItem item1 = new CandlestickItem(DateUtils.createDate(2008,
				1, 1), 10.1, 10.2, 9.02, 9.01, 2008.8);
		CandlestickItem item2 = new CandlestickItem(DateUtils.createDate(2008,
				1, 1), 10.1, 10.2, 9.02, 9.01, 2008.8);
		assertTrue(item1.equals(item2));
		assertTrue(item2.equals(item1));

		item2 = new CandlestickItem(DateUtils.createDate(2008, 1, 1), 10.10,
				10.20, 9.020, 9.010, 2008.80);
		assertTrue(item1.equals(item2));

		item2 = new CandlestickItem(DateUtils.createDate(2008, 2, 1), 10.1,
				10.2, 9.02, 9.01, 2008.8);
		assertFalse(item1.equals(item2));

		item2 = new CandlestickItem(DateUtils.createDate(2008, 1, 1), 10.11,
				10.2, 9.02, 9.01, 2008.8);
		assertFalse(item1.equals(item2));

		item2 = new CandlestickItem(DateUtils.createDate(2008, 1, 1), 10.1,
				10.21, 9.02, 9.01, 2008.8);
		assertFalse(item1.equals(item2));

		item2 = new CandlestickItem(DateUtils.createDate(2008, 1, 1), 10.1,
				10.21, 9.0200000000001, 9.01, 2008.8);
		assertFalse(item1.equals(item2));

		item2 = new CandlestickItem(DateUtils.createDate(2008, 1, 1), 10.1,
				10.21, 9.02, 9.00000000001, 2008.8);
		assertFalse(item1.equals(item2));

		item2 = new CandlestickItem(DateUtils.createDate(2008, 1, 1), 10.1,
				10.21, 9.02, 9.01, 2008.80000000000000000001);
		assertFalse(item1.equals(item2));
	}

	@Test
	public void testSerialization() {
		CandlestickItem item1 = new CandlestickItem(DateUtils.createDate(2008,
				1, 1), 10.1, 10.2, 9.02, 9.01, 2008.8);
		CandlestickItem item2 = null;
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(item1);
			out.close();

			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(buf.toByteArray()));
			item2 = (CandlestickItem) in.readObject();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		assertTrue(item1.equals(item2));
		assertTrue(item2.equals(item1));
	}
}
