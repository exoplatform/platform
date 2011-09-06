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

import java.util.Date;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.CellList.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 13, 2011  
 */
public class CellListData extends Composite {

  private static CellListDataUiBinder uiBinder = GWT.create(CellListDataUiBinder.class);

  interface CellListDataUiBinder extends UiBinder<Widget, CellListData> {
  }
  
  @UiField ShowMorePagerPanel pagerPanel;
  
  //@UiField RangeLabelPager rangeLabelPager;
  
  private final SingleSelectionModel<CalendarEventInfo> selectionModel;
  
  private CellList<CalendarEventInfo> cellList;
  
  interface EventCellListResource extends CellList.Resources {
    public static final Resources INSTANCE = GWT.create(EventCellListResource.class);
    
    @Source("org/exoplatform/calendar/client/EventCellList.css")
    Style cellListStyle();
  }
  
  /**
   * Because this class has a default constructor, it can
   * be used as a binder template. In other words, it can be used in other
   * *.ui.xml files as follows:
   * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
   *   xmlns:g="urn:import:**user's package**">
   *  <g:**UserClassName**>Hello!</g:**UserClassName>
   * </ui:UiBinder>
   * Note that depending on the widget that is used, it may be necessary to
   * implement HasHTML instead of HasText.
   */
  public CellListData() {
    initWidget(uiBinder.createAndBindUi(this));
    
    EventCell eventCell = new EventCell();
    cellList = new CellList<CalendarEventInfo>(eventCell, EventCellListResource.INSTANCE) ;
    cellList.setPageSize(10);
    
    // Add a cellList to a data provider.
    /*ListDataProvider<String> dataProvider = new ListDataProvider<String>();
    List<String> data = dataProvider.getList();
    for (int i = 0; i < 200; i++) {
      data.add("Item " + i);
    }
    dataProvider.addDataDisplay(cellList);*/
    
    selectionModel = new SingleSelectionModel<CalendarEventInfo>(CalendarEventInfo.KEY_PROVIDER);
    cellList.setSelectionModel(selectionModel);
    EventDatabase database = EventDatabase.get();
    
    database.addDataDisplay(cellList);
    
    pagerPanel.setDisplay(cellList);
    //rangeLabelPager.setDisplay(cellList);
    
  }
  
  public ShowMorePagerPanel getPager() {
    return this.pagerPanel;
  }
  
  static class EventCell extends AbstractCell<CalendarEventInfo> {
    
    @Override
    public void render(CalendarEventInfo value, Object key, SafeHtmlBuilder sb) {
      if (value==null) return;
      
      Date fromDate = value.getFromDate();
      
      if (fromDate == null) {
        sb.appendHtmlConstant("<div class='eventCell'>");
        sb.appendHtmlConstant("<span class='no-event'>");
        sb.appendEscaped("No event!");
        sb.appendHtmlConstant("</span></div>");
        return;
      }
      
      DateTimeFormat dateformat = DateTimeFormat.getFormat("dd/MM/yyyy");
      DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm");
      
      sb.appendHtmlConstant("<div class='eventCell'>");
      sb.appendHtmlConstant("<div class='event-date-label'>");
      sb.appendHtmlConstant("<span class='date-content'>");
      sb.appendEscaped(dateformat.format(fromDate));
      sb.appendHtmlConstant("</span></div>");
      sb.appendHtmlConstant("<div class='event-detail'>");
      sb.appendHtmlConstant("<span class='event-time-label'>");
      sb.appendEscaped(timeFormat.format(fromDate));
      sb.appendHtmlConstant("</span>&nbsp;&nbsp;");
      sb.appendHtmlConstant("<span class='event-summary'>");
      sb.appendEscaped(value.getSummary());
      sb.appendHtmlConstant("</span>");
      sb.appendHtmlConstant("</div>");
      sb.appendHtmlConstant("</div>");     
    }
  }
  
  public CellList<CalendarEventInfo> getCellList() {
    return cellList;
  }
  
  public SingleSelectionModel<CalendarEventInfo> getSelectionModel() {
    return this.selectionModel;
  }
}
