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

package org.jstockchart.test.axis.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.locate.stock.axis.TickAlignment;
import com.locate.stock.axis.logic.LogicDateAxis;
import com.locate.stock.axis.logic.LogicDateTick;
import com.locate.stock.util.DateUtils;

/**
 * Tests for <code>LogicDateAxis</code> class.
 * 
 * @author Sha Jiang
 */
public class LogicDateAxisTests {

	@Test
	public void testGetLogicTicks1() {
		Date baseDate = DateUtils.createDate(2008, 1, 1);
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		LogicDateAxis logicDateAxis = new LogicDateAxis(baseDate, formatter);
		logicDateAxis.addDateTick("07:00");
		logicDateAxis.addDateTick("08:01");
		logicDateAxis.addDateTick("09:00");

		List<LogicDateTick> logicTicks = logicDateAxis.getLogicTicks();

		LogicDateTick tick1 = logicTicks.get(0);
		assertEquals(DateUtils.createDate(2008, 1, 1, 7), tick1.getTickDate());
		assertEquals("07:00", tick1.getTickLabel());

		LogicDateTick tick2 = logicTicks.get(1);
		assertEquals(DateUtils.createDate(2008, 1, 1, 8, 1), tick2
				.getTickDate());
		assertEquals("08:01", tick2.getTickLabel());

		LogicDateTick tick3 = logicTicks.get(2);
		assertEquals(DateUtils.createDate(2008, 1, 1, 9), tick3.getTickDate());
		assertEquals("09:00", tick3.getTickLabel());

		try {
			logicTicks.get(3);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetLogicTicks2() {
		Date baseDate = DateUtils.createDate(2008, 1, 1);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LogicDateAxis logicDateAxis = new LogicDateAxis(baseDate, formatter);
		logicDateAxis.addDateTick("2008-01-01 07:00:00");
		logicDateAxis.addDateTick("2008-01-01 08:00:00");
		logicDateAxis.addDateTick("2008-01-01 09:00:00");
		logicDateAxis.addDateTick("2008-01-01 09:30:58");

		List<LogicDateTick> logicTicks = logicDateAxis.getLogicTicks();

		LogicDateTick tick1 = logicTicks.get(0);
		assertEquals(DateUtils.createDate(2008, 1, 1, 7), tick1.getTickDate());
		assertEquals("2008-01-01 07:00:00", tick1.getTickLabel());

		LogicDateTick tick2 = logicTicks.get(1);
		assertEquals(DateUtils.createDate(2008, 1, 1, 8), tick2.getTickDate());
		assertEquals("2008-01-01 08:00:00", tick2.getTickLabel());

		LogicDateTick tick3 = logicTicks.get(2);
		assertEquals(DateUtils.createDate(2008, 1, 1, 9), tick3.getTickDate());
		assertEquals("2008-01-01 09:00:00", tick3.getTickLabel());

		LogicDateTick tick4 = logicTicks.get(3);
		assertEquals(DateUtils.createDate(2008, 1, 1, 9, 30, 58), tick4
				.getTickDate());
		assertEquals("2008-01-01 09:30:58", tick4.getTickLabel());

		try {
			logicTicks.get(4);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}
	}

	public void testGetLogicTicks3() {
		Date baseDate = DateUtils.createDate(2008, 1, 1);
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		LogicDateAxis logicDateAxis = new LogicDateAxis(baseDate, formatter);
		logicDateAxis.addDateTick("07:00", TickAlignment.START);
		logicDateAxis.addDateTick("08:00", TickAlignment.END);
		logicDateAxis.addDateTick("09:00");

		List<LogicDateTick> logicTicks = logicDateAxis.getLogicTicks();

		LogicDateTick tick1 = logicTicks.get(0);
		assertEquals(DateUtils.createDate(2008, 1, 1, 7), tick1.getTickDate());
		assertEquals("07:00", tick1.getTickLabel());
		assertEquals(TickAlignment.START, tick1.getTickAlignment());

		LogicDateTick tick2 = logicTicks.get(1);
		assertEquals(DateUtils.createDate(2008, 1, 1, 8), tick2.getTickDate());
		assertEquals("08:00", tick2.getTickLabel());
		assertEquals(TickAlignment.END, tick2.getTickAlignment());

		LogicDateTick tick3 = logicTicks.get(2);
		assertEquals(DateUtils.createDate(2008, 1, 1, 9), tick3.getTickDate());
		assertEquals("09:00", tick3.getTickLabel());
		assertEquals(TickAlignment.MID, tick3.getTickAlignment());

		try {
			logicTicks.get(3);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}
	}
}
