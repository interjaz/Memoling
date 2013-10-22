<?php

class MemoAdapter extends DbAdapter {

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
					WHERE M.MemoBaseId = :MemoBaseId";
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

	public function insert($memo) {
		return self::insertDb(parent::transConnect(), $memo);
	}

	public static function insertDb($db, $memo) {

		$db->beginTransaction();

		try {
				
			if(strcmp($memo->WordA->LanguageIso639, $memo->WordB->LanguageIso639) > 0) {
				$tmp = $memo->WordA;
				$memo->WordA = $memo->WordB;
				$memo->WordB = $tmp;
			}

			WordAdapter::insertDb($db, $memo->WordA);
			WordAdapter::insertDb($db, $memo->WordB);
				
			// Insert Memo
			$memoId = Helper::newGuid();
			$query = "INSERT INTO
					memoling_Memos
					VALUES(:Mid,:Bid,:WAid,:WBid,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0,0,0,0)";
			$stm = $db->prepare($query);
			$stm->bindParam(":Mid", $memo->MemoId);
			$stm->bindParam(":Bid", $memo->MemoBaseId);
			$stm->bindParam(":WAid", $memo->WordA->WordId);
			$stm->bindParam(":WBid", $memo->WordB->WordId);

			if(!$stm->execute()) {
				throw new SqlException("Failed to create new Memo", $db);
			}

			$db->commit();
				
		} catch(Exception $ex) {
			$db->rollBack();
			throw $ex;
		}
	}

	public function delete($memoId) {
		return self::deleteDb(parent::transConnect(), $memoId);
	}

	public static function deleteDb($db, $memoId) {

		$db->beginTransaction();

		try {
				
			$memo = self::getDb($db, $memoId);
				
			$query = "DELETE FROM
					memoling_Memos
					WHERE
					MemoId = :MemoId
					";
				
			$stm = $db->prepare($query);
			$stm->bindParam(":MemoId", $memo->MemoId);
				
			if(!$stm->execute()) {
				throw new SqlException("Failed to delete Memo", $db);
			}
				
			WordAdapter::deleteDb($db, $memo->WordAId);
			WordAdapter::deleteDb($db, $memo->WordBId);
				
			$db->commit();				
		} catch(Exception $ex) {
			$db->rollBack();
				
			throw $ex;
		}
	}
}

?>