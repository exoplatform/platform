package org.exoplatform.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.component.UIBreadcumbs.*;

@ComponentConfig(   
  template = "system:/groovy/webui/component/UIBreadcumbs.gtmpl",
  events = @EventConfig(listeners = SelectPathActionListener.class)
)
public class UIBreadcumbs extends UIComponent {  
  
  private List<LocalPath>  path_ = new ArrayList<LocalPath>();
  private LocalPath selectedLocalPath_ ;
  private String styleBread = "default" ;
  
  public List<LocalPath> getPath(){ return path_;  }  
  public void setPath(List<LocalPath> list ){ path_ = list; }
  
  public LocalPath getSelectLocalPath(){  return selectedLocalPath_; }  
  public void setSelectLocalPath(LocalPath localPath){  selectedLocalPath_ = localPath; }
  
  public void setSelectPath(String path){
    List<LocalPath> list = getPath();
    for(LocalPath p : list) {
      if(!p.getId().equals(path)) continue;
      setSelectLocalPath(p);
      break ;
    }
  }
  
  public String event(String name, String beanId) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    if(uiForm != null) return uiForm.event(name, beanId);
    return super.event(name, beanId);
  }
  
  public String getBreadcumbsStyle() { return styleBread ; }
  public void setBreadcumbsStyle(String style) { styleBread = style ; }
  
  static  public class SelectPathActionListener extends EventListener<UIBreadcumbs> {    
    public void execute(Event<UIBreadcumbs> event) throws Exception {
      UIBreadcumbs uicomp = event.getSource() ;
      String objectId =  event.getRequestContext().getRequestParameter(OBJECTID) ;
      uicomp.setSelectPath(objectId);     
      uicomp.<UIComponent>getParent().broadcast(event, Event.Phase.PROCESS) ;     
    }
  }
  
  static public class LocalPath {
    
    private String label_ ;
    private String id_ ;
    
    public LocalPath(String id, String label){
      label_ = label;
      id_ = id;
    }
    
    public String getLabel() { return label_; }
    public void setLabel(String label) {  label_ = label;   }
    
    public String getId() { return id_;  }
    public void setId(String id) { id_ = id; }
  }
  
}