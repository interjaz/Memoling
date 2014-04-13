<?php

class MemoBase {
	public $MemoBaseId;
	public $Name;
	public $Created;
	public $Active;
	public $FacebookUserId;
    
	public $Memos;
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
		
		if($obj == null) {
			throw new JsonException();
		}
		
		$this->MemoBaseId = isset($obj->memoBaseId)?$obj->memoBaseId:null;
		$this->Name = isset($obj->name)?$obj->name:null;
		$this->Created = isset($obj->created)?gmdate("Y-m-d\TH:i:s",$obj->created):null;
		$this->Active = isset($obj->active)?strcasecmp($obj->active, "true")==0 || $obj->active == 1:null;
		
        //var_dump($obj->created);
        //var_dump($this);
        //exit;
        
        $this->Memos = null;
        
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
		
        $builder->put("memoBaseId", $this->MemoBaseId);
        $builder->put("created", strtotime($this->Created));
		$builder->put("name", $this->Name);
        $builder->put("active", $this->Active == 1 ? "true" : "false");
		$builder->put("memos", $this->Memos);
        
		return $builder->__toString();
	}
}
	
?>