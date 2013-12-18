<?php
require_once('../Init.php');

//TODO: Place it in the database (chirstmas lazyness
class Wiktionary extends  Webservice {
	
	private $db;	
	
	public function __construct() {
		$list = array();
		
		$w = new WiktionaryInfo();
		$w->WiktionaryInfoId = "02c7d17b-7f63-4c09-96d5-d73b067d75c0";
		$w->Description = "English wikitonary with translations, basic definitions and synonyms.";
		$w->DownloadUrl = "https://dl.dropboxusercontent.com/u/31079067/02c7d17b-7f63-4c09-96d5-d73b067d75c0.sqlite.gz";
		$w->Language = "en";
		$w->Name = "Wiktionary";
		$w->Version = 0;
		$w->DownloadSize = 17017453;
		$w->RealSize = 106040320;
		$list[] = $w;
		
		$w = new WiktionaryInfo();
		$w->WiktionaryInfoId = "d4a85889-32ec-484a-aa75-362ef4a5419b";
		$w->Description = "English wikitonary with translations, complex definitions and synonyms.";
		$w->DownloadUrl = "https://dl.dropboxusercontent.com/u/31079067/d4a85889-32ec-484a-aa75-362ef4a5419b.sqlite.gz";
		$w->Language = "en";
		$w->Name = "Wiktionary with Definitions";
		$w->Version = 0;
		$w->DownloadSize = 48778025;
		$w->RealSize = 210725888;
		$list[] = $w;
		
		$this->db = $list;
	}

	public function get() {
		return JsonBuilder::arrayToJson($this->db);
	}	
	
	public function isCurrent() {
		$wiktionaryInfoId = $_GET['wiktionaryInfoId'];
		$version = $_GET['version'];
		
		for($i=0;$i<sizeof($this->db);$i++) {
			$wiktionary = $this->db[$i];
			
			if($wiktionary->WikitonaryInfoId == $wiktionaryInfoId) {
				
				if($wiktionary->Version > $version) {
					return "false";
				}
				
				return "true";
			}
			
		}
		
		return "true";
	}
	
}


$handle = new Wiktionary();
echo $handle->action();

?>