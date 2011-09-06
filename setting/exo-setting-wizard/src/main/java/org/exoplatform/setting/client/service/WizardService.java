package org.exoplatform.setting.client.service;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("wizardsrv")
public interface WizardService extends RemoteService {
  
  Integer storeDatas(Map<String, String> datas, Integer toStep);
  Map<String, String> getSystemProperties();
  List<String> getDatasources();
}
