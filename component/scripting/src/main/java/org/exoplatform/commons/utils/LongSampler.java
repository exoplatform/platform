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

/**
 * An object that sample long values.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 * @todo move to common utils
 */
public class LongSampler extends BoundedBuffer<Long> {

  public LongSampler(int maxSize) {
    super(maxSize);
  }

  /**
   * Returns the average value.
   *
   * @return the average
   */
  public double average() {
    long sumTime = 0;
    int size = 0;
    for (long value : this) {
      sumTime += value;
      size++;
    }
    return size == 0 ? 0 : (double)sumTime / (double)size;
  }

  /**
   * Returns the number of values which are greater or equals to the threshold value.
   *
   * @param threshold the threshold value
   * @return the count of values above the provided threshold
   */
  public int countAboveThreshold(long threshold) {
    System.out.println("bbb"+threshold);
    int count = 0;
    for (long value : this) {
      System.out.println("aaaa"+value);
      if (value >= threshold) {
        count ++;
      }
    }
    return count;
  }
}
