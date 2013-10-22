<?php
include_once("../../Init.php");

function test() {
	if(!isset($_GET["word"])) {
		echo 'Provide $_GET["word"] and $_GET["lang"] and $_GET["password"]';
		return;
	}
	if(!isset($_GET['password']) || !strcmp(md5($_GET['password']), 'e82af5820966236e4e72f28613938b97') == 0) {
		echo 'incorrect password';
		return;
	}


	$word = $_GET["word"];
	$lang6391 = $_GET["lang"];
	$indexes = TatoebaSentenceAdapter::getSentenceIndexes($word, $lang6391);
	echo "Indexes<br />";
	var_dump($indexes);
	echo "<br/>";

	if($indexes != null) {
		$adapter = new TatoebaSentenceAdapter();
		$list = $adapter->get($word, $lang6391, "pl");
		echo '<pre>';
		var_dump($list);
		echo '</pre><br/>';
	}


}

$now = microtime(true);
test();
echo "Elapsed: ";
echo microtime(true) - $now ;
echo " sec <br />";


?>