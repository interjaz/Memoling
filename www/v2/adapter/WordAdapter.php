<?php

class WordAdapter extends DbAdapter {

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

	public function insert($word) {
		return self::inserDb(parent::transConnect(), $word);
	}

	public static function insertDb($db, $word) {
		$query = "INSERT INTO
				memoling_Words (WordId, LanguageIso639, Word, Description)
				VALUES(:Wid,:Lang,:Word,:Description)";

		$stm = $db->prepare($query);
		$stm->bindParam(":Wid", $word->WordId);
		$stm->bindParam(":Lang", $word->LanguageIso639);
		$stm->bindParam(":Word", $word->Word);
		$stm->bindParam(":Description", $word->Description);

		if(!$stm->execute()) {
			throw new SqlException("Failed to create new Word", $db);
		}
	}

	public function delete($wordId) {
		self::deleteDb(parent::transConnect(), $wordId);
	}

	public static function deleteDb($db, $wordId) {

		$query = "DELETE FROM
				memoling_Words
				WHERE
				WordId = :WordId
				";

		$stm = $db->prepare($query);
		$stm->bindParam(":WordId", $wordId);
		if(!$stm->execute()) {
			throw new SqlException("Failed to delete Word", $db);
		}
	}

}

?>