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

import com.locate.stock.axis.logic.CentralValueAxis;
import com.locate.stock.axis.logic.LogicNumberTick;

/**
 * Tests for <code>CentralValueAxis</code> class.
 * 
 * @author Sha Jiang
 */
public class CentralValueAxisTests {

	private static final double EPSILON = 0.00001;

	@Test
	public void testGetLogicTicks1() {
		Number centralValue = new Double(12);
		Range range = new Range(10, 14);
		int tickCount = 3;
		NumberFormat formatter = new DecimalFormat("0.0");
		CentralValueAxis centralValueAxis = new CentralValueAxis(centralValue,
				range, tickCount, formatter);
		List<LogicNumberTick> logicTicks = centralValueAxis.getLogicTicks();

		LogicNumberTick tick1 = logicTicks.get(0);
		assertEquals(10, tick1.getTickNumber().doubleValue(), EPSILON);
		assertEquals("10.0", tick1.getTickLabel());

		LogicNumberTick tick2 = logicTicks.get(1);
		assertEquals(12, tick2.getTickNumber().doubleValue(), EPSILON);
		assertEquals("12.0", tick2.getTickLabel());

		LogicNumberTick tick3 = logicTicks.get(2);
		assertEquals(14, tick3.getTickNumber().doubleValue(), EPSILON);
		assertEquals("14.0", tick3.getTickLabel());
	}

	@Test
	public void testGetLogicTicks2() {
		Number centralValue = new Double(11);
		Range range = new Range(10, 14);
		int tickCount = 3;
		NumberFormat formatter = new DecimalFormat("0.0");
		CentralValueAxis centralValueAxis = new CentralValueAxis(centralValue,
				range, tickCount, formatter);
		List<LogicNumberTick> logicTicks = centralValueAxis.getLogicTicks();

		LogicNumberTick tick1 = logicTicks.get(0);
		assertEquals(8, tick1.getTickNumber().doubleValue(), EPSILON);
		assertEquals("8.0", tick1.getTickLabel());

		LogicNumberTick tick2 = logicTicks.get(1);
		assertEquals(11, tick2.getTickNumber().doubleValue(), EPSILON);
		assertEquals("11.0", tick2.getTickLabel());

		LogicNumberTick tick3 = logicTicks.get(2);
		assertEquals(14, tick3.getTickNumber().doubleValue(), EPSILON);
		assertEquals("14.0", tick3.getTickLabel());

		/* ************************************************************ */

		logicTicks = centralValueAxis.getLogicTicks();

		LogicNumberTick tick4 = logicTicks.get(0);
		assertEquals(8, tick4.getTickNumber().doubleValue(), EPSILON);
		assertEquals("8.0", tick4.getTickLabel());

		LogicNumberTick tick5 = logicTicks.get(1);
		assertEquals(11, tick5.getTickNumber().doubleValue(), EPSILON);
		assertEquals("11.0", tick5.getTickLabel());

		LogicNumberTick tick6 = logicTicks.get(2);
		assertEquals(14, tick6.getTickNumber().doubleValue(), EPSILON);
		assertEquals("14.0", tick6.getTickLabel());
	}
}
