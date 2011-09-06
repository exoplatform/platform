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

import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 13, 2011  
 */
public class RangeLabelPager extends AbstractPager {

  /**
   * The label that shows the current range.
   */
  private final HTML label = new HTML();

  /**
   * Construct a new {@link RangeLabelPager}.
   */
  public RangeLabelPager() {
    initWidget(label);
  }

  @Override
  protected void onRangeOrRowCountChanged() {
    HasRows display = getDisplay();
    Range range = display.getVisibleRange();
    int start = range.getStart();
    int end = start + range.getLength();
    label.setText(start + " - " + end + " : " + display.getRowCount(),
        HasDirection.Direction.LTR);
  }
}