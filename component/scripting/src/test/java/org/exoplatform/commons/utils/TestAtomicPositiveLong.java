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
public class TestAtomicPositiveLong extends TestCase {

  public TestAtomicPositiveLong() {
  }

  public TestAtomicPositiveLong(String s) {
    super(s);
  }

  public void testConstructorThrowsIAE() {
    try {
      new AtomicPositiveLong(-1);
      fail();
    }
    catch (IllegalArgumentException expected) {
    }
  }

  public void testUpdateIfGreater1() {
    AtomicPositiveLong stat = new AtomicPositiveLong(4);
    assertEquals(4, stat.get());
    stat.setIfGreater(3);
    assertEquals(4, stat.get());
    stat.setIfGreater(5);
    assertEquals(5, stat.get());
  }

  public void testUpdateIfGreater2() {
    AtomicPositiveLong stat = new AtomicPositiveLong();
    assertEquals(-1, stat.get());
    stat.setIfGreater(0);
    assertEquals(0, stat.get());
  }

  public void testUpdateIfGreaterThrowsIAE() {
    AtomicPositiveLong stat = new AtomicPositiveLong(4);
    try {
      stat.setIfGreater(-1);
    }
    catch (IllegalArgumentException expected) {
      assertEquals(4, stat.get());
    }
  }

  public void testUpdateIfLowser1() {
    AtomicPositiveLong stat = new AtomicPositiveLong(4);
    assertEquals(4, stat.get());
    stat.setIfLower(5);
    assertEquals(4, stat.get());
    stat.setIfLower(3);
    assertEquals(3, stat.get());
  }

  public void testUpdateIfLowser2() {
    AtomicPositiveLong stat = new AtomicPositiveLong();
    assertEquals(-1, stat.get());
    stat.setIfLower(0);
    assertEquals(0, stat.get());
  }

  public void testUpdateIfLowerThrowsIAE() {
    AtomicPositiveLong stat = new AtomicPositiveLong(4);
    try {
      stat.setIfLower(-1);
    }
    catch (IllegalArgumentException expected) {
      assertEquals(4, stat.get());
    }
  }
}
