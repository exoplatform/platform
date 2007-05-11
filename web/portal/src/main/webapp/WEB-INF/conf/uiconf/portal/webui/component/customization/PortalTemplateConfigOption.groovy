import java.util.List;
import java.util.ArrayList;
import org.exoplatform.account.webui.component.model.UIPortalTemplateConfigOption ;
import org.exoplatform.organization.webui.component.UIAccessGroup;
import org.exoplatform.webui.component.model.SelectItemCategory;

List options = new ArrayList();

  SelectItemCategory guest = new SelectItemCategory("DefaultAccount");
  guest.addSelectItemOption(
    new UIPortalTemplateConfigOption("", "DefaultAccount", "Description for Guest Account", "DefaultAccount").
    addGroup((new UIAccessGroup()).getAccessGroup())
  );
  options.add(guest);

  SelectItemCategory community = new SelectItemCategory("CommunityAccount");
  community.addSelectItemOption(
    new UIPortalTemplateConfigOption("", "CommunityAccount", "Description for User Account", "CommunityAccount").                         
    addGroup((new UIAccessGroup()).getAccessGroup())
  );
  options.add(community);
  
  SelectItemCategory company = new SelectItemCategory("CompanyAccount");
  company.addSelectItemOption(
    new UIPortalTemplateConfigOption("", "CompanyAccount", "Description for Company Account", "CompanyAccount").                         
    addGroup((new UIAccessGroup()).getAccessGroup())
  );
  options.add(company);
  
  SelectItemCategory admin = new SelectItemCategory("AdminAccount");
  admin.addSelectItemOption(
    new UIPortalTemplateConfigOption("", "AdminAccount", "Description for Admin Account", "AdminAccount").
    addGroup((new UIAccessGroup()).getAccessGroup())
  );
  options.add(admin);

return options ;
