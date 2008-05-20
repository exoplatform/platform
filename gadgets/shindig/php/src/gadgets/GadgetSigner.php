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
 *  Handles generation of signing tokens for various request types.
 *  Implementations are free to define their own signing parameters in any
 *  way that is suitable for their site.
 */
abstract class GadgetSigner {
	/**
	 * Generates a token for the given gadget.
	 * Implementations should also add their own user-related context data
	 * to the token.
	 * 
	 * Or generates a token from an input string. This call must produce a token that
	 * will validate against a token produced directly from a gadget so that the
	 * following function will always returns a valid GadgetToken:
	 *
	 * <code>
	 * GadgetToken testToken(Gadget gadget, GadgetSigner signer) {
	 *   GadgetToken token = signer.createToken(gadget);
	 *   return signer.createToken(token.toSerialForm());
	 * }
	 * </code>
	 *
	 * @param tokenString String representation of the token to be created.
	 * @return The token representation of the input data.
	 * @throws GadgetException If tokenString is not a valid token
	 */
	abstract public function createToken($gadget);
}
