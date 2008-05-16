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

class BasicActivitiesService {

	public function getActivities($ids, $token)
	{
		$allActivities = XmlStateFileFetcher::get()->getActivities();
		$activities = array();
		foreach ($ids as $id) {
			if (isset($allActivities[$id])) {
				$activities[] = $allActivities[$id];
			}
		}
		// TODO: Sort them
		return new ResponseItem(null, null, $activities);
	}

	public function createActivity($personId, $activity, $token)
	{
		// TODO: Validate the activity and do any template expanding
		$activity->setUserId($personId);
		$activity->setPostedTime(time());
		XmlStateFileFetcher::get()->createActivity($personId, $activity);
		return new ResponseItem(null, null, array());
	}
}
