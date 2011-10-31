/**
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
eXo = {
  gadget : {}
};

function ServicesManagement() {
  this.DEFAULT_SERVICES_URL = "/rest/management";
}

ServicesManagement.prototype.init = function() { 
  var monitor = eXo.gadget.ServicesManagement;
  var prefs = new _IG_Prefs();
  var servicesURL = prefs.getString("servicesURL");
  if (servicesURL && $.trim(servicesURL) != "") {
    monitor.SERVICES_URL = $.trim(servicesURL);
  } else {
    monitor.SERVICES_URL = monitor.DEFAULT_SERVICES_URL;
  }
  
  function getContext(url) {    
    if (!url) return "";
    var fslash = url.indexOf("/");
    var lslash = url.indexOf("/", fslash + 2);
    var context = url.substring(0, lslash);
    return context;
  }
  
  if (monitor.SERVICES_URL.indexOf("http://") == 0 || 
      monitor.SERVICES_URL.indexOf("https://") == 0) {
    if (getContext(document.location.href) !== getContext(monitor.SERVICES_URL)) {
      alert(prefs.getMsg("failManage"));
      return;
    }
  }
  
  monitor.registerHandler();
  monitor.makeRequest(monitor.SERVICES_URL, monitor.renderServiceSelector);
};

ServicesManagement.prototype.renderServiceSelector = function(services) {
  if (!services || !services.value || services.value.length == 0) {
    alert(new _IG_Prefs().getMsg("noServices"));    
  }
  var servicesSelector = $("#servicesSelector");
  var optionsHtml = "";

  if (services && services.value) {
    var serviceNames = services.value;

    for ( var i = 0; i < serviceNames.length; i++) {
      optionsHtml += "<option>" + gadgets.util.escapeString(serviceNames[i])
          + "</option>";
    }
  }

  servicesSelector.html(optionsHtml);
  servicesSelector.change();
};

ServicesManagement.prototype.renderServiceDetailForHome = function(data) {
    if (data) {
        if(data.description) {
            $("#ServiceDescription").html(data.description);    
        } else {
          $("#ServiceDescription").html(new _IG_Prefs().getMsg("noDescription"));
        }
        
        if(data.methods) {
            eXo.gadget.ServicesManagement.renderMethodSelector(data);
        }
        
        if(data.properties) {
            eXo.gadget.ServicesManagement.renderPropertySelector(data);
        }
    }
};

ServicesManagement.prototype.renderMethodSelector = function(methodData) {
  var methodSelector = $("#methodsSelector");
  var optionsHtml = "";
  var methods = null;

  if (methodData && methodData.methods) {
    methods = methodData.methods;

    for ( var i = 0; i < methods.length; i++) {
      optionsHtml += "<option>" + gadgets.util.escapeString(methods[i].name)
          + "</option>";
    }
  }

  if (optionsHtml == "") {
    optionsHtml = "<option></option>";
  }

  methodSelector.html(optionsHtml);
  methodSelector.data('methods', methods);
  methodSelector.change();
};

ServicesManagement.prototype.renderPropertySelector = function(propertyData) {
    var propertySelector = $("#propertiesSelector");
    var optionsHtml = "";
    var properties = null;

    if (propertyData && propertyData.properties) {
        properties = propertyData.properties;

        for ( var i = 0; i < properties.length; i++) {
            optionsHtml += "<option>" + gadgets.util.escapeString(properties[i].name)
                    + "</option>";
        }
    }

    if (optionsHtml == "") {
        optionsHtml = "<option></option>";
    }

    propertySelector.html(optionsHtml);
    propertySelector.data('properties', properties);
    propertySelector.change();
};

ServicesManagement.prototype.renderMethodDetail = function(method) {
  if (!method) {
    method = {
      name : "",
      description : "",
      method : "",
      parameters : []
    };
  }
  var util = gadgets.util;

  $("#methodName").html(util.escapeString(method.name));
  $("#methodDescription").html(util.escapeString(method.description ? method.description : ""));
  $("#reqMethod").html(util.escapeString(method.method));

  var paramTable = "<table>";
  for ( var i = 0; i < method.parameters.length; i++) {
    paramTable += "<tr><td>" + util.escapeString(method.parameters[i].name)
        + "</td></tr>";
  }

  if (paramTable == "<table>") {
    paramTable += "<tr><td>[]</td></tr>";
  }
  paramTable += "</table>";
  $("#parametersTable").html(paramTable);
  eXo.gadget.ServicesManagement.resetHeight();
};

ServicesManagement.prototype.renderPropertyDetail = function(property) {
    if (!property) {
        property = {
            name : "",
            description : ""
        };
    }
    var util = gadgets.util;

    $("#propertyName").html(util.escapeString(property.name));
    $("#propertyDescription").html(util.escapeString(property.description ? property.description : ""));
    eXo.gadget.ServicesManagement.resetHeight();
};
// End Home View

// Start Canvas view
ServicesManagement.prototype.renderServiceDetailForCanvas = function(data) {
  if (data) {        
        if(data.description) {
            $("#ServiceDescription").html(data.description);    
        } else {
          $("#ServiceDescription").html(new _IG_Prefs().getMsg("noDescription"));
        }
      
    if(data.methods) {
      eXo.gadget.ServicesManagement.renderMethodsForCanvas(data);
    }
    
    if(data.properties) {
      eXo.gadget.ServicesManagement.renderPropertiesForCanvas(data);
    }        

    eXo.gadget.ServicesManagement.fadeIn($(".ContentSelected")[0]);
  }
};

ServicesManagement.prototype.getContentContainer = function(tab) {
  if (tab.id == "MethodsTab") {
    return $("#ServiceMethods")[0];
  } else {
    return  $("#ServiceProperties")[0];
  }
};

ServicesManagement.prototype.fadeIn = function(target, callback) {
  $(target).hide();
  $(target).fadeIn(700, callback);    
  eXo.gadget.ServicesManagement.resetHeight();
};

ServicesManagement.prototype.renderMethodsForCanvas = function(methodData) {
  if (!methodData || !methodData.methods) {
    return;
  }

  var methods = methodData.methods;
  var methodForCanvas = "";
  var util = gadgets.util;

  for ( var i = 0; i < methods.length; i++) {
    var method = methods[i];
    var methodName = util.escapeString(method.name);
    var methodDescription = util.escapeString(method.description ? method.description : "");
    var reqMethod = util.escapeString(method.method);

    var rowClass = i % 2 == 0 ? "EvenRow" : "OddRow";
    methodForCanvas += "<tr class='" + rowClass + "'>"
        + "<td><div class='Text methodName'>" + methodName + "</div></td>"
        + "<td><div class='Text methodDescription'>" + methodDescription + "</div></td>"
        + "<td><div class='Text reqMethod'>" + reqMethod + "</div></td>"
        + "<td><form style='margin-bottom: 0px;'>";
    for ( var j = 0; j < method.parameters.length; j++) {
      methodForCanvas += "<div class='SkinID'>"
          + util.escapeString(method.parameters[j].name) + " "
          + "<input type='text' name='"
          + util.escapeString(method.parameters[j].name) + "'>" + "</div>";
    }
    methodForCanvas += "</form></td>" + "<td>"
        + "<div class='MethodActionButton GadgetStyle FL'>"
        + "<div class='ButtonLeft'>" + "<div class='ButtonRight'>"
        + "<div class='ButtonMiddle'>" + "<a href='#'>Run</a>" + "</div>"
        + "</div>" + "</div>" + "</div>" + "</td></tr>";

  }
  if (methodForCanvas == "") {
    methodForCanvas = "<tr class='EventRow'><td colspan='5' align='center'><div class='Text'>" + new _IG_Prefs().getMsg("noMethod") + "</div></td></tr>";
  }
  $("#methodsForCanvas").html(methodForCanvas);  
};

/**
 * data is not null
 */
ServicesManagement.prototype.renderPropertiesForCanvas = function(data) {
  var props = data.properties;
  var propertyForCanvas = "";
  var util = gadgets.util;

  for ( var i = 0; i < props.length; i++) {
    var prop = props[i];
    var propName = util.escapeString(prop.name);
    var propDescription = util.escapeString(prop.description ? prop.description : "");

    var rowClass = i % 2 == 0 ? "EvenRow" : "OddRow";
    propertyForCanvas += "<tr class='" + rowClass + "'>"
        + "<td><div class='Text propName'>" + propName + "</div></td>"
        + "<td><div class='Text propDescription'>" + propDescription + "</div></td>";

    propertyForCanvas += "<td>"
        + "<div class='PropertyActionButton GadgetStyle FL'>"
        + "<div class='ButtonLeft'>" + "<div class='ButtonRight'>"
        + "<div class='ButtonMiddle'>" + "<a href='#'>Get</a>" + "</div>"
        + "</div>" + "</div>" + "</div>" + "</td></tr>";

  }
  if (propertyForCanvas == "") {
    propertyForCanvas = "<tr class='EvenRow'><td colspan='3' align='center'><div class='Text'>" + new _IG_Prefs().getMsg("noProperty") + "</div></td></tr>";
  }
  $("#propertiesForCanvas").html(propertyForCanvas);  
};

ServicesManagement.prototype.showMinimessage = function(jsonMessage) {
    var msgObj = $("#resultMessage")[0];
    $(msgObj).css("Visibility", "hidden");
    $(msgObj).html("");
  
  var parsedObj;
  try {
    parsedObj = gadgets.json.parse(jsonMessage);
  } catch (e) {
    parsedObj = jsonMessage;
  }
  var htmlTable = $.trim(eXo.gadget.ServicesManagement.objToTable(parsedObj));
  if (htmlTable == "" || htmlTable == "empty object") {
    htmlTable = "Method's executed, return no result";
  }

  var msg = new gadgets.MiniMessage("ServicesManagement", msgObj);
  var executeMsg = msg.createDismissibleMessage(htmlTable, function() {
    window.setTimeout(function() {gadgets.window.adjustHeight($(".UIGadget").height()); }, 500);          
    return true;
  });
  
  executeMsg.style.height = "100px";
  executeMsg.style.overflow = "auto";
  $(".mmlib_xlink").each(function() {
    $(this.parentNode).attr("style", "vertical-align: top");
    $(this).html("");
  });
  $(".mmlib_table .UIGrid").each(function() {
    $(this.parentNode).attr("style", "vertical-align: top");
  });
  
  eXo.gadget.ServicesManagement.resetHeight();  
  $(msgObj).hide();
  $(msgObj).slideDown(1200);
  $(msgObj).css("Visibility", "visible");
};

ServicesManagement.prototype.objToTable = function(obj) {
  var type = typeof (obj);
  if (type != "object") {
    return gadgets.util.escapeString(obj + "");
  }

  if (!obj || $.isEmptyObject(obj)
      || (obj.constructor == Array && obj.length == 0)) {
    return "empty object";
  }

  var str = "<table cellspacing='0' class='UIGrid'>";
  if (obj.constructor == Array) {
    for ( var i = 0; i < obj.length; i++) {
      var rowClass = i % 2 == 0 ? "EvenRow" : "OddRow";
      str += "<tr class='" + rowClass + "'><td><div class='Text'>";
      str += eXo.gadget.ServicesManagement.objToTable(obj[i]);
      str += "</div></td></tr>";
    }
  } else {
    str += "<tr>";
    for ( var prop in obj) {
      str += "<th>";
      str += eXo.gadget.ServicesManagement.objToTable(prop);
      str += "</th>";
    }
    str += "</tr>";

    str += "<tr>";
    for ( var prop in obj) {
      str += "<td>";
      str += eXo.gadget.ServicesManagement.objToTable(obj[prop]);
      str += "</td>";
    }
    str += "</tr>";
  }

  str += "</table>";
  return str;
};

ServicesManagement.prototype.resetHeight = function() {
  if ($.browser.safari) {
    gadgets.window.adjustHeight($(".UIGadget").height());
  } else {
    gadgets.window.adjustHeight();
  }
};

eXo.gadget.ServicesManagement = new ServicesManagement();