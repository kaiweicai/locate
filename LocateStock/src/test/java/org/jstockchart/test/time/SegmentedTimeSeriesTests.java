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

package org.jstockchart.test.time;

import static org.junit.Assert.assertEquals;

import java.util.TimeZone;

import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;
import org.junit.Test;

import com.locate.stock.time.SegmentedTimeSeries;
import com.locate.stock.util.DateUtils;

/**
 * Tests for <code>SegmentedTimeSeries</code> class.
 * 
 * @author Sha Jiang
 */
public class SegmentedTimeSeriesTests {

	@Test
	public void testAddItem1() {
		SegmentedTimeSeries segmentedTimeSeries = new SegmentedTimeSeries(
				"Test Timeseries", 1, null);

		RegularTimePeriod time1 = RegularTimePeriod.createInstance(
				Minute.class, DateUtils.createDate(2008, 1, 1, 11, 30),
				TimeZone.getDefault());
		TimeSeriesDataItem item1 = new TimeSeriesDataItem(time1, 21);
		segmentedTimeSeries.addItem(item1);
		assertEquals(segmentedTimeSeries.size(), 1);

		RegularTimePeriod time2 = RegularTimePeriod.createInstance(
				Minute.class, DateUtils.createDate(2008, 1, 1, 11, 32),
				TimeZone.getDefault());
		TimeSeriesDataItem item2 = new TimeSeriesDataItem(time2, 21);
		segmentedTimeSeries.addItem(item2);
		assertEquals(segmentedTimeSeries.size(), 3);
	}

	@Test
	public void testAddItem2() {
		SegmentedTimeline timeline = new SegmentedTimeline(
				SegmentedTimeline.MINUTE_SEGMENT_SIZE, 1351, 89);
		timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + 780
				* SegmentedTimeline.MINUTE_SEGMENT_SIZE);
		SegmentedTimeSeries segmentedTimeSeries = new SegmentedTimeSeries(
				"Test Timeseries", 1, timeline);

		RegularTimePeriod time1 = RegularTimePeriod.createInstance(
				Minute.class, DateUtils.createDate(2008, 1, 1, 11, 30),
				TimeZone.getDefault());
		TimeSeriesDataItem item1 = new TimeSeriesDataItem(time1, 21);
		segmentedTimeSeries.addItem(item1);
		assertEquals(segmentedTimeSeries.size(), 1);

		RegularTimePeriod time2 = RegularTimePeriod.createInstance(
				Minute.class, DateUtils.createDate(2008, 1, 1, 11, 32),
				TimeZone.getDefault());
		TimeSeriesDataItem item2 = new TimeSeriesDataItem(time2, 21);
		segmentedTimeSeries.addItem(item2);
		assertEquals(segmentedTimeSeries.size(), 2);
	}

	@Test
	public void testAddItem3() {
		SegmentedTimeline timeline = new SegmentedTimeline(
				SegmentedTimeline.MINUTE_SEGMENT_SIZE, 1351, 89);
		timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + 780
				* SegmentedTimeline.MINUTE_SEGMENT_SIZE);
		SegmentedTimeSeries segmentedTimeSeries = new SegmentedTimeSeries(
				"Test Timeseries", 5, timeline);

		RegularTimePeriod time1 = RegularTimePeriod.createInstance(
				Minute.class, DateUtils.createDate(2008, 1, 1, 10, 30),
				TimeZone.getDefault());
		TimeSeriesDataItem item1 = new TimeSeriesDataItem(time1, 21);
		segmentedTimeSeries.addItem(item1);
		assertEquals(segmentedTimeSeries.size(), 1);

		RegularTimePeriod time2 = RegularTimePeriod.createInstance(
				Minute.class, DateUtils.createDate(2008, 1, 1, 10, 33),
				TimeZone.getDefault());
		TimeSeriesDataItem item2 = new TimeSeriesDataItem(time2, 21);
		segmentedTimeSeries.addItem(item2);
		assertEquals(segmentedTimeSeries.size(), 2);

		RegularTimePeriod time3 = RegularTimePeriod.createInstance(
				Minute.class, DateUtils.createDate(2008, 1, 1, 10, 39),
				TimeZone.getDefault());
		TimeSeriesDataItem item3 = new TimeSeriesDataItem(time3, 21);
		segmentedTimeSeries.addItem(item3);
		assertEquals(segmentedTimeSeries.size(), 4);
	}
}
