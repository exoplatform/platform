/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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
package org.exoplatform.calendar.client;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 13, 2011  
 */
public class ShowMorePagerPanel extends AbstractPager {

  /**
   * The default increment size.
   */
  private static final int DEFAULT_INCREMENT = 5;

  /**
   * The increment size.
   */
  private int incrementSize = DEFAULT_INCREMENT;

  /**
   * The last scroll position.
   */
  private int lastScrollPos = 0;

  /**
   * The scrollable panel.
   */
  private final ScrollPanel scrollable = new ScrollPanel();

  /**
   * Construct a new {@link ShowMorePagerPanel}.
   */
  public ShowMorePagerPanel() {
    initWidget(scrollable);

    // Do not let the scrollable take tab focus.
    scrollable.getElement().setTabIndex(-1);
    scrollable.setAlwaysShowScrollBars(true);

    // Handle scroll events.
    scrollable.addScrollHandler(new ScrollHandler() {
      public void onScroll(ScrollEvent event) {
        // If scrolling up, ignore the event.
        int oldScrollPos = lastScrollPos;
        lastScrollPos = scrollable.getScrollPosition();
        if (oldScrollPos >= lastScrollPos) {
          return;
        }

        HasRows display = getDisplay();
        if (display == null) {
          return;
        }
        int maxScrollTop = scrollable.getWidget().getOffsetHeight()
            - scrollable.getOffsetHeight();
        if (lastScrollPos >= maxScrollTop) {
          // We are near the end, so increase the page size.
          int newPageSize = Math.min(
              display.getVisibleRange().getLength() + incrementSize,
              display.getRowCount());
          display.setVisibleRange(0, newPageSize);
        }
      }
    });
  }

  /**
   * Get the number of rows by which the range is increased when the scrollbar
   * reaches the bottom.
   *
   * @return the increment size
   */
  public int getIncrementSize() {
    return incrementSize;
  }

  @Override
  public void setDisplay(HasRows display) {
    assert display instanceof Widget : "display must extend Widget";
    scrollable.setWidget((Widget) display);
    super.setDisplay(display);
  }

  /**
   * Set the number of rows by which the range is increased when the scrollbar
   * reaches the bottom.
   *
   * @param incrementSize the incremental number of rows
   */
  public void setIncrementSize(int incrementSize) {
    this.incrementSize = incrementSize;
  }

  @Override
  protected void onRangeOrRowCountChanged() {
  }
  
  public ScrollPanel getScrollPanel() {
    return this.scrollable;
  }
}