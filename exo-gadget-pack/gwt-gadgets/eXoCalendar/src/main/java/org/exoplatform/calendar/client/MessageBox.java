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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com May
 * 13, 2011
 */
public final class MessageBox {

  static private DialogBox dialogBox;

  static private Label     label;
  static {
    dialogBox = new DialogBox();
    dialogBox.setAnimationEnabled(true);
    VerticalPanel hPanel = new VerticalPanel();
    hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    dialogBox.add(hPanel);
    label = new Label();
    label.setWidth("200px");
    label.setWordWrap(true);
    label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    hPanel.add(label);
    Button closeButton = new Button("close", new ClickHandler() {

      public void onClick(ClickEvent event) {
        dialogBox.hide();
      }
    });
    hPanel.add(closeButton);
  }

  public static void showMessage(String text) {
    label.setText(text);
    dialogBox.center();
    dialogBox.show();
  }

}
