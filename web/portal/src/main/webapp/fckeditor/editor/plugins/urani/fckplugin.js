var oNewCommand = new Object() ;
oNewCommand.Name = 'Urani' ;
oNewCommand.Execute = function() {
	 alert("o hay");
}

oNewCommand.GetState = function() {}

FCKCommands.RegisterCommand( 'Urani', oNewCommand ) ;

var oNewItem = new FCKToolbarButton('Urani') ;
oNewItem.IconPath = FCKConfig.PluginsPath + 'urani/urani.gif' ;
FCKToolbarItems.RegisterItem('Urani', oNewItem) ;	
