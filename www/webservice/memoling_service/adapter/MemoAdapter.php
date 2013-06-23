<?php
require_once("../Init.php");

class MemoAdapter extends DbAdapter {
	
	public function getAll() {
		
		$db = parent::connect();
		$stm = $db->prepare("SELECT * FROM Memos");
		$stm->execute();
	
		$list = array();
		while($row = $stm->fetch()) {
			$obj = new Memo();
			$obj->CorrectAnsweredWordA = $row["CorrectAnsweredWordA"];
			$obj->CorrectAnsweredWordB = $row["CorrectAnsweredWordB"];
			$obj->Created = $row["CreateAt"];
			$obj->Displayed = $row["Displayed"];
			$obj->LastReviewed = $row["LastReviewed"];
			$obj->MemoBaseId = $row["MemoBaseId"];
			$obj->MemoId = $row["MemoId"];
			$obj->WordAId = $row["WordAId"];
			$obj->WordBId = $row["WordBId"];
			$obj->Active = $row["Active"];
			$list[] = $obj;
		}
		
		return $list;
	}
	
	public function get($memoId) {
		
	}
	
}

?>