<?php
include_once("Init.php");


class Shortcut {
	
	public function __construct() {
		$key = key($_GET);
		if($key == null || $key == "") {
			Helper::redirect("index.php");
		}
		
		$adapter = new UrlShortcutAdapter();
		$url = $adapter->getUrl($key);
		
		if($url == null) {
			Helper::redirect("index.php");
		}
		
		$qs = explode("?", $url);		
		$this->recoverGet($qs[1]);
					
		include_once($qs[0]);
		die();
	}
	
	private function recoverGet($queryString) {
		
		$gets = explode("&", $queryString);
		
		foreach($gets as $get) {
			$val = explode("=", $get);
			$_GET[$val[0]] = $val[1];
		}
	}
}

new Shortcut();
?>