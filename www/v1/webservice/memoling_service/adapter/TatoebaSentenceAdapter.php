<?php
require_once("../Init.php");

class TatoebaSentenceAdapter extends DbAdapter {

	protected $db;

	public function __construct() {
		$this->db = parent::connect();
	}

	public static function getSentenceIndexes($sentence, $language) {

		$words = explode(" ", strtolower($sentence));
		$filtered = array();

		// Sort desc by length, filtering will be faster on indexes
		usort($words,'sortByLength');

		for($i=0;$i<sizeof($words);$i++) {
			$list = TatoebaSentenceAdapter::getWordIndexes($words[$i], $language);
			if($i == 0) {
				$filtered = $list;
			} else if($list == null || sizeof($filtered) == 0) {
				return null;
			} else {
				$new = array();

				foreach($list as $index) {
					if(in_array($index, $filtered)) {
						$new[] =  $index;
					}
				}

				$filtered = $new;
			}
		}

		if(sizeof($filtered) == 0) {
			return null;
		} else {
			$str = "";
			foreach($filtered as $val) {
				$str .= $val . ",";
			}
				
			return substr($str, 0, strlen($str)-1);
		}
	}

	public static function getWordIndexes($word, $language) {
		$language = LanguageTranslations::convertIso6391To6393($language);

		// Take only first word
		$letter = $word[0];

		if($letter < 'a' || $letter > 'z') {
			$letter = "_";
		}

		$path = Config::$Tatoeba_Root . "/" . $language . "/" . $letter . "/word.csv";

		if(!file_exists($path)) {
			return null;
		}

		$handle = fopen($path, "r");
		$data = array();
		$found = false;
		while(($line = fgets($handle)) != false) {
			$parts = explode("\t", $line);
			if($parts[0] == $word) {
				$data[] = (int)trim($parts[1]);
				$found = true;
			} else if($found) {
				break;
			}
		}
		fclose($handle);

		if(!$found) {
			return null;
		}

		return $data;
	}

	public function get($word, $languageFrom, $languageTo) {

		$to6393 = LanguageTranslations::convertIso6391To6393($languageTo);
		$toNum = LanguageTranslations::languageIso6393ToInt($to6393);

		$indexes = TatoebaSentenceAdapter::getSentenceIndexes($word, $languageFrom);

		if($indexes == null) {
			return array();
		}

		if($languageTo == "" || $languageTo == null) {
			$languageTo = $languageFrom;
		}

		if($languageFrom == $languageTo) {

			$query = "
					SELECT SF.Sentence AS Original, SF.LanguageNum AS 'From', SF.Sentence AS Translated, SF.LanguageNum AS 'To'
					FROM memoling_Tatoeba_Sentences AS SF
					WHERE SF.SentenceId IN (" . $indexes . ")
					AND SF.Sentence LIKE :Word
					LIMIT 5
					";
				
			$stm = $this->db->prepare($query);
			$stm->bindValue(":Word", "%" . $word .  "%");
				
		} else {

			$query = "
					SELECT SF.Sentence AS Original, SF.LanguageNum AS 'From', ST.Sentence AS Translated, ST.LanguageNum AS 'To'
					FROM memoling_Tatoeba_Sentences AS SF
					RIGHT OUTER JOIN memoling_Tatoeba_Links AS SL ON SF.SentenceId = SL.SentenceIdA
					RIGHT OUTER JOIN memoling_Tatoeba_Sentences AS ST ON SL.SentenceIdB = ST.SentenceId
					WHERE SF.SentenceId IN (" . $indexes . ")
					AND ST.LanguageNum = :LanguageTo
					AND SF.Sentence LIKE :Word
					LIMIT 5
					";
				
			$stm = $this->db->prepare($query);
			$stm->bindParam(":LanguageTo", $toNum, PDO::PARAM_INT);
			$stm->bindValue(":Word", "%" . $word .  "%");
		}
		$stm->execute();

		if($stm->rowCount() == 0 && $languageFrom != $languageTo) {
			// Nothing found - fallback
			// try finding only in 'from' language and fill 'to' entries with empty strings

			$query = "
					SELECT SF.Sentence AS Original, SF.LanguageNum AS 'From', '' AS Translated, :LanguageTo AS 'To'
					FROM memoling_Tatoeba_Sentences AS SF
					WHERE SF.SentenceId IN (" . $indexes . ")
					AND SF.Sentence LIKE :Word
					LIMIT 5
					";
			
			$stm = $this->db->prepare($query);
			$stm->bindValue(":Word", "%" . $word .  "%");
			$stm->bindParam(":LanguageTo", $toNum, PDO::PARAM_INT);
			$stm->execute();
		}

		$list = array();

		while($row = $stm->fetch()) {
			$obj = new TranslatedSentence();
			$from = new Sentence();
			$to = new Sentence();
				
			$from->LanguageIso639 = LanguageTranslations::convertIso6393To6391(LanguageTranslations::languageIntToIso6393($row["From"]));
			$from->Sentence = $row["Original"];
			$to->LanguageIso639 = LanguageTranslations::convertIso6393To6391(LanguageTranslations::languageIntToIso6393($row["To"]));
			$to->Sentence = $row["Translated"];

			$obj->Original = $from;
			$obj->Translated = $to;
			$list[] = $obj;
		}

		return $list;
	}

}


function sortByLength($a,$b){
	return strlen($b)-strlen($a);
}

?>