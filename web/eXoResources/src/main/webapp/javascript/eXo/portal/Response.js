/***********************************************************************************************
 * Portal  Ajax  Response Data Structure
 * {PortalResponse}
 *      |
 *      |--->{PortletResponse}
 *      |
 *      |--->{PortletResponse}
 *      |          |-->{portletId}
 *      |          |-->{portletTitle}
 *      |          |-->{portletMode}
 *      |          |-->{portletState}
 *      |          |
 *      |          |-->{Data}
 *      |          |      |
 *      |          |      |--->{BlockToUpdate}
 *      |          |      |         |-->{blockId}
 *      |          |      |         |-->{data}
 *      |          |      |
 *      |          |      |--->{BlockToUpdate}
 *      |          |--->{Script}
 *      |
 *      |--->{Data}
 *      |      |
 *      |      |--->{BlockToUpdate}
 *      |      |         |-->{blockId}
 *      |      |         |-->{data}
 *      |      |
 *      |      |--->{BlockToUpdate}
 *      |--->{Script}
 *
 **************************************************************************************************/

function PortletResponse(responseDiv) {
  var  DOMUtil = eXo.core.DOMUtil ;
  var div = eXo.core.DOMUtil.getChildrenByTagName(responseDiv, "div") ;
  this.portletId =  div[0].nodeValue ;
  this.portletTitle =  div[1].nodeValue ;
  this.portletMode =  div[2].nodeValue ;
  this.portletState =  div[3].nodeValue ;
  this.data =  div[4].innerHTML ;
  this.script = div[5].innerHTML ;
  this.blocksToUpdate = null ;
  
  var blocks = DOMUtil.findChildrenByClass(div[4], "div", "BlockToUpdate") ;
  if(blocks.length > 0 ) {
    this.blocksToUpdate = new Array() ;
    for(var i = 0 ; i < blocks.length; i++) {
      var obj = new Object() ; 
      var div = eXo.core.DOMUtil.getChildrenByTagName(blocks[i], "div") ;
      obj.blockId = div[0] ;
      obj.data = div[1] ;
      this.blocksToUpdate[i] = obj ;
    }
  }
};

/*****************************************************************************************/

function PortalResponse(responseDiv) {
  var  DOMUtil = eXo.core.DOMUtil ;
  var div = eXo.core.DOMUtil.getChildrenByTagName(responseDiv, "div") ;

  this.portletResponses = new Array() ;
  var div = eXo.core.DOMUtil.getChildrenByTagName(responseDiv, "div") ;

  for(var i = 0 ; i < div.length; i++) {
    if(div[i].className == "PortletResponse") {
      this.portletResponses[this.portletResponses.length] =  new PortletResponse(div[i]) ;
    } else if(div[i].className == "Data") {
      this.data = div[i] ;
    } else if(div[i].className == "Script") {
      this.script = div[i].innerHTML ;
    }
  }
};
