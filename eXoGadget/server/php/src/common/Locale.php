<?php

/**
 * Locale class doesn't exist in php, so to allow the code base to be closer to the java and it's spec
 * interpretation one, we created our own
 */
class Locale {
	public $language;
	public $country;

	public function __construct($language, $country)
	{
		$this->language = $language;
		$this->country = $country;
	}

	public function equals($obj)
	{
		if (! ($obj instanceof Locale)) {
			return false;
		}
		return ($obj->language == $this->language && $obj->country == $this->country);
	}

	public function getLanguage()
	{
		return $this->language;
	}

	public function getCountry()
	{
		return $this->country;
	}
}