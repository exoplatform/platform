import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.core.model.SelectItemCategory;

List categories = new ArrayList(); 
String config = null ;

SelectItemCategory normalPageConfigs = new SelectItemCategory("normalPageConfigs") ;
categories.add(normalPageConfigs);

config = "<page>" +
         "  <owner-type></owner-type>" +
         "  <owner-id></owner-id>" +
         "  <name>UIPage</name>"+         
         "</page>";
normalPageConfigs.addSelectItemOption(new SelectItemOption("Empty Layout", config, "EmptyLayout"));

config = "<page>" +         
         "  <owner-type></owner-type>" +
         "  <owner-id></owner-id>" +
         "  <name>UIPage</name>" +
         "  <factory-id>Desktop</factory-id>" +
         "</page>" ;

normalPageConfigs.addSelectItemOption(new SelectItemOption("Desktop Layout", config, "DesktopImage"));

SelectItemCategory columnPageConfigs = new SelectItemCategory("columnPageConfigs") ;
categories.add(columnPageConfigs);  
config = "<page>" +
         "  <owner-type></owner-type>" +
         "  <owner-id></owner-id>" +
         "  <name>UIPage</name>" +         
         "  <container template='system:/groovy/portal/webui/container/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "</page>";
columnPageConfigs.addSelectItemOption(new SelectItemOption("Two Columns Layout", config, "TwoColumnsLayout"));
config = "<page>" +
         "  <owner-type></owner-type>" +
         "  <owner-id></owner-id>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/container/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "</page>";
columnPageConfigs.addSelectItemOption(new SelectItemOption("Three Columns Layout", config, "ThreeColumnsLayout"));

SelectItemCategory rowPageConfigs = new SelectItemCategory("rowPageConfigs") ;
categories.add(rowPageConfigs); 

config = "<page>" +
         "  <owner-type></owner-type>" +
         "  <owner-id></owner-id>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "</page>";
rowPageConfigs.addSelectItemOption(new SelectItemOption("Two Rows Layout", config, "TwoRowsLayout"));

config = "<page>" +
         "  <owner-type></owner-type>" +
         "  <owner-id></owner-id>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "</page>";
rowPageConfigs.addSelectItemOption(new SelectItemOption("Three Rows Layout", config, "ThreeRowsLayout"));

SelectItemCategory tabsPageConfigs = new SelectItemCategory("tabsPageConfigs") ;
categories.add(tabsPageConfigs) ;

config = "<page>" +
				 "	<owner-type></owner-type>" +
				 "	<owner-id></owner-id>" +
				 "	<name>UIPage</name>" +
				 "	<container template=\"system:/groovy/portal/webui/container/UIContainer.gtmpl\">" +
         "		<container template=\"system:/groovy/portal/webui/container/UITabContainer.gtmpl\">" +
         "      <factory-id>TabContainer</factory-id>" +
         "  		<container template=\"system:/groovy/portal/webui/container/UIContainer.gtmpl\"></container>" +
         "  		<container template=\"system:/groovy/portal/webui/container/UIContainer.gtmpl\"></container>" +
         "		</container>" +
         "	</container>" +
         "</page>" ;
tabsPageConfigs.addSelectItemOption(new SelectItemOption("Two Tabs", config, "TwoTabsLayout")) ;

config = "<page>" +
				 "	<owner-type></owner-type>" +
				 "	<owner-id></owner-id>" +
				 "	<name>UIPage</name>" +
         "	<container template=\"system:/groovy/portal/webui/container/UIContainer.gtmpl\">" +
         "		<container template=\"system:/groovy/portal/webui/container/UITabContainer.gtmpl\">" +
         "      <factory-id>TabContainer</factory-id>" +
         "  		<container template=\"system:/groovy/portal/webui/container/UIContainer.gtmpl\"></container>" +
         "  		<container template=\"system:/groovy/portal/webui/container/UIContainer.gtmpl\"></container>" +
         "  		<container template=\"system:/groovy/portal/webui/container/UIContainer.gtmpl\"></container>" +
         "		</container>" +
	       "	</container>" +
         "</page>" ;
tabsPageConfigs.addSelectItemOption(new SelectItemOption("Three Tabs", config, "ThreeTabsLayout")) ;

SelectItemCategory mixPageConfigs = new SelectItemCategory("mixPageConfigs") ;
categories.add(mixPageConfigs); 
  
config = "<page>" +
         "  <owner-type></owner-type>" +
         "  <owner-id></owner-id>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/container/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "</page>";
mixPageConfigs.addSelectItemOption(new SelectItemOption("Two Columns One Row Layout", config, "TwoColumnsOneRowLayout"));

config = "<page>" +
         "  <owner-type></owner-type>" +
         "  <owner-id></owner-id>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/container/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "</page>";
mixPageConfigs.addSelectItemOption(new SelectItemOption("One Row Two Columns Layout", config, "OneRowTwoColumnsLayout"));

config = "<page>" +
         "  <owner-type></owner-type>" +
         "  <owner-id></owner-id>" +
         "  <name>UIPage</name>" +
         "  <container template='system:/groovy/portal/webui/container/UITableColumnContainer.gtmpl'>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "    <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  </container>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "  <container template='system:/groovy/portal/webui/container/UIContainer.gtmpl'></container>" +
         "</page>";
mixPageConfigs.addSelectItemOption(new SelectItemOption("Three Rows Two Columns Layout", config, "ThreeRowsTwoColumnsLayout"));

return categories;