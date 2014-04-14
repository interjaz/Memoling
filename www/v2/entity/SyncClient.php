<?php

class SyncClient {
	
	public $SyncClientId;
	public $FacebookUserId;
	public $Description;
    
    
    public function decode($json) {
        $obj = is_object($json)?$json:json_decode($json);
	
		if($obj == null) {
			throw new JsonException();
		}
	
		$this->SyncClientId = isset($obj->syncClientId)?$obj->syncClientId:null;
		$this->FacebookUserId = isset($obj->facebookUserId)?$obj->facebookUserId:null;
		$this->Description = isset($obj->description)?$obj->description:"";
    }
    
    public function encode() {
		$builder = new JsonBuilder();
	
		$builder->put("syncClientId", $this->SyncClientId, true);
		$builder->put("facebookUserId", $this->FacebookUserId, true);
		$builder->put("description", $this->Description, true);
	
		return $builder->__toString();
        
    }
}

?>