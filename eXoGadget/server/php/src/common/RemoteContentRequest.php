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

class RemoteContentRequest {
	// these are used for making the request
	private $url = '';
	private $headers = false;
	private $postBody = false;
	// these fields are filled in once the request has completed
	private $responseContent = false;
	private $responseSize = false;
	private $responseHeaders = false;
	private $httpCode = false;
	private $contentType = null;
	public $handle = false;

	public function __construct($url, $headers = false, $postBody = false)
	{
		$this->url = $url;
		$this->headers = $headers;
		$this->postBody = $postBody;
	}

	// returns a hash code which identifies this request, used for caching
	// takes url and postbody into account for constructing the sha1 checksum
	public function toHash()
	{
		return sha1($this->url . $this->postBody);
	}

	public function getContentType()
	{
		return $this->contentType;
	}

	public function getHttpCode()
	{
		return $this->httpCode;
	}

	public function getResponseContent()
	{
		return $this->responseContent;
	}

	public function getResponseHeaders()
	{
		return $this->responseHeaders;
	}

	public function getResponseSize()
	{
		return $this->responseSize;
	}

	public function getHeaders()
	{
		return $this->headers;
	}

	public function isPost()
	{
		return ($this->postBody != false);
	}

	public function hasHeaders()
	{
		return ! empty($this->headers);
	}

	public function getPostBody()
	{
		return $this->postBody;
	}

	public function getUrl()
	{
		return $this->url;
	}

	public function setContentType($type)
	{
		$this->contentType = $type;
	}

	public function setHttpCode($code)
	{
		$this->httpCode = intval($code);
	}

	public function setResponseContent($content)
	{
		$this->responseContent = $content;
	}

	public function setResponseHeaders($headers)
	{
		$this->responseHeaders = $headers;
	}

	public function setResponseSize($size)
	{
		$this->responseSize = intval($size);
	}

	public function setHeaders($headers)
	{
		$this->headers = $headers;
	}

	public function setPostBody($postBody)
	{
		$this->postBody = $postBody;
	}

	public function setUrl($url)
	{
		$this->url = $url;
	}

}