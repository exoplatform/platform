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

class UrlGenerator {

	static function getIframeURL($gadget, $context)
	{
		$inlineJs = '';
		foreach ($gadget->getJsLibraries() as $library) {
			$type = $library->getType();
			if ($type != 'URL') {
				$inlineJs .= $library->getContent() . "\n";
			}
		}
		$v = md5($inlineJs);
		
		$view = HttpUtil::getView($gadget, $context);
		
		$up = '';
		$prefs = $context->getUserPrefs();
		foreach ($gadget->getUserPrefs() as $pref) {
			$name = $pref->getName();
			$value = $prefs->getPref($name);
			if ($value == null) {
				$value = $pref->getDefaultValue();
			}
			$up .= '&up_' . urlencode($name) . '=' . urlencode($value);
		}
		
		// note: put the URL last, else some browsers seem to get confused (reported by hi5)
		return Config::get('default_iframe_prefix') . 'container=' . $context->getContainer() . ($context->getIgnoreCache() ? 'nocache=1' : '&v=' . $v) . ($context->getModuleId() != 0 ? '&mid=' . $context->getModuleId() : '') . '&lang=' . $context->getLocale()->getLanguage() . '&country=' . $context->getLocale()->getCountry() . '&view=' . $view->getName() . $up . '&url=' . urlencode($context->getUrl());
	}
}
