<?php

class TranslatedSentence {
	
	public $Original;
	public $Translated;
	
	public function encode() {
		$builder = new JsonBuilder();
	
		$builder->put("original", $this->Original);
		$builder->put("translated", $this->Translated);
	
		return $builder->__toString();
	}
	
}