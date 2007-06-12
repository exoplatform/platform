import java.util.List;
import java.util.ArrayList;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;

List categories = new ArrayList();

  SelectItemCategory skinDefault = new  SelectItemCategory("Default", false);
  skinDefault.addSelectItemOption(new SelectItemOption("Default", "Default", "Default"));
  skinDefault.setSelected(true);
  categories.add(skinDefault);
  
  SelectItemCategory skinMac = new  SelectItemCategory("Mac", false);
  skinMac.addSelectItemOption(new SelectItemOption("Mac", "Mac", "Mac"));
  categories.add(skinMac);
  
  SelectItemCategory skinVista = new  SelectItemCategory("Vista", false);
  skinVista.addSelectItemOption(new SelectItemOption("Vista", "Vista", "Vista"));
  categories.add(skinVista);
  
return categories;  