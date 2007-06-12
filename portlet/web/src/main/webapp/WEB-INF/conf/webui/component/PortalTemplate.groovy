import org.exoplatform.webui.core.model.SelectItemOption ;
import java.util.List;
import java.util.ArrayList;

List options = new ArrayList() ;
options.add(new SelectItemOption("label 1", "value 1", "Description for value 1")) ;
options.add(new SelectItemOption("label 2", "value 2", "Description for value 2")) ;
options.add(new SelectItemOption("label 3", "value 3", "Description for value 3")) ;
options.add(new SelectItemOption("label 3", "value 3", "Description for value 4")) ;
options.add(new SelectItemOption("label 3", "value 3", "Description for value 5")) ;


return options;
