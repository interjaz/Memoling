<?php
require_once("../Init.php");

class FacebookLocation {
	
	public $FacebookLocationId;
	public $Name;
	public $Created;
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
		
		if($obj == null) {
			throw new JsonException();
		}
		
		$this->FacebookLocationId= isset($obj->facebookLocationId)?$obj->facebookLocationId:null;
		$this->Name= isset($obj->name)?$obj->name:null;
		$this->Created= isset($obj->created)?$obj->created:null;
	}
	
}

?>