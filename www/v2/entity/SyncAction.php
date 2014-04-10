<?php

class SyncAction {
	
	const ACTION_DELETE = -1;
	const ACTION_UPDATE = 0;
	const ACTION_INSERT = 1;
	
	public $SyncActionId;
	public $SyncClientId;
	public $Table;
	public $PrimaryKey;
	public $Action;
	public $ServerTimestamp;
	public $UpdateColumn;
	public $SyncObjectId;
	
	public $SyncPackageContext;
	
	public function __construct() {
		$this->SyncActionId = Helper::newGuid();
		$this->ServerTimestamp = time();
	}
	
	public function getClientObject($adapter) {
		$encodedObject = $this->SyncPackageContext->ClientObject[$this->SyncObjectId];
		$obj = $adapter->decodeEntity($encodedObject);
		return $obj;
	}
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
	
		if($obj == null) {
			throw new JsonException();
		}
	
		$this->SyncActionId = isset($obj->syncActionId)?$obj->syncActionId:"";
		$this->SyncClientId = isset($obj->syncClietnId)?$obj->syncClientId:"";
		$this->Table = isset($obj->table)?$obj->table:"";
		$this->PrimaryKey = isset($obj->primaryKey)?$obj->primaryKey:"";
		$this->Action = isset($obj->action)?intval($obj->action):null;
		$this->ServerTimestamp = isset($obj->serverTimestamp)?intval($obj->serverTimestamp):null;
		$this->UpdateColumn = isset($obj->updateColumn)?$obj->updateColumn:null;
		$this->SyncObjectId = isset($obj->syncObjectId)?$obj->syncObjectId:null;
	}
	
	public function encode() {
		$builder = new JsonBuilder();
	
		$builder->put("syncActionId", $this->SyncActionId, true);
		$builder->put("syncClientId", $this->SyncClientId, true);
		$builder->put("table", $this->Table, true);
		$builder->put("primaryKey", $this->PrimaryKey, true);
		$builder->put("action", $this->Action, true);
		$builder->put("serverTimestamp", $this->ServerTimestamp, true);
		$builder->put("updateColumn", $this->UpdateColumn, true);
		$builder->put("syncObjectId", $this->SyncObjectId, true);
	
		return $builder->__toString();
	}
}

?>