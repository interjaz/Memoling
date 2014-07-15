<?php

class WordListController extends Controller {

    public function get() {
        $word = $_GET['word'];
        $language = $_GET['lang'];
        
        $words = explode(' ', $word);
        $lastWord = end($words);
        
        
        $adapter = new WordListAdapter();
        $results = $adapter->getStartingWith($lastWord, $language, 10);
        
        $wordBase = "";
        
        if(sizeof($words) > 0 ) {
            $wordBase = implode(' ', array_slice($words, 0, sizeof($words)-1)) . ' ';
        }
        
        for($i=0;$i<sizeof($results);$i++) {
            $results[$i]->Word = $wordBase . $results[$i]->Word;
        }
        
		echo JsonBuilder::arrayToJson($results);
    }
    
}


new WordListController();

?>