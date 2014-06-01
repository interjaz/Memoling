<?php

class MemoAdapter extends DbAdapter implements SyncAdapter {

	const SYNC_TABLE = "Memos";
	
	public function get($memoId) {
		return self::getDb(parent::connect(), $memoId);
	}

	public static function getDb($db, $memoId) {

		$stm = $db->prepare("SELECT * FROM memoling_Memos WHERE MemoId = :MemoId");
		$stm->bindParam("MemoId", $memoId);
        $stm->execute();
        
		if($row = $stm->fetch()) {
			$obj = self::bindMemo($row);
			return $obj;
		}
        
		return null;
	}

	public function getAll($memoBaseId=null) {
		return self::getAllDb(parent::connect(), $memoBaseId);
	}

	public static function getAllDb($db, $memoBaseId = null) {

		$stm = null;

		if($memoBaseId == null) {
			$query = "SELECT * FROM memoling_Memos";
			$stm = $db->prepare($query);
		} else {
			$query = "SELECT * FROM memoling_Memos WHERE MemoBaseId = :MemoBaseId";
			$stm = $db->prepare($query);
			$stm->bindParam("MemoBaseId", $memoBaseId);
		}

		$stm->execute();

		$list = array();
		while($row = $stm->fetch()) {
			$obj = self::bindMemo($row);
			$list[] = $obj;
		}

		return $list;
	}
	
	public function getAllDeep($memoBaseId=null) {
		return self::getAllDeepDb(parent::connect(), $memoBaseId);
	}
	
	public static function getAllDeepDb($db, $memoBaseId=null) {
		$stm = null;
		
		if($memoBaseId == null) {
			$query = "SELECT * FROM memoling_Memos";
			$stm = $db->prepare($query);
		} else {
			$query = "SELECT 
						M.MemoId,
						M.MemoBaseId,
						M.WordAId,
						M.WordBId,
						M.Created,
						M.LastReviewed,
						M.Displayed,
						M.CorrectAnsweredWordA,
						M.CorrectAnsweredWordB,
						M.Active,
						WA.WordId AS WA_WordId,
						WA.LanguageIso639 AS WA_LanguageIso639,
						WA.Word AS WA_Word,
						WA.Description AS WA_Description,
						WA.Class AS WA_Class,
						WA.Phonetic AS WA_Phonetic,
						WB.WordId AS WB_WordId,
						WB.LanguageIso639 AS WB_LanguageIso639,
						WB.Word AS WB_Word,
						WB.Description AS WB_Description,
						WB.Class AS WB_Class,
						WB.Phonetic AS WB_Phonetic 
					FROM memoling_Memos AS M
					JOIN memoling_Words AS WA ON M.WordAId = WA.WordId
					JOIN memoling_Words AS WB ON M.WordBId = WB.WordId
					WHERE M.MemoBaseId = :MemoBaseId
                    ORDER BY Created";
			$stm = $db->prepare($query);
			$stm->bindParam("MemoBaseId", $memoBaseId);
		}
		
		$stm->execute();
		
		$list = array();
		while($row = $stm->fetch()) {
			$obj = self::bindMemo($row);
			$obj->WordA = WordAdapter::bindWord($row, "WA_");
			$obj->WordB = WordAdapter::bindWord($row, "WB_");
			$list[] = $obj;
		}
		
		return $list;
	}
	
	public static function bindMemo($row, $prefix="") {
		$obj = new Memo();
		$obj->CorrectAnsweredWordA = $row[$prefix."CorrectAnsweredWordA"];
		$obj->CorrectAnsweredWordB = $row[$prefix."CorrectAnsweredWordB"];
		$obj->Created = $row[$prefix."Created"];
		$obj->Displayed = $row[$prefix."Displayed"];
		$obj->LastReviewed = $row[$prefix."LastReviewed"];
		$obj->MemoBaseId = $row[$prefix."MemoBaseId"];
		$obj->MemoId = $row[$prefix."MemoId"];
		$obj->WordAId = $row[$prefix."WordAId"];
		$obj->WordBId = $row[$prefix."WordBId"];
		$obj->Active = $row[$prefix."Active"];
		
		return $obj;
	}

	public function insert($memo, $syncClientId=null) {
		return self::insertDb(parent::transConnect(), $memo, $syncClientId);
	}

	public static function insertDb($dbTran, $memo, $syncClientId=null) {
		$syncAction = new SyncAction();
		$syncAction->Action = SyncAction::ACTION_INSERT;
		$syncAction->PrimaryKey = $memo->MemoId;
		$syncAction->Table = self::SYNC_TABLE;
		$syncAction->SyncClientId = $syncClientId;
		
		return self::insertDbSync($dbTran, $memo, $syncAction);
	}

	public function update($memo, $syncClientId=null) {
		return self::updateDb(parent::transConnect(), $memo, $syncClientId);
	}

	public static function updateDb($dbTran, $memo, $syncClientId=null) {
		
		$updateColumns = array();
        // This is the case when data is modified by user not sync mechanism
        // Thats why we need to see what have been changed/

        $dbMemo = self::getDb($dbTran, $memo->MemoId);

        if($dbMemo->LastReviewed != $memo->LastReviewed) {
            $updateColumns[] = "LastReviewed";
        }

        if($dbMemo->Displayed != $memo->Displayed) {
            $updateColumns[] = "Displayed";
        }

        if($dbMemo->CorrectAnsweredWordA != $memo->CorrectAnsweredWordA) {
            $updateColumns[] = "CorrectAnsweredWordA";
        }

        if($dbMemo->CorrectAnsweredWordB != $memo->CorrectAnsweredWordB) {
            $updateColumns[] = "CorrectAnsweredWordB";
        }

        if($dbMemo->Active != $memo->Active) {
            $updateColumns[] = "Active";
        }
		
		foreach($updateColumns as $updateColumn) {
		
			$syncAction = new SyncAction();
			$syncAction->Action = SyncAction::ACTION_UPDATE;
			$syncAction->PrimaryKey = $memo->MemoId;
			$syncAction->Table = self::SYNC_TABLE;
			$syncAction->SyncClientId = $syncClientId;
			$syncAction->UpdateColumn = $updateColumn;
				
			self::updateDbSync($dbTran, $memo, $syncAction);
		}
	
		if($memo->WordA != null && $memo->WordB != null) {
			WordAdapter::updateDb($dbTran, $memo->WordA, $syncClientId);
			WordAdapter::updateDb($dbTran, $memo->WordB, $syncClientId);
        }
	}

	public function delete($memoId, $syncClientId=null) {
		return self::deleteDb(parent::transConnect(), $memoId, $syncClientId);
	}

	public static function deleteDb($dbTran, $memoId, $syncClientId=null) {
		$syncAction = new SyncAction();
		$syncAction->Action = SyncAction::ACTION_DELETE;
		$syncAction->PrimaryKey = $memoId;
		$syncAction->Table = self::SYNC_TABLE;
		$syncAction->SyncClientId = $syncClientId;
		
		return self::deleteDbSync($dbTran, $memoId, $syncAction);
	}
	
	public function decodeEntity($json) {
		$memo = new Memo();
		$memo->decode($json);
		return $memo;
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
	
	private static function insertDbSync($dbTran, $memo, $syncAction) {
		$dbTran->beginTransaction();
        
		if($memo->WordA != null && $memo->WordB != null) {
            
			WordAdapter::insertDb($dbTran, $memo->WordA, $syncAction->SyncClientId);
			WordAdapter::insertDb($dbTran, $memo->WordB, $syncAction->SyncClientId);
			$memo->WordAId = $memo->WordA->WordId;
			$memo->WordBId = $memo->WordB->WordId;
		}
	
		// Insert Memo
        if($memo->MemoId == null) {
		  $memo->MemoId = Helper::newGuid();
        }
		$query = "INSERT INTO "
				 ."memoling_Memos(MemoId,MemoBaseId,WordAId,WordBId,Created,LastReviewed,Displayed,CorrectAnsweredWordA,CorrectAnsweredWordB,Active) " 
				 ."VALUES(:Mid,:Bid,:WAid,:WBid,:Created,:LastReviewed,:Displayed,:CorrectAnsweredWordA,:CorrectAnsweredWordB,:Active)";
		
		$stm = $dbTran->prepare($query);
		$stm->bindParam("Mid", $memo->MemoId);
		$stm->bindParam("Bid", $memo->MemoBaseId);
		$stm->bindParam("WAid", $memo->WordAId);
		$stm->bindParam("WBid", $memo->WordBId);
		$stm->bindParam("Created", $memo->Created);
		$stm->bindParam("LastReviewed", $memo->LastReviewed);
		$stm->bindParam("Displayed", $memo->Displayed, PDO::PARAM_INT);
		$stm->bindParam("CorrectAnsweredWordA", $memo->CorrectAnsweredWordA, PDO::PARAM_INT);
		$stm->bindParam("CorrectAnsweredWordB", $memo->CorrectAnsweredWordB, PDO::PARAM_INT);
		$stm->bindParam("Active", $memo->Active, PDO::PARAM_INT);	
		
		if(!$stm->execute()) {
			$dbTran->rollBack();
			throw new SqlException("Failed to create new Memo:", $stm);
		}

		SyncActionAdapter::insertAction($dbTran, $syncAction);
		
		$dbTran->commit();
	}
	
	private static function updateDbSync($dbTran, $memo, $syncAction) {
		$dbTran->beginTransaction();
        
		$query = "UPDATE memoling_Memos SET ";
		$value = null;
		$type = null;
	
		switch($syncAction->UpdateColumn) {
			case "LastReviewed":
				$query .= "LastReviewed = :Value ";
				$value = $memo->LastReviewed;
				$type = PDO::PARAM_STR;
				break;
			case "Displayed":
				$query .= "Displayed = :Value ";
				$value = $memo->Displayed;
				$type = PDO::PARAM_INT;
				break;
			case "CorrectAnsweredWordA":
				$query .= "CorrectAnsweredWordA = :Value ";
				$value = $memo->CorrectAnsweredWordA;
				$type = PDO::PARAM_INT;
				break;
			case "CorrectAnsweredWordB":
				$query .= "CorrectAnsweredWordB = :Value ";
				$value = $memo->CorrectAnsweredWordB;
				$type = PDO::PARAM_INT;
				break;
			case "Active";
				$query .= "Active = :Value ";
				$value = $memo->Active;
				$type = PDO::PARAM_BOOL;
			break;
		}
	
		$query .= "WHERE MemoId = :MemoId";
		
		$stm = $dbTran->prepare($query);
		$stm->bindParam("MemoId", $memo->MemoId);
		$stm->bindParam("Value", $value, $type);
	
		if(!$stm->execute()) {
			$dbTran->rollBack();
			throw new SqlException("Failed to update Memo", $dbTran);
		}
	
		SyncActionAdapter::updateAction($dbTran, $syncAction);
	
		$dbTran->commit();
	}
	
	private static function deleteDbSync($dbTran, $memoId, $syncAction) {
		$dbTran->beginTransaction();
		
		$memo = self::getDb($dbTran, $memoId);
	
		$query = "DELETE FROM
				memoling_Memos
				WHERE
				MemoId = :MemoId
				";
	
		$stm = $dbTran->prepare($query);
		$stm->bindParam(":MemoId", $memoId);
	
		if(!$stm->execute()) {
			$dbTran->rollBack();
			throw new SqlException("Failed to delete Memo", $dbTran);
		}
        
        if($memo != null) {
            
            // Remove all children and stop tracking them
            WordAdapter::deleteDb($dbTran, $memo->WordAId, null);
            SyncActionAdapter::removeActions($dbTran, WordAdapter::SYNC_TABLE, $memo->WordAId);

            WordAdapter::deleteDb($dbTran, $memo->WordBId, null);
            SyncActionAdapter::removeActions($dbTran, WordAdapter::SYNC_TABLE, $memo->WordBId);
		
        }
            
		SyncActionAdapter::deleteAction($dbTran, $syncAction);
	
		$dbTran->commit();
	}
}

?>