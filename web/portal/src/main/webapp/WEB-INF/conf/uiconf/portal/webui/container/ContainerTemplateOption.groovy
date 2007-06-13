import org.exoplatform.webui.core.model.SelectItemOption ;
import org.exoplatform.webui.core.model.SelectItemCategory ;
import java.util.List;
import java.util.ArrayList;

  List options = new ArrayList() ;
  
  SelectItemCategory defaultTemp  = new SelectItemCategory("Default");
  defaultTemp.addSelectItemOption(new SelectItemOption("Container template",
                                  "system:/groovy/portal/webui/container/UIContainer.gtmpl",
                                  "Description", "Default"));  
  options.add(defaultTemp);
  
  SelectItemCategory tableTemp  = new SelectItemCategory("Table Column");
  tableTemp.addSelectItemOption(new SelectItemOption("Table Column template",
                                "system:/groovy/portal/webui/container/UITableColumnContainer.gtmpl",
                                "Description","Default"));  
  options.add(tableTemp);

return options;