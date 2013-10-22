<?php
require_once("../Init.php");

class MemoBaseGenreAdapter extends DbAdapter {
	
	protected $db;
	
	public function __construct() {
		$this->db = parent::connect();
	}
	
	public function getAll() {
		
		$stm = $this->db->prepare("SELECT * FROM memoling_MemoBaseGenres");
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