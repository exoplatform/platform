<?php
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 */

/**
 * This class deals with the gadget rendering requests (in default config this
 * would be /gadgets/ifr?url=<some gadget's url>). It uses the gadget server and
 * gadget context to render the xml to a valid html file, and outputs it.
 * 
 */
class GadgetRenderingServlet extends HttpServlet {
	private $context;
	
	/**
	 * Creates the gadget using the GadgetServer class and calls outputGadget
	 *
	 */
	public function doGet()
	{
		try {
			if (empty($_GET['url'])) {
				throw new GadgetException("Missing required parameter: url");
			}
			// GadgetContext builds up all the contextual variables (based on the url or post) 
			// plus instances all required classes (feature registry, fetcher, blacklist, etc)
			$this->context = new GadgetContext('GADGET');
			// Unfortunatly we can't do caja content filtering here, hoping we'll have a RPC service
			// or command line caja to use for this at some point 
			$gadgetServer = new GadgetServer();
			$gadget = $gadgetServer->processGadget($this->context);
			$this->outputGadget($gadget, $this->context);
		} catch ( Exception $e ) {
			$this->outputError($e);
		}
	}
	
	/**
	 * If an error occured (Exception) this function echo's the Exception's message
	 * and if the config['debug'] is true, shows the debug backtrace in a div
	 *
	 * @param Exception $e the exception to show
	 */
	private function outputError($e)
	{
		header("HTTP/1.0 400 Bad Request", true, 400);
		echo "<html><body>";
		echo "<h1>Error</h1>";
		echo $e->getMessage();
		if (Config::get('debug')) {
			echo "<p><b>Debug backtrace</b></p><div style='overflow:auto; height:300px; border:1px solid #000000'><pre>";
			print_r(debug_backtrace());
			echo "</pre></div>>";
		}
		echo "</body></html>";
	}
	
	/**
	 * Takes the gadget to output, and depending on its content type calls either outputHtml-
	 * or outputUrlGadget
	 *
	 * @param Gadget $gadget gadget to render
	 * @param string $view the view to render (only valid with a html content type)
	 */
	private function outputGadget($gadget, $context)
	{
		$view = HttpUtil::getView($gadget, $context);
		switch ($view->getType()) {
			case 'HTML' :
				$this->outputHtmlGadget($gadget, $context, $view);
				break;
			case 'URL' :
				$this->outputUrlGadget($gadget, $context, $view);
				break;
		}
	}
	
	/**
	 * Outputs a html content type gadget.
	 * It creates a html page, with the javascripts from the features inline into the page, plus
	 * calls to 'gadgets.config.init' with the container configuration (config/container.js) and
	 * 'gadgets.Prefs.setMessages_' with all the substitutions. For external javascripts it adds
	 * a <script> tag.
	 *
	 * @param Gadget $gadget
	 * @param GadgetContext $context
	 */
	private function outputHtmlGadget($gadget, $context, $view)
	{
		$this->setContentType("text/html; charset=UTF-8");
		$output = '';
		if (!$view->getQuirks()) {
			$output .= "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
		}
		$output .= "<html>\n<head>\n";
		// TODO: This is so wrong. (todo copied from java shindig, but i would agree with it :))
		$output .= "<style type=\"text/css\">body,td,div,span,p{font-family:arial,sans-serif;} a {color:#0000cc;}a:visited {color:#551a8b;}a:active {color:#ff0000;}body{margin: 0px;padding: 0px;background-color:white;}</style>\n";
		$output .= "</head>\n<body>\n";
		$externJs = "";
		$inlineJs = "";
		$externFmt = "<script src=\"%s\"></script>";
		$forcedLibs = $context->getForcedJsLibs();
		foreach ( $gadget->getJsLibraries() as $library ) {
			$type = $library->getType();
			if ($type == 'URL') {
				// TODO: This case needs to be handled more gracefully by the js
				// servlet. We should probably inline external JS as well.
				$externJs .= sprintf($externFmt, $library->getContent()) . "\n";
			} else if ($type == 'INLINE') {
				$inlineJs .= $library->getContent() . "\n";
			} else {
				// FILE or RESOURCE
				if ($forcedLibs == null) {
					$inlineJs .= $library->getContent() . "\n";
				}
				// otherwise it was already included by config.forceJsLibs.
			}
		}
		// Forced libs first.
		if (! empty($forcedLibs)) {
			$libs = explode(':', $forcedLibs);
			$output .= sprintf($externFmt, Config::get('default_js_prefix').$this->getJsUrl($libs, $gadget)) . "\n";
		}
		if (strlen($inlineJs) > 0) {
			$output .= "<script><!--\n" . $inlineJs . "\n-->\n</script>\n";
		}
		if (strlen($externJs) > 0) {
			$output .= $externJs;
		}
		$output .= "<script><!--\n";
		$output .= $this->appendJsConfig($context, $gadget);
		$output .= $this->appendMessages($gadget);
		$output .= "-->\n</script>\n";
		$gadgetExceptions = array();
		$content = $gadget->getSubstitutions()->substitute($view->getContent());
		if (empty($content)) {
			// Unknown view
			$gadgetExceptions[] = "View: '" . $context->getView() . "' invalid for gadget: " . $gadget->getId()->getKey();
		}
		if (count($gadgetExceptions)) {
			throw new GadgetException(print_r($gadgetExceptions, true));
		}
		$output .= $content . "\n";
		$output .= "<script>gadgets.util.runOnLoadHandlers();</script>\n";
		$output .= "</body>\n</html>";
		if ($context->getIgnoreCache()) {
			// no cache was requested, set non-caching-headers
			$this->setNoCache(true);
		} elseif (isset($_GET['v'])) {
			// version was given, cache for a long long time (a year)
			$this->setCacheTime(365 * 24 * 60 * 60);
		} else {
			// no version was given, cache for 5 minutes
			$this->setCacheTime(5 * 60);
		}
		// Was a privacy policy header configured? if so set it
		if (Config::get('P3P') != '') {
			header("P3P: ".Config::get('P3P'));
		}
		echo $output;
	}
	
	/**
	 * Output's a URL content type gadget, it adds libs=<list:of:js:libraries>.js and user preferences
	 * to the href url, and redirects the browser to it
	 *
	 * @param Gadget $gadget
	 */
	private function outputUrlGadget($gadget, $context, $view)
	{
		// Preserve existing query string parameters.
		$redirURI = $view->getHref();
		$queryStr = strpos($redirURI, '?') !== false ? substr($redirURI, strpos($redirURI, '?')) : '';
		$query = $queryStr;
		// TODO: userprefs on the fragment rather than query string
		$query .= $this->getPrefsQueryString($gadget->getUserPrefValues());
		$libs = array();
		$forcedLibs = Config::get('focedJsLibs');
		if ($forcedLibs == null) {
			$reqs = $gadget->getRequires();
			foreach ( $reqs as $key => $val ) {
				$libs[] = $key;
			}
		} else {
			$libs = explode(':', $forcedLibs);
		}
		$query .= $this->appendLibsToQuery($libs, $gadget);
		// code bugs out with me because of the invalid url syntax since we dont have a URI class to fix it for us
		// this works around that
		if (substr($query, 0, 1) == '&') {
			$query = '?' . substr($query, 1);
		}
		$redirURI .= $query;
		header('Location: ' . $redirURI);
		die();
	}
	
	/**
	 * Returns the requested libs (from getjsUrl) with the libs_param_name prepended
	 * ie: in libs=core:caja:etc.js format
	 *
	 * @param string $libs the libraries
	 * @param Gadget $gadget
	 * @return string the libs=... string to append to the redirection url
	 */
	private function appendLibsToQuery($libs, $gadget)
	{
		$ret = "&";
		$ret .= Config::get('libs_param_name');
		$ret .= "=";
		$ret .= $this->getJsUrl($libs, $gadget);
		return $ret;
	}
	
	/**
	 * Returns the user preferences in &up_<name>=<val> format
	 *
	 * @param array $libs array of features this gadget requires
	 * @param Gadget $gadget
	 * @return string the up_<name>=<val> string to use in the redirection url
	 */
	private function getPrefsQueryString($prefVals)
	{
		$ret = '';
		foreach ( $prefVals->getPrefs() as $key => $val ) {
			$ret .= '&';
			$ret .= Config::get('userpref_param_prefix');
			$ret .= urlencode($key);
			$ret .= '=';
			$ret .= urlencode($val);
		}
		return $ret;
	}
	
	/**
	 * generates the library string (core:caja:etc.js) including a checksum of all the
	 * javascript content (?v=<sha1 of js) for cache busting
	 *
	 * @param string $libs
	 * @param Gadget $gadget
	 * @return string the list of libraries in core:caja:etc.js?v=checksum> format
	 */
	private function getJsUrl($libs, $gadget)
	{
		$buf = '';
		if (! is_array($libs) || ! count($libs)) {
			$buf = 'core';
		} else {
			$firstDone = false;
			foreach ( $libs as $lib ) {
				if ($firstDone) {
					$buf .= ':';
				} else {
					$firstDone = true;
				}
				$buf .= $lib;
			}
		}
		// Build a version string from the sha1() checksum of all included javascript
		// to ensure the client always has the right version
		$inlineJs = '';
		foreach ( $gadget->getJsLibraries() as $library ) {
			$type = $library->getType();
			if ($type != 'URL') {
				$inlineJs .= $library->getContent() . "\n";
			}
		}
		$buf .= ".js?v=" . md5($inlineJs);
		return $buf;
	}
	
	private function appendJsConfig($context, $gadget)
	{
		$container = $context->getContainer();
		$containerConfig = $context->getContainerConfig();
		$gadgetConfig = array();
		$featureConfig = $containerConfig->getConfig($container, 'gadgets.features');
		foreach ( $gadget->getJsLibraries() as $library ) {
			$feature = $library->getFeatureName();
			if (! isset($gadgetConfig[$feature]) && ! empty($featureConfig[$feature])) {
				$gadgetConfig[$feature] = $featureConfig[$feature];
			}
		}
		return "gadgets.config.init(" . json_encode($gadgetConfig) . ");\n";
	}
	
	private function appendMessages($gadget)
	{
		$msgs = '';
		if ($gadget->getMessageBundle()) {
			$bundle = $gadget->getMessageBundle();
			$msgs = json_encode($bundle->getMessages());
		}
		return "gadgets.Prefs.setMessages_($msgs);\n";
	}
}
