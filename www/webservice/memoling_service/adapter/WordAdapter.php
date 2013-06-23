<?php
require_once("../Init.php");

class WordAdapter extends DbAdapter {

	public function getAll() {
		
		$db = DbAdapter::connect();
		
		$stm = $db->prepare("SELECT * FROM Words");
		$stm->execute();
		
		$list = array();
		while($row = $stm->fetch()) {
			$obj = new Word();
			$obj->Language = $row["Language"];
			$obj->Word = $row["Word"];
			$obj->WordId = $row["WordId"];
			$list[] = $obj;
		}
		
		return $list;
	
	}

}

?>