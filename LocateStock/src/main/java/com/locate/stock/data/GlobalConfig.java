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

import org.jstockchart.model.CandlestickItem;

import com.db4o.Db4o;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;

/**
 * Configurations for db4o.
 * 
 * @author Sha Jiang
 */
public final class GlobalConfig {

	private GlobalConfig() {

	}

	public static Configuration globalConfig() {
		Configuration config = Db4o.newConfiguration();
		generalConfig(config);
		candleItemConfig(config);
		return config;
	}

	private static void generalConfig(Configuration config) {
		config.activationDepth(2);
	}

	private static void candleItemConfig(Configuration config) {
		ObjectClass candleItemObjectClass = config
				.objectClass(CandlestickItem.class);
		candleItemObjectClass.cascadeOnActivate(true);
		candleItemObjectClass.cascadeOnUpdate(true);
		candleItemObjectClass.cascadeOnDelete(true);
	}
}
