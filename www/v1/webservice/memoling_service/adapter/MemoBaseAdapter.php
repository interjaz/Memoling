<?php
require_once("../Init.php");

class MemoBaseAdapter extends DbAdapter {

	protected $db;
	
	public function __construct() {
		$this->db = parent::connect();
	}
	
	public function getAll() {
		
		$stm = $this->db->prepare("SELECT * FROM memoling_MemoBases");
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