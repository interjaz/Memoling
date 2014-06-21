<?php

class WordListAdapter extends DbAdapter  {
    
    public function getStartingWith($startsWith, $languageIso639, $limit) {
        return self::getStartingWithDb(parent::connect(), $startsWith, $languageIso639, $limit);
    }

    public static function getStartingWithDb($db, $startsWith, $languageIso639, $limit) {
        
		$query = "SELECT Word, LanguageIso639 FROM memoling_WordLists WHERE Word LIKE :Word AND LanguageIso639 = :LanguageIso639 LIMIT 0,:Limit";

        $stm = $db->prepare($query);
        $stm->bindValue("Word", $startsWith . "%");
        $stm->bindParam("LanguageIso639", $languageIso639);
        $stm->bindParam(":Limit", $limit, PDO::PARAM_INT);
        
		$stm->execute();

		$list = array();
		while($row = $stm->fetch()) {
			$obj = self::bindWordList($row);
			$list[] = $obj;
		}

		return $list;   
    }
    
    private static function bindWordList($row, $prefix="") {
        $obj = new WordList();
        $obj->Word = $row[$prefix."Word"];
        $obj->LanguageIso639 = $row[$prefix."LanguageIso639"];
        return $obj;
    }
}

?>