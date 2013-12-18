<?php

class Word {
	
	public $WordId;
	public $Word;
	public $LanguageIso639;	
	public $Description;
	public $Class;
	public $Phonetic;
	
	public function decode($json) {
		$obj = is_object($json)?$json:json_decode($json);
		
		if($obj == null) {
			throw new JsonException();
		}
		
		$this->WordId = isset($obj->wordId)?$obj->wordId:null;
		$this->Word = isset($obj->word)?$obj->word:null;
		$this->LanguageIso639 = isset($obj->languageIso639)?$obj->languageIso639:null;		
		$this->Description = isset($obj->description)?$obj->description:"";	
		$this->Class = isset($obj->class)?$obj->class:"";
		$this->Phonetic = isset($obj->phonetic)?$obj->phonetic:"";	
	}
	
	public function encode() {
		$builder = new JsonBuilder();
		
		$builder->put("word", $this->Word, true);
		$builder->put("languageIso639", $this->LanguageIso639, true);

		$extra = "";
		if($this->Class != "") {
			$extra .= $this->Class . ", ";
		}
		if($this->Phonetic != "") {
			$extra .= $this->Phonetic . ", ";
		}
		
		$builder->put("description", $extra . $this->Description, true);
		
		return $builder->__toString();
	}
}

?>