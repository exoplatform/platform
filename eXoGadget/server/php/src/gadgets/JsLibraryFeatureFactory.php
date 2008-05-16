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

class JsLibraryFeatureFactory extends GadgetFeatureFactory {
	private $JsLibraryFeature;

	public function __construct($gadgetLibraries, $containerLibraries)
	{
		// since we don't do strict type checking, this is one constructor for both a class, or a array of classes
		$this->JsLibraryFeature = new JsLibraryFeature($gadgetLibraries, $containerLibraries);
	}

	public function create()
	{
		return $this->JsLibraryFeature;
	}

	public function getLibraries($context)
	{
		return $context == 'GADGET' ? $this->JsLibraryFeature->gadgetLibraries : $this->JsLibraryFeature->containerLibraries;
	}
}

class JsLibraryFeature extends GadgetFeature {
	public $containerLibraries = array();
	public $gadgetLibraries = array();

	public function __construct($gadgetLibraries, $containerLibraries)
	{
		// we have a single constructor for both a single and multiple libraries, so handle it in code instead
		if ($gadgetLibraries != null && is_array($gadgetLibraries)) {
			$this->gadgetLibraries = array_merge($this->gadgetLibraries, $gadgetLibraries);
		} elseif ($gadgetLibraries != null && $gadgetLibraries instanceof JsLibrary) {
			$this->gadgetLibraries[] = $gadgetLibraries;
		}
		if ($containerLibraries != null && is_array($containerLibraries)) {
			$this->containerLibraries = array_merge($this->containerLibraries, $containerLibraries);
		} elseif ($containerLibraries != null && $containerLibraries instanceof JsLibrary) {
			$this->containerLibraries[] = $containerLibraries;
		}
	}

	public function prepare($gadget, $context, $params)
	{
		// do nothing
	}

	public function process($gadget, $context, $params)
	{
		$libraries = array();
		$libraries = $context->getRenderingContext() == 'GADGET' ? $this->gadgetLibraries : $this->containerLibraries;
		foreach ($libraries as $library) {
			$gadget->addJsLibrary($library);
		}
	}
}