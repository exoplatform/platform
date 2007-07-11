/**
 * @author uoc.nb
 */
function LogEngine() {
  this.WELCOME_MESSAGE = 'LogEngine v1.0<hr>' ;
  this.CONTROL_BUTTON_CLASS = 'Button' ;
  this.UISET = {DRAG_BAR : 'Dragbar',
                TOGGLE_COLLAPSE : 'Toggle collapse LogBoard!',
                CLEAR_BOARD : 'Clear LogBoard',
                MSG : 'MSG'} ;
  this.UICLASS = {BUTTON : 'Button', MSG : 'Message', BAR : 'Button Bar'} ;
  
  this.logPanelElem = false ;
  
  this.logControlElem = false ;
  
  this.dragBar = false ;
  
  this.toggleCollapseBtn = false ;
  
  this.clearLogBtn = false ;          
  
  this.logBoardElem = false ;
  
  this.logPanelCreated = false ;
}

LogEngine.prototype.createLogPanel = function() {
  if (this.logPanelCreated) {
    return;
  }
  this.logPanelElem = document.createElement('div');
  this.logPanelElem.className = 'LogPanel';
  document.body.appendChild(this.logPanelElem);
      
  this.logControlElem = document.createElement('div');
  this.logControlElem.className = 'Control';
  this.logPanelElem.appendChild(this.logControlElem);
  
                          
  this.dragBar = this.createUIControl('DRAG_BAR', 'BAR');
  this.toggleCollapseBtn = this.createUIControl('TOGGLE_COLLAPSE', 'BUTTON');
  this.clearLogBtn = this.createUIControl('CLEAR_BOARD', 'BUTTON');
  this.logControlElem.appendChild(this.dragBar);
  this.logControlElem.appendChild(this.toggleCollapseBtn);
  this.logControlElem.appendChild(this.clearLogBtn);
              
  this.logBoardElem = document.createElement('div');
  this.logBoardElem.className = 'LogBoard';
  this.logBoardElem.innerHTML = this.WELCOME_MESSAGE;
  this.logPanelElem.appendChild(this.logBoardElem);
  this.logPanelCreated = true;
}
  
LogEngine.prototype.createUIControl = function(_btnType, _uiType) {
  var controlBtn = document.createElement('div');
  controlBtn.className = this.UICLASS[_uiType];
  if (this.UISET[_btnType] != 'MSG') {
    controlBtn.setAttribute('title', this.UISET[_btnType]);
  }
  var clickAction = false;
  switch(_btnType) {
    case 'DRAG_BAR' :
      controlBtn.innerHTML = '&nbsp;' ;      
      controlBtn.onclick = function(e) {
        eXo.core.DragDrop.init([], this, eXo.core.LogEngine.logPanelElem, e) ;
      } ;
      break;
    case 'TOGGLE_COLLAPSE' : 
      controlBtn.innerHTML = '-/+';
      clickAction = function(e) {
        eXo.core.LogEngine._toggleCollapse(e, this);
      };
      break;
    case 'CLEAR_BOARD' :
      controlBtn.innerHTML = 'X';
      clickAction = function(e) {
        eXo.core.LogEngine._clearLogBoard(e, this);
      };
      break;
    default :
      controlBtn.innerHTML = '...';
      clickAction = function(e) {
        eXo.core.LogEngine._defaultAction(e, this);
      };
  }            
  if(clickAction) {
    controlBtn.onclick = clickAction;    
  }
  return controlBtn;
}
  
  // --+--
LogEngine.prototype._defaultAction = function(_e, _owner) {
  // .. 
}
  
LogEngine.prototype._toggleCollapse = function(_e, _owner) {
  if(!this.logBoardElem.style.display || this.logBoardElem.style.display == 'none') {
    this.logBoardElem.style.display = 'block';
  } else {
    this.logBoardElem.style.display = 'none';
  }
}
  
LogEngine.prototype.clearLog = function() {
  this._clearLogBoard();
}  

LogEngine.prototype._clearLogBoard = function(_em, _owner) {
  this.logBoardElem.innerHTML = this.WELCOME_MESSAGE;
}

LogEngine.prototype.logWriteLn = function(_msg) {
  this.logWrite(_msg + '<br>');
}

LogEngine.prototype.logWrite = function(_msg) {
  this._logWrite(_msg);
}  
  
LogEngine.prototype._logWrite = function(_msg) {    
  var msgNode = this.createUIControl('MSG', 'MSG');
  msgNode.innerHTML = _msg;    
  this.logBoardElem.appendChild(msgNode);
  this.logBoardElem.scrollTop = this.logBoardElem.scrollHeight;
}

eXo.core.LogEngine = new LogEngine() ; 