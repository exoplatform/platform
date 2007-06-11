import java.util.List;
import java.util.ArrayList;
import org.exoplatform.account.webui.component.model.UIAccountTemplateConfigOption ;
import org.exoplatform.organization.webui.component.UIUserMembershipSelector;
import org.exoplatform.webui.bean.SelectItemCategory;

List options = new ArrayList();

  SelectItemCategory guest = new SelectItemCategory("DefaultAccount");
  guest.addSelectItemOption(
    new UIAccountTemplateConfigOption("", "DefaultAccount", "Description for Guest Account", "DefaultAccount").
    addMembership(new UIUserMembershipSelector.Membership("exo","/guest","member"))
  );
  options.add(guest);

  SelectItemCategory community = new SelectItemCategory("CommunityAccount");
  community.addSelectItemOption(
    new UIAccountTemplateConfigOption("", "CommunityAccount", "Description for User Account", "CommunityAccount").                         
    addMembership(new UIUserMembershipSelector.Membership("community","/user","member")).
    addMembership(new UIUserMembershipSelector.Membership("community","/portal/community","member"))
  );
  options.add(community);
  
  SelectItemCategory company = new SelectItemCategory("CompanyAccount");
  company.addSelectItemOption(
    new UIAccountTemplateConfigOption("", "CompanyAccount", "Description for Company Account", "CompanyAccount").                         
    addMembership(new UIUserMembershipSelector.Membership("company","/user","member")).
    addMembership(new UIUserMembershipSelector.Membership("company","/portal/company","member"))
  );
  options.add(company);
  
  SelectItemCategory admin = new SelectItemCategory("AdminAccount");
  admin.addSelectItemOption(
    new UIAccountTemplateConfigOption("", "AdminAccount", "Description for Admin Account", "AdminAccount").
    addMembership(new UIUserMembershipSelector.Membership("exo","/user","member")).
    addMembership(new UIUserMembershipSelector.Membership("exoadmin","/admin","member"))
  );
  options.add(admin);

return options ;
