import org.exoplatform.webui.form.UIFormInputIconSelector.IconSet ;
import org.exoplatform.webui.form.UIFormInputIconSelector.IconCategory;
import org.exoplatform.webui.form.UIFormInputIconSelector.CategoryIconSet ;
import org.exoplatform.webui.form.UIFormInputIconSelector.CategoryIcon ;

CategoryIcon categorySet = new CategoryIcon("misc","32x32"); 
   
  IconSet misc = 
    new IconSet("misc").
    addCategories(
        new IconCategory("Show").
        addIcon("")
    );
  
  IconSet office = 
    new IconSet("offices").
    addCategories(
        new IconCategory("Show").
        addIcon("BoxMagnifier")
    );
  
  IconSet navigation = 
    new IconSet("navigation").
    addCategories(
        new IconCategory("Show").
        addIcon("CyanUpArrowDotted")
    );
  
  IconSet tool = 
    new IconSet("tool").
    addCategories(
        new IconCategory("Show").
        addIcon("NavyWheelDataBox").addIcon("Yellowbulb")
    );
  
  IconSet user = 
    new IconSet("user").
    addCategories(
        new IconCategory("Show").
        addIcon("")
    );
  
  categorySet.addCategory(misc) ;
  categorySet.addCategory(office) ;  
  categorySet.addCategory(navigation) ;
  categorySet.addCategory(tool) ;
  categorySet.addCategory(user) ;
  
return categorySet;
