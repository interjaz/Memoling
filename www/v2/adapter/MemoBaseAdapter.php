<?php

class MemoBaseAdapter extends DbAdapter {

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

	public function insert($memoBase) {
		return self::insertDb(parent::transConnect(), $memoBase);
	}

	public static function insertDb($db, $memoBase) {
		$db->beginTransaction();

		try {
				
			$query = "INSERT INTO
					memoling_MemoBases
					VALUES(:Bid,:Name,CURRENT_TIMESTAMP,0)";
			$stm = $db->prepare($query);
			$stm->bindParam(":Bid", $memoBase->MemoBaseId);
			$stm->bindParam(":Name", $memoBase->Name);
			if(!$stm->execute()) {
				throw new SqlException("Failed to create MemoBase", $db);
			}
				
			foreach($memoBase->Memos as $memo) {
				MemoAdapter::insertDb($db, $memo);
			}

			$db->commit();
		} catch(Exception $ex) {
			$db->rollBack();
				
			throw $ex;
		}

	}

	public function delete($memoBaseId) {
		return self::deleteDb(parent::transConnect(), $memoBaseId);
	}

	public static function deleteDb($db, $memoBaseId) {

		$db->beginTransaction();
			
		try {
				
			$memoBase = self::getDb($db, $memoBaseId);
			$memos = MemoAdapter::getAllDb($db, $memoBaseId);

			foreach($memos as $memo) {
				MemoAdapter::deleteDb($db, $memo->MemoId);
			}
				
			$query = "DELETE FROM
					memoling_MemoBases
					WHERE
					MemoBaseId = :MemoBaseId";
				
			$stm = $db->prepare($query);
			$stm->bindParam(":MemoBaseId", $memoBaseId);
			if(!$stm->execute()) {
				throw new SqlException("Failed to delete MemoBase", $db);
			}
				
			$db->commit();
		} catch(Exception $ex) {
			$db->rollBack();
				
			throw $ex;
		}
	}

}

?>