<?php
require_once("../Init.php");

class Sentences extends Webservice {
	
	private $m_sentenceAdapter;
	
	public function __construct() {
		$this->m_sentenceAdapter = new TatoebaSentenceAdapter();
	}
	
	public function get() {
		
		$word = $_GET['word'];
		$languageFrom = $_GET['from'];
		$languageTo = $_GET['to'];
		
		$result = $this->m_sentenceAdapter->get($word, $languageFrom, $languageTo);

		return JsonBuilder::arrayToJson($result);
	}
}

$handle = new Sentences();
echo $handle->action();

?>
