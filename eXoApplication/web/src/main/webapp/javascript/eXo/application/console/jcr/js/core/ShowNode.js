/**
 * @author Nguyen Ba Uoc
 */

function ShowNode() {
} ;

ShowNode.prototype.help = function() {
  return ('Usage: ShowNode [OPTION]... NodeID' +
  '<br/>Show human readable DOM Node structor' +
  '<br/>Mandatory arguments to long options are mandatory for short options too.' +
  '<br/> -ld, --leveldepth level of depth node to show. If not provide default is unlimit level' +
  '<br/>Examples: ' +
  '<br/>&&nbsp;nbsp;ShowNode firstDivID will show all child node from firstDivID(unlimit level)' +
  '<br/>&&nbsp;nbsp;ShowNode firstDivID 3 will show 3 level dowm from firstDivID') ;
} ;

ShowNode.prototype.execute = function(args, screen) {
  if (args.length <= 0) {
    return {retcode: -1,msg: 'Missing argument'} ;
  }

  var leveldepth = -1 ;

  if (args.length > 1) {
    leveldepth = args[0] ;
  }
  
  var node = false ;
  if (args.length < 2) {
    node = args[0] ;
  } else {
    node = args[1] ;
  }

  node = document.getElementById(node) ;
  if (!node) {
    return {retcode: -1,msg: 'Node not found'} ;
  }
  
  var treeNode = this._getNodeInfo(node, leveldepth, 0) ;
  return {retcode: 0, result: treeNode} ;
} ;

ShowNode.prototype._getNodeInfo = function(node, maxLevel, level) {
  var nodeInfo = '' ;
  if (maxLevel == -1 || level <= maxLevel) {
    nodeInfo += '<br/>' + level + 's-Name: ' + node.nodeName + ' ; ID: ' + node.id + ' ; CLASS: ' + node.className ;
    level ++ ;
    if (node.childNodes.length > 0) {
      var nodeList = node.childNodes ;
      for (var item in nodeList) {
        nodeInfo += this._getNodeInfo(item, maxLevel, level) ; 
      }
    }
  } 
  return nodeInfo ;
} ;

eXo.application.console.ShowNode = new ShowNode() ;
