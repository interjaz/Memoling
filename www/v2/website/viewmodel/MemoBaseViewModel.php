<?php

class MemoBaseViewModel {
	
    public $MemoBaseId;
	public $Name;
	public $Languages;
	public $Count;
	
    
    public function encode() {
        
		$builder = new JsonBuilder();
	
		$builder->put("memoBaseId", $this->MemoBaseId, true);
		$builder->put("name", $this->Name, true);
		$builder->put("languages", $this->Languages, true);
		$builder->put("count", $this->Count, true);
        
		return $builder->__toString();   
    }
}

?>