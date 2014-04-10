<?php

class SyncActionAdapter extends DbAdapter {
	
	private $adapterCache;
	
	public function __construct() {
		parent::__construct();
		
		$this->adapterCache = array();
		$this->adapterCache["Words"] = new WordAdapter();
		$this->adapterCache["Memos"] = new MemoAdapter();
		$this->adapterCache["MemoBases"] = new MemoBaseAdapter();
	}
	
	public function syncServer($syncClientActions) {
		$dbTran = parent::transConnect();
		$dbTran->beginTransaction();
		
		try {
            
            // If new item is created and updated afterwards, only update is sent
            // however, multiple updates may bey sent, but only one insert should be performed.
            $updateInsertContext = array();
			
			foreach($syncClientActions as $syncAction) {
				$adapter = $this->getAdapter($syncAction);
				
				switch ($syncAction->Action) {
					case SyncAction::ACTION_DELETE:
                        // One action per key
						$adapter->deleteEntity($dbTran, $syncAction->PrimaryKey, $syncAction);
						break;
					case SyncAction::ACTION_INSERT:
                        // One action per key
						$object = $syncAction->getClientObject($adapter);
						$adapter->insertEntity($dbTran, $object, $syncAction);
						break;
					case SyncACtion::ACTION_UPDATE:
                        // Multiple actions per key
                        $exists = in_array($syncAction->Table . $syncAction->PrimaryKey, $updateInsertContext) 
                            || $adapter->getEntity($syncAction->PrimaryKey) != null;
						$object = $syncAction->getClientObject($adapter);
                        
                        if(!$exists) { 
                            $adapter->insertEntity($dbTran, $object, $syncAction);
                            $updateInsertContext[] = $syncAction->Table . $syncAction->PrimaryKey;
                        } else {
						  $adapter->updateEntity($dbTran, $object, $syncAction);
                        }
                    
						break;
				}
			}
			
		} catch(Exception $ex) {
			$dbTran->rollBack();
			throw $ex;
		}
		
		$dbTran->commit();
	}
	
	// Returns SyncPackage
	public function syncClient($syncClientId, $syncServerActions) {

		$syncPackage = new SyncPackage();
		
		$syncObjects = array();
		$index = 0;
		
		$syncActionsToDiscard = array();
		foreach($syncServerActions as $syncAction) {
			$adapter = $this->getAdapter($syncAction);
			
			if($syncAction->Action != SyncAction::ACTION_DELETE) {
				$object = $adapter->getEntity($syncAction->PrimaryKey);
				
				// If object is null, this means that during sync
				// Some objects has been removed - e.g. Children of deleted Parent
				// Resolver does not know about the foreign key relationship
				// so we need to enforce it here
				
				if($object == null) {
					$syncActionsToDiscard[] = $syncAction;
					continue;
				}
				
				$key = array_search($object, $syncObjects);
				if($key == false) {
					$key = $index++;
				}
				$syncObjects[$key] = $object;
				$syncAction->SyncObjectId = $key;
			}
		}
		
		foreach($syncActionsToDiscard as $actionKey=>$action) {
			unset($syncServerActions[$actionKey]);
		}
        
		$syncPackage->SyncClientId = $syncClientId;
		$syncPackage->SyncActions = $syncServerActions;
		$syncPackage->ServerObjects = $syncObjects;
		
		return $syncPackage;
	}
	
	public static function insertAction($dbTran, $syncAction) {
		if($syncAction->SyncClientId == null) {
			// Not tracked one
			return;
		}
		
		$dbTran->beginTransaction();
		
		$existingRecords = self::getSimilar($dbTran, $syncAction->SyncClientId, $syncAction->Table, $syncAction->PrimaryKey, $syncAction->UpdateColumn);

		// Delete all existing actions add new insert action
		foreach($existingRecords as $existingRecord) {
			self::deleteDb($dbTran, $existingRecord->SyncActionId);
		}
		
		self::insertDb($dbTran, $syncAction);
		
		$dbTran->commit();
	}
	
	public static function deleteAction($dbTran, $syncAction) {
		if($syncAction->SyncClientId == null) {
			// Not tracked one
			return;
		}
		
		$dbTran->beginTransaction();
		
		$existingRecords = self::getSimilar($dbTran, $syncAction->SyncClientId, $syncAction->Table, $syncAction->PrimaryKey, $syncAction->UpdateColumn);
		
		// Delete all existing actions add new delete action
		foreach($existingRecords as $existingRecord) {
			self::deleteDb($dbTran, $existingRecord->SyncActionId);
		}

		self::insertDb($dbTran, $syncAction);

		$dbTran->commit();
	}
	
	public static function updateAction($dbTran, $syncAction) {
		if($syncAction->SyncClientId == null) {
			// Not tracked one
			return;
		}
		
		$dbTran->beginTransaction();
		
		$existingRecords = self::getSimilar($dbTran, $syncAction->SyncClientId, $syncAction->Table, $syncAction->PrimaryKey, $syncAction->UpdateColumn);
		
		// Delete all existing actions add new update action
		foreach($existingRecords as $existingRecord) {
			self::deleteDb($dbTran, $existingRecord->SyncActionId);
		}

		self::insertDb($dbTran, $syncAction);

		$dbTran->commit();
	}
	
	public static function removeActions($dbTran, $table, $primaryKey) {
		$dbTran->beginTransaction();
		
		$query = "DELETE FROM memoling_SyncActions WHERE ". 
				 "`Table` = :Table AND ".
				 "PrimaryKey = :PrimaryKey";
		
		$stm = $dbTran->prepare($query);
		$stm->bindParam("Table", $table);
		$stm->bindParam("PrimaryKey", $primaryKey);
		
		if(!$stm->execute()) {
			$dbTran->rollBack();
			throw new SqlException("Failed to remove sync actions from DB", $dbTran);
		}
		
		$dbTran->commit();
	}
	
	public function get($syncClientId, $timestmap) {
		return self::getDb(parent::connect(), $syncClientId, $timestmap);
	}
	
	public static function getDb($db, $syncClientId, $timestamp) {
	
		$stm = $db->prepare("SELECT * FROM memoling_SyncActions WHERE SyncClientId = :SyncClientId AND ServerTimestamp >= :ServerTimestamp ORDER BY ServerTimestamp");
		$stm->bindParam("SyncClientId", $syncClientId);
		$stm->bindParam("ServerTimestamp", $timestamp);
		$stm->execute();
	
		$array = array();
		while($row = $stm->fetch()) {
			$array[] = self::bindSyncAction($row);
		}
	
		return $array;
	}
	
	private function getAdapter($syncAction) {
		return $this->adapterCache[$syncAction->Table];
	}
	
	private static function getSimilar($dbTran, $syncClientId, $table, $primaryKey, $updateColumn) {
	
		$query = "SELECT * FROM memoling_SyncActions WHERE SyncClientId = :SyncClientId "
				."AND `Table` = :Table AND PrimaryKey = :PrimaryKey ";
	
		if($updateColumn != null) {
			$query .= "AND (UpdateColumn = :UpdateColumn OR UpdateColumn IS NULL) ";
		}
	
		$query .= "ORDER BY ServerTimestamp DESC";
	
		$stm = $dbTran->prepare($query);
		$stm->bindParam("SyncClientId", $syncClientId);
		$stm->bindParam("Table", $table);
		$stm->bindParam("PrimaryKey", $primaryKey);
		
		if($updateColumn != null) {
			$stm->bindParam("UpdateColumn", $updateColumn);
		}
		
		$stm->execute();
	
		$array = array();
		while($row = $stm->fetch()) {
			$array[] = self::bindSyncAction($row);
		}
	
		return $array;
	}
	
	private function insert($action) {
		return self::insertDb(parent::connect(), $action);
	}
	
	private static function insertDb($dbTran, $action) {
		$query = "INSERT INTO
				memoling_SyncActions(SyncActionId,SyncClientId,`Table`,PrimaryKey,Action,ServerTimestamp,UpdateColumn)
				VALUES(:SyncActionId,:SyncClientId,:Table,:PrimaryKey,:Action,:ServerTimestamp,:UpdateColumn)";
	
		$stm = $dbTran->prepare($query);
		$stm->bindParam("SyncActionId", $action->SyncActionId);
		$stm->bindParam("SyncClientId", $action->SyncClientId);
		$stm->bindParam("Table", $action->Table);
		$stm->bindParam("PrimaryKey", $action->PrimaryKey);
		$stm->bindParam("Action", $action->Action, PDO::PARAM_INT);
		$stm->bindParam("ServerTimestamp", $action->ServerTimestamp);
		$stm->bindParam("UpdateColumn", $action->UpdateColumn);
	
		if(!$stm->execute()) {
			throw new SqlException("Failed to create new SyncAction", $dbTran);
		}
	}
	
	private static function deleteDb($dbTran, $syncActionId) {
		
		$query = "DELETE FROM
				memoling_SyncActions
				WHERE
				SyncActionId = :SyncActionId
				";
		
		$stm = $dbTran->prepare($query);
		$stm->bindParam(":SyncActionId", $syncActionId);
		if(!$stm->execute()) {
			throw new SqlException("Failed to delete DeletWord", $dbTran);
		}
	}
	
	private static function bindSyncAction($row) {
		$obj = new SyncAction();
	
		$obj->SyncActionId = $row["SyncActionId"];
		$obj->SyncClientId = $row["SyncClientId"];
		$obj->Table = $row["Table"];
		$obj->Action = intval($row["Action"]);
		$obj->PrimaryKey = $row["PrimaryKey"];
		$obj->ServerTimestamp = $row["ServerTimestamp"];
		$obj->UpdateColumn  = $row["UpdateColumn"];
	
		return $obj;
	}
}

?>