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

class BasicPeopleService extends PeopleService {

	private function comparator($person, $person1)
	{
		$name = $person['name']->getUnstructured();
		$name1 = $person1['name']->getUnstructured();
		if ($name == $name1) {
			return 0;
		}
		return ($name < $name1) ? - 1 : 1;
	}

	public function getPeople($ids, $sortOrder, $filter, $first, $max, $profileDetails, $token)
	{
		$allPeople = XmlStateFileFetcher::get()->getAllPeople();
		$people = array();
		foreach ($ids as $id) {
			$person = null;
			if (isset($allPeople[$id])) {
				$person = $allPeople[$id];
				if ($id == $token->getViewerId()) {
					$person->setIsViewer(true);
				}
				if ($id == $token->getOwnerId()) {
					$person->setIsOwner(true);
				}
				//FIXME (see note below)
				// The java sample container code returns everything that is listed in the XML file
				// and filters out all the null values. I -think- the more correct thing to do is 
				// return a json object with only the requested profile details ... 
				// but double check later to make sure :)
				if (is_array($profileDetails) && count($profileDetails)) {
					$newPerson = array();
					$newPerson['isOwner'] = $person->isOwner;
					$newPerson['isViewer'] = $person->isViewer;
					$newPerson['name'] = $person->name;
					foreach ($profileDetails as $field) {
						if (isset($person->$field) && ! isset($newPerson[$field])) {
							$newPerson[$field] = $person->$field;
						}
						$person = $newPerson;
					}
					// return only the requested profile detail fields
				}
				$people[] = $person;
			}
		}
		// We can pretend that by default the people are in top friends order
		if ($sortOrder == 'name') {
			usort($people, array($this, 'comparator'));
		}
		
		//TODO: The samplecontainer doesn't support any filters yet. We should fix this.
		$totalSize = count($people);
		$last = $first + $max;
		$last = min($last, $totalSize);
		$people = array_slice($people, $first, $last);
		$collection = new ApiCollection($people, $first, $totalSize);
		return new ResponseItem(null, null, $collection);
	}

	public function getIds($idSpec, $token)
	{
		$friendIds = XmlStateFileFetcher::get()->getFriendIds();
		$ids = array();
		switch ($idSpec->getType()) {
			case 'OWNER':
				$ids[] = $token->getOwnerId();
				break;
			case 'VIEWER':
				$ids[] = $token->getViewerId();
				break;
			case 'OWNER_FRIENDS':
				$ids = $friendIds[$token->getOwnerId()];
				break;
			case 'VIEWER_FRIENDS':
				$ids = $friendIds[$token->getViewerId()];
				break;
			case 'USER_IDS':
				$ids = $idSpec->fetchUserIds();
				break;
		}
		return $ids;
	}
}
