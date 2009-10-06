/**
 * @author Nguyen Ba Uoc
 */

function ShowNode() {
  this.commandName = 'shownode' ;
} ;

ShowNode.prototype = eXo.application.console.Command.createInstance() ;

ShowNode.prototype.help = function() {
  return ('Usage: ShowNode NodeID [OPTION]... \
          <br>\
          <br/>Show human readable DOM Node structor\
          <br/>Mandatory arguments to long options are mandatory for short options too.\
          <br>\
          <br/>  -ld, --leveldepth level of depth node to show. If not provide default is unlimit level\
          <br>\
          <br/>Examples:\
          <br/>  ShowNode firstDivID will show all child node from firstDivID(unlimit level)\
          <br/>  ShowNode firstDivID -ld=3 will show 3 level dowm from firstDivID') ;
} ;

ShowNode.prototype.execute = function(args, consoleScreen) {
  this.parametersParser(args) ;
  if (!this.subCmds) {
    consoleScreen.write('Missing argument') ;
    return -1;
  }

  var leveldepth = -1 ;
  
  if (this.params) {
    if (this.params['-ld']) {
      leveldepth = this.params['-ld'] ;
    }
    
    if (this.params['--leveldepth']) {
      leveldepth = this.params['--leveldepth'] ;
    }
  }
  if (isNaN(leveldepth)) {
    leveldepth = -1 ;
  }

  var nodeId = this.subCmds[0] ;
  var node = false ;

  node = document.getElementById(nodeId) ;
  if (!node) {
    consoleScreen.write('Node not found')
    return -1 ;
  }
  
  var treeNode = 'DOM tree: [' + nodeId + ']' ;
  treeNode += this._getNodeInfo(node, leveldepth, 0) ;
  consoleScreen.write(treeNode) ;
  
  return 0 ;
} ;

ShowNode.prototype._getNodeInfo = function(node, maxLevel, level) {
  var nodeInfo = '' ;
  if (maxLevel == -1 || level <= maxLevel) {
    var nodeAttributes = [] ;
    if (node.nodeName) {
      nodeAttributes[nodeAttributes.length] = 'NAME: ' + node.nodeName ;
    }
    
    if (node.id) {
      nodeAttributes[nodeAttributes.length] = 'ID: ' + node.id ;
    }
    
    if (node.className) {
      nodeAttributes[nodeAttributes.length] = 'CLASS: ' + node.className ;
    }
    
    nodeInfo += '<br/>' + this.getSpace(level * 2) + '-[' + nodeAttributes.join('; ') + ']' ;
    level ++ ;
    if (node.nodeType == 1 && node.childNodes.length > 0) {
      var nodeLst = node.childNodes ;
      for (var item in nodeLst) {
        nodeInfo += this._getNodeInfo(nodeLst[item], maxLevel, level) ; 
      }
    }
  } 
  return nodeInfo ;
} ;

ShowNode.prototype.getSpace = function(n) {
  var strTmp = '' ;
  for(var i=0; i<n; i++) {
    strTmp += '&nbsp;' ;
  }
  return strTmp ;
}

eXo.application.console.ShowNode = new ShowNode() ;
eXo.application.console.CommandManager.addCommand(eXo.application.console.ShowNode) ;
