<?php
require_once("../Init.php");

class MemoBaseGenreAdapter extends DbAdapter {
	
	public function getAll() {
		
		$db = parent::connect();
		$stm = $db->prepare("SELECT * FROM memoling_MemoBaseGenres");
		$stm->execute();
		
		$list = array();
		
		while($row = $stm->fetch()) {
			$obj = new MemoBaseGenre();
			$obj->MemoBaseGenreId = $row["MemoBaseGenreId"];
			$obj->Genre = $row["Genre"];
			$list[] = $obj;
		}
		
		return $list;
	}
	
	
}