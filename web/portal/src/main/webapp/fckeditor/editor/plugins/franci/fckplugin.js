var initFun = function(O) {
	// init function
}

FCKCommands.RegisterCommand( 'Franci', new FCKDialogCommand( "Franci", "Franci", FCKConfig.PluginsPath + 'franci/franci.html'	, 600, 400, initFun, {A: 3, B: 4}) ) ;

var oNewItem = new FCKToolbarButton('Franci') ;
oNewItem.IconPath = FCKConfig.PluginsPath + 'franci/franci.gif' ;

FCKToolbarItems.RegisterItem('Franci', oNewItem) ;	
