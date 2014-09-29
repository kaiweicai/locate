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
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.locate.stock.model.TimeseriesItem;
import com.locate.stock.util.DateUtils;

/**
 * Tests for <code>TimeseriesItem</code> class.
 * 
 * @author Sha Jiang
 */
public class TimeseriesItemTests {

	private static final double EPSILON = 0.0000000001;

	@Test
	public void testConstructor() {
		TimeseriesItem item = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1), 10.23, 12345.60);
		assertEquals(DateUtils.createDate(2008, 1, 1), item.getTime());
		assertEquals(10.23, item.getPrice(), EPSILON);
		assertEquals(12345.60, item.getVolume(), EPSILON);
	}

	@Test
	public void testEquals() {
		TimeseriesItem item1 = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1), 10.23, 12345.60);
		TimeseriesItem item2 = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1), 10.23, 12345.60);
		assertTrue(item1.equals(item2));
		assertTrue(item2.equals(item1));

		item2 = new TimeseriesItem(DateUtils.createDate(2008, 1, 2), 10.23,
				12345.60);
		assertFalse(item2.equals(item1));

		item2 = new TimeseriesItem(DateUtils.createDate(2008, 1, 1), 10.231,
				12345.60);
		assertTrue(item2.equals(item1));

		item2 = new TimeseriesItem(DateUtils.createDate(2008, 1, 1), 10.23,
				12345.61);
		assertTrue(item2.equals(item1));
	}

	@Test
	public void testSerialization() {
		TimeseriesItem item1 = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1), 10.23, 12345.60);
		TimeseriesItem item2 = null;
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(buf);
			out.writeObject(item1);
			out.close();

			ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buf
					.toByteArray()));
			item2 = (TimeseriesItem) in.readObject();
			in.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		assertTrue(item1.equals(item2));
		assertTrue(item2.equals(item1));
	}
}
