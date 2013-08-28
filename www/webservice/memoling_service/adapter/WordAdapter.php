<?php
require_once("../Init.php");

class WordAdapter extends DbAdapter {

	protected $db;
	
	public function __construct() {
		$this->db = parent::connect();
	}
	
	public function getAll() {
		
		$stm = $this->db->prepare("SELECT * FROM memoling_Words");
		$stm->execute();
		
		$list = array();
		while($row = $stm->fetch()) {
			$obj = new Word();
			$obj->Language = $row["Language"];
			$obj->Word = $row["Word"];
			$obj->WordId = $row["WordId"];
			$obj->Class = $row["Class"];
			$obj->Phonetic = $row["Phonetic"];
			
			$obj->Description = $additional . $row["Description"];
			$list[] = $obj;
		}
		
		return $list;
	
	}

}

?>