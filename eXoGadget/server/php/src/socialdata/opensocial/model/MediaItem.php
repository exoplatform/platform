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
 */

/**
 * see
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Activity.MediaItem.Field.html
 *
 */
class MediaItem {
	public $mimeType;
	public $type;
	public $url;
	
	public $types = array('AUDIO', 'VIDEO', 'IMAGE');
	
	public function __construct($mimeType, $type, $url)
	{
		$this->setMimeType($mimeType);
		$this->setType($type);
		$this->setUrl($url);
	}
	
	public function getMimeType()
	{
		return $this->mimeType;
	}
	
	public function setMimeType($mimeType)
	{
		$this->mimeType = $mimeType;
	}
	
	public function getType()
	{
		return $this->type;
	}
	
	public function setType($type)
	{
		if (! in_array($type, $this->types)) {
			throw new Exception("Invalid Media type");
		}
		$this->type = $type;
	}
	
	public function getUrl()
	{
		return $this->url;
	}
	
	public function setUrl($url)
	{
		$this->url = $url;
	}
}
