<?php

class StateFileDataHandler extends GadgetDataHandler {
	private $handles = array('DUMP_STATE', 'SET_STATE', 'SET_EVILNESS');

	public function shouldHandle($requestType)
	{
		return in_array($requestType, $this->handles);
	}

	public function handleRequest($request)
	{
		// do stuff
	}
}