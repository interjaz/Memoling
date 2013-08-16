<?php
require_once("../Init.php");

class Word {
	public $WordId;
	public $Word;
	public $LanguageIso639;	
	public $Description;
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
		
		if($obj == null) {
			throw new JsonException();
		}
		
		$this->WordId = isset($obj->wordId)?$obj->wordId:null;
		$this->Word = isset($obj->word)?$obj->word:null;
		$this->LanguageIso639 = isset($obj->languageIso639)?$obj->languageIso639:null;		
		$this->Description = isset($obj->description)?$obj->description:"";
	}
	
	public function encode() {
		$builder = new JsonBuilder();
		
		$builder->put("word", $this->Word);
		$builder->put("description", $this->Description);
		$builder->put("languageIso639", $this->LanguageIso639);
		
		return $builder->__toString();
	}
}

?>