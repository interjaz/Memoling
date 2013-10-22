<?php

class PublishedMemoAdapter extends DbAdapter {

	// Assign artifical MemoBase for published ones
	const PublishedMemoBaseId = "2f775fe4-7b31-499d-9b55-d9d0bf3894a7";

	public function get($id) {

		$db = parent::connect();
		
		$memo = new Memo();
		$user = new FacebookUser();
		$wordA = new Word();
		$wordB = new Word();
		$memo->WordA = $wordA;
		$memo->WordB = $wordB;

		$published = new PublishedMemo();
		$published->Memo = $memo;
		$published->FacebookUser = $user;

		// Get Header
		$query = "SELECT
				P.PublishedMemoId as P_PublishedMemoId,
				P.MemoId AS P_MemoId,
				P.Created AS P_Created,
				P.FacebookUserId AS P_FacebookUserId,
				M.WordAId AS M_WordAId,
				M.WordBId AS M_WordBId,
				WA.Word AS WA_Word,
				WA.LanguageIso639 AS WA_LanguageIso639,
				WA.Description AS WA_Description,
				WA.Class AS WA_Class,
				WA.Phonetic AS WA_Phonetic,
				WB.Word AS WB_Word,
				WB.LanguageIso639 AS WB_LanguageIso639,
				WB.Description AS WB_Description,
				WB.Class AS WB_Class,
				WB.Phonetic AS WB_Phonetic,
				F.Name AS F_Name,
				F.Link AS F_Link
				FROM
				memoling_PublishedMemos AS P
				INNER JOIN
				memoling_Memos AS M ON P.MemoId = M.MemoId
				INNER JOIN
				memoling_Words AS WA ON M.WordAId = WA.WordId
				INNER JOIN
				memoling_Words AS WB ON M.WordBId = WB.WordId
				INNER JOIN
				memoling_FacebookUsers AS F ON P.FacebookUserId = F.FacebookUserId
				WHERE
				P.PublishedMemoId = :PublishedMemoId

				";
		$stm = $db->prepare($query);
		$stm->bindParam(":PublishedMemoId", $id);
		$stm->execute();


		if($stm->rowCount() == 0) {
			return null;
		}

		$row = $stm->fetch();

		$published->PublishedMemoId = $row["P_PublishedMemoId"];
		$published->Created = $row["P_Created"];
		$published->MemoId = $row["P_MemoId"];
		$published->FacebookUserId = $row["P_FacebookUserId"];

		$memo->MemoId = $row["P_MemoId"];
		$memo->WordAId = $row["M_WordAId"];
		$memo->WordBId = $row["M_WordBId"];

		$user->Name = $row["F_Name"];
		$user->Link = $row["F_Link"];

		$wordA->Word  = $row["WA_Word"];
		$wordA->WordId = $row["M_WordAId"];
		$wordA->LanguageIso639  = $row["WA_LanguageIso639"];
		$wordA->Description = $row["WA_Description"];
		$wordA->Phonetic = $row["WA_Phonetic"];
		$wordA->Class = $row["WA_Class"];

		$wordB->Word  = $row["WB_Word"];
		$wordB->WordId = $row["M_WordBId"];
		$wordB->LanguageIso639  = $row["WB_LanguageIso639"];
		$wordB->Description = $row["WB_Description"];
		$wordB->Phonetic = $row["WB_Phonetic"];
		$wordB->Class = $row["WB_Class"];

		return $published;
	}

	public function uploadShare($publishedMemo) {
		$db = parent::transConnect();

		try {
			$db->beginTransaction();	
			
			// Change Ids
			$publishedMemo->PublishedMemoId = Helper::newGuid();
			$publishedMemo->Memo->MemoBaseId = self::PublishedMemoBaseId;
			$publishedMemo->MemoId = Helper::newGuid();
			$publishedMemo->Memo->MemoId = $publishedMemo->MemoId ;
			$publishedMemo->Memo->WordA->WordId = Helper::newGuid();
			$publishedMemo->Memo->WordB->WordId = Helper::newGuid();
				
			self::insertDb($db, $publishedMemo);
				
			$db->commit();
				
			// Return new id
			return $publishedMemo->PublishedMemoId;

		} catch(Exception $ex) {
			$db->rollBack();
			Log::save("Failed to uploadShare Memo", $ex, Log::PRIO_HIGH);
		}
	}

	public static function insertDb($db, $publishedMemo) {

		$db->beginTransaction();
			
		try {

			MemoAdapter::insertDb($db, $publishedMemo->Memo);

			$query = "INSERT INTO memoling_PublishedMemos VALUES(
					:PublishedMemoId, :FacebookUserId, :MemoId, UTC_TIMESTAMP())
					";

			$stm = $db->prepare($query);
			$stm->bindParam("PublishedMemoId", $publishedMemo->PublishedMemoId);
			$stm->bindParam("FacebookUserId", $publishedMemo->FacebookUserId);
			$stm->bindParam("MemoId", $publishedMemo->Memo->MemoId);

			if(!$stm->execute()) {
				throw new SqlException("Failed to create new PublishedMemo", $db);
			}

			$db->commit();
		} catch(Exception $ex) {
			$db->rollBack();

			throw $ex;
		}

	}

	public function delete($id) {

		$db = parent::transConnect();
		$db->beginTransaction();

		try {
			$published = $this->get($id);

			foreach($published->MemoBase->Memos as $memo) {
				$query = "DELETE FROM
						memoling_Words
						WHERE
						WordId = :WordIdA OR WordId = :WordIdB
						";

				$stm = $db->prepare($query);
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

				$stm = $db->prepare($query);
				$stm->bindParam(":MemoId", $memo->MemoId);

				if(!$stm->execute()) {
					throw new Exception("Failed to delete Memo");
				}
			}

			$query = "DELETE FROM
					memoling_PublishedMemoBases
					WHERE
					PublishedMemoBaseId = :PublishedMemoBaseId";
			$stm = $db->prepare($query);
			$stm->bindParam(":PublishedMemoBaseId", $id);
			if(!$stm->execute()) {
				throw new Exception("Failed to delete PublishedMemoBase");
			}

			$query = "DELETE FROM
					memoling_MemoBases
					WHERE
					MemoBaseId = :MemoBaseId";
			$stm = $db->prepare($query);
			$stm->bindParam(":MemoBaseId", $published->MemoBaseId);
			if(!$stm->execute()) {
				throw new Exception("Failed to delete MemoBase");
			}

			$db->commit();
		} catch(Exception $ex) {
			$db->rollBack();
			Log::save("Failed to delete PublishedMemoBase", $ex, Log::PRIO_HIGH);
			return false;
		}
		return true;
	}
}


?>