import org.exoplatform.webui.component.model.SelectItemOption ;
import java.util.List;
import java.util.ArrayList;

def decorator = ["DefaultStyle", "RoundedTopCornerLinedStyle", 
                      "GrayBorderRoundedCornerStyle", "BlueCloudBackgroundStyle",
                      "CuttedShapeCornerStyle", "DottedShapeCornerStyle",
                      "LightBrownDoubleDotLineBorderBoxStyle", "BlueBorderRoundedCornerBoxStyle"
                      ];
List templates = new ArrayList() ;
int index = 1 ;  
for(tem in decorator) {    
    templates.add(new SelectItemOption("decorator "+index,tem,tem));
    index++ ;
  }

return templates;
  