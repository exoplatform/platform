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

import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>A thread safe bounded buffer.</p>
 *
 * <p>The idea of this class is that it retains only the last elements added
 * to the buffer up to a determined size, but it is possible to make snapshot of the buffer elements and iterate
 * over them with a neglectable impact on synchronization.</p>
 *
 * <p>It maintains a linked list. When a new element is added, the first element will have for
 * successor that new element. If the number of elements is greater than the max size then the last element
 * is discarded.</p>
 *
 * <p> When a snapshot for iteration is required, the class only needs to keep a reference to the last element of the list
 * and keep also the actual size of the list. The copy is made in an atomic manner for consistency. Note that this class
 * expose only a notion of iterator to its client instead of a notion of list as iterator have a notion of being short
 * lived objects. Indeed keeping a reference on an iterator would create a memory leak and so this class must be used
 * with caution.</p>
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 * @todo move to common utils
 * @todo develop a non blocking implementation pretty much like the ConcurrentLinkedQueue does
 * @todo although I don't think this class is a bottleneck as there is very short lived synchronization
 * @todo and actually I don't know if it's feasible to have a non blocking impl due to the size value
 *
 */
public class BoundedBuffer<T> implements Iterable<T> {

  /** The max size. */
  private final int maxSize;

  /** The elder element. */
  private ObjectRef<T> last;

  /** The younger element. */
  private ObjectRef<T> first;

  /** The size, it is declared as volatile for the @link{getSize()} method. */
  private volatile int size;

  public BoundedBuffer(int maxSize) {
    if (maxSize < 1) {
      throw new IllegalArgumentException("Buffer size needs to be greater than zero");
    }

    //
    this.maxSize = maxSize;
    this.size = 0;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public int getSize() {
    return size;
  }


  /**
   * Add an element to the buffer.
   *
   * @param t the element to add
   */
  public void add(T t) {
    synchronized (this) {
      if (first == null) {
        first = new ObjectRef<T>(t);
        last = first;
        size = 1;
      } else {
        ObjectRef<T> tmp = first;
        first = new ObjectRef<T>(t);
        tmp.next.set(first);
        if (size < maxSize) {
          size++;
        } else {
          last = last.next.get();
        }
      }
    }
  }

  /**
   * Make a snapshot of the buffer and iterate over the elements. It is important to not keep reference
   * on an iterator returned by this method otherwise it could create a memory leak.
   *
   * @return an iterator over the elements
   */
  public Iterator<T> iterator() {
    if (size == 0) {
      List<T> empty = Collections.emptyList();
      return empty.iterator();
    } else {
      // Get consistent state
      final ObjectRef<T> lastSnapshot;
      final int sizeSnapshot;
      synchronized (this) {
        lastSnapshot = last;
        sizeSnapshot = size;
      }
      return new BoundedIterator<T>(lastSnapshot, sizeSnapshot);
    }
  }

  private static final class BoundedIterator<T> implements Iterator<T> {

    final int size;
    ObjectRef<T> current;
    int count = 0;

    private BoundedIterator(ObjectRef<T> current, int size) {
      this.current = current;
      this.size = size;
      this.count = 0;
    }

    public boolean hasNext() {
      return count < size && current != null;
    }

    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      T next = current.object;
      current = current.next.get();
      count++;
      return next;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
