function TemplateEngine() {
  this.cacheTemplates = new eXo.core.HashMap();
} ;

//TODO: Test eXo.env.developing  flag
TemplateEngine.prototype.merge = function(template, context, jsLocation) {
  if(jsLocation == null) jsLocation = '/eXoResources/javascript/' ;
  var path = jsLocation  + template;
  var cacheDirective = "max-age=864000" ;
  if(eXo.env.developing)  {
    cacheDirective = "no-store" ;
    this.cacheTemplates.put(path, null);
  }
  var script = this.cacheTemplates.get(path);
  if(script == null) {
    window.status = "Loading Javascript Template " + path ;
    var request =  eXo.core.Browser.createHttpRequest() ;
    request.open('GET', path, false);
    request.setRequestHeader("Cache-Control", cacheDirective);
    request.send(null);
    script = this.compile(request.responseText);
    this.cacheTemplates.put(path, script);
    //request.close() ;
  }
  return this.mergeScript(script, context);
};

TemplateEngine.prototype.toHTML = function(template, context) {
  var script = this.compile(template) ;
  return this.mergeScript(script, context) ;
};

TemplateEngine.prototype.mergeScript = function(script, context) {
  eXo.core.TemplateEngine.currentContext = context ;
  eval(script) ;
  var result = eXo.core.TemplateEngine.currentResult ;
  eXo.core.TemplateEngine.currentContext = null ;
  eXo.core.TemplateEngine.currentResult = null ;
  return result ;
};

TemplateEngine.prototype.compile = function(template) {
  //For IE ,  remove \r if  the developer use a text editor on window
  template = template.replace(/\r/g, "");
  template = template.replace(/\"/g, "\\\"");
  //alert(template);
  var lines = template.split(/\n/g);
  var script = "var result = '' ;\n" ;
  script  += "var context = eXo.core.TemplateEngine.currentContext ;\n" ;
  for(var i = 0; i < lines.length; i++) {
    var line = lines[i] ;
    if (line.match(/^[ ]*<%[^=]/)) {
      //alert("code: " + line) ;
      line = line.replace(/<%/g, "");
      line = line.replace(/%>/g, "");
      script  += line + '\n';
    } else {
      script  += 'result += "' ;
      line = line.replace(/<%=/g, "\" + ");
      line = line.replace(/%>/g, " + \"");
      //alert(line) ;
      script  += line  + '";\n';
    }
  }
  script  += 'eXo.core.TemplateEngine.currentResult = result ;' ;
  return script ;
}

eXo.core.TemplateEngine = new TemplateEngine() ;
