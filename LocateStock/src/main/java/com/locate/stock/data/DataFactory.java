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

package com.locate.stock.data;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jstockchart.model.TimeseriesItem;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

/**
 * Queries data from db4o data file.
 * 
 * @author Sha Jiang
 */
public class DataFactory {

	private ObjectContainer db = null;

	private Calendar calendar = Calendar.getInstance();

	public DataFactory(ObjectContainer db) {
		this.db = db;
	}

	public List<TimeseriesItem> getTimeseriesItem() {
		return getTimeseriesItem(new Date(0), new Date(Long.MAX_VALUE));
	}

	public List<TimeseriesItem> getTimeseriesItem(final Date startTime,
			final Date endTime) {
		return getTimeseriesItem(startTime, endTime, 1, Calendar.MINUTE);
	}

	/**
	 * Queries data.
	 * 
	 * @param startTime
	 *            data starting time.
	 * @param endTime
	 *            data ending time.
	 * @param step
	 *            time step.
	 * @param type
	 *            time type(Calendar.MINUTE, Calendar.SECOND, ...).
	 * @return <code>TimeseriesItem<code> list;
	 */
	public List<TimeseriesItem> getTimeseriesItem(final Date startTime,
			final Date endTime, final int type, final int step) {
		List<TimeseriesItem> result = db.query(new Predicate<TimeseriesItem>() {

			private static final long serialVersionUID = -5213020916970452036L;

			public boolean match(TimeseriesItem item) {
				Date time = item.getTime();
				if (time.getTime() >= startTime.getTime()
						&& time.getTime() <= endTime.getTime()
						&& testTime(time, type, step)) {
					return true;
				}
				return false;
			}
		}, comparator);
		return result;
	}

	private boolean testTime(Date time, int type, int step) {
		boolean result = false;
		calendar.clear();
		calendar.setTime(time);
		int number = calendar.get(type);
		if (number % step == 0) {
			result = true;
		}

		return result;
	}

	private static final Comparator<TimeseriesItem> comparator = new Comparator<TimeseriesItem>() {
		public int compare(TimeseriesItem item1, TimeseriesItem item2) {
			return (int) (item1.getTime().getTime() - item2.getTime().getTime());
		}
	};
}
