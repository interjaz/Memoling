<?php
require_once("../Init.php");

class MemoBaseAdapter extends DbAdapter {

	public function getAll() {
		
		$db = DbAdapter::connect();
		
		$stm = $db->prepare("SELECT * FROM MemoBases");
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

}

?>