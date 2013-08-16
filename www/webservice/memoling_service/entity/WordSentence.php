<?php
require_once("../Init.php");

class WordSentence {
	
	public $Word;
	public $Sentences;


	public function encode() {
		$builder = new JsonBuilder();
	
		$builder->put("word", $this->Word);
		$builder->put("sentences", JsonBuilder::arrayToJson($this->Sentences));
	
		return $builder->__toString();
	}
}

?>