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

package com.locate.stock.plot;

import java.awt.BasicStroke;

import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;

import com.locate.stock.area.PriceArea;
import com.locate.stock.area.TimeseriesArea;
import com.locate.stock.area.VolumeArea;
import com.locate.stock.axis.TimeseriesDateAxis;
import com.locate.stock.axis.TimeseriesNumberAxis;
import com.locate.stock.axis.logic.CentralValueAxis;
import com.locate.stock.axis.logic.LogicDateAxis;
import com.locate.stock.axis.logic.LogicNumberAxis;
import com.locate.stock.dataset.TimeseriesDataset;

/**
 * Creates <code>CombinedDomainXYPlot</code> and <code>XYPlot</code> for the
 * timeseries chart.
 * 
 * @author Sha Jiang
 */
public class TimeseriesPlot {

	private static final long serialVersionUID = 8799771872991017065L;

	private TimeseriesDataset dataset = null;

	private SegmentedTimeline timeline = null;

	private TimeseriesArea timeseriesArea = null;

	/**
	 * Creates a new <code>TimeseriesPlot</code> instance.
	 * 
	 * @param dataset
	 *            timeseries data set(<code>null</code> not permitted).
	 * @param timeline
	 *            a "segmented" timeline.
	 * @param timeseriesArea
	 *            timeseries area.
	 */
	public TimeseriesPlot(TimeseriesDataset dataset,
			SegmentedTimeline timeline, TimeseriesArea timeseriesArea) {
		if (dataset == null) {
			throw new IllegalArgumentException("Null 'dataset' argument.");
		}
		this.dataset = dataset;

		this.timeline = timeline;

		if (timeseriesArea == null) {
			throw new IllegalArgumentException(
					"Null 'timeseriesArea' argument.");
		}
		this.timeseriesArea = timeseriesArea;
	}

	private CombinedDomainXYPlot createCombinedXYPlot() {
		LogicDateAxis logicDateAxis = timeseriesArea.getlogicDateAxis();
		TimeseriesDateAxis dateAxis = new TimeseriesDateAxis(logicDateAxis
				.getLogicTicks());
		if (timeline != null) {
			dateAxis.setTimeline(timeline);
		}

		CombinedDomainXYPlot combinedDomainXYPlot = new CombinedDomainXYPlot(
				dateAxis);
		combinedDomainXYPlot.setGap(timeseriesArea.getGap());
		combinedDomainXYPlot.setOrientation(timeseriesArea.getOrientation());
		combinedDomainXYPlot.setDomainAxis(dateAxis);
		combinedDomainXYPlot.setDomainAxisLocation(timeseriesArea
				.getDateAxisLocation());

		if (timeseriesArea.getPriceWeight() <= 0
				&& timeseriesArea.getVolumeWeight() <= 0) {
			throw new IllegalArgumentException(
					"Illegal weight value: priceWeight="
							+ timeseriesArea.getPriceWeight()
							+ ", volumeWeight="
							+ timeseriesArea.getVolumeWeight());
		}

		if (timeseriesArea.getPriceWeight() > 0) {
			XYPlot pricePlot = createPricePlot();
			combinedDomainXYPlot
					.add(pricePlot, timeseriesArea.getPriceWeight());
		}

		if (timeseriesArea.getVolumeWeight() > 0) {
			XYPlot volumePlot = createVolumePlot();
			combinedDomainXYPlot.add(volumePlot, timeseriesArea
					.getVolumeWeight());
		}

		return combinedDomainXYPlot;
	}

	private XYPlot createPricePlot() {
		PriceArea priceArea = timeseriesArea.getPriceArea();
		TimeSeriesCollection priceDataset = new TimeSeriesCollection();
		priceDataset.addSeries(dataset.getPriceTimeSeries().getTimeSeries());
		if (priceArea.isAverageVisible()) {
			priceDataset.addSeries(dataset.getAverageTimeSeries()
					.getTimeSeries());
		}

		CentralValueAxis logicPriceAxis = priceArea.getLogicPriceAxis();
		TimeseriesNumberAxis priceAxis = new TimeseriesNumberAxis(
				logicPriceAxis.getLogicTicks());
		XYLineAndShapeRenderer priceRenderer = new XYLineAndShapeRenderer(true,
				false);
		priceAxis.setUpperBound(logicPriceAxis.getUpperBound());
		priceAxis.setLowerBound(logicPriceAxis.getLowerBound());
		priceRenderer.setSeriesPaint(0, priceArea.getPriceColor());
		priceRenderer.setSeriesPaint(1, priceArea.getAverageColor());

		TimeseriesNumberAxis rateAxis = new TimeseriesNumberAxis(logicPriceAxis
				.getRatelogicTicks());
		rateAxis.setUpperBound(logicPriceAxis.getUpperBound());
		rateAxis.setLowerBound(logicPriceAxis.getLowerBound());

		XYPlot plot = new XYPlot(priceDataset, null, priceAxis, priceRenderer);
		plot.setBackgroundPaint(priceArea.getBackgroudColor());
		plot.setOrientation(priceArea.getOrientation());
		plot.setRangeAxisLocation(priceArea.getPriceAxisLocation());

		if (priceArea.isRateVisible()) {
			plot.setRangeAxis(1, rateAxis);
			plot.setRangeAxisLocation(1, priceArea.getRateAxisLocation());
			plot.setDataset(1, null);
			plot.mapDatasetToRangeAxis(1, 1);
		}

		if (priceArea.isMarkCentralValue()) {
			Number centralPrice = logicPriceAxis.getCentralValue();
			if (centralPrice != null) {
				plot.addRangeMarker(new ValueMarker(centralPrice.doubleValue(),
						priceArea.getCentralPriceColor(), new BasicStroke()));
			}
		}
		return plot;
	}

	private XYPlot createVolumePlot() {
		VolumeArea volumeArea = timeseriesArea.getVolumeArea();
		LogicNumberAxis logicVolumeAxis = volumeArea.getLogicVolumeAxis();

		TimeseriesNumberAxis volumeAxis = new TimeseriesNumberAxis(
				logicVolumeAxis.getLogicTicks());
		volumeAxis.setUpperBound(logicVolumeAxis.getUpperBound());
		volumeAxis.setLowerBound(logicVolumeAxis.getLowerBound());
		volumeAxis.setAutoRangeIncludesZero(false);
		XYBarRenderer volumeRenderer = new XYBarRenderer();
		volumeRenderer.setSeriesPaint(0, volumeArea.getVolumeColor());
		volumeRenderer.setShadowVisible(false);

		XYPlot plot = new XYPlot(new TimeSeriesCollection(dataset
				.getVolumeTimeSeries()), null, volumeAxis, volumeRenderer);
		plot.setBackgroundPaint(volumeArea.getBackgroudColor());
		plot.setOrientation(volumeArea.getOrientation());
		plot.setRangeAxisLocation(volumeArea.getVolumeAxisLocation());
		return plot;
	}

	public CombinedDomainXYPlot getTimeseriesPlot() {
		return createCombinedXYPlot();
	}

	public TimeseriesDataset getDataset() {
		return dataset;
	}

	public void setDataset(TimeseriesDataset dataset) {
		if (dataset == null) {
			throw new IllegalArgumentException("Null 'dataset' argument.");
		}
		this.dataset = dataset;
	}

	public SegmentedTimeline getTimeline() {
		return timeline;
	}

	public void setTimeline(SegmentedTimeline timeline) {
		this.timeline = timeline;
	}

	public TimeseriesArea getTimeseriesArea() {
		return timeseriesArea;
	}

	public void setTimeseriesArea(TimeseriesArea timeseriesArea) {
		if (timeseriesArea == null) {
			throw new IllegalArgumentException(
					"Null 'timeseriesArea' argument.");
		}
		this.timeseriesArea = timeseriesArea;
	}
}
