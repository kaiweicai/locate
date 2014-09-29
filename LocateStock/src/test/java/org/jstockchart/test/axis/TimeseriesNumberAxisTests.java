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

package org.jstockchart.test.axis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.Tick;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.junit.Before;
import org.junit.Test;

import com.locate.stock.axis.TimeseriesNumberAxis;
import com.locate.stock.axis.logic.LogicNumberTick;

/**
 * Tests for <code>TimeseriesNumberAxis</code> class.
 * 
 * @author Sha Jiang
 */
public class TimeseriesNumberAxisTests {

	private static final double EPSILON = 0.00001;

	private static final Number NUMBER1 = new Double(12345.67);

	private static final Number NUMBER2 = new Double(-67.54321);

	private static final String LABEL1 = "12345.670000";

	private static final String LABEL2 = "-67.543210";

	private TimeseriesNumberAxis timeseriesNumberAxis = null;

	@Before
	public void setUp() {
		List<LogicNumberTick> logicNumberTicks = new ArrayList<LogicNumberTick>();
		LogicNumberTick logicNumberTick1 = new LogicNumberTick(NUMBER1, LABEL1);
		logicNumberTicks.add(logicNumberTick1);
		LogicNumberTick logicNumberTick2 = new LogicNumberTick(NUMBER2, LABEL2);
		logicNumberTicks.add(logicNumberTick2);
		timeseriesNumberAxis = new TimeseriesNumberAxis(logicNumberTicks);
	}

	@Test
	public void testRefreshTicksHorizontal() {
		/* ===== horizontal tick label ===== */
		timeseriesNumberAxis.setVerticalTickLabels(false);

		/*  ***** RectangleEdge.TOP ***** */
		List<Tick> numberTicks1 = timeseriesNumberAxis.refreshTicksHorizontal(
				null, null, RectangleEdge.TOP);
		NumberTick numberTick11 = ((NumberTick) numberTicks1.get(0));
		assertEquals(NUMBER1, numberTick11.getNumber());
		assertEquals(LABEL1, numberTick11.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick11.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick11.getRotationAnchor());
		assertEquals(0, numberTick11.getAngle(), EPSILON);
		NumberTick numberTick12 = ((NumberTick) numberTicks1.get(1));
		assertEquals(NUMBER2, numberTick12.getNumber());
		assertEquals(LABEL2, numberTick12.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick12.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick12.getRotationAnchor());
		assertEquals(0, numberTick12.getAngle(), EPSILON);
		try {
			numberTicks1.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.BOTTOM ***** */
		List<Tick> numberTicks2 = timeseriesNumberAxis.refreshTicksHorizontal(
				null, null, RectangleEdge.BOTTOM);
		NumberTick numberTick21 = ((NumberTick) numberTicks2.get(0));
		assertEquals(NUMBER1, numberTick21.getNumber());
		assertEquals(LABEL1, numberTick21.getText());
		assertEquals(TextAnchor.TOP_CENTER, numberTick21.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, numberTick21.getRotationAnchor());
		assertEquals(0, numberTick21.getAngle(), EPSILON);
		NumberTick numberTick22 = ((NumberTick) numberTicks2.get(1));
		assertEquals(NUMBER2, numberTick22.getNumber());
		assertEquals(LABEL2, numberTick22.getText());
		assertEquals(TextAnchor.TOP_CENTER, numberTick22.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, numberTick22.getRotationAnchor());
		assertEquals(0, numberTick22.getAngle(), EPSILON);
		try {
			numberTicks2.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.LEFT ***** */
		List<Tick> numberTicks3 = timeseriesNumberAxis.refreshTicksHorizontal(
				null, null, RectangleEdge.LEFT);
		NumberTick numberTick31 = ((NumberTick) numberTicks3.get(0));
		assertEquals(NUMBER1, numberTick31.getNumber());
		assertEquals(LABEL1, numberTick31.getText());
		assertEquals(TextAnchor.TOP_CENTER, numberTick31.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, numberTick31.getRotationAnchor());
		assertEquals(0, numberTick31.getAngle(), EPSILON);
		NumberTick numberTick32 = ((NumberTick) numberTicks3.get(1));
		assertEquals(NUMBER2, numberTick32.getNumber());
		assertEquals(LABEL2, numberTick32.getText());
		assertEquals(TextAnchor.TOP_CENTER, numberTick32.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, numberTick32.getRotationAnchor());
		assertEquals(0, numberTick32.getAngle(), EPSILON);
		try {
			numberTicks3.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.RIGHT ***** */
		List<Tick> numberTicks5 = timeseriesNumberAxis.refreshTicksHorizontal(
				null, null, RectangleEdge.RIGHT);
		NumberTick numberTick51 = ((NumberTick) numberTicks5.get(0));
		assertEquals(NUMBER1, numberTick51.getNumber());
		assertEquals(LABEL1, numberTick51.getText());
		assertEquals(TextAnchor.TOP_CENTER, numberTick51.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, numberTick51.getRotationAnchor());
		assertEquals(0, numberTick51.getAngle(), EPSILON);
		NumberTick numberTick52 = ((NumberTick) numberTicks5.get(1));
		assertEquals(NUMBER2, numberTick52.getNumber());
		assertEquals(LABEL2, numberTick52.getText());
		assertEquals(TextAnchor.TOP_CENTER, numberTick52.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, numberTick52.getRotationAnchor());
		assertEquals(0, numberTick52.getAngle(), EPSILON);
		try {
			numberTicks5.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ===== vertical tick label ===== */
		timeseriesNumberAxis.setVerticalTickLabels(true);

		/*  ***** RectangleEdge.TOP ***** */
		numberTicks1 = timeseriesNumberAxis.refreshTicksHorizontal(null, null,
				RectangleEdge.TOP);
		numberTick11 = ((NumberTick) numberTicks1.get(0));
		assertEquals(NUMBER1, numberTick11.getNumber());
		assertEquals(LABEL1, numberTick11.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick11.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick11.getRotationAnchor());
		assertEquals(Math.PI / 2.0, numberTick11.getAngle(), EPSILON);
		numberTick12 = ((NumberTick) numberTicks1.get(1));
		assertEquals(NUMBER2, numberTick12.getNumber());
		assertEquals(LABEL2, numberTick12.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick12.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick12.getRotationAnchor());
		assertEquals(Math.PI / 2.0, numberTick12.getAngle(), EPSILON);
		try {
			numberTicks1.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.BOTTOM ***** */
		numberTicks2 = timeseriesNumberAxis.refreshTicksHorizontal(null, null,
				RectangleEdge.BOTTOM);
		numberTick21 = ((NumberTick) numberTicks2.get(0));
		assertEquals(NUMBER1, numberTick21.getNumber());
		assertEquals(LABEL1, numberTick21.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick21.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick21.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, numberTick21.getAngle(), EPSILON);
		numberTick22 = ((NumberTick) numberTicks2.get(1));
		assertEquals(NUMBER2, numberTick22.getNumber());
		assertEquals(LABEL2, numberTick22.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick22.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick22.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, numberTick22.getAngle(), EPSILON);
		try {
			numberTicks2.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.LEFT ***** */
		numberTicks3 = timeseriesNumberAxis.refreshTicksHorizontal(null, null,
				RectangleEdge.LEFT);
		numberTick31 = ((NumberTick) numberTicks3.get(0));
		assertEquals(NUMBER1, numberTick31.getNumber());
		assertEquals(LABEL1, numberTick31.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick31.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick31.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, numberTick31.getAngle(), EPSILON);
		numberTick32 = ((NumberTick) numberTicks3.get(1));
		assertEquals(NUMBER2, numberTick32.getNumber());
		assertEquals(LABEL2, numberTick32.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick32.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick32.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, numberTick32.getAngle(), EPSILON);
		try {
			numberTicks3.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.RIGHT ***** */
		numberTicks5 = timeseriesNumberAxis.refreshTicksHorizontal(null, null,
				RectangleEdge.RIGHT);
		numberTick51 = ((NumberTick) numberTicks5.get(0));
		assertEquals(NUMBER1, numberTick51.getNumber());
		assertEquals(LABEL1, numberTick51.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick51.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick51.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, numberTick51.getAngle(), EPSILON);
		numberTick52 = ((NumberTick) numberTicks5.get(1));
		assertEquals(NUMBER2, numberTick52.getNumber());
		assertEquals(LABEL2, numberTick52.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick52.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick52.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, numberTick52.getAngle(), EPSILON);
		try {
			numberTicks5.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}
	}

	@Test
	public void testRefreshTicksVertical() {
		/* ===== horizontal tick label ===== */
		timeseriesNumberAxis.setVerticalTickLabels(false);

		/*  ***** RectangleEdge.LEFT ***** */
		List<Tick> numberTicks1 = timeseriesNumberAxis.refreshTicksVertical(
				null, null, RectangleEdge.LEFT);
		NumberTick numberTick11 = ((NumberTick) numberTicks1.get(0));
		assertEquals(NUMBER1, numberTick11.getNumber());
		assertEquals(LABEL1, numberTick11.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick11.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick11.getRotationAnchor());
		assertEquals(0, numberTick11.getAngle(), EPSILON);
		NumberTick numberTick12 = ((NumberTick) numberTicks1.get(1));
		assertEquals(NUMBER2, numberTick12.getNumber());
		assertEquals(LABEL2, numberTick12.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick12.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, numberTick12.getRotationAnchor());
		assertEquals(0, numberTick12.getAngle(), EPSILON);
		try {
			numberTicks1.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.BOTTOM ***** */
		List<Tick> numberTicks2 = timeseriesNumberAxis.refreshTicksVertical(
				null, null, RectangleEdge.BOTTOM);
		NumberTick numberTick21 = ((NumberTick) numberTicks2.get(0));
		assertEquals(NUMBER1, numberTick21.getNumber());
		assertEquals(LABEL1, numberTick21.getText());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick21.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick21.getRotationAnchor());
		assertEquals(0, numberTick21.getAngle(), EPSILON);
		NumberTick numberTick22 = ((NumberTick) numberTicks2.get(1));
		assertEquals(NUMBER2, numberTick22.getNumber());
		assertEquals(LABEL2, numberTick22.getText());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick22.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick22.getRotationAnchor());
		assertEquals(0, numberTick22.getAngle(), EPSILON);
		try {
			numberTicks2.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.TOP ***** */
		List<Tick> numberTicks3 = timeseriesNumberAxis.refreshTicksVertical(
				null, null, RectangleEdge.TOP);
		NumberTick numberTick31 = ((NumberTick) numberTicks3.get(0));
		assertEquals(NUMBER1, numberTick31.getNumber());
		assertEquals(LABEL1, numberTick31.getText());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick31.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick31.getRotationAnchor());
		assertEquals(0, numberTick31.getAngle(), EPSILON);
		NumberTick numberTick32 = ((NumberTick) numberTicks3.get(1));
		assertEquals(NUMBER2, numberTick32.getNumber());
		assertEquals(LABEL2, numberTick32.getText());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick32.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick32.getRotationAnchor());
		assertEquals(0, numberTick32.getAngle(), EPSILON);
		try {
			numberTicks3.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.RIGHT ***** */
		List<Tick> numberTicks5 = timeseriesNumberAxis.refreshTicksVertical(
				null, null, RectangleEdge.RIGHT);
		NumberTick numberTick51 = ((NumberTick) numberTicks5.get(0));
		assertEquals(NUMBER1, numberTick51.getNumber());
		assertEquals(LABEL1, numberTick51.getText());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick51.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick51.getRotationAnchor());
		assertEquals(0, numberTick51.getAngle(), EPSILON);
		NumberTick numberTick52 = ((NumberTick) numberTicks5.get(1));
		assertEquals(NUMBER2, numberTick52.getNumber());
		assertEquals(LABEL2, numberTick52.getText());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick52.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, numberTick52.getRotationAnchor());
		assertEquals(0, numberTick52.getAngle(), EPSILON);
		try {
			numberTicks5.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ===== vertical tick label ===== */
		timeseriesNumberAxis.setVerticalTickLabels(true);

		/*  ***** RectangleEdge.LEFT ***** */
		numberTicks1 = timeseriesNumberAxis.refreshTicksVertical(null, null,
				RectangleEdge.LEFT);
		numberTick11 = ((NumberTick) numberTicks1.get(0));
		assertEquals(NUMBER1, numberTick11.getNumber());
		assertEquals(LABEL1, numberTick11.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick11.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick11.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, numberTick11.getAngle(), EPSILON);
		numberTick12 = ((NumberTick) numberTicks1.get(1));
		assertEquals(NUMBER2, numberTick12.getNumber());
		assertEquals(LABEL2, numberTick12.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick12.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick12.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, numberTick12.getAngle(), EPSILON);
		try {
			numberTicks1.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.BOTTOM ***** */
		numberTicks2 = timeseriesNumberAxis.refreshTicksVertical(null, null,
				RectangleEdge.BOTTOM);
		numberTick21 = ((NumberTick) numberTicks2.get(0));
		assertEquals(NUMBER1, numberTick21.getNumber());
		assertEquals(LABEL1, numberTick21.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick21.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick21.getRotationAnchor());
		assertEquals(Math.PI / 2.0, numberTick21.getAngle(), EPSILON);
		numberTick22 = ((NumberTick) numberTicks2.get(1));
		assertEquals(NUMBER2, numberTick22.getNumber());
		assertEquals(LABEL2, numberTick22.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick22.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick22.getRotationAnchor());
		assertEquals(Math.PI / 2.0, numberTick22.getAngle(), EPSILON);
		try {
			numberTicks2.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.TOP ***** */
		numberTicks3 = timeseriesNumberAxis.refreshTicksVertical(null, null,
				RectangleEdge.TOP);
		numberTick31 = ((NumberTick) numberTicks3.get(0));
		assertEquals(NUMBER1, numberTick31.getNumber());
		assertEquals(LABEL1, numberTick31.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick31.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick31.getRotationAnchor());
		assertEquals(Math.PI / 2.0, numberTick31.getAngle(), EPSILON);
		numberTick32 = ((NumberTick) numberTicks3.get(1));
		assertEquals(NUMBER2, numberTick32.getNumber());
		assertEquals(LABEL2, numberTick32.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick32.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick32.getRotationAnchor());
		assertEquals(Math.PI / 2.0, numberTick32.getAngle(), EPSILON);
		try {
			numberTicks3.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/*  ***** RectangleEdge.RIGHT ***** */
		numberTicks5 = timeseriesNumberAxis.refreshTicksVertical(null, null,
				RectangleEdge.RIGHT);
		numberTick51 = ((NumberTick) numberTicks5.get(0));
		assertEquals(NUMBER1, numberTick51.getNumber());
		assertEquals(LABEL1, numberTick51.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick51.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick51.getRotationAnchor());
		assertEquals(Math.PI / 2.0, numberTick51.getAngle(), EPSILON);
		numberTick52 = ((NumberTick) numberTicks5.get(1));
		assertEquals(NUMBER2, numberTick52.getNumber());
		assertEquals(LABEL2, numberTick52.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick52.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, numberTick52.getRotationAnchor());
		assertEquals(Math.PI / 2.0, numberTick52.getAngle(), EPSILON);
		try {
			numberTicks5.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}
	}
}
