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
import java.util.Date;
import java.util.List;

import org.jfree.chart.axis.DateTick;
import org.jfree.chart.axis.Tick;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.junit.Before;
import org.junit.Test;

import com.locate.stock.axis.TimeseriesDateAxis;
import com.locate.stock.axis.logic.LogicDateTick;
import com.locate.stock.util.DateUtils;

/**
 * Tests for <code>TimeseriesDateAxis</code> class.
 * 
 * @author Sha Jiang
 */
public class TimeseriesDateAxisTests {

	private static final double EPSILON = 0.00001;

	private static final Date DATE1 = DateUtils.createDate(2008, 1, 1, 1, 25);

	private static final Date DATE2 = DateUtils.createDate(2008, 1, 1, 1, 30);

	private static final String LABEL1 = "01:25";

	private static final String LABEL2 = "01:30";

	private TimeseriesDateAxis timeseriesDateAxis = null;

	@Before
	public void setUp() {
		List<LogicDateTick> logicDateTicks = new ArrayList<LogicDateTick>();
		LogicDateTick logicDateTick1 = new LogicDateTick(DATE1, LABEL1);
		logicDateTicks.add(logicDateTick1);
		LogicDateTick logicDateTick2 = new LogicDateTick(DATE2, LABEL2);
		logicDateTicks.add(logicDateTick2);
		timeseriesDateAxis = new TimeseriesDateAxis(logicDateTicks);
	}

	@Test
	public void testRefreshTicksHorizontal() {
		/* ===== horizontal tick label ===== */
		timeseriesDateAxis.setVerticalTickLabels(false);

		/* ***** RectangleEdge.TOP ***** */
		List<Tick> dateTicks1 = timeseriesDateAxis.refreshTicksHorizontal(null,
				null, RectangleEdge.TOP);
		DateTick dateTick11 = ((DateTick) dateTicks1.get(0));
		assertEquals(DATE1, dateTick11.getDate());
		assertEquals(LABEL1, dateTick11.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick11.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick11.getRotationAnchor());
		assertEquals(0, dateTick11.getAngle(), EPSILON);
		DateTick dateTick12 = ((DateTick) dateTicks1.get(1));
		assertEquals(DATE2, dateTick12.getDate());
		assertEquals(LABEL2, dateTick12.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick12.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick12.getRotationAnchor());
		assertEquals(0, dateTick12.getAngle(), EPSILON);
		try {
			dateTicks1.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.BOTTOM ***** */
		List<Tick> dateTicks2 = timeseriesDateAxis.refreshTicksHorizontal(null,
				null, RectangleEdge.BOTTOM);
		DateTick dateTick21 = ((DateTick) dateTicks2.get(0));
		assertEquals(DATE1, dateTick21.getDate());
		assertEquals(LABEL1, dateTick21.getText());
		assertEquals(TextAnchor.TOP_CENTER, dateTick21.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, dateTick21.getRotationAnchor());
		assertEquals(0, dateTick21.getAngle(), EPSILON);
		DateTick dateTick22 = ((DateTick) dateTicks2.get(1));
		assertEquals(DATE2, dateTick22.getDate());
		assertEquals(LABEL2, dateTick22.getText());
		assertEquals(TextAnchor.TOP_CENTER, dateTick22.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, dateTick22.getRotationAnchor());
		assertEquals(0, dateTick22.getAngle(), EPSILON);
		try {
			dateTicks2.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.LEFT ***** */
		List<Tick> dateTicks3 = timeseriesDateAxis.refreshTicksHorizontal(null,
				null, RectangleEdge.LEFT);
		DateTick dateTick31 = ((DateTick) dateTicks3.get(0));
		assertEquals(DATE1, dateTick31.getDate());
		assertEquals(LABEL1, dateTick31.getText());
		assertEquals(TextAnchor.TOP_CENTER, dateTick31.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, dateTick31.getRotationAnchor());
		assertEquals(0, dateTick31.getAngle(), EPSILON);
		DateTick dateTick32 = ((DateTick) dateTicks3.get(1));
		assertEquals(DATE2, dateTick32.getDate());
		assertEquals(LABEL2, dateTick32.getText());
		assertEquals(TextAnchor.TOP_CENTER, dateTick32.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, dateTick32.getRotationAnchor());
		assertEquals(0, dateTick32.getAngle(), EPSILON);
		try {
			dateTicks3.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.RIGHT ***** */
		List<Tick> dateTicks5 = timeseriesDateAxis.refreshTicksHorizontal(null,
				null, RectangleEdge.RIGHT);
		DateTick dateTick51 = ((DateTick) dateTicks5.get(0));
		assertEquals(DATE1, dateTick51.getDate());
		assertEquals(LABEL1, dateTick51.getText());
		assertEquals(TextAnchor.TOP_CENTER, dateTick51.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, dateTick51.getRotationAnchor());
		assertEquals(0, dateTick51.getAngle(), EPSILON);
		DateTick dateTick52 = ((DateTick) dateTicks5.get(1));
		assertEquals(DATE2, dateTick52.getDate());
		assertEquals(LABEL2, dateTick52.getText());
		assertEquals(TextAnchor.TOP_CENTER, dateTick52.getTextAnchor());
		assertEquals(TextAnchor.TOP_CENTER, dateTick52.getRotationAnchor());
		assertEquals(0, dateTick52.getAngle(), EPSILON);
		try {
			dateTicks5.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ===== vertical tick label ===== */
		timeseriesDateAxis.setVerticalTickLabels(true);

		/* ***** RectangleEdge.TOP ***** */
		dateTicks1 = timeseriesDateAxis.refreshTicksHorizontal(null, null,
				RectangleEdge.TOP);
		dateTick11 = ((DateTick) dateTicks1.get(0));
		assertEquals(DATE1, dateTick11.getDate());
		assertEquals(LABEL1, dateTick11.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick11.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick11.getRotationAnchor());
		assertEquals(Math.PI / 2.0, dateTick11.getAngle(), EPSILON);
		dateTick12 = ((DateTick) dateTicks1.get(1));
		assertEquals(DATE2, dateTick12.getDate());
		assertEquals(LABEL2, dateTick12.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick12.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick12.getRotationAnchor());
		assertEquals(Math.PI / 2.0, dateTick12.getAngle(), EPSILON);
		try {
			dateTicks1.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.BOTTOM ***** */
		dateTicks2 = timeseriesDateAxis.refreshTicksHorizontal(null, null,
				RectangleEdge.BOTTOM);
		dateTick21 = ((DateTick) dateTicks2.get(0));
		assertEquals(DATE1, dateTick21.getDate());
		assertEquals(LABEL1, dateTick21.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick21.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick21.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, dateTick21.getAngle(), EPSILON);
		dateTick22 = ((DateTick) dateTicks2.get(1));
		assertEquals(DATE2, dateTick22.getDate());
		assertEquals(LABEL2, dateTick22.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick22.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick22.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, dateTick22.getAngle(), EPSILON);
		try {
			dateTicks2.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.LEFT ***** */
		dateTicks3 = timeseriesDateAxis.refreshTicksHorizontal(null, null,
				RectangleEdge.LEFT);
		dateTick31 = ((DateTick) dateTicks3.get(0));
		assertEquals(DATE1, dateTick31.getDate());
		assertEquals(LABEL1, dateTick31.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick31.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick31.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, dateTick31.getAngle(), EPSILON);
		dateTick32 = ((DateTick) dateTicks3.get(1));
		assertEquals(DATE2, dateTick32.getDate());
		assertEquals(LABEL2, dateTick32.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick32.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick32.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, dateTick32.getAngle(), EPSILON);
		try {
			dateTicks3.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.RIGHT ***** */
		dateTicks5 = timeseriesDateAxis.refreshTicksHorizontal(null, null,
				RectangleEdge.RIGHT);
		dateTick51 = ((DateTick) dateTicks5.get(0));
		assertEquals(DATE1, dateTick51.getDate());
		assertEquals(LABEL1, dateTick51.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick51.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick51.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, dateTick51.getAngle(), EPSILON);
		dateTick52 = ((DateTick) dateTicks5.get(1));
		assertEquals(DATE2, dateTick52.getDate());
		assertEquals(LABEL2, dateTick52.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick52.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick52.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, dateTick52.getAngle(), EPSILON);
		try {
			dateTicks5.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}
	}

	@Test
	public void testRefreshTicksVertical() {
		/* ===== horizontal tick label ===== */
		timeseriesDateAxis.setVerticalTickLabels(false);

		/* ***** RectangleEdge.LEFT ***** */
		List<Tick> dateTicks1 = timeseriesDateAxis.refreshTicksVertical(null,
				null, RectangleEdge.LEFT);
		DateTick dateTick11 = ((DateTick) dateTicks1.get(0));
		assertEquals(DATE1, dateTick11.getDate());
		assertEquals(LABEL1, dateTick11.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick11.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick11.getRotationAnchor());
		assertEquals(0, dateTick11.getAngle(), EPSILON);
		DateTick dateTick12 = ((DateTick) dateTicks1.get(1));
		assertEquals(DATE2, dateTick12.getDate());
		assertEquals(LABEL2, dateTick12.getText());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick12.getTextAnchor());
		assertEquals(TextAnchor.CENTER_RIGHT, dateTick12.getRotationAnchor());
		assertEquals(0, dateTick12.getAngle(), EPSILON);
		try {
			dateTicks1.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.BOTTOM ***** */
		List<Tick> dateTicks2 = timeseriesDateAxis.refreshTicksVertical(null,
				null, RectangleEdge.BOTTOM);
		DateTick dateTick21 = ((DateTick) dateTicks2.get(0));
		assertEquals(DATE1, dateTick21.getDate());
		assertEquals(LABEL1, dateTick21.getText());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick21.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick21.getRotationAnchor());
		assertEquals(0, dateTick21.getAngle(), EPSILON);
		DateTick dateTick22 = ((DateTick) dateTicks2.get(1));
		assertEquals(DATE2, dateTick22.getDate());
		assertEquals(LABEL2, dateTick22.getText());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick22.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick22.getRotationAnchor());
		assertEquals(0, dateTick22.getAngle(), EPSILON);
		try {
			dateTicks2.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.TOP ***** */
		List<Tick> dateTicks3 = timeseriesDateAxis.refreshTicksVertical(null,
				null, RectangleEdge.TOP);
		DateTick dateTick31 = ((DateTick) dateTicks3.get(0));
		assertEquals(DATE1, dateTick31.getDate());
		assertEquals(LABEL1, dateTick31.getText());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick31.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick31.getRotationAnchor());
		assertEquals(0, dateTick31.getAngle(), EPSILON);
		DateTick dateTick32 = ((DateTick) dateTicks3.get(1));
		assertEquals(DATE2, dateTick32.getDate());
		assertEquals(LABEL2, dateTick32.getText());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick32.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick32.getRotationAnchor());
		assertEquals(0, dateTick32.getAngle(), EPSILON);
		try {
			dateTicks3.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.RIGHT ***** */
		List<Tick> dateTicks5 = timeseriesDateAxis.refreshTicksVertical(null,
				null, RectangleEdge.RIGHT);
		DateTick dateTick51 = ((DateTick) dateTicks5.get(0));
		assertEquals(DATE1, dateTick51.getDate());
		assertEquals(LABEL1, dateTick51.getText());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick51.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick51.getRotationAnchor());
		assertEquals(0, dateTick51.getAngle(), EPSILON);
		DateTick dateTick52 = ((DateTick) dateTicks5.get(1));
		assertEquals(DATE2, dateTick52.getDate());
		assertEquals(LABEL2, dateTick52.getText());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick52.getTextAnchor());
		assertEquals(TextAnchor.CENTER_LEFT, dateTick52.getRotationAnchor());
		assertEquals(0, dateTick52.getAngle(), EPSILON);
		try {
			dateTicks5.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ===== vertical tick label ===== */
		timeseriesDateAxis.setVerticalTickLabels(true);

		/* ***** RectangleEdge.LEFT ***** */
		dateTicks1 = timeseriesDateAxis.refreshTicksVertical(null, null,
				RectangleEdge.LEFT);
		dateTick11 = ((DateTick) dateTicks1.get(0));
		assertEquals(DATE1, dateTick11.getDate());
		assertEquals(LABEL1, dateTick11.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick11.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick11.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, dateTick11.getAngle(), EPSILON);
		dateTick12 = ((DateTick) dateTicks1.get(1));
		assertEquals(DATE2, dateTick12.getDate());
		assertEquals(LABEL2, dateTick12.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick12.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick12.getRotationAnchor());
		assertEquals(-Math.PI / 2.0, dateTick12.getAngle(), EPSILON);
		try {
			dateTicks1.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.BOTTOM ***** */
		dateTicks2 = timeseriesDateAxis.refreshTicksVertical(null, null,
				RectangleEdge.BOTTOM);
		dateTick21 = ((DateTick) dateTicks2.get(0));
		assertEquals(DATE1, dateTick21.getDate());
		assertEquals(LABEL1, dateTick21.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick21.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick21.getRotationAnchor());
		assertEquals(Math.PI / 2.0, dateTick21.getAngle(), EPSILON);
		dateTick22 = ((DateTick) dateTicks2.get(1));
		assertEquals(DATE2, dateTick22.getDate());
		assertEquals(LABEL2, dateTick22.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick22.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick22.getRotationAnchor());
		assertEquals(Math.PI / 2.0, dateTick22.getAngle(), EPSILON);
		try {
			dateTicks2.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.TOP ***** */
		dateTicks3 = timeseriesDateAxis.refreshTicksVertical(null, null,
				RectangleEdge.TOP);
		dateTick31 = ((DateTick) dateTicks3.get(0));
		assertEquals(DATE1, dateTick31.getDate());
		assertEquals(LABEL1, dateTick31.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick31.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick31.getRotationAnchor());
		assertEquals(Math.PI / 2.0, dateTick31.getAngle(), EPSILON);
		dateTick32 = ((DateTick) dateTicks3.get(1));
		assertEquals(DATE2, dateTick32.getDate());
		assertEquals(LABEL2, dateTick32.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick32.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick32.getRotationAnchor());
		assertEquals(Math.PI / 2.0, dateTick32.getAngle(), EPSILON);
		try {
			dateTicks3.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}

		/* ***** RectangleEdge.RIGHT ***** */
		dateTicks5 = timeseriesDateAxis.refreshTicksVertical(null, null,
				RectangleEdge.RIGHT);
		dateTick51 = ((DateTick) dateTicks5.get(0));
		assertEquals(DATE1, dateTick51.getDate());
		assertEquals(LABEL1, dateTick51.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick51.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick51.getRotationAnchor());
		assertEquals(Math.PI / 2.0, dateTick51.getAngle(), EPSILON);
		dateTick52 = ((DateTick) dateTicks5.get(1));
		assertEquals(DATE2, dateTick52.getDate());
		assertEquals(LABEL2, dateTick52.getText());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick52.getTextAnchor());
		assertEquals(TextAnchor.BOTTOM_CENTER, dateTick52.getRotationAnchor());
		assertEquals(Math.PI / 2.0, dateTick52.getAngle(), EPSILON);
		try {
			dateTicks5.get(2);
			fail("Index should be out of bound");
		} catch (Exception expected) {
			assertTrue(true);
		}
	}
}
