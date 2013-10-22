<?php

class Sentence {
	
	public $Sentence;
	public $LanguageIso639;
	
	public function encode() {
		$builder = new JsonBuilder();
	
		$builder->put("sentence", $this->Sentence, true);
		$builder->put("languageIso639", $this->LanguageIso639);
	
		return $builder->__toString();
	}
	
}

?>