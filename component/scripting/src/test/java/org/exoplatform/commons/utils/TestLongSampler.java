/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.commons.utils;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TestLongSampler extends TestCase {

  public TestLongSampler() {
  }

  public TestLongSampler(String s) {
    super(s);
  }

  public void testAverage() {
    LongSampler sampler = new LongSampler(2);
    sampler.add(2L);
    sampler.add(4L);
    assertEquals(3D, sampler.average());
    sampler.add(8L);
    assertEquals(6D, sampler.average());
  }

  public void testAboveThreshold() {
    LongSampler sampler = new LongSampler(2);
    sampler.add(1L);
    sampler.add(2L);
    assertEquals(0, sampler.countAboveThreshold(3L));
    assertEquals(1, sampler.countAboveThreshold(2L));
    assertEquals(2, sampler.countAboveThreshold(1L));
    assertEquals(2, sampler.countAboveThreshold(0L));
  }
}
