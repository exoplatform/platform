import java.util.List;
import java.util.ArrayList;
import org.exoplatform.portal.webui.portal.PortalTemplateConfigOption ;
import org.exoplatform.webui.core.model.SelectItemCategory;

List options = new ArrayList();

  SelectItemCategory guest = new SelectItemCategory("ClassicPortal");
  guest.addSelectItemOption(
      //new PortalTemplateConfigOption("", "site", "Site Portal", "SitePortal").addGroup("/guest")
      new PortalTemplateConfigOption("", "classic", "Classic Portal", "ClassicPortal").addGroup("/platform/guests")
  );
  options.add(guest);
  
return options ;
