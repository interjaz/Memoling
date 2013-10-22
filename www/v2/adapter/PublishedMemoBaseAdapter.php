<?php

class PublishedMemoBaseAdapter extends DbAdapter {

	public function getAll($from, $perPage) {

		$db = parent::connect();
		
		$query = "SELECT
				P.*, Count(M.MemoBaseId) AS Memos
				FROM
				memoling_PublishedMemoBases AS P
				INNER JOIN
				memoling_MemoBases AS B ON P.MemoBaseId = B.MemoBaseId
				INNER JOIN
				memoling_Memos AS M ON B.MemoBaseId = M.MemoBaseId
				GROUP BY
				M.MemoBAseId
				LIMIT :from,:perPage";

		$stm = $db->prepare($query);
		$stm->bindParam(':from', $from, PDO::PARAM_INT);
		$stm->bindParam(':perPage', $perPage, PDO::PARAM_INT);
		$stm->execute();

		$list = array();
		while($row = $stm->fetch()) {
			$obj = new PublishedMemoBase();
			$obj->AdminsScore = $row["AdminsScore"];
			$obj->Created = $row["Created"];
			$obj->Description = $row["Description"];
			$obj->Downloads = $row["Downloads"];
			$obj->FacebookUserId = $row["FacebookUserId"];
			$obj->MemoBaseGenreId = $row["MemoBaseGenreId"];
			$obj->MemoBaseId = $row["MemoBaseId"];
			$obj->PublishedMemoBaseId = $row["PublishedMemoBaseId"];
			$obj->UsersScore = $row["UsersScore"];
			$obj->MemosCount = $row["Memos"];

			$list[] = $obj;
		}

		return $list;
	}

	public function search($keyword, $genreId, $languageA, $languageB, $from, $perPage) {

		$db = parent::connect();
		
		if($languageA != null && $languageB != null && strcmp($languageA, $languageB) > 0) {
			$tmp = $languageA;
			$languageA = $languageB;
			$languageB = $tmp;
		}

		$query = "SELECT
				P.*, B.Name, Count(M.MemoBaseId) AS Memos
				FROM
				memoling_PublishedMemoBases AS P
				INNER JOIN
				memoling_MemoBases AS B ON P.MemoBaseId = B.MemoBaseId
				INNER JOIN
				memoling_Memos AS M ON B.MemoBaseId = M.MemoBaseId
				WHERE
				(
				B.Name LIKE :keyword OR
				P.Description LIKE :keyword
				)";

		if($genreId != null) {
			$query .= "
					AND (
					P.MemoBaseGenreId = :GenreId
					)";
		}

		if($languageA != null && $languageB != null) {

			$query .=
			"
					AND
					(
					P.PrimaryLanguageAIso639 = :PrimaryLanguageA AND
					P.PrimaryLanguageBIso639 = :PrimaryLanguageB
					)";

		} else if($languageA != null || $languageB != null) {
			if($languageA == null) {
				$languageA = $languageB;
			} else {
				$languageB = $languageA;
			}

			$query .= "
					AND
					(
					P.PrimaryLanguageAIso639 = :PrimaryLanguageA OR
					P.PrimaryLanguageBIso639 = :PrimaryLanguageB
					)";
		}

		$query .= "
				GROUP BY
				M.MemoBAseId
				ORDER BY
				P.Downloads DESC
				LIMIT :from,:perPage";

		$stm = $db->prepare($query);
		$stm->bindParam(':from', $from, PDO::PARAM_INT);
		$stm->bindParam(':perPage', $perPage, PDO::PARAM_INT);
		$stm->bindValue(':keyword', '%' . $keyword . '%');

		if($genreId != null) {
			$stm->bindParam(":GenreId", $genreId);
		}

		if($languageA != null && $languageB != null) {
			$stm->bindParam(":PrimaryLanguageA", $languageA);
			$stm->bindParam(":PrimaryLanguageB", $languageB);
		}

		$stm->execute();

		$list = array();
		while($row = $stm->fetch()) {
			$obj = new PublishedMemoBase();
			$obj->AdminsScore = $row["AdminsScore"];
			$obj->Created = $row["Created"];
			$obj->Description = $row["Description"];
			$obj->Downloads = $row["Downloads"];
			$obj->FacebookUserId = $row["FacebookUserId"];
			$obj->MemoBaseGenreId = $row["MemoBaseGenreId"];
			$obj->MemoBaseId = $row["MemoBaseId"];
			$obj->PublishedMemoBaseId = $row["PublishedMemoBaseId"];
			$obj->UsersScore = $row["UsersScore"];
			$obj->MemosCount = $row["Memos"];
			$obj->MemoBaseGenreId = $row["MemoBaseGenreId"];
			$obj->PrimaryLanguageAIso639 = $row["PrimaryLanguageAIso639"];
			$obj->PrimaryLanguageBIso639 = $row["PrimaryLanguageBIso639"];
			$base = new MemoBase();
			$base->Name = $row["Name"];
			$obj->MemoBase = $base;

			$list[] = $obj;
		}

		return $list;
	}

	public function get($publishedMemoBaseId) {
		return self::getDb(parent::connect(), $publishedMemoBaseId);
	}

	public static function getDb($db, $publishedMemoBaseId) {

		$publishedMemoBase = new PublishedMemoBase();

		$query = "SELECT * FROM memoling_PublishedMemoBases WHERE PublishedMemoBaseId = :PublishedMemoBaseId";
		$stm = $db->prepare($query);
		$stm->bindParam("PublishedMemoBaseId", $publishedMemoBaseId);
		$stm->execute();
		
		if($stm->rowCount() == 0) {
			return null;
		}

		$row = $stm->fetch();
		
		$publishedMemoBase->PublishedMemoBaseId = $row["PublishedMemoBaseId"];
		$publishedMemoBase->MemoBaseId = $row["MemoBaseId"];
		$publishedMemoBase->MemosCount = MemoBaseAdapter::getMemoCountDb($db, $publishedMemoBase->MemoBaseId);
		$publishedMemoBase->AdminsScore = $row["AdminsScore"];
		$publishedMemoBase->Created = $row["Created"];
		$publishedMemoBase->Description = $row["Description"];
		$publishedMemoBase->Downloads = $row["Downloads"];
		$publishedMemoBase->FacebookUserId = $row["FacebookUserId"];
		$publishedMemoBase->MemoBaseGenreId = $row["MemoBaseGenreId"];
		$publishedMemoBase->UsersScore = $row["UsersScore"];
		$publishedMemoBase->PrimaryLanguageAIso639 = $row["PrimaryLanguageAIso639"];
		$publishedMemoBase->PrimaryLanguageBIso639 = $row["PrimaryLanguageBIso639"];
		$publishedMemoBase->MemoBaseGenre = new MemoBaseGenre();
		$publishedMemoBase->MemoBaseGenre->MemoBaseGenreId = $row["MemoBaseGenreId"];
		
		// Get MemoBase
		$publishedMemoBase->MemoBase = MemoBaseAdapter::getDb($db, $publishedMemoBase->MemoBaseId);
		$publishedMemoBase->MemoBase->Memos = MemoAdapter::getAllDeepDb($db, $publishedMemoBase->MemoBaseId);
		

		return $publishedMemoBase;
	}

	public function updateDownload($id) {
		$db = parent::transConnect();
		
		$query = "UPDATE
				memoling_PublishedMemoBases
				SET
				Downloads = Downloads+1
				WHERE
				PublishedMemoBaseId = :PublishedMemoBaseId";
		$stm = $db->prepare($query);
		$stm->bindParam(":PublishedMemoBaseId", $id);
		$stm->execute();
	}

	public function upload($publishedMemoBase) {
		$db = parent::transConnect();
		$db->beginTransaction();

		try {
			// Change ids
			$publishedMemoBase->PublishedMemoBaseId = Helper::newGuid();
			$publishedMemoBase->MemoBaseId = Helper::newGuid();
			$publishedMemoBase->MemoBase->MemoBaseId = $publishedMemoBase->MemoBaseId;

			foreach($publishedMemoBase->MemoBase->Memos as $memo) {
				// Insert Words
				$memo->MemoId = Helper::newGuid();
				$memo->MemoBaseId = $publishedMemoBase->MemoBaseId;
				$memo->WordA->WordId = Helper::newGuid();
				$memo->WordB->WordId = Helper::newGuid();
			}

			self::insertDb($db, $publishedMemoBase);

			$db->commit();
		} catch(Exception $ex) {
			$db->rollBack();
			Log::save("Failed to upload PublishedMemoBase", $ex, Log::PRIO_HIGH);
			return false;
		}
		return true;
	}

	public function insert($publishedMemoBase) {
		return self::insertDb(parent::transConnect(), $publishedMemoBase);
	}

	public static function insertDb($db, $publishedMemoBase) {
		$db->beginTransaction();
			
		try {

			MemoBaseAdapter::insertDb($db, $publishedMemoBase->MemoBase);
			
			// Insert PublishedMemoBase
			$query = "INSERT INTO
					memoling_PublishedMemoBases
					VALUES(:Pid,:Fid,:Bid,:Gid,:Desc,0,0,0,UTC_TIMESTAMP(),:PrimaryLanguageA,:PrimaryLanguageB)";
			$stm = $db->prepare($query);
			$stm->bindParam(":Pid", $publishedMemoBase->PublishedMemoBaseId);
			$stm->bindParam(":Fid", $publishedMemoBase->FacebookUserId);
			$stm->bindParam(":Bid", $publishedMemoBase->MemoBaseId);
			$stm->bindParam(":Gid", $publishedMemoBase->MemoBaseGenreId);
			$stm->bindParam(":Desc",$publishedMemoBase->Description);
			$stm->bindParam(":PrimaryLanguageA", $publishedMemoBase->PrimaryLanguageAIso639);
			$stm->bindParam(":PrimaryLanguageB", $publishedMemoBase->PrimaryLanguageBIso639);

			if(!$stm->execute()) {
				throw new SqlException("Failed to create PublishedMemoBase", $db);
			}

			$db->commit();

		} catch(Exception $ex) {
			$db->rollBack();
			throw $ex;
		}
	}

	public function delete($publishedMemoBaseId) {
		return self::deleteDb(parent::transConnect(), $publishedMemoBaseId);
	}

	public static function deleteDb($db, $publishedMemoBaseId) {

		$db->beginTransaction();

		try {
			$publishedMemoBase = self::getDb($db,$publishedMemoBaseId);

			$query = "DELETE FROM
					memoling_PublishedMemoBases
					WHERE
					PublishedMemoBaseId = :PublishedMemoBaseId";
			$stm = $db->prepare($query);
			$stm->bindParam(":PublishedMemoBaseId", $publishedMemoBaseId);
			if(!$stm->execute()) {
				throw new SqlException("Failed to delete PublishedMemoBase", $db);
			}

			MemoBaseAdapter::deleteDb($db, $publishedMemoBase->MemoBaseId);

			$db->commit();
		} catch(Exception $ex) {
			$db->rollBack();

			Log::save("Failed to delete PublishedMemoBase", $ex, Log::PRIO_HIGH);
			throw $ex;
		}
	}
}


?>