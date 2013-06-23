<?php
require_once("../Init.php");

class MemoBase {
	public $MemoBaseId;
	public $Name;
	public $Created;
	public $Active;
	
	public $Memos;
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
		
		if($obj == null) {
			throw new JsonException();
		}
		
		$this->MemoBaseId = isset($obj->memoBaseId)?$obj->memoBaseId:null;
		$this->Name = isset($obj->name)?$obj->name:null;
		$this->Created = isset($obj->created)?$obj->created:null;
		$this->Active = isset($obj->active)?$obj->active:null;
		
		if(isset($obj->memos)) {
			
			$Memos = array();
			foreach($obj->memos as $jMemo) {
				$memo = new Memo();
				$memo->decode($jMemo);
				$Memos[] = $memo;
			}
			
			$this->Memos = $Memos;
		}
		
	}
	
	public function encode() {
		$builder = new JsonBuilder();
		
		$builder->put("name", $this->Name);
		
		return $builder->__toString();
	}
}
	
?>