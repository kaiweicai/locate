package com.locate.stock.chart;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.data.Range;
import org.jfree.data.time.Second;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.locate.stock.JStockChartFactory;
import com.locate.stock.area.PriceArea;
import com.locate.stock.area.TimeseriesArea;
import com.locate.stock.area.VolumeArea;
import com.locate.stock.axis.TickAlignment;
import com.locate.stock.axis.logic.CentralValueAxis;
import com.locate.stock.axis.logic.LogicDateAxis;
import com.locate.stock.axis.logic.LogicNumberAxis;
import com.locate.stock.data.DataFactory;
import com.locate.stock.data.GlobalConfig;
import com.locate.stock.dataset.TimeseriesDataset;
import com.locate.stock.model.TimeseriesItem;
import com.locate.stock.util.DateUtils;

public class RealTimeChart {
	private static LogicDateAxis logicDateAxis = null;

	public void generateRealChart() {
		String imageDir = "./images";
		File images = new File(imageDir);
		if (!images.exists()) {
			images.mkdir();
		}
		String imageFile = imageDir + "/jstockchart-timeseries.png";

		String dbFile = "data/jstockchart-timeseries.db4o";
		ObjectContainer db = Db4o.openFile(GlobalConfig.globalConfig(), dbFile);
		DataFactory dataFactory = new DataFactory(db);
		Date startTime = DateUtils.beforeCurrentDate(10);
		Date endTime = DateUtils.currentDate();
		// 'data' is a list of TimeseriesItem instances.
		List<TimeseriesItem> dataList = new ArrayList<TimeseriesItem>();
		// the 'timeline' indicates the segmented time range '00:00-11:30,
		// 13:00-24:00'.
		long time = DateUtils.beforeCurrentDate(-3).getTime();
		System.out.println("-----------------" + new Date(time));
		double price = 17.8;
		double amount = 300;
		int x = -1;
		x = -x;
		double deltaPrice = new Random().nextDouble();
//		TimeseriesItem timeseriesItem = new TimeseriesItem(new Date(time), price += x * deltaPrice, amount += x
//				* (int) (Math.random() * 5 + 1));
		TimeseriesItem timeseriesItem = null;
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 6; j++) {
				timeseriesItem = new TimeseriesItem(new Date(time += 1000L), price,
						amount);
				dataList.add(timeseriesItem);
			}
		}
		System.out.println(dataList.size());
		System.out.println("```````````````````" + new Date(time));
		SegmentedTimeline timeline = new SegmentedTimeline(SegmentedTimeline.MINUTE_SEGMENT_SIZE, 1440, 0);
		timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900());

		// Creates timeseries data set.
		TimeseriesDataset dataset = new TimeseriesDataset(Second.class, 1, timeline, true);
		dataset.addDataItems(dataList);

		// Creates logic price axis.
		CentralValueAxis logicPriceAxis = new CentralValueAxis(new Double(dataList.get(0).getPrice()), new Range(
				dataset.getMinPrice().doubleValue() * 0.8, dataset.getMaxPrice().doubleValue() * 1.2), 9,
				new DecimalFormat(".00"));
		PriceArea priceArea = new PriceArea(logicPriceAxis);

		// Creates logic volume axis.
		LogicNumberAxis logicVolumeAxis = new LogicNumberAxis(new Range(dataset.getMinVolume().doubleValue(), dataset
				.getMaxVolume().doubleValue()), 5, new DecimalFormat("0"));
		VolumeArea volumeArea = new VolumeArea(logicVolumeAxis);

		TimeseriesArea timeseriesArea = new TimeseriesArea(priceArea, volumeArea,
				createlogicDateAxis(DateUtils.createDate(2014, 10, 8)));
		db.close();
		JFreeChart jfreechart = JStockChartFactory.createTimeseriesChart("comex3月铜行情走势图", dataset, timeline,
				timeseriesArea, true);
		
		ChartFrame chatFrame = new ChartFrame("股票图", jfreechart);
		chatFrame.pack();
		chatFrame.setVisible(true);

		for (int j = 0; j < 100000; j++) {
//			timeseriesArea.updateTimeSeriesArea(priceArea, volumeArea, updatelogicDateAxis());
			// Creates logic price axis.
			for(int l=0;l<3;l++){
			for (int k = 0; k < 60; k++) {
				System.out.println("%%%%%%%%%%%%%%%%%%%%"+dataset.size());
				x = -x;
				deltaPrice = new Random().nextDouble();
				timeseriesItem = new TimeseriesItem(new Date(time += 1000L), price += x * deltaPrice, amount += x
						* (int) (Math.random() * 5 + 1));
				dataset.pushDataItem(timeseriesItem);
				logicPriceAxis = new CentralValueAxis(new Double(dataList.get(0).getPrice()), new Range(
						dataset.getMinPrice().doubleValue() , dataset.getMaxPrice().doubleValue() ), 9,
						new DecimalFormat(".00"));
				priceArea = new PriceArea(logicPriceAxis);

				// Creates logic volume axis.
				logicVolumeAxis = new LogicNumberAxis(new Range(dataset.getMinVolume().doubleValue(), dataset
						.getMaxVolume().doubleValue()), 5, new DecimalFormat("0"));
				volumeArea = new VolumeArea(logicVolumeAxis);
				timeseriesArea = new TimeseriesArea(priceArea, volumeArea,
						createlogicDateAxis(DateUtils.createDate(2014, 10, 8)));
				jfreechart = JStockChartFactory.createTimeseriesChart("comex3月铜行情走势图", dataset, timeline,
						timeseriesArea, true);
				chatFrame.getChartPanel().setChart(jfreechart);
//				chatFrame = new ChartFrame("股票图", jfreechart);
//				chatFrame.pack();
//				chatFrame.setVisible(true);
				
				
//				logicPriceAxis.updateAxis(null, new Range(dataset.getMinPrice().doubleValue(), dataset.getMaxPrice()
//						.doubleValue()), 9, null);
//				priceArea.setlogicPriceAxis(logicPriceAxis);
//				logicVolumeAxis.updateAxis(new Range(dataset.getMinVolume().doubleValue(), dataset.getMaxVolume()
//						.doubleValue()), 5, new DecimalFormat("0"));
//				chatFrame.pack();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
		}
		// ChartUtilities
		// .saveChartAsPNG(new File(imageFile), jfreechart, 545, 300);
	}

	// Specifies date axis ticks.
	private static LogicDateAxis createlogicDateAxis(Date baseDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		logicDateAxis = new LogicDateAxis(baseDate, simpleDateFormat);
		int changedMinutes = -3;
		String currentDate = simpleDateFormat.format(DateUtils.beforeCurrentDate(changedMinutes++));
		System.out.println(currentDate);
		 logicDateAxis.addDateTick(currentDate, TickAlignment.START);
		currentDate = simpleDateFormat.format(DateUtils.beforeCurrentDate(changedMinutes++));
		System.out.println(currentDate);
		logicDateAxis.addDateTick(currentDate);
		currentDate = simpleDateFormat.format(DateUtils.beforeCurrentDate(changedMinutes++));
		System.out.println(currentDate);
		logicDateAxis.addDateTick(currentDate);
		currentDate = simpleDateFormat.format(DateUtils.beforeCurrentDate(changedMinutes++));
		System.out.println(currentDate);
		logicDateAxis.addDateTick(currentDate, TickAlignment.END);
		return logicDateAxis;
	}

	private static LogicDateAxis updatelogicDateAxis() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		LogicDateAxis logicDateAxis = new LogicDateAxis(DateUtils.createDate(2014, 10, 7),
				simpleDateFormat);
		int changedMinutes = -3;
		String currentDate = simpleDateFormat.format(DateUtils.beforeCurrentDate(changedMinutes++));
		logicDateAxis.pushDateTick(currentDate);
//		int changedMinutes = -3;
//		String currentDate = simpleDateFormat.format(DateUtils.beforeCurrentDate(changedMinutes++));
//		System.out.println(currentDate);
//		logicDateAxis.addDateTick(currentDate, TickAlignment.START);
//		currentDate = simpleDateFormat.format(DateUtils.beforeCurrentDate(changedMinutes++));
//		System.out.println(currentDate);
//		logicDateAxis.addDateTick(currentDate);
//		currentDate = simpleDateFormat.format(DateUtils.beforeCurrentDate(changedMinutes++));
//		System.out.println(currentDate);
//		logicDateAxis.addDateTick(currentDate);
//		currentDate = simpleDateFormat.format(DateUtils.beforeCurrentDate(changedMinutes++));
//		System.out.println(currentDate);
//		logicDateAxis.addDateTick(currentDate, TickAlignment.END);
		return logicDateAxis;
	}

	public static void main(String[] args) {
		Date time = new Date(SegmentedTimeline.firstMondayAfter1900() + 780 * SegmentedTimeline.MINUTE_SEGMENT_SIZE);
		System.out.println(time);
		RealTimeChart realChart = new RealTimeChart();
		realChart.generateRealChart();
	}
}
