package org.exoplatform.setting.client.service;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface WizardServiceAsync {
  
  void storeDatas(Map<String, String> datas, Integer toStep, AsyncCallback<Integer> callback);
}
