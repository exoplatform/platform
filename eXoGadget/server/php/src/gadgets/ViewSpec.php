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
 * Represents a Content section, but normalized into an individual
 * view value after views are split on commas.
 */
class ViewSpec {
	
	public $name;
	public $type;
	public $href;
	public $quirks;
	public $content;
	public $view;
	
	public function __construct($name, $gadgetContent)
	{
		$attributes = $gadgetContent->attributes();
		$this->name = $name;
		$this->view = isset($attributes['view']) ? trim($attributes['view']) : '';
		$this->quirks = trim($attributes['quirks']);
		if (empty($this->quirks)) {
			$this->quirks = true;
		} else {
			$this->quirks = false;
		}
		if (strtolower(trim($attributes['type'])) == 'url') {
			if (empty($attributes['href'])) {
				throw new SpecParserException("Malformed <Content> href value");
			}
			$this->type = 'URL';
			$this->href = trim($attributes['href']);
		} else {
			$this->type = 'HTML';
		}
	}
	
	public function getName()
	{
		return $this->name;
	}
	
	public function getType()
	{
		return $this->type;
	}
	
	public function getHref()
	{
		return $this->href;
	}
	
	public function getQuirks()
	{
		return $this->quirks;
	}
	
	public function getContent()
	{
		return $this->content;
	}
	
	public function getView()
	{
		return $this->view;
	}
	
	public function addContent($data)
	{
		$this->content .= $data;
	}
}
