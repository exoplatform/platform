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
 * This class impliments a basic on disk caching. That will work fine on a single host
 * but on a multi server setup this could lead to some problems due to inconsistencies
 * between the various cached versions on the different servers. Other methods like
 * memcached should be used instead really.
 * 
 * When using this file based backend, its adviced to make a cron job that scans thru the
 * cache dir, and removes all files that are older then 24 hours (or whatever your
 * config's CACHE_TIME is set too).
 */

//TODO add cache stampeding prevention using file locking mechanisms


class CacheFile extends Cache {

	function get($key)
	{
		$cacheFile = $this->getCacheFile($key);
		if (file_exists($cacheFile) && is_readable($cacheFile)) {
			$now = time();
			if (($mtime = filemtime($cacheFile)) !== false && ($now - $mtime) < Config::get('cache_time')) {
				if (($data = @file_get_contents($cacheFile)) !== false) {
					$data = unserialize($data);
					return $data;
				}
			}
		}
		return false;
	}

	function set($key, $value)
	{
		// use the first 2 characters of the hash as a directory prefix
		// this should prevent slowdowns due to huge directory listings
		// and thus give some basic amount of scalability
		$cacheDir = $this->getCacheDir($key);
		$cacheFile = $this->getCacheFile($key);
		if (! is_dir($cacheDir)) {
			if (! @mkdir($cacheDir, 0755, true)) {
				throw new CacheException("Could not create cache directory");
			}
		}
		// we serialize the whole request object, since we don't only want the
		// responseContent but also the postBody used, headers, size, etc
		$data = serialize($value);
		if (! file_put_contents($cacheFile, $data)) {
			throw new CacheException("Could not store data in cache file");
		}
	}

	function delete($key)
	{
		$file = $this->getCacheFile($key);
		if (! @unlink($file)) {
			throw new CacheException("Cache file could not be deleted");
		}
	}

	private function getCacheDir($hash)
	{
		return Config::get('cache_root') . '/' . substr($hash, 0, 2);
	}

	private function getCacheFile($hash)
	{
		return $this->getCacheDir($hash) . '/' . $hash;
	}
}