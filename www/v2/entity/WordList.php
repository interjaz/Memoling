<?php

class WordList {
 
    public $Word;
    public $LanguageIso639;
 
    
    public function encode() {
		$builder = new JsonBuilder();    
        
        $builder->put("Word", $this->Word);
        $builder->put("LanguageIso639", $this->LanguageIso639);
        
        return $builder->__toString();
    }
    
}


?>