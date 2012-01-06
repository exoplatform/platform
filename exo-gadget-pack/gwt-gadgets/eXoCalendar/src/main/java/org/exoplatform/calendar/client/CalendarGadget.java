package org.exoplatform.calendar.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exoplatform.calendar.client.EventDatabase;
import org.exoplatform.calendar.client.UserPrefs;
import org.exoplatform.calendar.client.JSO.Calendar;
import org.exoplatform.calendar.client.JSO.CalendarEvent;
import org.exoplatform.calendar.client.JSO.ResponseData;
import org.exoplatform.calendar.client.JSO.RestRequestController;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.NeedsDynamicHeight;
import com.google.gwt.gadgets.client.Gadget.ModulePrefs;
import com.google.gwt.gadgets.client.io.Response;
import com.google.gwt.gadgets.client.io.ResponseReceivedHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

@ModulePrefs(title = "eXo Calendar Gadget", author = "Le Thanh Quang - Do Hoang Khiem (eXo CT)", author_email = "letquang@gmail.com, dohoangkhiem@gmail.com")
public class CalendarGadget extends Gadget<UserPrefs> implements NeedsDynamicHeight {
  
  List<String> visibleCals = new ArrayList<String>();
  
  DynamicHeightFeature heightFeature;
  
  DatePicker datePicker;
  
  VerticalPanel mainPanel;
  
  VerticalPanel topPanel;
  
  MenuBar menubar;
  
  CellListData cellList;

  public void refresh() {
    highlightDates();
    getEventData();
    heightFeature.adjustHeight();
  }
  public void initData() {
    // load calendars 
    visibleCals.clear();
    RestRequestController.instance().makeGetJsonRequest("/calgad/calendars/personal", new ResponseReceivedHandler<JavaScriptObject>() {

      public void onResponseReceived(com.google.gwt.gadgets.client.io.ResponseReceivedHandler.ResponseReceivedEvent<JavaScriptObject> event) {
        Response<JavaScriptObject> response = event.getResponse();
        if (response.getStatusCode() == 200) {
          ResponseData responseData = (ResponseData) response.getData();
          JavaScriptObject[] list = responseData.getList();
          if (list.length > 0) {
            for (JavaScriptObject e : list) {
              Calendar cal = (Calendar) e;
              visibleCals.add(cal.getId());
            }
          }
          
          if (visibleCals.size() > 0) {
            highlightDates();
            getEventData();
          }
        }
      }
      
    });
    
  }
  
  public void showTopPanel() {
    mainPanel.insert(topPanel, 0);
  }
  
  public void hideTopPanel() {
    mainPanel.remove(topPanel);
  }
  
  @Override
  protected void init(UserPrefs preferences) {
    String restUrl = preferences.restURL().getValue();
    if (restUrl != null && restUrl.length() > 0) {
      if (restUrl.endsWith("/")) restUrl = restUrl.substring(0, restUrl.length() - 1);
      RestRequestController.REST_BASE_URL = restUrl;
    }
    mainPanel = new VerticalPanel();
    mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    mainPanel.setStylePrimaryName("calgadMainPanel");
//    mainPanel.setSize("500", "1000");
    CalGadMenubarBuilder menubarBuilder = new CalGadMenubarBuilder(this);
    
    
    topPanel = new VerticalPanel();
    topPanel.setStylePrimaryName("topPanel");
//    mainPanel.add(topPanel);
    
    datePicker = new DatePicker();
    datePicker.setValue(new Date());
    mainPanel.add(datePicker);
    
    cellList = new CellListData();
    mainPanel.add(cellList);
    
    initData();
    //getEventData();
    
    
    handleEventListSelection();
    
    datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
      
      public void onValueChange(ValueChangeEvent<Date> event) {
        // TODO Auto-generated method stub
        handleDatePickerSelection(event.getValue());
      }
    });
    
    datePicker.addShowRangeHandler(new ShowRangeHandler<Date>() {
      
      public void onShowRange(ShowRangeEvent<Date> event) {
        highlightDates();
        getEventData();
      }
    });
    
    cellList.getCellList().addRangeChangeHandler(new RangeChangeEvent.Handler() {
      
      public void onRangeChange(RangeChangeEvent event) {
        // TODO Auto-generated method stub
        heightFeature.adjustHeight();
      }
    });
        
    RootPanel rootPanel = RootPanel.get();
    menubar = menubarBuilder.createMenuBar();
    rootPanel.add(menubar);
    rootPanel.setStyleName("rootPanel");
    rootPanel.add(mainPanel);
    heightFeature.adjustHeight();
  }

  public void initializeFeature(DynamicHeightFeature feature) {
    this.heightFeature = feature;
  }
  
  public void highlightDates() {
    
    Date fromDate = datePicker.getFirstDate();
    Date toDate = datePicker.getLastDate();
    
    for(Date d = new Date(fromDate.getTime()); d.getTime() <= toDate.getTime(); d.setTime(d.getTime() + 24 * 60 * 60 * 1000)) {
      datePicker.removeStyleFromDates("bold-date", d);
    }
    
    String relPath = "/calgad/hdays/" + fromDate.getTime() + "/" + toDate.getTime() + "/";
    StringBuilder sb = new StringBuilder();
    for (String s : visibleCals) {
      sb.append(s).append('/');
    }
    relPath += sb.toString();
    
    RestRequestController.instance().makeGetJsonRequest(relPath, new ResponseReceivedHandler<JavaScriptObject>() {
      public void onResponseReceived(ResponseReceivedEvent<JavaScriptObject> event) {
        // update datePicker to highlight dates
        ResponseData jso = (ResponseData) event.getResponse().getData();
        JavaScriptObject[] list = jso.getList();
        if (list.length < 1) return;
        Date fromDate = datePicker.getFirstDate();
        Date firstDateOfYear = new Date(fromDate.getYear(), 0, 1);
        int fromDateOffSet = CalendarUtil.getDaysBetween(firstDateOfYear, fromDate) + 1;
        for (JavaScriptObject day : list) {
          int highlightDate = Integer.parseInt(day.toString());
          int highlightDateOffset = highlightDate - fromDateOffSet;
          Date hlDate = CalendarUtil.copyDate(fromDate);
          CalendarUtil.addDaysToDate(hlDate, highlightDateOffset);
          datePicker.addStyleToDates("bold-date", hlDate);
        }     
      }
    });
  }
  
  public void getEventData() {
    Date fromDate = datePicker.getFirstDate();
    Date toDate = datePicker.getLastDate();
    String relPath = "/calgad/events/" + fromDate.getTime() + "/" + toDate.getTime() + "/";
    for (String s : visibleCals) {
      relPath += s + '/';
    }
    RestRequestController.instance().makeGetJsonRequest(relPath, new ResponseReceivedHandler<JavaScriptObject>() {
      public void onResponseReceived(ResponseReceivedHandler.ResponseReceivedEvent<JavaScriptObject> event) {
        // update list data provider
        EventDatabase database = EventDatabase.get();
        database.cleanUp();
        ResponseData jso = (ResponseData) event.getResponse().getData();
        JavaScriptObject[] list = jso.getList();
        if (list == null || list.length < 1) {
          CalendarEventInfo empty = new CalendarEventInfo("empty-id", "No event", "empty-description", "empty", null, null);
          database.addEvent(empty);
          database.refreshDisplays();
          heightFeature.adjustHeight();
          return;
        }
    
        for (JavaScriptObject obj : list) {
          CalendarEvent calEvent = (CalendarEvent)obj;
          CalendarEventInfo jsoEvent = new CalendarEventInfo(calEvent);
          database.addEvent(jsoEvent);
        }
        database.refreshDisplays();
        heightFeature.adjustHeight();
      }
    });
  }
  
  public void handleEventListSelection() {
    final SingleSelectionModel<CalendarEventInfo> selectionModel = cellList.getSelectionModel();
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      
      public void onSelectionChange(SelectionChangeEvent event) {
        CalendarEventInfo selectedEvent = selectionModel.getSelectedObject();
        Date selectedDate = selectedEvent.getFromDate();
        if (selectedDate == null) return;
        datePicker.setValue(selectedDate, false);
      }
    });
  }
  
  public void handleDatePickerSelection(Date selectedDate) {
    // 
    EventDatabase database = EventDatabase.get();
    ListDataProvider<CalendarEventInfo> dataProvider = database.getDataProvider();
    // get the first available date after selectedDate
    Date firstDate = null;
    int pos = 0;
    if (dataProvider.getList() == null || dataProvider.getList().size() == 0) return;
    for (CalendarEventInfo ce : dataProvider.getList()) {
      pos++;
      if (!selectedDate.after(ce.getFromDate())) {
        firstDate = ce.getFromDate();
        break;
      }
    }
    
    if (firstDate != null) {
      // get key?
      cellList.getCellList().getRowElement(pos-1).scrollIntoView(); 
    }
  }
  
  public void clearPanel(Panel panel) {
    panel.clear();
    heightFeature.adjustHeight();
  }
    
  public static final native void debug(Object text) /*-{
    console.debug(text);
  }-*/;
    
}
