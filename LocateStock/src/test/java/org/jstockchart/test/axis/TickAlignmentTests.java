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

import org.junit.Test;

import com.locate.stock.axis.TickAlignment;

/**
 * Tests for <code>TickAlignment</code> class.
 * 
 * @author Sha Jiang
 */
public class TickAlignmentTests {

	@Test
	public void testEquals() {
		TickAlignment alignment1 = TickAlignment.START;
		TickAlignment alignment2 = TickAlignment.START;
		TickAlignment alignment3 = TickAlignment.MID;

		assertEquals(alignment1, alignment2);
		assertFalse(alignment1.equals(alignment3));
	}

	@Test
	public void testHashcode() {
		TickAlignment alignment1 = TickAlignment.START;
		TickAlignment alignment2 = TickAlignment.START;
		TickAlignment alignment3 = TickAlignment.MID;

		assertTrue(alignment1.hashCode() == alignment2.hashCode());
		assertFalse(alignment1.hashCode() == alignment3.hashCode());
	}
}
