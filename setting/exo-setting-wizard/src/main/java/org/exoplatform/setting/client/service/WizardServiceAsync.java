package org.exoplatform.setting.client.service;

import java.util.List;
import java.util.Map;

import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface WizardServiceAsync {
  
  void getSystemProperties(AsyncCallback<Map<String, String>> callback);
  void getDatasources(AsyncCallback<List<String>> callback);
  void saveDatas(Map<SetupWizardData, String> datas, AsyncCallback<String> callback);
  void startPlatform(AsyncCallback<String> callback);
}
