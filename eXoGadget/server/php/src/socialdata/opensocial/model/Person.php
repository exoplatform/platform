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

/**
 * see
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Person.Field.html
 *
 */
class Person {
	public $aboutMe;
	public $activities;
	public $addresses;
	public $age;
	public $bodyType;
	public $books;
	public $cars;
	public $children;
	public $currentLocation;
	public $dateOfBirth;
	public $drinker;
	public $emails;
	public $ethnicity;
	public $fashion;
	public $food;
	public $gender;
	public $happiestWhen;
	public $heroes;
	public $humor;
	public $id;
	public $interests;
	public $jobInterests;
	public $jobs;
	public $languagesSpoken;
	public $livingArrangement;
	public $lookingFor;
	public $movies;
	public $music;
	public $name;
	public $nickname;
	public $pets;
	public $phoneNumbers;
	public $politicalViews;
	public $profileSong;
	public $profileUrl;
	public $profileVideo;
	public $quotes;
	public $relationshipStatus;
	public $religion;
	public $romance;
	public $scaredOf;
	public $schools;
	public $sexualOrientation;
	public $smoker;
	public $sports;
	public $status;
	public $tags;
	public $thumbnailUrl;
	public $timeZone;
	public $turnOffs;
	public $turnOns;
	public $tvShows;
	public $urls;
	
	// Note: Not in the opensocial js person object directly
	public $isOwner = false;
	public $isViewer = false;
	
	public function __construct($id, $name)
	{
		$this->id = $id;
		$this->name = $name;
	}
	
	public function getAboutMe()
	{
		return $this->aboutMe;
	}
	
	public function setAboutMe($aboutMe)
	{
		$this->aboutMe = $aboutMe;
	}
	
	public function getActivities()
	{
		return $this->activities;
	}
	
	public function setActivities($activities)
	{
		$this->activities = $activities;
	}
	
	public function getAddresses()
	{
		return $this->addresses;
	}
	
	public function setAddresses($addresses)
	{
		$this->addresses = $addresses;
	}
	
	public function getAge()
	{
		return $this->age;
	}
	
	public function setAge($age)
	{
		$this->age = $age;
	}
	
	public function getBodyType()
	{
		return $this->bodyType;
	}
	
	public function setBodyType($bodyType)
	{
		$this->bodyType = $bodyType;
	}
	
	public function getBooks()
	{
		return $this->books;
	}
	
	public function setBooks($books)
	{
		$this->books = $books;
	}
	
	public function getCars()
	{
		return $this->cars;
	}
	
	public function setCars($cars)
	{
		$this->cars = $cars;
	}
	
	public function getChildren()
	{
		return $this->children;
	}
	
	public function setChildren($children)
	{
		$this->children = $children;
	}
	
	public function getCurrentLocation()
	{
		return $this->currentLocation;
	}
	
	public function setCurrentLocation($currentLocation)
	{
		$this->currentLocation = $currentLocation;
	}
	
	public function getDateOfBirth()
	{
		return $this->dateOfBirth;
	}
	
	public function setDateOfBirth($dateOfBirth)
	{
		$this->dateOfBirth = $dateOfBirth;
	}
	
	public function getDrinker()
	{
		return $this->$this->drinker;
	}
	
	public function setDrinker($newDrinker)
	{
		$this->drinker = $newDrinker;
	}
	
	public function getEmails()
	{
		return $this->emails;
	}
	
	public function setEmails($emails)
	{
		$this->emails = $emails;
	}
	
	public function getEthnicity()
	{
		return $this->ethnicity;
	}
	
	public function setEthnicity($ethnicity)
	{
		$this->ethnicity = $ethnicity;
	}
	
	public function getFashion()
	{
		return $this->fashion;
	}
	
	public function setFashion($fashion)
	{
		$this->fashion = $fashion;
	}
	
	public function getFood()
	{
		return $this->food;
	}
	
	public function setFood($food)
	{
		$this->food = $food;
	}
	
	public function getGender()
	{
		return $this->$this->gender;
	}
	
	public function setGender($newGender)
	{
		$this->gender = $newGender;
	}
	
	public function getHappiestWhen()
	{
		return $this->happiestWhen;
	}
	
	public function setHappiestWhen($happiestWhen)
	{
		$this->happiestWhen = $happiestWhen;
	}
	
	public function getHeroes()
	{
		return $this->heroes;
	}
	
	public function setHeroes($heroes)
	{
		$this->heroes = $heroes;
	}
	
	public function getHumor()
	{
		return $this->humor;
	}
	
	public function setHumor($humor)
	{
		$this->humor = $humor;
	}
	
	public function getId()
	{
		return $this->id;
	}
	
	public function setId($id)
	{
		$this->id = $id;
	}
	
	public function getInterests()
	{
		return $this->interests;
	}
	
	public function setInterests($interests)
	{
		$this->interests = $interests;
	}
	
	public function getJobInterests()
	{
		return $this->jobInterests;
	}
	
	public function setJobInterests($jobInterests)
	{
		$this->jobInterests = $jobInterests;
	}
	
	public function getJobs()
	{
		return $this->jobs;
	}
	
	public function setJobs($jobs)
	{
		$this->jobs = $jobs;
	}
	
	public function getLanguagesSpoken()
	{
		return $this->languagesSpoken;
	}
	
	public function setLanguagesSpoken($languagesSpoken)
	{
		$this->languagesSpoken = $languagesSpoken;
	}
	
	public function getLivingArrangement()
	{
		return $this->livingArrangement;
	}
	
	public function setLivingArrangement($livingArrangement)
	{
		$this->livingArrangement = $livingArrangement;
	}
	
	public function getLookingFor()
	{
		return $this->lookingFor;
	}
	
	public function setLookingFor($lookingFor)
	{
		$this->lookingFor = $lookingFor;
	}
	
	public function getMovies()
	{
		return $this->movies;
	}
	
	public function setMovies($movies)
	{
		$this->movies = $movies;
	}
	
	public function getMusic()
	{
		return $this->music;
	}
	
	public function setMusic($music)
	{
		$this->music = $music;
	}
	
	public function getName()
	{
		return $this->name;
	}
	
	public function setName($name)
	{
		$this->name = $name;
	}
	
	public function getNickname()
	{
		return $this->nickname;
	}
	
	public function setNickname($nickname)
	{
		$this->nickname = $nickname;
	}
	
	public function getPets()
	{
		return $this->pets;
	}
	
	public function setPets($pets)
	{
		$this->pets = $pets;
	}
	
	public function getPhoneNumbers()
	{
		return $this->phoneNumbers;
	}
	
	public function setPhoneNumbers($phoneNumbers)
	{
		$this->phoneNumbers = $phoneNumbers;
	}
	
	public function getPoliticalViews()
	{
		return $this->politicalViews;
	}
	
	public function setPoliticalViews($politicalViews)
	{
		$this->politicalViews = $politicalViews;
	}
	
	public function getProfileSong()
	{
		return $this->profileSong;
	}
	
	public function setProfileSong($profileSong)
	{
		$this->profileSong = $profileSong;
	}
	
	public function getProfileUrl()
	{
		return $this->profileUrl;
	}
	
	public function setProfileUrl($profileUrl)
	{
		$this->profileUrl = $profileUrl;
	}
	
	public function getProfileVideo()
	{
		return $this->profileVideo;
	}
	
	public function setProfileVideo($profileVideo)
	{
		$this->profileVideo = $profileVideo;
	}
	
	public function getQuotes()
	{
		return $this->quotes;
	}
	
	public function setQuotes($quotes)
	{
		$this->quotes = $quotes;
	}
	
	public function getRelationshipStatus()
	{
		return $this->relationshipStatus;
	}
	
	public function setRelationshipStatus($relationshipStatus)
	{
		$this->relationshipStatus = $relationshipStatus;
	}
	
	public function getReligion()
	{
		return $this->religion;
	}
	
	public function setReligion($religion)
	{
		$this->religion = $religion;
	}
	
	public function getRomance()
	{
		return $this->romance;
	}
	
	public function setRomance($romance)
	{
		$this->romance = $romance;
	}
	
	public function getScaredOf()
	{
		return $this->scaredOf;
	}
	
	public function setScaredOf($scaredOf)
	{
		$this->scaredOf = $scaredOf;
	}
	
	public function getSchools()
	{
		return $this->schools;
	}
	
	public function setSchools($schools)
	{
		$this->schools = $schools;
	}
	
	public function getSexualOrientation()
	{
		return $this->sexualOrientation;
	}
	
	public function setSexualOrientation($sexualOrientation)
	{
		$this->sexualOrientation = $sexualOrientation;
	}
	
	public function getSmoker()
	{
		return $this->$this->smoker;
	}
	
	public function setSmoker($newSmoker)
	{
		$this->smoker = $newSmoker;
	}
	
	public function getSports()
	{
		return $this->sports;
	}
	
	public function setSports($sports)
	{
		$this->sports = $sports;
	}
	
	public function getStatus()
	{
		return $this->status;
	}
	
	public function setStatus($status)
	{
		$this->status = $status;
	}
	
	public function getTags()
	{
		return $this->tags;
	}
	
	public function setTags($tags)
	{
		$this->tags = $tags;
	}
	
	public function getThumbnailUrl()
	{
		return $this->thumbnailUrl;
	}
	
	public function setThumbnailUrl($thumbnailUrl)
	{
		$this->thumbnailUrl = $thumbnailUrl;
	}
	
	public function getTimeZone()
	{
		return $this->timeZone;
	}
	
	public function setTimeZone($timeZone)
	{
		$this->timeZone = $timeZone;
	}
	
	public function getTurnOffs()
	{
		return $this->turnOffs;
	}
	
	public function setTurnOffs($turnOffs)
	{
		$this->turnOffs = $turnOffs;
	}
	
	public function getTurnOns()
	{
		return $this->turnOns;
	}
	
	public function setTurnOns($turnOns)
	{
		$this->turnOns = $turnOns;
	}
	
	public function getTvShows()
	{
		return $this->tvShows;
	}
	
	public function setTvShows($tvShows)
	{
		$this->tvShows = $tvShows;
	}
	
	public function getUrls()
	{
		return $this->urls;
	}
	
	public function setUrls($urls)
	{
		$this->urls = $urls;
	}
	
	public function getIsOwner()
	{
		return $this->isOwner;
	}
	
	public function setIsOwner($isOwner)
	{
		$this->isOwner = $isOwner;
	}
	
	public function getIsViewer()
	{
		return $this->isViewer;
	}
	
	public function setIsViewer($isViewer)
	{
		$this->isViewer = $isViewer;
	}
}
