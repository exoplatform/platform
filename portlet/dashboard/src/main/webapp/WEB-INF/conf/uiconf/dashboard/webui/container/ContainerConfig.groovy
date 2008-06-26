import org.exoplatform.webui.core.model.SelectItemOption ;

List containers = new ArrayList(2) ;

containers.add(new SelectItemOption("column",
        "<container template=\"app:/groovy/dashboard/webui/component/UIColumnContainer.gtmpl\"></container>");
        
containers.add(new SelectItemOption("row",
        "<container template=\"app:/groovy/dashboard/webui/component/UIContainer.gtmpl\"></container>");

return containers ;