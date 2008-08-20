import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.application.WebuiRequestContext;
import java.util.ResourceBundle;

List categories = new ArrayList(); 
WebuiRequestContext contextres = WebuiRequestContext.getCurrentInstance();
ResourceBundle res = contextres.getApplicationResourceBundle();
    
SelectItemCategory normalPageConfigs = new SelectItemCategory("normalPageConfigs") ;
categories.add(normalPageConfigs);
normalPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.normalPageConfigs.EmptyLayout"), "empty", "EmptyLayout"));
normalPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.normalPageConfigs.DesktopImage"), "desktop", "DesktopImage"));

SelectItemCategory columnPageConfigs = new SelectItemCategory("columnPageConfigs") ;
categories.add(columnPageConfigs);  
columnPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.columnPageConfigs.TwoColumnsLayout"), "two-columns", "TwoColumnsLayout"));
columnPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.columnPageConfigs.ThreeColumnsLayout"), "three-columns", "ThreeColumnsLayout"));

SelectItemCategory rowPageConfigs = new SelectItemCategory("rowPageConfigs") ;
categories.add(rowPageConfigs); 
rowPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.rowPageConfigs.TwoRowsLayout"), "two-rows", "TwoRowsLayout"));
rowPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.rowPageConfigs.ThreeRowsLayout"), "three-rows", "ThreeRowsLayout"));

SelectItemCategory tabsPageConfigs = new SelectItemCategory("tabsPageConfigs") ;
categories.add(tabsPageConfigs) ;
tabsPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.tabsPageConfigs.TwoTabsLayout"), "two-tabs", "TwoTabsLayout")) ;
tabsPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.tabsPageConfigs.ThreeTabsLayout"), "three-tabs", "ThreeTabsLayout")) ;

SelectItemCategory mixPageConfigs = new SelectItemCategory("mixPageConfigs") ;
categories.add(mixPageConfigs); 
mixPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.mixPageConfigs.TwoColumnsOneRowLayout"), "two-columns-one-row", "TwoColumnsOneRowLayout"));
mixPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.mixPageConfigs.OneRowTwoColumnsLayout"), "one-row-two-columns", "OneRowTwoColumnsLayout"));
mixPageConfigs.addSelectItemOption(new SelectItemOption(res.getString("UIWizardPageSelectLayoutForm.mixPageConfigs.ThreeRowsTwoColumnsLayout"), "three-rows-two-columns", "ThreeRowsTwoColumnsLayout"));

return categories;

