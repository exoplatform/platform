import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.component.model.SelectItemCategory;

List categories = new ArrayList(); 
String config = null ;

SelectItemCategory normalPageConfigs = new SelectItemCategory("normalPageConfigs") ;
categories.add(normalPageConfigs);

config = "<page>" +
         "  <name>UIPage</name>"+         
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "</page>";
normalPageConfigs.addSelectItemOption(new SelectItemOption("EmptyLayout", config, "EmptyLayout"));

config = "<page>" +         
         "  <factory-id>Desktop</factory-id>" +
         "  <name>UIPage</name>" +
         "</page>" ;

normalPageConfigs.addSelectItemOption(new SelectItemOption("DesktopLayout", config, "DesktopImage"));

SelectItemCategory columnPageConfigs = new SelectItemCategory("columnPageConfigs") ;
categories.add(columnPageConfigs);  
config = "<page>" +
         "  <name>UIPage</name>" +         
         "  <container template='system:/groovy/portal/webui/component/view/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "</page>";
columnPageConfigs.addSelectItemOption(new SelectItemOption("TwoColumnsLayout", config, "TwoColumnsLayout"));
config = "<page>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/component/view/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "</page>";
columnPageConfigs.addSelectItemOption(new SelectItemOption("ThreeColumnsLayout", config, "ThreeColumnsLayout"));

SelectItemCategory rowPageConfigs = new SelectItemCategory("rowPageConfigs") ;
categories.add(rowPageConfigs); 

config = "<page>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "</page>";
rowPageConfigs.addSelectItemOption(new SelectItemOption("TwoRowsLayout", config, "TwoRowsLayout"));

config = "<page>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "</page>";
rowPageConfigs.addSelectItemOption(new SelectItemOption("ThreeRowsLayout", config, "ThreeRowsLayout"));

SelectItemCategory mixPageConfigs = new SelectItemCategory("mixPageConfigs") ;
categories.add(mixPageConfigs); 
  
config = "<page>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/component/view/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "</page>";
mixPageConfigs.addSelectItemOption(new SelectItemOption("TwoColumnsOneRowLayout", config, "TwoColumnsOneRowLayout"));

config = "<page>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/component/view/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "</page>";
mixPageConfigs.addSelectItemOption(new SelectItemOption("OneRowTwoColumnsLayout", config, "OneRowTwoColumnsLayout"));

config = "<page>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/component/view/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "  <container template='system:/groovy/portal/webui/component/view/UIContainer.gtmpl'></container>" +
         "</page>";
mixPageConfigs.addSelectItemOption(new SelectItemOption("ThreeRowsTwoColumnsLayout", config, "ThreeRowsTwoColumnsLayout"));

return categories;