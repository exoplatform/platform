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

class ProxyServlet extends HttpServlet {

	public function doGet()
	{
		$this->noHeaders = true;
		$context = new GadgetContext('GADGET');
		// those should be doable in one statement, but php seems to still evauluate the second ? and : pair,
		// so throws an error about undefined index on post, even though it found it in get ... odd bug 
		$url = isset($_GET['url']) ? $_GET['url'] : false;
		if (! $url) {
			$url = isset($_POST['url']) ? $_POST['url'] : false;
		}
		$url = urldecode($url);
		$method = isset($_GET['httpMethod']) ? $_GET['httpMethod'] : false;
		if (! $method) {
			$method = isset($_POST['httpMethod']) ? $_POST['httpMethod'] : 'GET';
		}
		if (! $url) {
			header("HTTP/1.0 400 Bad Request", true);
			echo "<html><body><h1>400 - Missing url parameter</h1></body></html>";
		}
		$gadgetSigner = Config::get('gadget_signer');
		$gadgetSigner = new $gadgetSigner();
		$proxyHandler = new ProxyHandler($context);
		if (! empty($_GET['output']) && $_GET['output'] == 'js') {
			$proxyHandler->fetchJson($url, $gadgetSigner, $method);
		} else {
			$proxyHandler->fetch($url, $gadgetSigner, $method);
		}
	}

	public function doPost()
	{
		$this->doGet();
	}
}
