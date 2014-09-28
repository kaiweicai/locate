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

package com.locate.stock.chart;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.data.Range;
import org.jfree.data.time.Minute;
import org.jstockchart.JStockChartFactory;
import org.jstockchart.area.PriceArea;
import org.jstockchart.area.TimeseriesArea;
import org.jstockchart.area.VolumeArea;
import org.jstockchart.axis.TickAlignment;
import org.jstockchart.axis.logic.CentralValueAxis;
import org.jstockchart.axis.logic.LogicDateAxis;
import org.jstockchart.axis.logic.LogicNumberAxis;
import org.jstockchart.dataset.TimeseriesDataset;
import org.jstockchart.model.TimeseriesItem;
import org.jstockchart.util.DateUtils;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.locate.stock.data.DataFactory;
import com.locate.stock.data.GlobalConfig;

/**
 * Demo application for JStockChart timeseries.
 * 
 * @author Sha Jiang
 */
public class TimeseriesChartDemo {

	public static void main(String[] args) throws IOException {
		String imageDir = "images";
		File images = new File(imageDir);
		if (!images.exists()) {
			images.mkdir();
		}
		String imageFile = imageDir + "/jstockchart-timeseries.png";

		String dbFile = "data/jstockchart-timeseries.db4o";
		ObjectContainer db = Db4o.openFile(GlobalConfig.globalConfig(), dbFile);
		DataFactory dataFactory = new DataFactory(db);
		Date startTime = DateUtils.createDate(2008, 1, 1, 9, 30, 0);
		Date endTime = DateUtils.createDate(2008, 1, 1, 15, 0, 0);
		// 'data' is a list of TimeseriesItem instances.
		List<TimeseriesItem> data = dataFactory.getTimeseriesItem(startTime,
				endTime, Calendar.MINUTE, 1);

		// the 'timeline' indicates the segmented time range '00:00-11:30, 13:00-24:00'.
		SegmentedTimeline timeline = new SegmentedTimeline(
				SegmentedTimeline.MINUTE_SEGMENT_SIZE, 1351, 89);
		timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + 780
				* SegmentedTimeline.MINUTE_SEGMENT_SIZE);

		// Creates timeseries data set.
		TimeseriesDataset dataset = new TimeseriesDataset(Minute.class, 1,
				timeline, true);
		dataset.addDataItems(data);

		// Creates logic price axis.
		CentralValueAxis logicPriceAxis = new CentralValueAxis(
				new Double("21"), new Range(
						dataset.getMinPrice().doubleValue(), dataset
								.getMaxPrice().doubleValue()), 9,
				new DecimalFormat(".00"));
		PriceArea priceArea = new PriceArea(logicPriceAxis);

		// Creates logic volume axis.
		LogicNumberAxis logicVolumeAxis = new LogicNumberAxis(new Range(dataset
				.getMinVolume().doubleValue(), dataset.getMaxVolume()
				.doubleValue()), 5, new DecimalFormat("0"));
		VolumeArea volumeArea = new VolumeArea(logicVolumeAxis);

		TimeseriesArea timeseriesArea = new TimeseriesArea(priceArea,
				volumeArea, createlogicDateAxis(DateUtils
						.createDate(2008, 1, 1)));

		JFreeChart jfreechart = JStockChartFactory.createTimeseriesChart(
				"comex3月铜行情走势图", dataset, timeline, timeseriesArea,
				false);
		db.close();
		ChartFrame chatFrame = new ChartFrame("股票图", jfreechart);
		chatFrame.pack();
		chatFrame.setVisible(true);
//		ChartUtilities
//				.saveChartAsPNG(new File(imageFile), jfreechart, 545, 300);
	}

	// Specifies date axis ticks.
	private static LogicDateAxis createlogicDateAxis(Date baseDate) {
		LogicDateAxis logicDateAxis = new LogicDateAxis(baseDate,
				new SimpleDateFormat("HH:mm"));
		logicDateAxis.addDateTick("09:30", TickAlignment.START);
		logicDateAxis.addDateTick("10:00");
		logicDateAxis.addDateTick("10:30");
		logicDateAxis.addDateTick("11:00");
		logicDateAxis.addDateTick("11:30", TickAlignment.END);
		logicDateAxis.addDateTick("13:00", TickAlignment.START);
		logicDateAxis.addDateTick("13:30");
		logicDateAxis.addDateTick("14:00");
		logicDateAxis.addDateTick("14:30", TickAlignment.END);
		logicDateAxis.addDateTick("15:00", TickAlignment.END);
		return logicDateAxis;
	}
}
