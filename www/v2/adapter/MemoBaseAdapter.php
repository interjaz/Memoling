<?php

class MemoBaseAdapter extends DbAdapter implements SyncAdapter {

	const SYNC_TABLE = "MemoBases";
	
	public function get($memoBaseId) {
		return self::getDb(parent::connect(), $memoBaseId);
	}

	public static function getDb($db, $memoBaseId) {

		$stm = $db->prepare("SELECT * FROM memoling_MemoBases WHERE MemoBaseId = :MemoBaseId");
		$stm->bindParam(":MemoBaseId", $memoBaseId);
		$stm->execute();

		if($row = $stm->fetch()) {
			$obj = new MemoBase();
			$obj->Active = $row["Active"];
			$obj->Created = $row["Created"];
			$obj->MemoBaseId = $row["MemoBaseId"];
			$obj->Name = $row["Name"];
			return $obj;
		}

		return null;
	}

	public function getAll() {

		$db = parent::connect();
		$stm = $db->prepare("SELECT * FROM memoling_MemoBases");
		$stm->execute();

		$list = array();
		while($row = $stm->fetch()) {
			$obj = new MemoBase();
			$obj->Active = $row["Active"];
			$obj->Created = $row["Created"];
			$obj->MemoBaseId = $row["MemoBaseId"];
			$obj->Name = $row["Name"];
			$list[] = $obj;
		}

		return $list;

	}
    
    public function getAllForNavList($facebookUserId) {
        $db = parent::connect();
        
        $query = "SELECT 
            MB.MemoBaseId AS MemoBaseId, MB.Name AS Name,
            COUNT(M.MemoId) AS Count 
            FROM memoling_MemoBases AS MB
            LEFT OUTER JOIN memoling_Memos AS M 
                ON MB.MemoBaseId = M.MemoBaseId
            GROUP BY MB.MemoBaseId, MB.FacebookUserId
            HAVING MB.FacebookUserId = '100002530762250'
            ORDER BY MB.Name";
        
		$stm = $db->prepare($query);
        $stm->bindParam("FacebookUserId", $facebookUserId);
		$stm->execute();

		$list = array();
		while($row = $stm->fetch()) {
			$obj = new MemoBaseViewModel();
			$obj->Name = $row["Name"];
			$obj->Count = $row["Count"];
			$obj->MemoBaseId = $row["MemoBaseId"];
			$obj->Languages = self::getMemoLanguagesDb($db, $obj->MemoBaseId);
			$list[] = $obj;
		}
        
		return $list;
    }
	
    public static function getMemoLanguagesDb($db, $memoBaseId) {
        
        $query = "SELECT Languages FROM  (
                    SELECT 
                        DISTINCT WA.LanguageIso639 AS Languages
                    FROM memoling_Memos AS M
                    JOIN memoling_Words AS WA 
                        ON M.WordAId = WA.WordId
                    WHERE M.MemoBaseId = :MemoBaseId
                    ) AS A UNION (
                    SELECT 
                        DISTINCT WB.LanguageIso639 AS Languages
                    FROM memoling_Memos AS M
                    JOIN memoling_Words AS WB 
                        ON M.WordBId = WB.WordId
                    WHERE M.MemoBaseId = :MemoBaseId
                    )";
        
		$stm = $db->prepare($query);
        $stm->bindParam("MemoBaseId", $memoBaseId);
		$stm->execute();

		$list = array();
		while($row = $stm->fetch()) {
            $language = $row["Languages"];
			$list[] = $language;
		}

		return $list;
    }
    
	public function getMemoCount($memoBaseId) {
		return self::getMemoCountDb(parent::connect(), $memoBaseId);
	}

	public static function getMemoCountDb($db, $memoBaseId) {
		$query = "SELECT COUNT(1) AS MemoCount FROM memoling_Memos WHERE MemoBaseId = :MemoBaseId";
		$stm = $db->prepare($query);
		$stm->bindParam("MemoBaseId", $memoBaseId);
		$stm->execute();
		
		if($row = $stm->fetch()) {
			return $row["MemoCount"];
		} else {
			return null;
		}
	}

	public function insert($memoBase, $syncClientId=null) {
		return self::insertDb(parent::transConnect(), $memoBase, $syncClientId);
	}

	public static function insertDb($dbTran, $memoBase, $syncClientId=null) {
		$syncAction = new SyncAction();
		$syncAction->Action = SyncAction::ACTION_INSERT;
		$syncAction->PrimaryKey = $memoBase->MemoBaseId;
		$syncAction->Table = self::SYNC_TABLE;
		$syncAction->SyncClientId = $syncClientId;
		
		return self::insertDbSync($dbTran, $memoBase, $syncAction);
	}
	
	public function update($memoBase, $syncClientId=null) {
		return self::updateDb(parent::transConnect(), $memoBase, $syncClientId);
	}

	public static function updateDb($dbTran, $memoBase, $syncClientId=null) {

		$updateColumns = array();
			// This is the case when data is modified by user not sync mechanism
			// Thats why we need to see what have been changed/
		
        $dbMemoBase = self::getDb($dbTran, $memoBase->MemoBaseId);

        if($dbMemoBase->Name != $memoBase->Name) {
            $updateColumns[] = "Name";
        }
        if($dbMemoBase->Active != $memoBase->Active) {
            $updateColumns[] = "Active";
        }
		
		foreach($updateColumns as $updateColumn) {
		
			$syncAction = new SyncAction();
			$syncAction->Action = SyncAction::ACTION_UPDATE;
			$syncAction->PrimaryKey = $dbMemoBase->MemoBaseId;
			$syncAction->Table = self::SYNC_TABLE;
			$syncAction->SyncClientId = $syncClientId;
			$syncAction->UpdateColumn = $updateColumn;
		
			self::updateDbSync($dbTran, $memoBase, $syncAction);
		}
        
		if($memoBase->Memos != null) {
            foreach($memo as $memoBase->Memos) {
                MemoAdapter::updateDb($dbTran, $memo, $syncClientId);
            }
        }
	}

	public function delete($memoBaseId, $syncClientId=null) {
		return self::deleteDb(parent::transConnect(), $memoBaseId, $syncClientId);
	}

	public static function deleteDb($dbTran, $memoBaseId, $syncClientId=null) {
		$syncAction = new SyncAction();
		$syncAction->Action = SyncAction::ACTION_DELETE;
		$syncAction->PrimaryKey = $memoBaseId;
		$syncAction->Table = self::SYNC_TABLE;
		$syncAction->SyncClientId = $syncClientId;

		return self::deleteDbSync($dbTran, $memoBaseId, $syncAction);
	}
	
	public function decodeEntity($json) {
		$memoBase = new MemoBase();
		$memoBase->decode($json);
		return $memoBase;
	}
	
	public function getEntity($primaryKey) {
		$obj = $this->get($primaryKey);
		return $obj;
	}

	public function insertEntity($dbTran, $object, $syncAction) {
		self::insertDbSync($dbTran, $object, $syncAction);
	}
	
	public function updateEntity($dbTran, $object, $syncAction) {
		self::updateDbSync($dbTran, $object, $syncAction);
	}
	
	public function deleteEntity($dbTran, $primaryKey, $syncAction) {
		self::deleteDbSync($dbTran, $primaryKey, $syncAction);
	}
	
	private static function insertDbSync($dbTran, $memoBase, $syncAction) {
		$dbTran->beginTransaction();
		
        if($syncAction->SyncClientId != null &&
           $memoBase->FacebookUserId == null) {
            $syncClient = SyncClientAdapter::getDb($dbTran, $syncAction->SyncClientId);
            $memoBase->FacebookUserId = $syncClient->FacebookUserId;
        }
        
		$query = "INSERT INTO
				memoling_MemoBases(MemoBaseId,Name,Created,Active,FacebookUserId)
				VALUES(:Bid,:Name,:Created,:Active,:FacebookUserId)";
		$stm = $dbTran->prepare($query);
		
		$stm->bindParam("Bid", $memoBase->MemoBaseId);
		$stm->bindParam("Name", $memoBase->Name);
		$stm->bindParam("Created", $memoBase->Created);
		$stm->bindParam("Active", $memoBase->Active, PDO::PARAM_INT);
        $stm->bindParam("FacebookUserId", $memoBase->FacebookUserId);
		
		if(!$stm->execute()) {
			$dbTran->rollBack();
			throw new SqlException("Failed to create MemoBase", $dbTran);
		}
	
		if($memoBase->Memos != null) {
			foreach($memoBase->Memos as $memo) {
				MemoAdapter::insertDb($dbTran, $memo, $syncAction->SyncClientId);
			}
		}
	
		SyncActionAdapter::insertAction($dbTran, $syncAction);
	
		$dbTran->commit();
	}
	
	private static function updateDbSync($dbTran, $memoBase, $syncAction) {
		$dbTran->beginTransaction();
	
		$query = "UPDATE memoling_MemoBases SET ";
		$value = null;
		$type = null;
	
		switch($syncAction->UpdateColumn) {
			case "Name":
				$query .= "Name = :Value ";
				$value = $memoBase->Name;
				$type = PDO::PARAM_STR;
				break;
			case "Active":
				$query .= "Active = :Value ";
				$value = $memoBase->Active;
				$type = PDO::PARAM_INT;
				break;
		}
	
		$query .= "WHERE MemoBaseId = :MemoBaseId";
	
		$stm = $dbTran->prepare($query);
		$stm->bindParam("MemoBaseId", $memoBase->MemoBaseId);
		$stm->bindParam("Value", $value, $type);
	
		if(!$stm->execute()) {
			$dbTran->rollBack();
			throw new SqlException("Failed to update MemoBase", $dbTran);
		}
	
		SyncActionAdapter::updateAction($dbTran, $syncAction);
	
		$dbTran->commit();
	}
	
	private static function deleteDbSync($dbTran, $memoBaseId, $syncAction) {
		$dbTran->beginTransaction();
	

		$memoBase = self::getDb($dbTran, $memoBaseId);
		$memos = MemoAdapter::getAllDb($dbTran, $memoBaseId);
		
        if($memoBase != null) {
        
            foreach($memos as $memo) {
                MemoAdapter::deleteDb($dbTran, $memo->MemoId, null);
                SyncActionAdapter::removeActions($dbTran, MemoAdapter::SYNC_TABLE, $memo->MemoId);
            }

        }
		
		$query = "DELETE FROM
					memoling_MemoBases
					WHERE
					MemoBaseId = :MemoBaseId";
		
		$stm = $dbTran->prepare($query);
		$stm->bindParam(":MemoBaseId", $memoBaseId);
		if(!$stm->execute()) {
			$db->rollBack();
			throw new SqlException("Failed to delete MemoBase", $dbTran);
		}

		SyncActionAdapter::deleteAction($dbTran, $syncAction);
	
		$dbTran->commit();
	}

}

?>