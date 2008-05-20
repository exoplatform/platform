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

/*
 * In Java terms this would be the Gadget, GadgetView and GadgetSpec all rolled into one
 * We combined it into one class since it makes more sense in PHP and provides a nice 
 * speedup too.
 */

class Gadget {
	private $jsLibraries;
	private $substitutions;
	private $userPrefValues;
	private $messageBundle = array();
	// As in UserPref, no enums so fake it
	public $contentTypes = array('HTML', 'URL');
	public $id;
	public $author;
	public $authorEmail;
	public $description;
	public $directoryTitle;
	public $contentData = array();
	public $localeSpecs = array();
	public $preloads = array();
	public $requires = array();
	public $screenshot;
	public $thumbnail;
	public $title;
	public $titleUrl = null;
	public $userPrefs = array();
	public $authorAffiliation;
	public $authorLocation;
	public $authorPhoto;
	public $authorAboutMe;
	public $authorQuote;
	public $authorLink;
	public $showStats;
	public $showInDirectory;
	public $string;
	public $width;
	public $height;
	public $category;
	public $category2;
	public $singleton;
	public $renderInline;
	public $scaling;
	public $scrolling;
	public $views = array();

	public function __construct($id = false, $context)
	{
		if ($id) {
			$this->id = $id;
		}
		if ($context->getUserPrefs()) {
			$this->setPrefs($context->getUserPrefs());
		}
		$this->substitutions = new Substitutions();
		$this->jsLibraries = array();
	}

	public function setId($id)
	{
		$this->id = $id;
	}

	public function setPrefs($prefs)
	{
		$this->userPrefValues = $prefs;
	}

	public function getAuthor()
	{
		return $this->substitutions->substitute($this->author);
	}

	public function getAuthorEmail()
	{
		return $this->substitutions->substitute($this->authorEmail);
	}

	public function getMessageBundle()
	{
		return $this->messageBundle;
	}

	public function getDescription()
	{
		return $this->substitutions->substitute($this->description);
	}

	public function getDirectoryTitle()
	{
		return $this->substitutions->substitute($this->directoryTitle);
	}

	public function getId()
	{
		return $this->id;
	}

	public function getJsLibraries()
	{
		return $this->jsLibraries;
	}

	public function addJsLibrary($library)
	{
		$this->jsLibraries[] = $library;
	}

	public function getLocaleSpecs()
	{
		return $this->localeSpecs;
	}

	public function getFeatureParams($gadget, $feature)
	{
		//FIXME not working atm
		$spec = $gadget->getRequires();
		$spec = isset($spec[$feature->getName()]) ? $spec[$feature->getName()] : null;
		if ($spec == null) {
			return array();
		} else {
			return $spec->getParams();
		}
	}

	public function getPreloads()
	{
		$ret = array();
		foreach ($this->preloads as $preload) {
			$ret[] = $this->substitutions->substitute($preload);
		}
		return $ret;
	}

	public function getRequires()
	{
		return $this->requires;
	}

	public function getScreenshot()
	{
		return $this->substitutions->substitute($this->screenshot);
	}

	public function getSubstitutions()
	{
		return $this->substitutions;
	}

	public function getThumbnail()
	{
		return $this->substitutions->substitute($this->thumbnail);
	}

	public function getTitle()
	{
		return $this->substitutions->substitute($this->title);
	}

	public function getTitleUrl()
	{
		$ret = null;
		if (! empty($this->titleUrl)) {
			$ret = $this->substitutions->substitute($this->titleUrl);
		}
		return $ret;
	}

	public function getAuthorAffiliation()
	{
		$this->substitutions->substitute($this->authorAffiliation);
	}

	public function getAuthorLocation()
	{
		$this->substitutions->substitute($this->authorLocation);
	}

	public function getAuthorPhoto()
	{
		$this->substitutions->substitute($this->authorPhoto);
	}

	public function getAuthorAboutme()
	{
		$this->substitutions->substitute($this->authorAboutMe);
	}

	public function getAuthorQuote()
	{
		$this->substitutions->substitute($this->authorQuote);
	}

	public function getAuthorLink()
	{
		$this->substitutions->substitute($this->authorLink);
	}

	public function getShowStats()
	{
		return $this->showStats;
	}

	public function getShowInDirectory()
	{
		return $this->showInDirectory;
	}

	public function getString()
	{
		$this->substitutions->substitute($this->string);
	}

	public function getWidth()
	{
		return $this->width;
	}

	public function getHeight()
	{
		return $this->height;
	}

	public function getCategory()
	{
		return $this->category;
	}

	public function getCategory2()
	{
		return $this->category2;
	}

	public function getSingleton()
	{
		return $this->singleton;
	}

	public function getRenderInline()
	{
		return $this->renderInline;
	}

	public function getScaling()
	{
		return $this->scaling;
	}

	public function getScrolling()
	{
		return $this->scrolling;
	}

	public function getUserPrefs()
	{
		return $this->userPrefs;
	}

	public function getUserPrefValues()
	{
		return $this->userPrefValues;
	}

	public function setMessageBundle($messageBundle)
	{
		$this->messageBundle = $messageBundle;
	}

	public function getViews()
	{
		return $this->views;
	}

	public function getView($viewName)
	{
		return isset($this->views[$viewName]) ? $this->views[$viewName] : $this->views[DEFAULT_VIEW];
	}
}


