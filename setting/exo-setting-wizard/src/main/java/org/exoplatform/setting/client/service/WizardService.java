package org.exoplatform.setting.client.service;

import java.util.List;
import java.util.Map;

import org.exoplatform.setting.shared.data.SetupWizardData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("wizardsrv")
public interface WizardService extends RemoteService {
  
  Map<String, String> getSystemProperties();
  List<String> getDatasources();
  String saveDatas(Map<SetupWizardData, String> datas);
  String startPlatform();
}
