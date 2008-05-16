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
 * This class impliments memcached based caching It'll generally be more
 * usefull in a multi-server envirionment then the file based caching,
 * (in a single server setup file based caching is actually faster)
 */
class CacheMemcache extends Cache {
	private $connection = false;

	public function __construct()
	{
		if (! function_exists('memcache_connect')) {
			throw new CacheException("Memcache functions not available");
		}
		if (Config::get('cache_host') == '' || Config::get('cache_port') == '') {
			throw new CacheException("You need to configure a cache server host and port to use the memcache backend");
		}
		$this->host = Config::get('cache_host');
		$this->port = Config::get('cache_port');
	}

	public function __destruct()
	{
		// if we were connected, close the connection again
		if (is_resource($this->connection)) {
			memcache_close($this->connection);
		}
	}

	// I prefer lazy initalization since the cache isn't used every request
	// so this potentially saves a lot of overhead
	private function connect()
	{
		if (! $this->connection = memcache_connect($this->host, $this->port)) {
			throw new CacheException("Couldn't connect to memcache server");
		}
	}

	private function check()
	{
		if (! $this->connection) {
			$this->connect();
		}
	}

	// using memcache_add behavior for cache stampeding prevention
	private function add($key, $var, $timeout)
	{
		$this->check();
		if (! memcache_add($this->connection, $key, $var, 0, $timeout)) {
			throw new CacheException("Couldn't add to cache");
		}
	}

	public function get($key)
	{
		$this->check();
		if (($ret = memcache_get($this->connection, $key)) === false) {
			return false;
		}
		return $ret;
	}

	public function set($key, $value)
	{
		$this->check();
		if (memcache_set($this->connection, $key, $value, 0, Config::Get('cache_time')) === false) {
			throw new CacheException("Couldn't store data in cache");
		}
	}

	function delete($key)
	{
	
	}
}
