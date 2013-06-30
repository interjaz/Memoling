<?php
require_once("../Init.php");

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
	
	public function get($id, $limit=null) {
		
		$db = parent::connect();

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
					Count(M.MemoBaseId) AS M_Memos,
					B.Name AS B_Name
				  FROM
					memoling_PublishedMemoBases AS P
				  INNER JOIN 
					memoling_MemoBases AS B ON P.MemoBaseId = B.MemoBaseId
				  INNER JOIN
					memoling_Memos AS M ON B.MemoBaseId = M.MemoBaseId
				  WHERE 
					P.PublishedMemoBaseId = :PublishedMemoBaseId
				  GROUP BY
					M.MemoBaseId
				";
		$stm = $db->prepare($query);
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
		
		$baseId = $row["P_MemoBaseId"];
		$base->Name = $row["B_Name"];
				
		// Get Words
		$query = "SELECT
					M.MemoId AS M_MemoId,
					M.MemoBaseId AS M_BaseId,
					WA.Word AS W_WordA,
					WA.LanguageIso639 AS W_WordALang,
					WB.Word AS W_WordB,
					WB.LanguageIso639 AS W_WordBLang
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
		
		$stm = $db->prepare($query);
		$stm->bindParam(":MemoBaseId", $baseId);
		if($limit != null) {
			$stm->bindParam(":Limit", $limit, PDO::PARAM_INT);
		}
		
		$stm->execute();
		
		$list = array();
		while($row = $stm->fetch()) {
			$obj = new Memo();
			$obj->WordA = new Word();
			$obj->WordA->Word = $row["W_WordA"];
			$obj->WordA->LanguageIso639 = $row["W_WordALang"];
			$obj->WordB = new Word();
			$obj->WordB->Word = $row["W_WordB"];
			$obj->WordB->LanguageIso639 = $row["W_WordBLang"];
			$list[] = $obj;
		}
		$base->Memos = $list;			
		
		return $published;		
	}
	
	public function updateDownload($id) {

		$db = parent::connect();
		
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
		
		$db = parent::connect();
		
		$db->beginTransaction();
		
		try { 			
			// Insert MemoBase
			$baseId = Helper::newGuid();
			$query = "INSERT INTO
						memoling_MemoBases
					  VALUES(:Bid,:Name,CURRENT_TIMESTAMP,0)";
			$stm = $db->prepare($query);
			$stm->bindParam(":Bid", $baseId);
			$stm->bindParam(":Name", $publishedMemoBase->MemoBase->Name);
			if(!$stm->execute()) {
				throw new SqlException("Failed to create MemoBase", $db);
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
							memoling_Words
						   VALUES(:Wid,:Lang,:Word)";
			    $stm = $db->prepare($query);
				$stm->bindParam(":Wid", $wordAId);
				$stm->bindParam(":Lang", $memo->WordA->LanguageIso639);
				$stm->bindParam(":Word", $memo->WordA->Word);
				if(!$stm->execute()) {
					$db->rollBack();
					return false;
				}
				
				$query = "INSERT INTO
							memoling_Words
						   VALUES(:Wid,:Lang,:Word)";
			    $stm = $db->prepare($query);
				$stm->bindParam(":Wid", $wordBId);
				$stm->bindParam(":Lang", $memo->WordB->LanguageIso639);
				$stm->bindParam(":Word", $memo->WordB->Word);
				if(!$stm->execute()) {
					throw new SqlException("Failed to create new Word", $db);
				}
				
				// Insert Memo
				$memoId = Helper::newGuid();
				$query = "INSERT INTO
							memoling_Memos
						   VALUES(:Mid,:Bid,:WAid,:WBid,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,0,0,0,0)";
			    $stm = $db->prepare($query);
				$stm->bindParam(":Mid", $memoId);
				$stm->bindParam(":Bid", $baseId);
				$stm->bindParam(":WAid", $wordAId);
				$stm->bindParam(":WBid", $wordBId);
				
				if(!$stm->execute()) {
					throw new SqlException("Failed to create new Memo", $db);
				}
			}
			
			// Insert PublishedMemoBase
			$publishedId = Helper::newGuid();
			$query = "INSERT INTO
						memoling_PublishedMemoBases
					  VALUES(:Pid,:Fid,:Bid,:Gid,:Desc,0,0,0,CURRENT_TIMESTAMP,:PrimaryLanguageA,:PrimaryLanguageB)";
			$stm = $db->prepare($query);
			$stm->bindParam(":Pid", $publishedId);
			$stm->bindParam(":Fid", $publishedMemoBase->FacebookUserId);
			$stm->bindParam(":Bid", $baseId);
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
			Log::save("Failed to upload PublishedMemoBase", $ex, Log::PRIO_HIGH);
			return false;
		}
		return true;
	}
}


?>