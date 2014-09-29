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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.data.Range;
import org.junit.Test;

import com.locate.stock.axis.logic.LogicNumberAxis;
import com.locate.stock.axis.logic.LogicNumberTick;

/**
 * Tests for <code>LogicNumberAxis</code> class.
 * 
 * @author Sha Jiang
 */
public class LogicNumberAxisTests {

	private static final double EPSILON = 0.00001;

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetLogicTicks1() {
		Range range = new Range(10, 14);
		int tickCount = 5;
		NumberFormat formatter = new DecimalFormat("0.0");
		LogicNumberAxis logicNumberAxis = new LogicNumberAxis(range, tickCount,
				formatter);
		List<LogicNumberTick> logicTicks = logicNumberAxis.getLogicTicks();

		LogicNumberTick tick1 = logicTicks.get(0);
		assertEquals(tick1.getTickNumber().doubleValue(), 10, EPSILON);
		assertEquals(tick1.getTickLabel(), "10.0");

		LogicNumberTick tick2 = logicTicks.get(1);
		assertEquals(tick2.getTickNumber().doubleValue(), 11, EPSILON);
		assertEquals(tick2.getTickLabel(), "11.0");

		LogicNumberTick tick3 = logicTicks.get(2);
		assertEquals(tick3.getTickNumber().doubleValue(), 12, EPSILON);
		assertEquals(tick3.getTickLabel(), "12.0");

		LogicNumberTick tick4 = logicTicks.get(3);
		assertEquals(tick4.getTickNumber().doubleValue(), 13, EPSILON);
		assertEquals(tick4.getTickLabel(), "13.0");

		LogicNumberTick tick5 = logicTicks.get(4);
		assertEquals(tick5.getTickNumber().doubleValue(), 14, EPSILON);
		assertEquals(tick5.getTickLabel(), "14.0");

		logicTicks.get(tickCount);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetLogicTicks2() {
		Range range = new Range(10, 15);
		int tickCount = 5;
		NumberFormat formatter = new DecimalFormat("0.00");
		LogicNumberAxis logicNumberAxis = new LogicNumberAxis(range, tickCount,
				formatter);
		List<LogicNumberTick> logicTicks = logicNumberAxis.getLogicTicks();

		LogicNumberTick tick1 = logicTicks.get(0);
		assertEquals(tick1.getTickNumber().doubleValue(), 10, EPSILON);
		assertEquals(tick1.getTickLabel(), "10.00");

		LogicNumberTick tick2 = logicTicks.get(1);
		assertEquals(tick2.getTickNumber().doubleValue(), 11.25, EPSILON);
		assertEquals(tick2.getTickLabel(), "11.25");

		LogicNumberTick tick3 = logicTicks.get(2);
		assertEquals(tick3.getTickNumber().doubleValue(), 12.5, EPSILON);
		assertEquals(tick3.getTickLabel(), "12.50");

		LogicNumberTick tick4 = logicTicks.get(3);
		assertEquals(tick4.getTickNumber().doubleValue(), 13.75, EPSILON);
		assertEquals(tick4.getTickLabel(), "13.75");

		LogicNumberTick tick5 = logicTicks.get(4);
		assertEquals(tick5.getTickNumber().doubleValue(), 15, EPSILON);
		assertEquals(tick5.getTickLabel(), "15.00");

		logicTicks.get(tickCount);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetLogicTicks3() {
		Range range = new Range(10, 15);
		int tickCount = 4;
		NumberFormat formatter = new DecimalFormat("0.00");
		LogicNumberAxis logicNumberAxis = new LogicNumberAxis(range, tickCount,
				formatter);
		List<LogicNumberTick> logicTicks = logicNumberAxis.getLogicTicks();

		LogicNumberTick tick1 = logicTicks.get(0);
		assertEquals(tick1.getTickNumber().doubleValue(), 10, EPSILON);
		assertEquals(tick1.getTickLabel(), "10.00");

		LogicNumberTick tick2 = logicTicks.get(1);
		assertEquals(tick2.getTickNumber().doubleValue(), 11.67, EPSILON);
		assertEquals(tick2.getTickLabel(), "11.67");

		LogicNumberTick tick3 = logicTicks.get(2);
		assertEquals(tick3.getTickNumber().doubleValue(), 13.34, EPSILON);
		assertEquals(tick3.getTickLabel(), "13.34");

		LogicNumberTick tick4 = logicTicks.get(3);
		assertEquals(tick4.getTickNumber().doubleValue(), 15.01, EPSILON);
		assertEquals(tick4.getTickLabel(), "15.01");

		logicTicks.get(tickCount);
	}
}
