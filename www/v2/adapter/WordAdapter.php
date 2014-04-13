<?php

class WordAdapter extends DbAdapter implements SyncAdapter {

	const SYNC_TABLE = "Words";
	
	public function get($wordId) {
		return self::getDb(parent::connect(), $wordId);
	}

	public static function getDb($db, $wordId) {

		$stm = $db->prepare("SELECT * FROM memoling_Words WHERE WordId = :WordId");
		$stm->bindParam("WordId", $wordId);
		$stm->execute();

		if($row = $stm->fetch()) {
			$obj = self::bindWord($row);
			return $obj;
		}

		return null;
	}

	public function getAll($memoBaseId = null) {
		return self::getAllDb(parent::connect(), $memoBaseId);
	}

	public static function getAllDb($db, $memoBaseId = null) {

		if($memoBaseId == null) {
			$stm = $db->prepare("SELECT * FROM memoling_Words");
		} else {
			$stm = $db->prepare("SELECT * FROM memoling_Words WHERE MemoBaseId = :MemoBaseId");
			$stm->bindParam("MemoBaseId", $memoBaseId);
		}

		$stm->execute();

		$list = array();
		while($row = $stm->fetch()) {
			$obj = self::bindWord($row);
			$list[] = $obj;
		}

		return $list;
	}
	
	public static function bindWord($row, $prefix="") {
		$obj = new Word();
		$obj->LanguageIso639 = $row[$prefix."LanguageIso639"];
		$obj->Word = $row[$prefix."Word"];
		$obj->WordId = $row[$prefix."WordId"];
		$obj->Class = $row[$prefix."Class"];
		$obj->Phonetic = $row[$prefix."Phonetic"];
			
		$obj->Description = $row[$prefix."Description"];
		return $obj;
	}

	public function insert($word, $syncClientId=null) {
		return self::inserDb(parent::transConnect(), $word, $syncClientId);
	}

	public function update($word, $syncClientId=null) {
		return self::updateDb(parent::transConnect(), $word, $syncClientId);
	}

	public function delete($wordId, $syncClientId=null) {
		self::deleteDb(parent::transConnect(), $wordId, $syncClientId);
	}
	
	public static function insertDb($dbTran, $word, $syncClientId=null) {

		$syncAction = new SyncAction();
		$syncAction->Action = SyncAction::ACTION_INSERT;
		$syncAction->PrimaryKey = $word->WordId;
		$syncAction->Table = self::SYNC_TABLE;
		$syncAction->SyncClientId = $syncClientId;
		
		return self::insertDbSync($dbTran, $word, $syncAction);
	}

	public static function updateDb($dbTran, $word, $syncClientId=null) {

		$updateColumns = array();
        // This is the case when data is modified by user not sync mechanism
        // Thats why we need to see what have been changed/

        $dbWord = self::getDb($dbTran, $word->WordId);

        if($dbWord->Word != $word->Word) {
            $updateColumns[] = "Word";
        }

        if($dbWord->LanguageIso639 != $word->LanguageIso639) {
            $updateColumns[] = "LanguageIso639";
        }

        if($dbWord->Description != $word->Description) {
            $updateColumns[] = "Description";
        }

        if($dbWord->Class != $word->Class) {
            $updateColumns[] = "Class";
        }

        if($dbWord->Phonetic != $word->Phonetic) {
            $updateColumns[] = "Phonetic";
        }
		
		foreach($updateColumns as $updateColumn) {

			$syncAction = new SyncAction();
			$syncAction->Action = SyncAction::ACTION_UPDATE;
			$syncAction->PrimaryKey = $word->WordId;
			$syncAction->Table = self::SYNC_TABLE;
			$syncAction->SyncClientId = $syncClientId;
			$syncAction->UpdateColumn = $updateColumn;
			
			self::updateDbSync($dbTran, $word, $syncAction);
		}
	}
	
	public static function deleteDb($dbTran, $wordId, $syncClientId=null) {

		$syncAction = new SyncAction();
		$syncAction->Action = SyncAction::ACTION_DELETE;
		$syncAction->PrimaryKey = $wordId;
		$syncAction->Table = self::SYNC_TABLE;
		$syncAction->SyncClientId = $syncClientId;
		
		return self::deleteDbSync($dbTran, $wordId, $syncAction);
	}

	public function decodeEntity($json) {
		$word = new Word();
		$word->decode($json);
		return $word;
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
	
	private static function insertDbSync($dbTran, $word, $syncAction) {
		$dbTran->beginTransaction();
		
		$query = "INSERT INTO
				memoling_Words (WordId, LanguageIso639, Word, Description)
				VALUES(:Wid,:Lang,:Word,:Description)";
		
		$stm = $dbTran->prepare($query);
		$stm->bindParam("Wid", $word->WordId);
		$stm->bindParam("Lang", $word->LanguageIso639);
		$stm->bindParam("Word", $word->Word);
		$stm->bindParam("Description", $word->Description);
		
		if(!$stm->execute()) {
			$dbTran->rollBack();
			throw new SqlException("Failed to create new Word", $dbTran);
		}
		
		SyncActionAdapter::insertAction($dbTran, $syncAction);

		$dbTran->commit();
	}
	
	private static function updateDbSync($dbTran, $word, $syncAction) {
		$dbTran->beginTransaction();
		
		$query = "UPDATE memoling_Words SET ";
		$value = null;
		$type = null;
		
		switch($syncAction->UpdateColumn) {
			case "Word":
				$query .= "Word = :Value ";
				$value = $word->Word;
				$type = PDO::PARAM_STR;
				break;
			case "LanguageIso639":
				$query .= "LanguageIso639 = :Value ";
				$value = $word->LanguageIso639;
				$type = PDO::PARAM_STR;
				break;
			case "Description":
				$query .= "Description = :Value ";
				$value = $word->Description;
				$type = PDO::PARAM_STR;
				break;
			case "Phonetic":
				$query .= "Phonetic = :Value ";
				$value = $word->Phonetic;
				$type = PDO::PARAM_STR;
				break;
			case "Class";
				$query .= "Class = :Value ";
				$value = $word->Class;
				$type = PDO::PARAM_STR;
				break;
		}
		
		$query .= "WHERE WordId = :WordId";
		
		$stm = $dbTran->prepare($query);
		$stm->bindParam("WordId", $word->WordId);
		
		if($syncAction->UpdateColumn != null)
		$stm->bindParam("Value", $value, $type);

		if(!$stm->execute()) {
			$dbTran->rollBack();
			throw new SqlException("Failed to update Word", $dbTran);
		}
		
		SyncActionAdapter::updateAction($dbTran, $syncAction);

		$dbTran->commit();
	}
	
	
	private static function deleteDbSync($dbTran, $wordId, $syncAction) {
		$dbTran->beginTransaction();
		
		$query = "DELETE FROM
				memoling_Words
				WHERE
				WordId = :WordId
				";
		
		$stm = $dbTran->prepare($query);
		$stm->bindParam(":WordId", $wordId);
		if(!$stm->execute()) {
			$dbTran->rollBack();
			throw new SqlException("Failed to delete Word", $dbTran);
		}
			
		SyncActionAdapter::deleteAction($dbTran, $syncAction);
		
		$dbTran->commit();
	}
}

?>