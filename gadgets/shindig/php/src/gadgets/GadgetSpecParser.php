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

class SpecParserException extends Exception {}

class GadgetSpecParser {

	public function parse($xml, $context)
	{
		if (empty($xml)) {
			throw new SpecParserException("Empty XML document");
		}
		//TODO add libxml_get_errors() functionality so we can have a bit more understandable errors..
		if (($doc = simplexml_load_string($xml, 'SimpleXMLElement', LIBXML_NOCDATA)) == false) {
			throw new SpecParserException("Invalid XML document");
		}
		if (count($doc->ModulePrefs) != 1) {
			throw new SpecParserException("Missing or duplicated <ModulePrefs>");
		}
		$gadget = new Gadget($context->getGadgetId(), $context);
		// process ModulePref attributes
		$this->processModulePrefs($gadget, $doc->ModulePrefs);
		// process UserPrefs, if any
		foreach ($doc->UserPref as $pref) {
			$this->processUserPref($gadget, $pref);
		}
		foreach ($doc->Content as $content) {
			$this->processContent($gadget, $content);
		}
		foreach ($doc->ModulePrefs->Require as $feature) {
			$this->processFeature($gadget, $feature, true);
		}
		foreach ($doc->ModulePrefs->Optional as $feature) {
			$this->processFeature($gadget, $feature, false);
		}
		//TODO Parse icons
		return $gadget;
	}

	private function processModulePrefs(&$gadget, $ModulePrefs)
	{
		$attributes = $ModulePrefs->attributes();
		if (empty($attributes['title'])) {
			throw new SpecParserException("Missing or empty \"title\" attribute.");
		}
		// Get ModulePrefs base and extended attributes
		// See http://code.google.com/apis/gadgets/docs/gadgets-extended-xsd.html 
		// (trim is used here since it not only cleans up the text, but also auto-casts the SimpleXMLElement to a string)
		$gadget->title = trim($attributes['title']);
		$gadget->author = isset($attributes['author']) ? trim($attributes['author']) : '';
		$gadget->authorEmail = isset($attributes['author_email']) ? trim($attributes['author_email']) : '';
		$gadget->description = isset($attributes['description']) ? trim($attributes['description']) : '';
		$gadget->directoryTitle = isset($attributes['directory_title']) ? trim($attributes['directory_title']) : '';
		$gadget->screenshot = isset($attributes['screenshot']) ? trim($attributes['screenshot']) : '';
		$gadget->thumbnail = isset($attributes['thumbnail']) ? trim($attributes['thumbnail']) : '';
		$gadget->titleUrl = isset($attributes['title_url']) ? trim($attributes['title_url']) : '';
		// Extended spec fields
		$gadget->authorAffiliation = isset($attributes['author_affiliation']) ? trim($attributes['author_affiliation']) : '';
		$gadget->authorLocation = isset($attributes['author_location']) ? trim($attributes['author_location']) : '';
		$gadget->authorPhoto = isset($attributes['author_photo']) ? trim($attributes['author_photo']) : '';
		$gadget->authorAboutMe = isset($attributes['author_aboutme']) ? trim($attributes['author_aboutme']) : '';
		$gadget->authorQuote = isset($attributes['author_quote']) ? trim($attributes['author_quote']) : '';
		$gadget->authorLink = isset($attributes['author_link']) ? trim($attributes['author_link']) : '';
		$gadget->showStats = isset($attributes['show_stats']) ? trim($attributes['show_stats']) : '';
		$gadget->showInDirectory = isset($attributes['show_in_directory']) ? trim($attributes['show_in_directory']) : '';
		$gadget->string = isset($attributes['string']) ? trim($attributes['string']) : '';
		$gadget->width = isset($attributes['width']) ? trim($attributes['width']) : '';
		$gadget->height = isset($attributes['height']) ? trim($attributes['height']) : '';
		$gadget->category = isset($attributes['category']) ? trim($attributes['category']) : '';
		$gadget->category2 = isset($attributes['category2']) ? trim($attributes['category2']) : '';
		$gadget->singleton = isset($attributes['singleton']) ? trim($attributes['singleton']) : '';
		$gadget->renderInline = isset($attributes['render_inline']) ? trim($attributes['render_inline']) : '';
		$gadget->scaling = isset($attributes['scaling']) ? trim($attributes['scaling']) : '';
		$gadget->scrolling = isset($attributes['scrolling']) ? trim($attributes['scrolling']) : '';
		foreach ($ModulePrefs->Locale as $locale) {
			$gadget->localeSpecs[] = $this->processLocale($locale);
		}
	}

	private function processLocale($locale)
	{
		$attributes = $locale->attributes();
		$messageAttr = isset($attributes['messages']) ? trim($attributes['messages']) : '';
		$languageAttr = isset($attributes['lang']) ? trim($attributes['lang']) : 'all';
		$countryAttr = isset($attributes['country']) ? trim($attributes['country']) : 'all';
		$rtlAttr = isset($attributes['language_direction']) ? trim($attributes['language_direction']) : '';
		$rightToLeft = $rtlAttr == 'rtl';
		$locale = new LocaleSpec();
		$locale->rightToLeft = $rightToLeft;
		//FIXME java seems to use a baseurl here, probably for the http:// part but i'm not sure yet. Should verify behavior later to see if i got it right
		$locale->url = $messageAttr;
		$locale->locale = new Locale($languageAttr, $countryAttr);
		return $locale;
	}

	private function processUserPref(&$gadget, $pref)
	{
		$attributes = $pref->attributes();
		$preference = new UserPref();
		if (empty($attributes['name'])) {
			throw new SpecParserException("All UserPrefs must have name attributes.");
		}
		$preference->name = trim($attributes['name']);
		$preference->displayName = isset($attributes['display_name']) ? trim($attributes['display_name']) : '';
		// if its set -and- in our valid 'enum' of types, use it, otherwise assume STRING, to try and emulate java's enum behavior
		$preference->dataType = isset($attributes['datatype']) && in_array(strtoupper($attributes['datatype']), $preference->DataTypes) ? strtoupper($attributes['datatype']) : 'STRING';
		$preference->defaultValue = isset($attributes['default_value']) ? trim($attributes['default_value']) : '';
		if (isset($pref->EnumValue)) {
			foreach ($pref->EnumValue as $enum) {
				$attr = $enum->attributes();
				// java based shindig doesn't throw an exception here, but it -is- invalid and should trigger a parse error
				if (empty($attr['value'])) {
					throw new SpecParserException("EnumValue must have a value field.");
				}
				$valueText = trim($attr['value']);
				$displayText = ! empty($attr['display_value']) ? trim($attr['display_value']) : $valueText;
				$preference->enumValues[$valueText] = $displayText;
			}
		}
		$gadget->userPrefs[] = $preference;
	}

	private function processContent(&$gadget, $content)
	{
		$attributes = $content->attributes();
		if (empty($attributes['type'])) {
			throw new SpecParserException("No content type specified!");
		}
		$view = isset($attributes['view']) ? trim($attributes['view']) : '';
		$views = explode(',', $view);
		$html = (string)$content; // no trim here since empty lines can have structural meaning, so typecast to string instead
		foreach ($views as $view) {
			if (empty($view)) {
				$view = DEFAULT_VIEW;
			}
			$viewSpec = new ViewSpec($view, $content);
			if (! isset($gadget->views[$view])) {
				$viewSpec->content = $html;
				$gadget->views[$view] = $viewSpec;
			} else {
				if ($gadget->views[$view]->getName() == $viewSpec->getName() && $viewSpec->getType() != $gadget->views[$view]->getType()) {
					throw new SpecParserException("You may not mix content " . " types in the same view.");
				}
				$gadget->views[$view]->addContent($html);
			}
		}
	}

	private function processFeature(&$gadget, $feature, $required)
	{
		$featureSpec = new FeatureSpec();
		$attributes = $feature->attributes();
		if (empty($attributes['feature'])) {
			throw new SpecParserException("Feature not specified in <" . ($required ? "Required" : "Optional") . "> tag");
		}
		$featureSpec->name = trim($attributes['feature']);
		$featureSpec->optional = ! $required;
		foreach ($feature->Param as $param) {
			$attr = $param->attributes();
			if (empty($attr['name'])) {
				throw new SpecParserException("Missing name attribute in <Param>.");
			}
			$name = trim($attr['name']);
			$value = trim($param);
			$featureSpec->params[$name] = $value;
		}
		$gadget->requires[$featureSpec->name] = $featureSpec;
	}
}
