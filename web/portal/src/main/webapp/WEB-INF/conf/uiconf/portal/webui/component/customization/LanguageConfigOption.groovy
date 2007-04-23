import java.util.List;
import java.util.ArrayList;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;

List categories = new ArrayList();

  SelectItemCategory languageEuro = new  SelectItemCategory("Euro", false);
  languageEuro.addSelectItemOption(new SelectItemOption("French", "fr", "French", "French",true));
  languageEuro.addSelectItemOption(new SelectItemOption("German", "gr", "German"));
  languageEuro.setSelected(true);
  categories.add(languageEuro);
  
  SelectItemCategory languageAsia = new  SelectItemCategory("Asia", false);
  languageAsia.addSelectItemOption(new SelectItemOption("Vietnamese", "vi", "Vietnamese"));
  languageAsia.addSelectItemOption(new SelectItemOption("Japanese", "jp", "Japanese"));
  categories.add(languageAsia);
  
 return categories;