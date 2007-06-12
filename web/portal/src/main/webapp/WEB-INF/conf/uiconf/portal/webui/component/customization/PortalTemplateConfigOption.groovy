import java.util.List;
import java.util.ArrayList;
import org.exoplatform.portal.component.model.PortalTemplateConfigOption ;
import org.exoplatform.webui.core.model.SelectItemCategory;

List options = new ArrayList();

  SelectItemCategory guest = new SelectItemCategory("SitePortal");
  guest.addSelectItemOption(
      new PortalTemplateConfigOption("", "site", "Site Portal", "SitePortal").addGroup("/guest")
  );
  options.add(guest);

  SelectItemCategory webos = new SelectItemCategory("WebOSPortal");
  webos.addSelectItemOption(
    new PortalTemplateConfigOption("", "webos", "WebOS Portal", "WebOSPortal").addGroup("/guest")
  );
  options.add(webos);
  
return options ;
