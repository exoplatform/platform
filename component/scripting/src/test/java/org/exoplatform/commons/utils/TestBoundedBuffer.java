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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TestBoundedBuffer extends TestCase {

  public TestBoundedBuffer() {
  }

  public TestBoundedBuffer(String s) {
    super(s);
  }

  public void testNegativeMaxSize() {
    try {
      new BoundedBuffer<Object>(-1);
    }
    catch (IllegalArgumentException expected) {
    }
  }

  public void testSnapshot() {
    BoundedBuffer<Object> buffer = new BoundedBuffer<Object>(2);
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();
    Iterator<Object> i0 = buffer.iterator();
    buffer.add(o1);
    Iterator<Object> i1 = buffer.iterator();
    buffer.add(o2);
    Iterator<Object> i2 = buffer.iterator();
    buffer.add(o3);
    Iterator<Object> i3 = buffer.iterator();
    assertEquals(Arrays.asList(), toList(i0));
    assertEquals(Arrays.asList(o1), toList(i1));
    assertEquals(Arrays.asList(o1, o2), toList(i2));
    assertEquals(Arrays.asList(o2, o3), toList(i3));
  }

  public void testNoLeak() {
    BoundedBuffer<Object> buffer = new BoundedBuffer<Object>(2);
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();
    buffer.add(o1);
    buffer.add(o2);
    buffer.add(o3);
    WeakReference<Object> ref1 = new WeakReference<Object>(o1);
    WeakReference<Object> ref2 = new WeakReference<Object>(o2);
    WeakReference<Object> ref3 = new WeakReference<Object>(o3);
    o1 = null;
    o2 = null;
    o3 = null;
    forceGC();
    assertNull(ref1.get());
    assertNotNull(ref2.get());
    assertNotNull(ref3.get());
  }

  private <T> List<T> toList(Iterator<T> iterable) {
    List<T> list = new ArrayList<T>();
    while (iterable.hasNext()) {
      T t = iterable.next();
      list.add(t);
    }
    return list;
  }

  /**
   * Force a garbage collector.
   */
  protected static void forceGC() {
    WeakReference<Object> dumbReference = new WeakReference<Object>(new Object());
    // A loop that will wait GC, using the minimal time as possible
    while (dumbReference.get() != null) {
      System.gc();
      try {
        Thread.sleep(500);
      }
      catch (InterruptedException e) {
      }
    }
  }
}
