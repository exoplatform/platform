/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.samples.forminput.webui;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.download.DownloadResource;
import org.exoplatform.download.DownloadService;
import org.exoplatform.download.InputStreamDownloadResource;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormUploadInput;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Apr 14, 2009  
 */
@ComponentConfig(
                 lifecycle = UIApplicationLifecycle.class
)
public class UIDownloadUploadPortlet extends UIPortletApplication {

  public UIDownloadUploadPortlet() throws Exception {
    addChild(UIDownloadUpload.class, null, null);
  }
  
  @ComponentConfig(
                   lifecycle = UIFormLifecycle.class,
                   template = "app:/groovy/webui/component/UIDownloadUpload.gtmpl",
                   events = {
                     @EventConfig(listeners = UIDownloadUpload.SubmitActionListener.class)
                   }
  )
  static public class UIDownloadUpload extends UIForm {
    
    Map<String, String> data = new HashMap<String, String>();
    
    private String[] downloadLink;
    private String[] fileName;
    private String[] inputName;
    
    public UIDownloadUpload() throws Exception {
    	addUIFormInput(new UIFormUploadInput("name0","value0"));
    	addUIFormInput(new UIFormUploadInput("name1","value1"));
    	addUIFormInput(new UIFormUploadInput("name2","value2"));
    }
    public void setDownloadLink(String[] downloadLink) {
		this.downloadLink = downloadLink;
	}
	public String[] getDownloadLink() {
		return downloadLink;
	}
	public void setFileName(String[] fileName) {
		this.fileName = fileName;
	}
	public String[] getFileName() {
		return fileName;
	}
	public void setInputName(String[] inputName) {
		this.inputName = inputName;
	}
	public String[] getInputName() {
		return inputName;
	}
	static public class SubmitActionListener extends EventListener<UIDownloadUpload> {

      public void execute(Event<UIDownloadUpload> event) throws Exception {
    	UIDownloadUpload uiForm = event.getSource();
    	DownloadService dservice = uiForm.getApplicationComponent(DownloadService.class);
    	String[] downloadLink = new String[3];
    	String[] fileName = new String[3];
    	String[] inputName = new String[3];
    	for (int index = 0; index <=2; index++ ) {
    		UIFormUploadInput input = uiForm.getChildById("name"+index);
    		UploadResource uploadResource = input.getUploadResource() ;
        	if (uploadResource != null) {
	        	DownloadResource dresource = new InputStreamDownloadResource(input.getUploadDataAsStream(), uploadResource.getMimeType());
	        	dresource.setDownloadName(uploadResource.getFileName());
	        	downloadLink[index] = dservice.getDownloadLink(dservice.addDownloadResource(dresource));
	        	fileName[index] = uploadResource.getFileName();
	        	inputName[index] = "name" + index;
        	}
    	}
    	
    	uiForm.setDownloadLink(downloadLink);
    	uiForm.setFileName(fileName);
    	uiForm.setInputName(inputName);

    	event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent());
      }
      
    }
  }
}
