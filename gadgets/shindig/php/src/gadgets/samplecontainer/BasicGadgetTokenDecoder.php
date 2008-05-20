<?php
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

class BasicGadgetTokenDecoder extends GadgetTokenDecoder {
	private $OWNER_INDEX = 0;
	private $VIEWER_INDEX = 1;
	private $APP_ID_INDEX = 2;
	private $CONTAINER_INDEX = 3;
	private $APP_URL_INDEX = 4;
	private $MODULE_ID_INDEX = 5;

	/**
	 * {@inheritDoc}
	 *
	 * Returns a token with some faked out values.
	 */
	public function createToken($stringToken)
	{
		if (empty($stringToken)) {
			throw new GadgetException('INVALID_GADGET_TOKEN');
		}
		try {
			//TODO remove this once we have a better way to generate a fake token
			// in the example files
			if (Config::get('allow_plaintext_token') && count(explode(':', $stringToken)) == 6) {
				$tokens = explode(":", $stringToken);
				return new BasicGadgetToken(null, null, urldecode($tokens[$this->OWNER_INDEX]), urldecode($tokens[$this->VIEWER_INDEX]), urldecode($tokens[$this->APP_ID_INDEX]), urldecode($tokens[$this->CONTAINER_INDEX]), urldecode($tokens[$this->APP_URL_INDEX]), urldecode($tokens[$this->MODULE_ID_INDEX]));
			} else {
				return BasicGadgetToken::createFromToken($stringToken, Config::get('st_max_age'));
			}
		} catch (Exception $e) {
			throw new GadgetException('INVALID_GADGET_TOKEN');
		}
	}
}
