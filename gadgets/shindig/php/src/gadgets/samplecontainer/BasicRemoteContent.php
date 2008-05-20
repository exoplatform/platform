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

class BasicRemoteContent extends RemoteContent {

	public function fetch($request, $context)
	{
		$cache = Config::get('data_cache');
		$cache = new $cache();
		$remoteContentFetcher = new BasicRemoteContentFetcher();
		if (! ($request instanceof RemoteContentRequest)) {
			throw new RemoteContentException("Invalid request type in remoteContent");
		}
		// determine which requests we can load from cache, and which we have to actually fetch
		if (! $context->getIgnoreCache() && ($cachedRequest = $cache->get($request->toHash())) !== false) {
			$ret = $cachedRequest;
		} else {
			$ret = $remoteContentFetcher->fetchRequest($request);
			// only cache requests that returned a 200 OK
			if ($request->getHttpCode() == '200') {
				$cache->set($request->toHash(), $request);
			}
		}
		return $ret;
	}
}