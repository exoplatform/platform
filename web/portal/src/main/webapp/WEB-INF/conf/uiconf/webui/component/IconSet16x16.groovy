import org.exoplatform.webui.component.UIFormInputIconSelector ;

UIFormInputIconSelector.CategoryIcon categorySet = new UIFormInputIconSelector.CategoryIcon("misc","16x16"); 
UIFormInputIconSelector.IconSet misc = 
  new UIFormInputIconSelector.IconSet("misc").                      
  addCategories(
      new UIFormInputIconSelector.IconCategory("show").
      addIcon("HandPoint16x16").addIcon("TripleBox16x16").    
      addIcon("EditPortlet16x16").addIcon("View16x16").
      addIcon("Help16x16").addIcon("Minimize16x16").
      addIcon("Maximize16x16").addIcon("Restore16x16").
      addIcon("Node16x16").addIcon("BlueSquare16x16").
      addIcon("Level16x16").addIcon("Feature16x16").
      addIcon("RSS16x16").addIcon("Earth16x16").
      addIcon("AdminView16x16").addIcon("Briefcase16x16").
      addIcon("Node16x16").addIcon("Config16x16")
  ) ;
UIFormInputIconSelector.IconSet office = 
  new UIFormInputIconSelector.IconSet("offices").                      
  addCategories(
      new UIFormInputIconSelector.IconCategory("show").
      addIcon("Save16x16").addIcon("Cancel16x16").    
      addIcon("Back16x16").addIcon("CloseFolder16x16").
      addIcon("OpenFolder16x16").addIcon("Pencil16x16").
      addIcon("DustBin16x16").addIcon("Level116x16").
      addIcon("Level216x16").addIcon("Level316x16").
      addIcon("CircleInfo16x16").addIcon("Lock16x16").
      addIcon("PastePalate16x16")
  ) ;
UIFormInputIconSelector.IconSet navigation = 
  new UIFormInputIconSelector.IconSet("navigation").
  addCategories(
      new UIFormInputIconSelector.IconCategory("show").
      addIcon("BlueBackArrow16x16").addIcon("BlueNextArrow16x16").
      addIcon("BlueCircleLeftArrow16x16").addIcon("BlueCircleRightArrow16x16").
      addIcon("BlueGridRightArrowIcon16x16").addIcon("GrayDoubleGridDownArrow16x16").
      addIcon("BlueBack16x16").addIcon("BlueNext16x16")        
  );  
UIFormInputIconSelector.IconSet tool = 
  new UIFormInputIconSelector.IconSet("tool").
  addCategories(
      new UIFormInputIconSelector.IconCategory("show").
      addIcon("YellowBulb16x16").addIcon("LightBlueGlobal16x16").
      addIcon("BlueActionWheel16x16").addIcon("GrayFillRightArrow16x16").
      addIcon("BlueSquareRightArrow16x16")
  );
UIFormInputIconSelector.IconSet user = 
  new UIFormInputIconSelector.IconSet("user").
  addCategories(
      new UIFormInputIconSelector.IconCategory("show").
      addIcon("BlueBalanced16x16").addIcon("BlueCogWheel16x16").
      addIcon("Earth16x16").addIcon("Finance16x16").
      addIcon("FootBall16x16").addIcon("HotNews16x16").
      addIcon("Medical16x16").addIcon("Politic16x16").
      addIcon("RainyCloud16x16").addIcon("Social16x16").
      addIcon("YellowTriangleFace16x16")
  ) ;
categorySet.addCategory(misc) ;
categorySet.addCategory(office) ;
categorySet.addCategory(navigation) ;
categorySet.addCategory(tool) ;
categorySet.addCategory(user) ;	  
return categorySet;
