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

class JsFeatureLoader {
	private $debug;

	public function __construct($debug)
	{
		$this->debug = $debug;
	}

	public function loadFeatures($path, $registry)
	{
		$registered = array();
		$entries = array();
		$deps = array();
		$deps = $this->loadFiles($path, $deps);
		// This ensures that we register everything in the right order.
		foreach ($deps as $entry) {
			$feature = $entry;
			$feat = $this->register($registry, $feature, $registered, $deps);
			if ($feat != null) {
				$entries[] = $feat;
			}
		}
		return $entries;
	}

	private function loadFiles($path, &$features)
	{
		if (is_dir($path)) {
			foreach (glob("$path/*") as $file) {
				// prevents us from looping over '.', '..' and 'hidden files', this last bit IS 
				// different from the java version but it's the unix standard really..
				if (substr(basename($file), 0, 1) != '.') {
					$features = $this->loadFiles($file, $features);
				}
			}
		} else {
			if (basename($path) == 'feature.xml') {
				$feature = $this->processFile($path);
				if ($feature != null) {
					$features[$feature->name] = $feature;
				}
			}
		}
		return $features;
	}

	private function processFile($file)
	{
		$feature = null;
		if (file_exists($file) && is_file($file) && is_readable($file)) {
			if (($content = @file_get_contents($file))) {
				$feature = $this->parse($content, dirname($file) . '/');
			}
		}
		return $feature;
	}

	private function register(&$registry, $feature, $registered, $all)
	{
		if (isset($registered[$feature->name])) {
			return null;
		}
		foreach ($feature->deps as $dep) {
			if (isset($all[$dep]) && ! in_array($dep, $registered)) {
				$this->register($registry, $all[$dep], $registered, $all);
			}
		}
		$factory = new JsLibraryFeatureFactory($feature->gadgetJs, $feature->containerJs);
		$registered[] = $feature->name;
		return $registry->register($feature->name, $feature->deps, $factory);
	}

	private function parse($content, $path)
	{
		$doc = simplexml_load_string($content);
		$feature = new ParsedFeature();
		$feature->basePath = $path;
		if (! isset($doc->name)) {
			throw new GadgetException('Invalid name in feature: ' . $path);
		}
		$feature->name = trim($doc->name);
		
		foreach ($doc->gadget as $gadget) {
			$feature = $this->processContext($feature, $gadget, false);
		}
		foreach ($doc->container as $container) {
			$feature = $this->processContext($feature, $container, true);
		}
		foreach ($doc->dependency as $dependency) {
			$feature->deps[] = trim($dependency);
		}
		return $feature;
	}

	private function processContext(&$feature, $context, $isContainer)
	{
		foreach ($context->script as $script) {
			$attributes = $script->attributes();
			if (! isset($attributes['src'])) {
				// inline content
				$type = 'INLINE';
				$content = (string)$script;
			} else {
				$content = trim($attributes['src']);
				if (strtolower(substr($content, 0, strlen("http://"))) == "http://") {
					$type = 'URL';
				} elseif (strtolower(substr($content, 0, strlen("//"))) == "//") {
					$type = 'URL';
				} else {
					// as before, we skip over the resource parts since we dont support them
					$type = 'FILE';
					$content = $feature->basePath . '/' . $content;
				}
			}
			$library = JsLibrary::create($type, $content, $this->debug);
			if ($library != null) {
				if ($isContainer) {
					$feature->containerJs[] = $library;
				} else {
					$feature->gadgetJs[] = $library;
				}
			}
		}
		return $feature;
	}
}

class ParsedFeature {
	public $name = "";
	public $basePath = "";
	public $containerJs = array();
	public $gadgetJs = array();
	public $deps = array();
}
