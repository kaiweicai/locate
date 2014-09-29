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

package com.locate.stock.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTick;
import org.jfree.chart.axis.Tick;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.locate.stock.axis.logic.LogicDateTick;
import com.locate.stock.util.ChartConst;

/**
 * <code>TimeseriesDateAxis</code> extends <code>DateAxis</code>, and provides
 * <code>DateTick</code> for the axis.
 * 
 * @author Sha Jiang
 */
public class TimeseriesDateAxis extends DateAxis {

	private static final long serialVersionUID = 4021216012631598964L;

	private List<LogicDateTick> logicTicks = null;

	/**
	 * Creates a new <code>TimeseriesDateAxis</code> instance.
	 * 
	 * @param label
	 *            the axis label.
	 * @param logicTicks
	 *            the logic date ticks.
	 * @param zone
	 *            the time zone.
	 */
	public TimeseriesDateAxis(String label, List<LogicDateTick> logicTicks,
			TimeZone zone, Locale locale) {
		super(label, zone, locale);
		defaultSetting();

		if (logicTicks == null || logicTicks.size() < 1) {
			throw new IllegalArgumentException(
					"Null 'logicTicks' argement; or 'logicTicks.size' < 1.");
		}
		this.logicTicks = logicTicks;
	}

	/**
	 * Creates a new <code>TimeseriesDateAxis</code> instance.
	 * 
	 * @param lable
	 *            the axis label.
	 * @param logicTicks
	 *            the logic date ticks.
	 */
	public TimeseriesDateAxis(String lable, List<LogicDateTick> logicTicks) {
		this(lable, logicTicks, TimeZone.getDefault(), Locale.getDefault());
	}

	/**
	 * Creates a new <code>TimeseriesDateAxis</code> instance.
	 * 
	 * @param logicTicks
	 *            the logic date ticks.
	 */
	public TimeseriesDateAxis(List<LogicDateTick> logicTicks) {
		this(null, logicTicks, TimeZone.getDefault(), Locale.getDefault());
	}

	public List<Tick> refreshTicksHorizontal(Graphics2D g2,
			Rectangle2D dataArea, RectangleEdge edge) {
		TextAnchor anchor = null;
		TextAnchor rotationAnchor = null;
		double angle = 0.0;
		if (isVerticalTickLabels()) {
			anchor = TextAnchor.CENTER_RIGHT;
			rotationAnchor = TextAnchor.CENTER_RIGHT;
			if (edge == RectangleEdge.TOP) {
				angle = Math.PI / 2.0;
			} else {
				angle = -Math.PI / 2.0;
			}
		} else {
			if (edge == RectangleEdge.TOP) {
				anchor = TextAnchor.BOTTOM_CENTER;
				rotationAnchor = TextAnchor.BOTTOM_CENTER;
			} else {
				anchor = TextAnchor.TOP_CENTER;
				rotationAnchor = TextAnchor.TOP_CENTER;
			}
		}
		return createTicksHorizontal(logicTicks, anchor, rotationAnchor, angle);
	}

	private static List<Tick> createTicksHorizontal(
			List<LogicDateTick> logicDateTicks, TextAnchor anchor,
			TextAnchor rotationAnchor, double angle) {
		List<Tick> result = new ArrayList<Tick>();
		for (int i = 0; i < logicDateTicks.size(); i++) {
			LogicDateTick buf = (LogicDateTick) logicDateTicks.get(i);
			TickAlignment tickAlignment = buf.getTickAlignment();
			result.add(new DateTick(buf.getTickDate(), buf.getTickLabel(),
					adjustAnchorHorizontal(anchor, tickAlignment),
					adjustAnchorHorizontal(rotationAnchor, tickAlignment),
					angle));
		}

		return result;
	}

	private static TextAnchor adjustAnchorHorizontal(TextAnchor anchor,
			TickAlignment alignment) {
		TextAnchor result = null;
		if (alignment.equals(TickAlignment.MID)) {
			result = anchor;
		} else if (alignment.equals(TickAlignment.START)) {
			if (anchor.equals(TextAnchor.CENTER_RIGHT)) {
				result = TextAnchor.CENTER_RIGHT;
			} else if (anchor.equals(TextAnchor.BOTTOM_CENTER)) {
				result = TextAnchor.BOTTOM_LEFT;
			} else if (anchor.equals(TextAnchor.TOP_CENTER)) {
				result = TextAnchor.TOP_LEFT;
			} else {
				result = anchor;
			}
		} else if (alignment.equals(TickAlignment.END)) {
			if (anchor.equals(TextAnchor.CENTER_RIGHT)) {
				result = TextAnchor.CENTER_RIGHT;
			} else if (anchor.equals(TextAnchor.BOTTOM_CENTER)) {
				result = TextAnchor.BOTTOM_RIGHT;
			} else if (anchor.equals(TextAnchor.TOP_CENTER)) {
				result = TextAnchor.TOP_RIGHT;
			} else {
				result = anchor;
			}
		}

		return result;
	}

	public List<Tick> refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea,
			RectangleEdge edge) {
		TextAnchor anchor = null;
		TextAnchor rotationAnchor = null;
		double angle = 0.0;
		if (isVerticalTickLabels()) {
			anchor = TextAnchor.BOTTOM_CENTER;
			rotationAnchor = TextAnchor.BOTTOM_CENTER;
			if (edge == RectangleEdge.LEFT) {
				angle = -Math.PI / 2.0;
			} else {
				angle = Math.PI / 2.0;
			}
		} else {
			if (edge == RectangleEdge.LEFT) {
				anchor = TextAnchor.CENTER_RIGHT;
				rotationAnchor = TextAnchor.CENTER_RIGHT;
			} else {
				anchor = TextAnchor.CENTER_LEFT;
				rotationAnchor = TextAnchor.CENTER_LEFT;
			}
		}
		return createTicksVertical(logicTicks, anchor, rotationAnchor, angle);
	}

	private static List<Tick> createTicksVertical(
			List<LogicDateTick> logicDateTicks, TextAnchor anchor,
			TextAnchor rotationAnchor, double angle) {
		List<Tick> result = new ArrayList<Tick>();
		for (int i = 0; i < logicDateTicks.size(); i++) {
			LogicDateTick buf = logicDateTicks.get(i);
			TickAlignment tickAlignment = buf.getTickAlignment();
			result
					.add(new DateTick(
							buf.getTickDate(),
							buf.getTickLabel(),
							adjustAnchorVertical(anchor, tickAlignment),
							adjustAnchorVertical(rotationAnchor, tickAlignment),
							angle));
		}

		return result;
	}

	private static TextAnchor adjustAnchorVertical(TextAnchor anchor,
			TickAlignment alignment) {
		TextAnchor result = null;
		if (alignment.equals(TickAlignment.MID)) {
			result = anchor;
		} else if (alignment.equals(TickAlignment.START)) {
			if (anchor.equals(TextAnchor.BOTTOM_CENTER)) {
				result = TextAnchor.BOTTOM_CENTER;
			} else if (anchor.equals(TextAnchor.CENTER_RIGHT)) {
				result = TextAnchor.BOTTOM_RIGHT;
			} else if (anchor.equals(TextAnchor.CENTER_LEFT)) {
				result = TextAnchor.BOTTOM_LEFT;
			} else {
				result = anchor;
			}
		} else if (alignment.equals(TickAlignment.END)) {
			if (anchor.equals(TextAnchor.BOTTOM_CENTER)) {
				result = TextAnchor.BOTTOM_CENTER;
			} else if (anchor.equals(TextAnchor.CENTER_RIGHT)) {
				result = TextAnchor.TOP_RIGHT;
			} else if (anchor.equals(TextAnchor.CENTER_LEFT)) {
				result = TextAnchor.TOP_LEFT;
			} else {
				result = anchor;
			}
		}

		return result;
	}

	private void defaultSetting() {
		setLowerMargin(ChartConst.DEFAULT_NUMBER_AXIS_MARGIN);
		setUpperMargin(ChartConst.DEFAULT_NUMBER_AXIS_MARGIN);
		setTickLabelFont(ChartConst.DEFAULT_DATE_TICK_LABEL_FONT);
		setTickLabelPaint(ChartConst.DEFAULT_DATE_TICK_LABEL_COLOR);
	}
}
