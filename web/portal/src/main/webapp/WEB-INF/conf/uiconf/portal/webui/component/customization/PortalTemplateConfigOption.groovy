import java.util.List;
import java.util.ArrayList;
import org.exoplatform.portal.component.model.PortalTemplateConfigOption ;
import org.exoplatform.webui.component.model.SelectItemCategory;

List options = new ArrayList();

  SelectItemCategory guest = new SelectItemCategory("SitePortal");
  guest.addSelectItemOption(
      new PortalTemplateConfigOption("", "Site", "Site Portal").addGroup("/guest")
  );
  options.add(guest);

  SelectItemCategory webos = new SelectItemCategory("WebOSPortal");
  webos.addSelectItemOption(
    new PortalTemplateConfigOption("", "WebOS", "WebOS Portal").addGroup("/guest")
  );
  options.add(webos);
  
return options ;
