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

class JsonRpcServlet extends HttpServlet {

	public function doPost()
	{
		try {
			// we support both a raw http post (without application/x-www-form-urlencoded headers) like java does
			// and a more php / curl safe version of a form post with 'request' as the post field that holds the request json data
			if (isset($GLOBALS['HTTP_RAW_POST_DATA']) || isset($_POST['request'])) {
				$request = json_decode(isset($GLOBALS['HTTP_RAW_POST_DATA']) ? $GLOBALS['HTTP_RAW_POST_DATA'] : $_POST['request']);
				if ($request == (isset($GLOBALS['HTTP_RAW_POST_DATA']) ? $GLOBALS['HTTP_RAW_POST_DATA'] : $_POST['request'])) {
					throw new Exception("Malformed json string");
				}
				$handler = new JsonRpcHandler();
				$response = $handler->process($request);
				echo json_encode(array('gadgets' => $response));
			} else {
				throw new Exception("No post data set");
			}
		} catch (Exception $e) {
			header("HTTP/1.0 500 Internal Server Error", true, 500);
			echo "<html><body><h1>Internal Server Error</h1><br />";
			if (Config::get('debug')) {
				echo $e->getMessage() . "<br /><pre>";
				print_r(debug_backtrace());
				echo "</pre>";
			}
			echo "</body></html>";
		}
	}

	public function doGet()
	{
		header("HTTP/1.0 400 Bad Request", true, 400);
		echo "<html><body>";
		echo "<h1>Error</h1>";
		echo "<body></html>";
	}
}