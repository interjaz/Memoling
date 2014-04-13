<?php

class SyncPackage {
	
	public $ServerTimestamp;
	public $SyncClientId;
	public $SyncActions;
	
	// Encoded objects, created by the Client.
	// They cannot be at this point strong, since SyncAction does not know anything about the underlying objects.  
	public $ClientObject;
	
	// Strong objects, created by Server.
	public $ServerObjects;
	
	public function __construct() {
		$this->ServerTimestamp = time();
	}
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
        
		if($obj == null || !isset($obj->syncClientId) || !isset($obj->serverTimestamp)) {
			throw new JsonException();
		}

		$this->SyncClientId = $obj->syncClientId;
		$this->ServerTimestamp = intval($obj->serverTimestamp);

		$this->SyncActions = array();
		foreach($obj->syncActions as $jsonSyncAction) {
			$syncAction = new SyncAction();
			$syncAction->SyncPackageContext = &$this;
			$syncAction->decode($jsonSyncAction); 
			$syncAction->SyncClientId = $this->SyncClientId;
			$this->SyncActions[] = $syncAction;
		}
		
		$this->ClientObject = array();
		foreach($obj->syncObjects as $key=>$jsonSyncObjects) {
			$this->ClientObject[$key] = $jsonSyncObjects;
		}
	}
	
	public function encode() {
		$builder = new JsonBuilder();
		
		$builder->put("syncClientId", $this->SyncClientId, true);
		$builder->put("serverTimestamp", $this->ServerTimestamp);
		$builder->put("syncActions", $this->SyncActions, true);
        
        $jsonArray = "";
        foreach($this->ServerObjects as $key=>$value) {
            $jsonArray .= '"'.$key.'":' . $value->encode() . ','; 
        }
        if(strlen($jsonArray) > 0) {
            $jsonArray = substr($jsonArray, 0, strlen($jsonArray)-1);
        }
        
        $jsonArray = ',"syncObjects":{' . $jsonArray . '}';
        
		//$builder->put("syncObjects", $this->ServerObjects, true);
		
        $json = $builder->__toString();
        $json = substr_replace($json, $jsonArray, strlen($json)-1, 0);
        
		return $json;
		
	}
	
}


?>