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

// State file to use
define('DEFAULT_STATE_FILE', Config::get('base_path') . '/../javascript/samplecontainer/state-basicfriendlist.xml');
// Evil javascript strings
define('REDEFINE_NEW_DATA_REQUEST', "opensocial.newDataRequest = function() { alert('Ha! I attacked you!')}; ");
define('MAKE_PAGE_RED', "document.body.style.backgroundColor = 'red'; ");
define('SCRIPT_PREFIX', "<div onMouseOver=\"" . REDEFINE_NEW_DATA_REQUEST . MAKE_PAGE_RED . "\">");
define('SCRIPT_SUFFIX', "</div>");

class XmlStateFileFetcher {
	private $stateFile;
	private $doEvil = false;
	private $document = null;
	private $allData = null;
	private $friendIdMap = null;
	private $allActivities = null;
	
	// Singleton
	private static $fetcher;
	private function __construct()
	{
		$this->stateFile = DEFAULT_STATE_FILE;
	}
	
	private function __clone()
	{
		// private, don't allow cloning of a singleton
	}
	
	static function get()
	{
		// This object is a singleton
		if (! isset(XmlStateFileFetcher::$fetcher)) {
			XmlStateFileFetcher::$fetcher = new XmlStateFileFetcher();
		}
		return XmlStateFileFetcher::$fetcher;
	}
	
	public function resetStateFile($stateFile)
	{
		$this->stateFile = $stateFile;
		$this->document = null;
		$this->allData = null;
		$this->friendIdMap = null;
		$this->allPeople = null;
		$this->allActivities = null;
	}
	
	public function setEvilness($doEvil)
	{
		$this->doEvil = $doEvil;
	}
	
	private function fetchStateDocument()
	{
		if ($this->document != null) {
			return $this->document;
		}
		// relying on fopen wrappers (which can also handle remote files) and friends
		// since we also want to be able to fwrite new state's in the php version
		// the java version keeps the changes in memory, but since php is multi process
		// instead of single threaded, thats not an option here
		if (($xml = @simplexml_load_file($this->stateFile)) === false) {
			throw new Exception("The state file  {$this->stateFile} could not be fetched and parsed.");
		}
		$this->document = $xml;
		return $this->document;
	}
	
	private function turnEvil($originalString)
	{
		return $this->doEvil ? SCRIPT_PREFIX . $originalString . SCRIPT_SUFFIX : $originalString;
	}
	
	public function getAppData()
	{
		if ($this->allData == null) {
			$this->setupAppData();
		}
		return $this->allData;
	}
	
	private function setupAppData()
	{
		$this->allData = array();
		$xml = $this->fetchStateDocument();
		foreach ( $xml->personAppData->data as $data ) {
			$person = (string)$data['person'];
			$key = (string)$data['field'];
			$value = (string)$data;
			if (!isset($this->allData[$person])) {
				$this->allData[$person] = array();
			}
			$this->allData[$person][$key] = $value;
		}
	}
	
	public function setAppData($id, $key, $value)
	{
		if ($this->allData == null) {
			$this->setupAppData();
		}
		if (! isset($this->allData[$id])) {
			$this->allData[$id] = array();
		}
		$this->allData[$id][$key] = $value;
		
		// Ok to have a fully functioning compliance test in the sample container we need
		// to be able to save data too ... since we don't have shared memory between
		// processes as the java side does, we'll do it the hard way and store the changes
		// in xml.. Oh ps. SimpleXML->addChild() requires PHP >= 5.1.3

		// loop thru the app data to see if this person/key value already exists
		$existingNode = false;
		foreach ( $this->document->personAppData->data as $data ) {
			if ((string)$data['person'] == $id && (string)$data['field'] == $key) {
				$existingNode = $data;
			}
		}
		// if it exists, replace it with the new node, otherwise append the new node
		if ($existingNode !== false) {
			// SimpleXML doesnt have a replace.. so we fall back on dom functions
			$data = new SimpleXMLElement("<data person=\"" . htmlentities($id) . "\" field=\"" . htmlentities($key) . "\">" . htmlentities($value) . "</data>");
			$node1 = dom_import_simplexml($existingNode);
			$dom_sxe = dom_import_simplexml($data);
			$node2 = $node1->ownerDocument->importNode($dom_sxe, true);
			$node1->parentNode->replaceChild($node2, $node1);
		} else {
			// Add a new node with the correct person/field/value to the personData node
			$data = $this->document->personAppData->addChild('data', $value);
			$data->addAttribute('person', $id);
			$data->addAttribute('field', $key);
		}
		if (! @file_put_contents(DEFAULT_STATE_FILE, $this->document->asXML())) {
			throw new Exception("Could not write appData to state file, check the file permissions");
		}
	}
	
	public function getFriendIds()
	{
		if ($this->friendIdMap == null) {
			$this->setupPeopleData();
		}
		return $this->friendIdMap;
	}
	
	public function getAllPeople()
	{
		if ($this->allPeople == null) {
			$this->setupPeopleData();
		}
		return $this->allPeople;
	}
	
	private function setupPeopleData()
	{
		$xml = $this->fetchStateDocument();
		$this->allPeople = array();
		$this->friendIdMap = array();
		
		foreach ( $xml->people->person as $personNode ) {
			$name = (string)$personNode['name'];
			$id = (string)$personNode['id'];
			$person = new Person($id, new Name($this->turnEvil($name)));
			$phoneItem = (string)$personNode['phone'];
			if (! empty($phoneItem)) {
				$phones = array();
				$phones[] = new Phone($this->turnEvil($phoneItem), null);
				$person->setPhoneNumbers($phones);
			}
			$genderItem = (string)$personNode['gender'];
			if (! empty($genderItem)) {
				if ($genderItem == 'F') {
					$person->setGender('FEMALE');
				} else {
					$person->setGender('MALE');
				}
			}
			$this->allPeople[$id] = $person;
			$this->friendIdMap[$id] = $this->getFriends($personNode);
		}
	}
	
	private function getFriends($personNode)
	{
		$friends = array();
		foreach ( $personNode as $friend ) {
			$friend = trim((string)$friend);
			if (! empty($friend)) {
				$friends[] = $friend;
			}
		}
		return $friends;
	}
	
	public function getActivities()
	{
		if ($this->allActivities == null) {
			$this->setupActivities();
		}
		return $this->allActivities;
	}
	
	private function setupActivities()
	{
		$this->allActivities = array();
		$xml = $this->fetchStateDocument();
		foreach ( $xml->activities->stream as $streamItem ) {
			$streamTitle = isset($streamItem['title']) ? (string)$streamItem['title'] : '';
			$userId = isset($streamItem['userId']) ? (string)$streamItem['userId'] : '';
			$this->createActivities($streamItem, $userId, $streamTitle);
		}
	}
	
	private function createActivities($streamItem, $userId, $streamTitle)
	{
		foreach ( $streamItem->activity as $activityItem ) {
			$title = isset($activityItem['title']) ? (string)$activityItem['title'] : '';
			$id = isset($activityItem['id']) ? (string)$activityItem['id'] : 0;
			$body = isset($activityItem['body']) ? (string)$activityItem['body'] : '';
			$activity = new Activity($id, $userId);
			$activity->setStreamTitle($this->turnEvil($streamTitle));
			$activity->setTitle($this->turnEvil($title));
			$activity->setBody($this->turnEvil($body));
			$activity->setMediaItems($this->getMediaItems($activityItem));
			$this->createActivity($userId, $activity);
		}
	}
	
	private function getMediaItems($activityItem)
	{
		$media = array();
		foreach ( $activityItem->mediaItem as $mediaItem ) {
			$type = isset($mediaItem['type']) ? (string)$mediaItem['type'] : '';
			$mimeType = isset($mediaItem['mimeType']) ? $mediaItem['mimeType'] : '';
			$url = isset($mediaItem['url']) ? $mediaItem['url'] : '';
			$media[] = new MediaItem($mimeType, $type, $url);
		}
		return $media;
	}
	
	public function createActivity($userId, $activity)
	{
		
		if ($this->allActivities == null && !is_array($this->allActivities)) {
			$this->setupActivities();
		}
		if (! isset($this->allActivities[$userId])) {
			$this->allActivities[$userId] = array();
		}
		$this->allActivities[$userId][] = $activity;
	}
}

