<?php

class JsonRpcHandler {

	public function process($requests)
	{
		$response = array();
		foreach ($requests->gadgets as $gadget) {
			try {
				$gadgetUrl = $gadget->url;
				$gadgetModuleId = $gadget->moduleId;
				$context = new JsonRpcGadgetContext($requests->context, $gadgetUrl);
				$gadgetServer = new GadgetServer();
				$gadget = $gadgetServer->processGadget($context);
				$response[] = $this->makeResponse($gadget, $gadgetModuleId, $gadgetUrl, $context);
			} catch (Exception $e) {
				$response[] = array('errors' => array($e->getMessage()), 'moduleId' => $gadgetModuleId, 'url' => $gadgetUrl);
			}
		}
		return $response;
	}

	private function makeResponse($gadget, $gadgetModuleId, $gadgetUrl, $context)
	{
		$response = array();
		$prefs = array();
		foreach ($gadget->getUserPrefs() as $pref) {
			$prefs[$pref->getName()] = array('displayName' => $pref->getDisplayName(), 'type' => $pref->getDataType(), 'default' => $pref->getDefaultValue(), 'enumValues' => $pref->getEnumValues());
		}
		$features = array();
		foreach ($gadget->getRequires() as $feature) {
			$features[] = $feature->getName();
		}
		
		$views = array();
		
		//TODO add views and actual iframe url
		$response['showInDirectory'] = $gadget->getShowInDirectory();
		$response['width'] = $gadget->getWidth();
		$response['title'] = $gadget->getTitle();
		$response['singleton'] = $gadget->getSingleton();
		$response['categories'] = Array($gadget->getCategory(), $gadget->getCategory2());
		$response['views'] = '';
		/*stdClass Object
                       (
                            $response['default'] = stdClass Object
                                (
                                    $response['type'] = html
                                    $response['quirks'] = 1
                                )

                        )*/
		$response['description'] = $gadget->getDescription();
		$response['screenshot'] = $gadget->getScreenShot();
		$response['thumbnail'] = $gadget->getThumbnail();
		$response['height'] = $gadget->getHeight();
		$response['scaling'] = $gadget->getScaling();
		$response['moduleId'] = $gadgetModuleId;
		$response['features'] = $features;
		$response['showStats'] = $gadget->getShowStats();
		$response['scrolling'] = $gadget->getScrolling();
		$response['url'] = $gadgetUrl;
		$response['authorEmail'] = $gadget->getAuthorEmail();
		$response['titleUrl'] = $gadget->getTitleUrl();
		$response['directoryTitle'] = $gadget->getDirectoryTitle();
		$response['author'] = $gadget->getAuthor();
		$response['iframeUrl'] = UrlGenerator::getIframeURL($gadget, $context);
		$response['userPrefs'] = $prefs;
		return $response;
	}
}