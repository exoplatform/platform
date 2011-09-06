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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exoplatform.calendar.client.JSO.Calendar;
import org.exoplatform.calendar.client.JSO.ResponseData;
import org.exoplatform.calendar.client.JSO.RestRequestController;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gadgets.client.io.Response;
import com.google.gwt.gadgets.client.io.ResponseReceivedHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com May
 * 13, 2011
 */
public class CalGadMenubarBuilder {

  final private CalendarGadget calgad;

  final TextBox                eventTitle;

  final DateBox                fromBox;

  final DateBox                toBox;

  final ListBox                calendarsBox;

  public CalGadMenubarBuilder(CalendarGadget calGadget) {
    this.calgad = calGadget;
    eventTitle = new TextBox();
    eventTitle.setStyleName("eventtitle");
    // eventTitle.setSize("100px", "20px");
    fromBox = new DateBox();
    // fromBox.setSize("100px", "20px");
    toBox = new DateBox();
    // toBox.setSize("100px", "20px");
    calendarsBox = new ListBox();
    // calendarsBox.setSize("100px", "20px");
  }

  public MenuBar createMenuBar() {
    MenuBar menuBar = new MenuBar();
    //menuBar.setWidth("280px");
    menuBar.setAnimationEnabled(true);
    menuBar.setAutoOpen(true);
    // setting item
    MenuItem selectCalendars = new MenuItem("Appearance", new Command() {

      public void execute() {
        makeCalAppearancePanel();
        calgad.heightFeature.adjustHeight();
      }
    });

    menuBar.addItem(selectCalendars);

    // quick add item
    MenuItem quickAdd = new MenuItem("Quick add", new Command() {
      
      public void execute() {
        makeQuickAddPanel();
        calgad.heightFeature.adjustHeight();
      }
    });
    quickAdd.addStyleDependentName("quickadd");
    menuBar.addItem(quickAdd);
    
 // quick add item
    MenuItem refresh = new MenuItem("Refresh", new Command() {
      
      public void execute() {
        calgad.refresh();
      }
    });
    menuBar.addItem(refresh);
    
    return menuBar;
  }

  public void makeCalAppearancePanel() {
    final VerticalPanel panel = calgad.topPanel;
    calgad.showTopPanel();
    panel.clear();
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    final HorizontalPanel butsPanel = new HorizontalPanel();
    RestRequestController.instance()
                         .makeGetJsonRequest("/calgad/calendars/personal", new ResponseReceivedHandler<JavaScriptObject>() {

                          public void onResponseReceived(com.google.gwt.gadgets.client.io.ResponseReceivedHandler.ResponseReceivedEvent<JavaScriptObject> event) {
                            Label label = new Label("Select visible calendars");
                            panel.add(label);
                            Response<JavaScriptObject> response = event.getResponse();
                            if (response.getStatusCode() == 200) {
                              ResponseData responseData = (ResponseData) response.getData();
                              JavaScriptObject[] list = responseData.getList();
                              if (list.length > 0) {
                                for (JavaScriptObject e : list) {
                                  Calendar cal = (Calendar) e;
                                  CalendarCheckBox c = new CalendarCheckBox();
                                  c.setText(cal.getName());
                                  c.setValue(true);
                                  c.setCalId(cal.getId());
                                  panel.add(c);
                                }
                              }
                            }
                            panel.add(butsPanel);
                            calgad.heightFeature.adjustHeight();
                          }
                        });
    
    
    butsPanel.setStylePrimaryName("buttonsPanel");
    Button closeButton = new Button("close", new ClickHandler() {

      public void onClick(ClickEvent event) {
        calgad.hideTopPanel();
        calgad.heightFeature.adjustHeight();
      }
    });
    butsPanel.add(closeButton);
    butsPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
    Button saveButton = new Button("save", new ClickHandler() {

      public void onClick(ClickEvent event) {
        List<String> cals = new ArrayList<String>();
        for (int i = 0; i < panel.getWidgetCount(); i++) {
          Widget w = panel.getWidget(i);
          if (w instanceof CalendarCheckBox) {
            CalendarCheckBox calcb = (CalendarCheckBox) w;
            if (calcb.getValue()) cals.add(calcb.getCalId());
          }
        }
        calgad.hideTopPanel();
        calgad.visibleCals.clear();
        calgad.visibleCals.addAll(cals);
        calgad.refresh();
      }
    });
    butsPanel.add(saveButton);
    butsPanel.setCellHorizontalAlignment(saveButton, HasHorizontalAlignment.ALIGN_LEFT);
  }

  public void makeQuickAddPanel() {
    final VerticalPanel panel = calgad.topPanel;
    calgad.showTopPanel();
    panel.clear();
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    
    panel.add(calendarsBox);
    
    HorizontalPanel hPanel = new HorizontalPanel();
    
    Label fromLabel = new Label("From");
    fromBox.setValue(new Date());
    hPanel.add(fromLabel);
    hPanel.setCellHorizontalAlignment(fromLabel, HasHorizontalAlignment.ALIGN_RIGHT);
    hPanel.setCellWidth(fromLabel, "30%");
    hPanel.add(fromBox);
    hPanel.setCellHorizontalAlignment(fromBox, HasHorizontalAlignment.ALIGN_LEFT);
    panel.add(hPanel);

    hPanel = new HorizontalPanel();
    Label toLabel = new Label("To");
    toBox.setValue(new Date(System.currentTimeMillis() + 60 * 60 * 1000));
    hPanel.add(toLabel);
    hPanel.setCellHorizontalAlignment(toLabel, HasHorizontalAlignment.ALIGN_RIGHT);
    hPanel.setCellWidth(toLabel, "30%");
    hPanel.add(toBox);
    hPanel.setCellHorizontalAlignment(toBox, HasHorizontalAlignment.ALIGN_LEFT);
    panel.add(hPanel);
    
    calendarsBox.clear();
    calendarsBox.addItem("loading ...");
    calendarsBox.setSelectedIndex(0);
    RestRequestController.instance()
                         .makeGetJsonRequest("/calgad/calendars/personal",
                                             new ResponseReceivedHandler<JavaScriptObject>() {

                                               public void onResponseReceived(com.google.gwt.gadgets.client.io.ResponseReceivedHandler.ResponseReceivedEvent<JavaScriptObject> event) {
                                                 Response<JavaScriptObject> response = event.getResponse();
                                                 if (response.getStatusCode() == 200) {
                                                   ResponseData responseData = (ResponseData) event.getResponse()
                                                                                                   .getData();
                                                   JavaScriptObject[] list = responseData.getList();
                                                   if (list.length > 0) {
                                                     calendarsBox.clear();
                                                     for (JavaScriptObject e : list) {
                                                       Calendar c = (Calendar) e;
                                                       calendarsBox.addItem(c.getName(), c.getId());
                                                     }
                                                     calendarsBox.setItemSelected(0, true);
                                                   }
                                                 }
                                                 calgad.heightFeature.adjustHeight();
                                               }
                                               

                                             });
    hPanel = new HorizontalPanel();
    Label eventLabel = new Label("Summary");
    eventTitle.setText("");
    hPanel.add(eventLabel);
    hPanel.setCellWidth(eventLabel, "30%");
    hPanel.setCellHorizontalAlignment(eventLabel, HasHorizontalAlignment.ALIGN_RIGHT);
    hPanel.add(eventTitle);
    hPanel.setCellHorizontalAlignment(eventTitle, HasHorizontalAlignment.ALIGN_LEFT);
    panel.add(hPanel);
    
    HorizontalPanel butsPanel = new HorizontalPanel();
    butsPanel.setStylePrimaryName("buttonsPanel");
    panel.add(butsPanel);
    Button closeButton = new Button("close", new ClickHandler() {

      public void onClick(ClickEvent event) {
        calgad.hideTopPanel();
        calgad.heightFeature.adjustHeight();
      }
    });
    butsPanel.add(closeButton);
    butsPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
    Button addButton = new Button("add", new ClickHandler() {

      public void onClick(ClickEvent event) {
        final String title = eventTitle.getText();
        final String calId = calendarsBox.getValue(calendarsBox.getSelectedIndex());
        if (title == null || title.length() == 0) {
          MessageBox.showMessage("event title is null");
          return;
        } else {
          JSONObject postdata = new JSONObject();
          postdata.put("calId", new JSONString(calId));
          postdata.put("eventTitle", new JSONString(title));
          postdata.put("from", new JSONString(String.valueOf(fromBox.getValue().getTime())));
          postdata.put("to", new JSONString(String.valueOf(toBox.getValue().getTime())));
          RestRequestController.instance()
                               .makePostJsonRequest("/calgad/addevent",
                                                    postdata,
                                                    new ResponseReceivedHandler<JavaScriptObject>() {

                                                      public void onResponseReceived(com.google.gwt.gadgets.client.io.ResponseReceivedHandler.ResponseReceivedEvent<JavaScriptObject> event) {
                                                        Response response = event.getResponse();
                                                        if (response.getStatusCode() == 200) {
                                                          calgad.hideTopPanel();
                                                          calgad.refresh();
                                                          MessageBox.showMessage("new event: "
                                                              + title
                                                              + " has been added successfully");
                                                        } else {
                                                          MessageBox.showMessage("Can not add event: "
                                                              + title + " into " + calId);
                                                        }
                                                      }

                                                    });
        }

      }
    });

    butsPanel.add(addButton);
    butsPanel.setCellHorizontalAlignment(addButton, HasHorizontalAlignment.ALIGN_LEFT);
  }

  
  class CalendarCheckBox extends CheckBox {
    String calId;

    public String getCalId() {
      return calId;
    }

    public void setCalId(String calId) {
      this.calId = calId;
    }
    
  }
  
}
