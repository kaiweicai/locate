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

package com.locate.stock.axis.logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.locate.stock.axis.TickAlignment;

/**
 * <code>LogicDateAxis</code> represents logic date axis.
 * 
 * @author Sha Jiang
 */
public class LogicDateAxis extends AbstractLogicAxis {

	private static final long serialVersionUID = 4580299807368807495L;

	private List<LogicDateTick> ticks = new ArrayList<LogicDateTick>();

	private DateFormat formatter = null;

	private Calendar calendar = Calendar.getInstance();

	private Date baseDate = null;

	public LogicDateAxis(Date baseDate, DateFormat formatter) {
		if (baseDate == null) {
			throw new IllegalArgumentException("Null 'baseDate' argumented.");
		}
		this.baseDate = baseDate;

		if (formatter == null) {
			throw new IllegalArgumentException("Null 'formatter' argumented.");
		}
		this.formatter = formatter;
	}

	public void addDateTick(String timeStr, TickAlignment tickAlignment) {
		ticks
				.add(new LogicDateTick(parseTime(timeStr), timeStr,
						tickAlignment));
	}

	public void addDateTick(String timeStr) {
		addDateTick(timeStr, TickAlignment.MID);
	}

	private Date parseTime(String timeStr) {
		Date date = null;
		try {
			date = formatter.parse(timeStr);
		} catch (ParseException pe) {
			throw new IllegalArgumentException("Parse time failed: '" + timeStr
					+ "'");
		}
		calendar.clear();
		calendar.setTime(date);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		calendar.clear();
		calendar.setTime(baseDate);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		return calendar.getTime();
	}

	public List<LogicDateTick> getLogicTicks() {
		return ticks;
	}

	public DateFormat getFormatter() {
		return formatter;
	}
}
