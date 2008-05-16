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

class GadgetFeatureRegistry {
	private $features = array();
	private $core = array();
	private $coreDone = false;
	private $debug;

	public function __construct($featurePath, $debug = false)
	{
		$this->debug = $debug;
		$this->registerFeatures($featurePath);
	}

	public function registerFeatures($featurePath)
	{
		if (empty($featurePath) || $featurePath == null) {
			return;
		}
		$coreDeps = array();
		$loader = new JsFeatureLoader($this->debug);
		$jsFeatures = $loader->loadFeatures($featurePath, $this);
		if (! $this->coreDone) {
			foreach ($jsFeatures as $entry) {
				if (strtolower(substr($entry->name, 0, strlen('core'))) == 'core') {
					$coreDeps[] = $entry->name;
					$this->core[$entry->name] = $entry->name;
				}
			}
			// Make sure non-core features depend on core.
			foreach ($jsFeatures as $entry) {
				if (strtolower(substr($entry->name, 0, strlen('core'))) != 'core') {
					$entry->deps = array_merge($entry->deps, $this->core);
				}
			}
			$this->coreDone = true;
		}
	}

	public function register($name, $deps, $feature)
	{
		// Core entries must come first.
		$entry = isset($this->features[$name]) ? $this->features[$name] : null;
		if ($entry == null) {
			$entry = new GadgetFeatureRegistryEntry($name, $deps, $feature, $this);
			if ($this->coreDone) {
				$entry->deps = array_merge($entry->deps, $this->core);
			}
			$this->features[$name] = $entry;
			$this->validateFeatureGraph();
		}
		return $entry;
	}

	private function validateFeatureGraph()
	{
		// TODO: ensure that features form a DAG and that all deps are provided
	}

	public function getAllFeatures()
	{
		return $this->features;
	}

	public function getIncludedFeatures($needed, &$resultsFound, &$resultsMissing)
	{
		$resultsFound = array();
		$resultsMissing = array();
		if (! count($needed)) {
			// Shortcut for gadgets that don't have any explicit dependencies.
			$resultsFound = $this->core;
			return true;
		}
		foreach ($needed as $featureName) {
			$entry = isset($this->features[$featureName]) ? $this->features[$featureName] : null;
			if ($entry == null) {
				$resultsMissing[] = $featureName;
			} else {
				$this->addEntryToSet($resultsFound, $entry);
			}
		}
		return count($resultsMissing) == 0;
	}

	private function addEntryToSet(&$results, $entry)
	{
		foreach ($entry->deps as $dep) {
			$this->addEntryToSet($results, $this->features[$dep]);
		}
		$results[$entry->name] = $entry->name;
	}

	public function getEntry($name)
	{
		return $this->features[$name];
	}
}

// poor man's namespacing
class GadgetFeatureRegistryEntry {
	public $name;
	public $deps = array();
	public $feature;

	public function __construct($name, $deps, $feature, $registry)
	{
		$this->name = $name;
		if ($deps != null) {
			foreach ($deps as $dep) {
				$entry = $registry->getEntry($dep);
				$this->deps[$entry->name] = $entry->name;
			}
		}
		$this->feature = $feature;
	}

	public function getName()
	{
		return $this->name;
	}

	public function getDependencies()
	{
		return $this->deps;
	}

	public function getFeature()
	{
		return $this->feature;
	}
}

