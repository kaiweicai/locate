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

package org.jstockchart.test.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.junit.Test;

import com.locate.stock.dataset.TimeseriesDataset;
import com.locate.stock.model.TimeseriesItem;
import com.locate.stock.util.DateUtils;

/**
 * Tests for <code>TimeseriesDataset</code> class.
 * 
 * @author Sha Jiang
 */
public class TimeseriesDatasetTests {

	private static final double EPSILON = 0.00001;

	@Test
	public void testAddDataItem1() {
		TimeseriesDataset dataset = new TimeseriesDataset(Minute.class, 1,
				null, true);

		TimeseriesItem item1 = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1, 9, 30), 21.0, 50);
		dataset.addDataItem(item1);
		assertEquals(dataset.getMaxPrice().doubleValue(), 21, EPSILON);
		assertEquals(dataset.getMinPrice().doubleValue(), 21, EPSILON);
		assertEquals(dataset.getMaxVolume().doubleValue(), 50, EPSILON);
		assertEquals(dataset.getMinVolume().doubleValue(), 50, EPSILON);

		TimeSeries priceTimeSeries = dataset.getPriceTimeSeries()
				.getTimeSeries();
		TimeSeriesDataItem priceDataItem0 = priceTimeSeries.getDataItem(0);
		assertTrue(priceDataItem0.getPeriod().getStart().equals(
				DateUtils.createDate(2008, 1, 1, 9, 30)));
		assertEquals(priceDataItem0.getValue().doubleValue(), 21, EPSILON);

		TimeSeries averageTimeSeries = dataset.getAverageTimeSeries()
				.getTimeSeries();
		TimeSeriesDataItem averageDataItem0 = averageTimeSeries.getDataItem(0);
		assertTrue(averageDataItem0.getPeriod().getStart().equals(
				DateUtils.createDate(2008, 1, 1, 9, 30)));
		assertEquals(averageDataItem0.getValue().doubleValue(), 21, EPSILON);

		TimeSeries volumeTimeSeries = dataset.getVolumeTimeSeries();
		TimeSeriesDataItem volumeDataItem1 = volumeTimeSeries.getDataItem(0);
		assertTrue(volumeDataItem1.getPeriod().getStart().equals(
				DateUtils.createDate(2008, 1, 1, 9, 30)));
		assertEquals(volumeDataItem1.getValue().doubleValue(), 50, EPSILON);

		/* ************************************************************** */

		TimeseriesItem item2 = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1, 9, 31), 20, 40);
		dataset.addDataItem(item2);
		assertEquals(dataset.getMaxPrice().doubleValue(), 21, EPSILON);
		assertEquals(dataset.getMinPrice().doubleValue(), 20, EPSILON);
		assertEquals(dataset.getMaxVolume().doubleValue(), 50, EPSILON);
		assertEquals(dataset.getMinVolume().doubleValue(), 40, EPSILON);

		TimeSeriesDataItem averageDataItem2 = averageTimeSeries.getDataItem(1);
		assertEquals(averageDataItem2.getValue().doubleValue(), 20.55556,
				EPSILON);

		/* ************************************************************** */

		TimeseriesItem item3 = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1, 9, 33), 20, 100);
		dataset.addDataItem(item3);
		assertEquals(dataset.getMaxPrice().doubleValue(), 21, EPSILON);
		assertEquals(dataset.getMinPrice().doubleValue(), 20, EPSILON);
		assertEquals(dataset.getMaxVolume().doubleValue(), 100, EPSILON);
		assertEquals(dataset.getMinVolume().doubleValue(), 40, EPSILON);

		TimeSeriesDataItem averageDataItem3 = averageTimeSeries.getDataItem(2);
		assertEquals(averageDataItem3.getValue().doubleValue(), 20.55556,
				EPSILON);
		TimeSeriesDataItem averageDataItem4 = averageTimeSeries.getDataItem(3);
		assertEquals(averageDataItem4.getValue().doubleValue(), 20.26315,
				EPSILON);

		/* ************************************************************** */

		TimeseriesItem item4 = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1, 9, 36), 22, 100);
		dataset.addDataItem(item4);
		assertEquals(dataset.getMaxPrice().doubleValue(), 22, EPSILON);
		assertEquals(dataset.getMinPrice().doubleValue(), 20, EPSILON);
		assertEquals(dataset.getMaxVolume().doubleValue(), 100, EPSILON);
		assertEquals(dataset.getMinVolume().doubleValue(), 40, EPSILON);

		TimeSeriesDataItem averageDataItem5 = averageTimeSeries.getDataItem(4);
		assertEquals(averageDataItem5.getValue().doubleValue(), 20.26315,
				EPSILON);
		TimeSeriesDataItem averageDataItem6 = averageTimeSeries.getDataItem(5);
		assertEquals(averageDataItem6.getValue().doubleValue(), 20.26315,
				EPSILON);
		TimeSeriesDataItem averageDataItem7 = averageTimeSeries.getDataItem(6);
		assertEquals(averageDataItem7.getValue().doubleValue(), 20.86207,
				EPSILON);
	}

	@Test
	public void testAddDataItem2() {
		SegmentedTimeline timeline = new SegmentedTimeline(
				SegmentedTimeline.MINUTE_SEGMENT_SIZE, 1351, 89);
		timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + 780
				* SegmentedTimeline.MINUTE_SEGMENT_SIZE);
		TimeseriesDataset dataset = new TimeseriesDataset(Minute.class, 1,
				timeline, true);

		TimeseriesItem item1 = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1, 11, 30), 21.0, 50);
		dataset.addDataItem(item1);

		TimeseriesItem item2 = new TimeseriesItem(DateUtils.createDate(2008, 1,
				1, 11, 32), 21.0, 50);
		dataset.addDataItem(item2);

		TimeSeries priceTimeSeries = dataset.getPriceTimeSeries()
				.getTimeSeries();
		try {
			priceTimeSeries.getDataItem(2);
			fail("Don't add the 'missed' data that isn't in the segmented timeline.");
		} catch (IndexOutOfBoundsException expected) {
			assertTrue(true);
		}
	}
}
