package org.exoplatform.portal.sample.gadget.gwt.client;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.Gadget;

@Gadget.ModulePrefs(title = "Hello World", author = "Jeremi Joslin", author_email = "jeremi@exoplatform.com")
public class HelloWorld extends Gadget<UserPreferences> {

  @Override
  protected void init(UserPreferences preferences) {
    Button simpleButton = new Button("SimpleGadget");
    simpleButton.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
        Window.alert("Hello World!");
      }
    });
    RootPanel.get().add(simpleButton);
  }
}
