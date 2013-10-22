<?php
require_once("../Init.php");

class PublishedMemoBaseAdapter extends DbAdapter {

	protected $db;

	public function __construct() {
		$this->db = parent::connect();
	}

	public function getAll($from, $perPage) {

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

		$stm = $this->db->prepare($query);
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

		$stm = $this->db->prepare($query);
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

	public function get($id, $limit=null) {

		$base = new MemoBase();
		$published = new PublishedMemoBase();
		$published->MemoBase = $base;

		// Get Header
		$query = "SELECT
				P.PublishedMemoBaseId as P_PublishedMemoBaseId,
				P.MemoBaseId AS P_MemoBaseId,
				P.AdminsScore AS P_AdminsScore,
				P.Created AS P_Created,
				P.Description AS P_Description,
				P.Downloads AS P_Downloads,
				P.FacebookUserId AS P_FacebookUserId,
				P.MemoBaseGenreId AS P_MemoBaseGenreId,
				P.MemoBaseId AS P_MemoBaseId,
				P.UsersScore AS P_UsersScore,
				P.PrimaryLanguageAIso639 AS P_PrimaryLanguageAIso639,
				P.PrimaryLanguageBIso639 AS P_PrimaryLanguageBIso639,
				G.Genre AS G_Genre,
				Count(M.MemoBaseId) AS M_Memos,
				B.Name AS B_Name
				FROM
				memoling_PublishedMemoBases AS P
				INNER JOIN
				memoling_MemoBases AS B ON P.MemoBaseId = B.MemoBaseId
				INNER JOIN
				memoling_Memos AS M ON B.MemoBaseId = M.MemoBaseId
				INNER JOIN
				memoling_MemoBaseGenres AS G ON P.MemoBaseGenreId = G.MemoBaseGenreId
				WHERE
				P.PublishedMemoBaseId = :PublishedMemoBaseId
				GROUP BY
				M.MemoBaseId
				";
		$stm = $this->db->prepare($query);
		$stm->bindParam(":PublishedMemoBaseId", $id);
		$stm->execute();

		if($stm->rowCount() == 0) {
			return null;
		}

		$row = $stm->fetch();

		$published->PublishedMemoBaseId = $row["P_PublishedMemoBaseId"];
		$published->MemosCount = $row["M_Memos"];
		$published->AdminsScore = $row["P_AdminsScore"];
		$published->Created = $row["P_Created"];
		$published->Description = $row["P_Description"];
		$published->Downloads = $row["P_Downloads"];
		$published->FacebookUserId = $row["P_FacebookUserId"];
		$published->MemoBaseGenreId = $row["P_MemoBaseGenreId"];
		$published->MemoBaseId = $row["P_MemoBaseId"];
		$published->UsersScore = $row["P_UsersScore"];
		$published->PrimaryLanguageAIso639 = $row["P_PrimaryLanguageAIso639"];
		$published->PrimaryLanguageBIso639 = $row["P_PrimaryLanguageBIso639"];
		$published->MemoBaseGenre = new MemoBaseGenre();
		$published->MemoBaseGenre->MemoBaseGenreId = $row["P_MemoBaseGenreId"];
		$published->MemoBaseGenre->Genre = $row["G_Genre"];

		$baseId = $row["P_MemoBaseId"];
		$base->Name = $row["B_Name"];
		$base->MemoBaseId = $row["P_MemoBaseId"];

		// Get Words
		$query = "SELECT
				M.MemoId AS M_MemoId,
				M.MemoBaseId AS M_MemoBaseId,
				WA.WordId AS W_WordAId,
				WA.Word AS W_WordA,
				WA.Description AS W_DescriptionA,
				WA.Class AS W_ClassA,
				WA.Phonetic AS W_PhoneticA,
				WA.LanguageIso639 AS W_WordALang,
				WB.WordId AS W_WordBId,
				WB.Word AS W_WordB,
				WB.LanguageIso639 AS W_WordBLang,
				WB.Description AS W_DescriptionB,
				WB.Class AS W_ClassB,
				WB.Phonetic AS W_PhoneticB
				FROM
				memoling_Memos AS M
				INNER JOIN
				memoling_Words AS WA ON M.WordAId = WA.WordId
				INNER JOIN
				memoling_Words AS WB ON M.WordBId = WB.WordId
				WHERE
				M.MemoBaseId = :MemoBaseId
				";

		if($limit != null) {
			$query .= " LIMIT 0, :Limit";
		}

		$stm = $this->db->prepare($query);
		$stm->bindParam(":MemoBaseId", $baseId);
		if($limit != null) {
			$stm->bindParam(":Limit", $limit, PDO::PARAM_INT);
		}

		$stm->execute();

		$list = array();
		while($row = $stm->fetch()) {
			$obj = new Memo();
			$obj->MemoId = $row["M_MemoId"];
			$obj->MemoBaseId = $row["M_MemoBaseId"];
			
			$obj->WordA = new Word();
			$obj->WordA->WordId = $row["W_WordAId"];
			$obj->WordAId = $obj->WordA->WordId;
			$obj->WordA->Word = $row["W_WordA"];
			$obj->WordA->Description = $row["W_DescriptionA"];
			$obj->WordA->LanguageIso639 = $row["W_WordALang"];
			$obj->WordA->Class = $row["W_ClassA"];
			$obj->WordA->Phonetic = $row["W_PhoneticA"];
			
			$obj->WordB = new Word();
			$obj->WordB->WordId = $row["W_WordBId"];
			$obj->WordBId = $obj->WordB->WordId;
			$obj->WordB->Word = $row["W_WordB"];
			$obj->WordB->Description =$row["W_DescriptionB"];
			$obj->WordB->LanguageIso639 = $row["W_WordBLang"];
			$obj->WordB->Class = $row["W_ClassB"];
			$obj->WordB->Phonetic = $row["W_PhoneticB"];
			
			$list[] = $obj;
		}
		$base->Memos = $list;
		
		return $published;
	}

	public function updateDownload($id) {

		$query = "UPDATE
				memoling_PublishedMemoBases
				SET
				Downloads = Downloads+1
				WHERE
				PublishedMemoBaseId = :PublishedMemoBaseId";
		$stm = $this->db->prepare($query);
		$stm->bindParam(":PublishedMemoBaseId", $id);
		$stm->execute();
	}

	public function getDownload($id) {
		$result = $this->get($id);
		$this->updateDownload($id);
		return $result;
	}

	public function getSample($id, $sampleSize) {
		$result = $this->get($id, $sampleSize);
		return $result;
	}

	public function upload($publishedMemoBase) {

		$this->db->beginTransaction();

		try {
			// Insert MemoBase
			$baseId = Helper::newGuid();
			$query = "INSERT INTO
					memoling_MemoBases
					VALUES(:Bid,:Name,CURRENT_TIMESTAMP,0)";
			$stm = $this->db->prepare($query);
			$stm->bindParam(":Bid", $baseId);
			$stm->bindParam(":Name", $publishedMemoBase->MemoBase->Name);
			if(!$stm->execute()) {
				throw new SqlException("Failed to create MemoBase", $this->db);
			}

			// Insert Memos
			foreach($publishedMemoBase->MemoBase->Memos as $memo) {
				// Insert Words
				$wordAId = Helper::newGuid();
				$wordBId = Helper::newGuid();

				if(strcmp($memo->WordA->LanguageIso639, $memo->WordB->LanguageIso639) > 0) {
					$tmp = $memo->wordA;
					$memo->WordA = $memo->WordB;
					$memo->WordB = tmp;
				}

				$query = "INSERT INTO
						memoling_Words (WordId, LanguageIso639, Word, Description)
						VALUES(:Wid,:Lang,:Word,:Description)";
				$stm = $this->db->prepare($query);
				$stm->bindParam(":Wid", $wordAId);
				$stm->bindParam(":Lang", $memo->WordA->LanguageIso639);
				$stm->bindParam(":Word", $memo->WordA->Word);
				$stm->bindParam(":Description", $memo->WordA->Description);
				if(!$stm->execute()) {
					$this->db->rollBack();
					return false;
				}

				$query = "INSERT INTO
						memoling_Words (WordId, LanguageIso639, Word, Description)
						VALUES(:Wid,:Lang,:Word,:Description)";
				$stm = $this->db->prepare($query);
				$stm->bindParam(":Wid", $wordBId);
				$stm->bindParam(":Lang", $memo->WordB->LanguageIso639);
				$stm->bindParam(":Word", $memo->WordB->Word);
				$stm->bindParam(":Description", $memo->WordB->Description);
				if(!$stm->execute()) {
					throw new SqlException("Failed to create new Word", $this->db);
				}

				// Insert Memo
				$memoId = Helper::newGuid();
				$query = "INSERT INTO
						memoling_Memos
						VALUES(:Mid,:Bid,:WAid,:WBid,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0,0,0,0)";
				$stm = $this->db->prepare($query);
				$stm->bindParam(":Mid", $memoId);
				$stm->bindParam(":Bid", $baseId);
				$stm->bindParam(":WAid", $wordAId);
				$stm->bindParam(":WBid", $wordBId);

				if(!$stm->execute()) {
					throw new SqlException("Failed to create new Memo", $this->db);
				}
			}

			// Insert PublishedMemoBase
			$publishedId = Helper::newGuid();
			$query = "INSERT INTO
					memoling_PublishedMemoBases
					VALUES(:Pid,:Fid,:Bid,:Gid,:Desc,0,0,0,CURRENT_TIMESTAMP,:PrimaryLanguageA,:PrimaryLanguageB)";
			$stm = $this->db->prepare($query);
			$stm->bindParam(":Pid", $publishedId);
			$stm->bindParam(":Fid", $publishedMemoBase->FacebookUserId);
			$stm->bindParam(":Bid", $baseId);
			$stm->bindParam(":Gid", $publishedMemoBase->MemoBaseGenreId);
			$stm->bindParam(":Desc",$publishedMemoBase->Description);
			$stm->bindParam(":PrimaryLanguageA", $publishedMemoBase->PrimaryLanguageAIso639);
			$stm->bindParam(":PrimaryLanguageB", $publishedMemoBase->PrimaryLanguageBIso639);

			if(!$stm->execute()) {
				throw new SqlException("Failed to create PublishedMemoBase", $this->db);
			}

			$this->db->commit();
		} catch(Exception $ex) {
			$this->db->rollBack();
			Log::save("Failed to upload PublishedMemoBase", $ex, Log::PRIO_HIGH);
			return false;
		}
		return true;
	}

	public function delete($id) {

		$this->db->beginTransaction();

		try {
			$published = $this->get($id);
				
			foreach($published->MemoBase->Memos as $memo) {
				$query = "DELETE FROM
						memoling_Words
						WHERE
						WordId = :WordIdA OR WordId = :WordIdB
						";

				$stm = $this->db->prepare($query);
				$stm->bindParam(":WordIdA", $memo->WordAId);
				$stm->bindParam(":WordIdB", $memo->WordBId);
				if(!$stm->execute()) {
					throw new Exception("Failed to delete Word");
				}
				
				$query = "DELETE FROM
						memoling_Memos
						WHERE
						MemoId = :MemoId
						";

				$stm = $this->db->prepare($query);
				$stm->bindParam(":MemoId", $memo->MemoId);
				
				if(!$stm->execute()) {
					throw new Exception("Failed to delete Memo");
				}
			}
				
			$query = "DELETE FROM
					memoling_PublishedMemoBases
					WHERE
					PublishedMemoBaseId = :PublishedMemoBaseId";
			$stm = $this->db->prepare($query);
			$stm->bindParam(":PublishedMemoBaseId", $id);			
			if(!$stm->execute()) {
				throw new Exception("Failed to delete PublishedMemoBase");
			}

			$query = "DELETE FROM
					memoling_MemoBases
					WHERE
					MemoBaseId = :MemoBaseId";
			$stm = $this->db->prepare($query);
			$stm->bindParam(":MemoBaseId", $published->MemoBaseId);
			if(!$stm->execute()) {
				throw new Exception("Failed to delete MemoBase");
			}

			$this->db->commit();
		} catch(Exception $ex) {
			$this->db->rollBack();
			Log::save("Failed to delete PublishedMemoBase", $ex, Log::PRIO_HIGH);
			return false;
		}
		return true;
	}
}


?>