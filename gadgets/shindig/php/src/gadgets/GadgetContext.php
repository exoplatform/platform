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

define('DEFAULT_VIEW', 'default');

/*
 * GadgetContext contains all contextual variables and classes that are relevant for this request,
 * such as url, httpFetcher, feature registry, etc.
 * Server wide variables are stored in config.php
 */
class GadgetContext {
	private $httpFetcher = null;
	private $locale = null;
	private $renderingContext = null;
	private $registry = null;
	private $userPrefs = null;
	private $gadgetId = null;
	private $view = null;
	private $moduleId = null;
	private $url = null;
	private $cache = null;
	private $blacklist = null;
	private $ignoreCache = null;
	private $forcedJsLibs = null;
	private $containerConfig = null;
	private $container = null;

	public function __construct($renderingContext)
	{
		// Rendering context is set by the calling event handler (either GADGET or CONTAINER)
		$this->setRenderingContext($renderingContext);
		
		// Request variables
		$this->setIgnoreCache($this->getIgnoreCacheParam());
		$this->setForcedJsLibs($this->getFocedJsLibsParam());
		$this->setUrl($this->getUrlParam());
		$this->setModuleId($this->getModuleIdParam());
		$this->setView($this->getViewParam());
		$this->setContainer($this->getContainerParam());
		//NOTE All classes are initialized when called (aka lazy loading) because we don't 
	//need all of them in every situation
	}

	private function getContainerParam()
	{
		$container = 'default';
		if (! empty($_GET['container'])) {
			$container = $_GET['container'];
		} elseif (! empty($_POST['container'])) {
			$container = $_POST['container'];
			//FIXME The paramater used to be called 'synd' & is scheduled for removal
		} elseif (! empty($_GET['synd'])) {
			$container = $_GET['synd'];
		} elseif (! empty($_POST['synd'])) {
			$container = $_POST['synd'];
		}
		return $container;
	}

	private function getIgnoreCacheParam()
	{
		// Support both the old Orkut style &bpc and new standard style &nocache= params
		return (isset($_GET['nocache']) && intval($_GET['nocache']) == 1) || (isset($_GET['bpc']) && intval($_GET['bpc']) == 1);
	}

	private function getFocedJsLibsParam()
	{
		return isset($_GET['libs']) ? trim($_GET['libs']) : null;
	}

	private function getUrlParam()
	{
		if (! empty($_GET['url'])) {
			return $_GET['url'];
		} elseif (! empty($_POST['url'])) {
			return $_POST['url'];
		}
		return null;
	}

	private function getModuleIdParam()
	{
		return isset($_GET['mid']) && is_numeric($_GET['mid']) ? intval($_GET['mid']) : 0;
	}

	private function getViewParam()
	{
		return ! empty($_GET['view']) ? $_GET['view'] : DEFAULT_VIEW;
	}

	private function instanceBlacklist()
	{
		$blackListClass = Config::get('blacklist_class');
		if (! empty($blackListClass)) {
			return new $blackListClass();
		} else {
			return null;
		}
	}

	private function instanceUserPrefs()
	{
		$prefs = array();
		$userPrefParamPrefix = Config::get('userpref_param_prefix');
		foreach ($_GET as $key => $val) {
			if (substr($key, 0, strlen($userPrefParamPrefix)) == $userPrefParamPrefix) {
				$name = substr($key, strlen($userPrefParamPrefix));
				$prefs[$name] = $val;
			}
		}
		return new UserPrefs($prefs);
	}

	private function instanceGadgetId($url, $moduleId)
	{
		return new GadgetId($url, $moduleId);
	}

	private function instanceHttpFetcher()
	{
		$remoteContent = Config::get('remote_content');
		return new $remoteContent();
	}

	private function instanceCache()
	{
		$dataCache = Config::get('data_cache');
		return new $dataCache();
	}

	private function instanceRegistry()
	{
		// Profiling showed 40% of the processing time was spend in the feature registry
		// So by caching this and making it a one time initialization, we almost double the performance  
		if (! ($registry = $this->getCache()->get(sha1(Config::get('features_path'))))) {
			$registry = new GadgetFeatureRegistry(Config::get('features_path'));
			$this->getCache()->set(sha1(Config::get('features_path')), $registry);
		}
		return $registry;
	}

	private function instanceLocale()
	{
		$language = 'all';
		$country = 'all';
		if (! empty($_SERVER['HTTP_ACCEPT_LANGUAGE'])) {
			$acceptLanguage = explode(';', $_SERVER['HTTP_ACCEPT_LANGUAGE']);
			$acceptLanguage = $acceptLanguage[0];
			if (strpos($acceptLanguage, '-') !== false) {
				$lang = explode('-', $acceptLanguage);
				$language = $lang[0];
				$country = $lang[1];
				if (strpos($country, ',') !== false) {
					$country = explode(',', $country);
					$country = $country[0];
				}
			} else {
				$language = $acceptLanguage;
			}
		
		}
		return new Locale($language, $country);
	}

	private function instanceContainerConfig()
	{
		return new ContainerConfig(Config::get('container_path'));
	}

	public function getContainer()
	{
		return $this->container;
	}

	public function getContainerConfig()
	{
		if ($this->containerConfig == null) {
			$this->containerConfig = $this->instanceContainerConfig();
		}
		return $this->containerConfig;
	}

	public function getCache()
	{
		if ($this->cache == null) {
			$this->setCache($this->instanceCache());
		}
		return $this->cache;
	}

	public function getGadgetId()
	{
		if ($this->gadgetId == null) {
			$this->setGadgetId($this->instanceGadgetId($this->getUrl(), $this->getModuleId()));
		}
		return $this->gadgetId;
	}

	public function getModuleId()
	{
		return $this->moduleId;
	}

	public function getRegistry()
	{
		if ($this->registry == null) {
			$this->setRegistry($this->instanceRegistry());
		}
		return $this->registry;
	}

	public function getUrl()
	{
		return $this->url;
	}

	public function getUserPrefs()
	{
		if ($this->userPrefs == null) {
			$this->setUserPrefs($this->instanceUserPrefs());
		}
		return $this->userPrefs;
	}

	public function getView()
	{
		return $this->view;
	}

	public function setContainer($container)
	{
		$this->container = $container;
	}

	public function setContainerConfig($containerConfig)
	{
		$this->containerConfig = $containerConfig;
	}

	public function setBlacklist($blacklist)
	{
		$this->blacklist = $blacklist;
	}

	public function setCache($cache)
	{
		$this->cache = $cache;
	}

	public function setGadgetId($gadgetId)
	{
		$this->gadgetId = $gadgetId;
	}

	public function setHttpFetcher($httpFetcher)
	{
		$this->httpFetcher = $httpFetcher;
	}

	public function setLocale($locale)
	{
		$this->locale = $locale;
	}

	public function setModuleId($moduleId)
	{
		$this->moduleId = $moduleId;
	}

	public function setRegistry($registry)
	{
		$this->registry = $registry;
	}

	public function setRenderingContext($renderingContext)
	{
		$this->renderingContext = $renderingContext;
	}

	public function setUrl($url)
	{
		$this->url = $url;
	}

	public function setUserPrefs($userPrefs)
	{
		$this->userPrefs = $userPrefs;
	}

	public function setView($view)
	{
		$this->view = $view;
	}

	public function setIgnoreCache($ignoreCache)
	{
		$this->ignoreCache = $ignoreCache;
	}

	public function setForcedJsLibs($forcedJsLibs)
	{
		$this->forcedJsLibs = $forcedJsLibs;
	}

	public function getIgnoreCache()
	{
		return $this->ignoreCache;
	}

	public function getForcedJsLibs()
	{
		return $this->forcedJsLibs;
	}

	public function getBlacklist()
	{
		if ($this->blacklist == null) {
			$this->setBlacklist($this->instanceBlacklist());
		}
		return $this->blacklist;
	}

	public function getRenderingContext()
	{
		return $this->renderingContext;
	}

	public function getHttpFetcher()
	{
		if ($this->httpFetcher == null) {
			$this->setHttpFetcher($this->instanceHttpFetcher());
		}
		return $this->httpFetcher;
	}

	public function getLocale()
	{
		if ($this->locale == null) {
			$this->setLocale($this->instanceLocale());
		}
		return $this->locale;
	}

	public function getFeatureRegistry()
	{
		return $this->registry;
	}
}
