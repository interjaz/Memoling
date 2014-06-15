<?php

class WordListController extends Controller {

    public function get() {
        $word = $_GET['word'];
        $language = $_GET['lang'];
        
        $adapter = new WordListAdapter();
        $result = $adapter->getStartingWith($word, $language, 10);
        
		echo JsonBuilder::arrayToJson($result);
    }
    
}


new WordListController();

?>