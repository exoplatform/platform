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
 * Basic remote content fetcher, uses curl_multi to fetch multiple resources at the same time
 */

class BasicRemoteContentFetcher extends RemoteContentFetcher {
	private $requests = array();

	public function fetchRequest($request)
	{
		$request->handle = curl_init();
		curl_setopt($request->handle, CURLOPT_URL, $request->getUrl());
		curl_setopt($request->handle, CURLOPT_FOLLOWLOCATION, 1);
		curl_setopt($request->handle, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($request->handle, CURLOPT_AUTOREFERER, 1);
		curl_setopt($request->handle, CURLOPT_MAXREDIRS, 10);
		curl_setopt($request->handle, CURLOPT_CONNECTTIMEOUT, 10);
		curl_setopt($request->handle, CURLOPT_TIMEOUT, 20);
		curl_setopt($request->handle, CURLOPT_HEADER, 1);
		if ($request->hasHeaders()) {
			$headers = explode("\n", $request->getHeaders());
			$outHeaders = array();
			foreach ($headers as $header) {
				if (strpos($header, ':')) {
					$key = trim(substr($header, 0, strpos($header, ':')));
					$val = trim(substr($header, strpos($header, ':') + 1));
					if (strcasecmp($key, "Transfer-Encoding") != 0 && strcasecmp($key, "Cache-Control") != 0 && strcasecmp($key, "Expires") != 0 && strcasecmp($key, "Content-Length") != 0) {
						$outHeaders[] = "$key: $val";
					}
				}
			}
			curl_setopt($request->handle, CURLOPT_HTTPHEADER, $outHeaders);
		}
		if ($request->isPost()) {
			curl_setopt($request->handle, CURLOPT_POST, 1);
			curl_setopt($request->handle, CURLOPT_POSTFIELDS, $request->getPostBody());
		}
		// Execute the request
		$content = @curl_exec($request->handle);
		$header = '';
		$body = '';
		// on redirects and such we get multiple headers back from curl it seems, we really only want the last one
		while (substr($content, 0, strlen('HTTP')) == 'HTTP' && strpos($content, "\r\n\r\n") !== false) {
			$header = substr($content, 0, strpos($content, "\r\n\r\n"));
			$content = $body = substr($content, strlen($header) + 4);
		}
		$httpCode = curl_getinfo($request->handle, CURLINFO_HTTP_CODE);
		$contentType = curl_getinfo($request->handle, CURLINFO_CONTENT_TYPE);
		if (! $httpCode) {
			$httpCode = '404';
		}
		$request->setHttpCode($httpCode);
		$request->setContentType($contentType);
		$request->setResponseHeaders($header);
		$request->setResponseContent($body);
		$request->setResponseSize(strlen($content));
		curl_close($request->handle);
		unset($request->handle);
		return $request;
	}
}
