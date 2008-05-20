<?php

class JsonRpcGadgetContext extends GadgetContext {
	private $locale = null;
	private $view = null;
	private $url = null;
	private $container = null;

	public function __construct($jsonContext, $url)
	{
		parent::__construct('GADGET');
		$this->url = $url;
		$this->view = $jsonContext->view;
		$this->locale = new Locale($jsonContext->language, $jsonContext->country);
		$this->container = $jsonContext->container;
	}

	public function getUrl()
	{
		return $this->url;
	}

	public function getView()
	{
		return $this->view;
	}

	public function getLocale()
	{
		return $this->locale;
	}

	public function getContainer()
	{
		return $this->container;
	}
}