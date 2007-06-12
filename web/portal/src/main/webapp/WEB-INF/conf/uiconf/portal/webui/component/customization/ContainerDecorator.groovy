import org.exoplatform.webui.core.model.SelectItemOption ;
import org.exoplatform.webui.core.model.SelectItemCategory;
import java.util.List;
import java.util.ArrayList;

List categories = new ArrayList();

  SelectItemCategory decorator1 = 
    new SelectItemCategory("Decorator1").
    addSelectItemOption(new SelectItemOption("Decorator","DefaultStyle")).
    addSelectItemOption(new SelectItemOption("Decorator","RoundedTopCornerLinedStyle")).
    addSelectItemOption(new SelectItemOption("Decorator","GrayBorderRoundedCornerStyle")).    
    addSelectItemOption(new SelectItemOption("Decorator","BlueCloudBackgroundStyle")).
    addSelectItemOption(new SelectItemOption("Decorator","CuttedShapeCornerStyle"));    
  
  SelectItemCategory decorator2 = 
    new SelectItemCategory("Decorator2").
    addSelectItemOption(new SelectItemOption("Decorator","RoundedTopCornerBoxStyle")).
    addSelectItemOption(new SelectItemOption("Decorator","DottedShapeCornerStyle")).
    addSelectItemOption(new SelectItemOption("Decorator","LightBrownDoubleDotLineBorderBoxStyle")).
    addSelectItemOption(new SelectItemOption("Decorator","BlueBorderRoundedCornerBoxStyle")).    
    addSelectItemOption(new SelectItemOption("Decorator","RoundedCornerInnerGlowBoxStyle"));
    
  
  categories.add(decorator1);
  categories.add(decorator2);
  

return categories;