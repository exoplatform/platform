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
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Address.Field.html
 *
 */
class Address {
	public $country;
	public $extendedAddress;
	public $latitude;
	public $longitude;
	public $locality;
	public $poBox;
	public $postalCode;
	public $region;
	public $streetAddress;
	public $type;
	public $unstructuredAddress;
	
	public function __construct($unstructuredAddress)
	{
		$this->unstructuredAddress = $unstructuredAddress;
	}
	
	public function getCountry()
	{
		return $this->country;
	}
	
	public function setCountry($country)
	{
		$this->country = $country;
	}
	
	public function getExtendedAddress()
	{
		return $this->extendedAddress;
	}
	
	public function setExtendedAddress($extendedAddress)
	{
		$this->extendedAddress = $extendedAddress;
	}
	
	public function getLatitude()
	{
		return $this->latitude;
	}
	
	public function setLatitude($latitude)
	{
		$this->latitude = $latitude;
	}
	
	public function getLocality()
	{
		return $this->locality;
	}
	
	public function setLocality($locality)
	{
		$this->locality = $locality;
	}
	
	public function getLongitude()
	{
		return $this->longitude;
	}
	
	public function setLongitude($longitude)
	{
		$this->longitude = $longitude;
	}
	
	public function getPoBox()
	{
		return $this->poBox;
	}
	
	public function setPoBox($poBox)
	{
		$this->poBox = $poBox;
	}
	
	public function getPostalCode()
	{
		return $this->postalCode;
	}
	
	public function setPostalCode($postalCode)
	{
		$this->postalCode = $postalCode;
	}
	
	public function getRegion()
	{
		return $this->region;
	}
	
	public function setRegion($region)
	{
		$this->region = $region;
	}
	
	public function getStreetAddress()
	{
		return $this->streetAddress;
	}
	
	public function setStreetAddress($streetAddress)
	{
		$this->streetAddress = $streetAddress;
	}
	
	public function getType()
	{
		return $this->type;
	}
	
	public function setType($type)
	{
		$this->type = $type;
	}
	
	public function getUnstructuredAddress()
	{
		return $this->unstructuredAddress;
	}
	
	public function setUnstructuredAddress($unstructuredAddress)
	{
		$this->unstructuredAddress = $unstructuredAddress;
	}

}
