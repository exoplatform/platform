<?php

class GadgetId {
	private $uri;
	private $moduleId;

	public function GadgetId($uri, $moduleId)
	{
		$this->uri = $uri;
		$this->moduleId = $moduleId;
	}

	public function getURI()
	{
		return $this->uri;
	}

	public function getModuleId()
	{
		return $this->moduleId;
	}

	public function getKey()
	{
		return $this->getURI();
	}
}