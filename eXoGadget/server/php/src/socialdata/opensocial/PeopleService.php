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

abstract class PeopleService {
	public $sortOrder = array('topFriends', 'name');
	public $filterType = array('all', 'hasApp');

	/**
	 * Returns a list of people ids that the other handlers (currently data
	 * and activities) can use to fetch their own objects
	 *
	 * @param idSpec The idSpec to translate into ids
	 * @return a list of person ids
	 * @throws JSONException If the idSpec is malformed
	 */
	abstract public function getIds($idSpec, $token);

	/**
	 * Returns a list of people that correspond to the passed in person ids.
	 * @param ids The ids of the people to fetch.
	 * @param sortOrder How to sort the people
	 * @param filter How the people should be filtered.
	 * @param first The index of the first person to fetch.
	 * @param max The max number of people to fetch.
	 * @return a list of people.
	 */
	abstract public function getPeople($ids, $sortOrder, $filter, $first, $max, $profileDetails, $token);
}
